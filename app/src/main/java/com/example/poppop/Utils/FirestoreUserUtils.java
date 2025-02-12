package com.example.poppop.Utils;


import android.util.Log;

import com.example.poppop.Model.ImageModel;
import com.example.poppop.Model.UserModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class FirestoreUserUtils {

    public ListenerRegistration listenToUserData(String uid, EventListener<DocumentSnapshot> listener) {
        DocumentReference userRef = FirebaseUtils.getUserReference(uid);
        return userRef.addSnapshotListener(listener);
    }
    public static Task<UserModel> checkIfUserExistsThenAdd(FirebaseUser user) {
        DocumentReference userRef = FirebaseUtils.getUserReference(user.getUid());
        return userRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // User exists, retrieve data from Firestore and create UserModel
                    if (document.getData() != null) {
                        UserModel existingUserModel = document.toObject(UserModel.class);
                        if (existingUserModel != null) {
                            // Check if the isAdmin field exists in the document
                            if (document.contains("isAdmin")) {
                                // Set the isAdmin field in the UserModel
                                existingUserModel.setAdmin(document.getBoolean("isAdmin"));
                            }
                            return existingUserModel;
                        } else {
                            // Handle the case where existingUserModel is null
                            return null;
                        }
                    } else {
                        // Handle the case where the document doesn't have any fields
                        return null;
                    }
                } else {
                    // User does not exist, create a new UserModel
                    UserModel newUserModel = createNewUserModel(user);
                    addUserToFirestore(userRef, newUserModel)
                            .addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    // If the update is successful, start the new activity
                                } else {
                                    // Handle errors here
                                    Exception exception = task2.getException();
                                    if (exception != null) {
                                        exception.printStackTrace();
                                    }
                                }
                            });;
                    return newUserModel;
                }
            } else {
                Log.e("Firestore", "Error checking if user exists", task.getException());
                throw Objects.requireNonNull(task.getException());
            }
        });
    }
    private static UserModel createNewUserModel(FirebaseUser user) {
        UserModel userModel = new UserModel();
        userModel.setUserId(user.getUid());
        userModel.setName(user.getDisplayName());
        userModel.setProfile(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
        userModel.setPremium(false);
        userModel.setNumOfImages(0);
        userModel.setPhotoUrl(userModel.getProfile());
        // Set Preference
        List<Integer> ageRangePref = new ArrayList<>();
        ageRangePref.add(18);
        ageRangePref.add(35);
        userModel.setAgeRangePref(ageRangePref);
        userModel.setMaxDistPref(30);
        userModel.setGenderPref("Everyone");
        userModel.setBanned(false);
        return userModel;
    }
    private static Task<Void> addUserToFirestore(DocumentReference userRef, UserModel userModel) {
        // Add user data to Firestore
        return userRef.set(userModel)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User added successfully to Firestore!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding user to Firestore", e));
    }
    public static CompletableFuture<Void> updateFCMTokenForUser(UserModel existingUserModel) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        FCMSender.getFCMToken()
                .thenAccept(token -> {
                    // Use the token as needed
                    Log.d("FCM token", "Get token successfully");

                    // Update the existing user model with the new FCM token
                    existingUserModel.setFcmToken(token);

                    // Update the FCM token in Firestore on a background thread
                    Tasks.call(() -> {
                        // Create a map with only the FCM token
                        Map<String, Object> tokenMap = new HashMap<>();
                        tokenMap.put("fcmToken", existingUserModel.getFcmToken());

                        // Update the FCM token in Firestore
                        FirebaseUtils.getUserReference(existingUserModel.getUserId())
                                .update(tokenMap)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("FCM token update", "Successfully updated FCM token in Firestore");
                                        future.complete(null); // Successfully updated FCM token in Firestore
                                    } else {
                                        Log.e("FCM token update", "Failed to update FCM token in Firestore: " + task.getException());
                                        future.completeExceptionally(task.getException());
                                    }
                                });
                        return null;
                    });
                })
                .exceptionally(throwable -> {
                    // Handle error
                    Log.e("FCM token", "Get token unsuccessfully: " + throwable.getMessage());
                    future.completeExceptionally(throwable);
                    return null;
                });

        return future;
    }

    public static Task<UserModel> getUserModelByUid(String uid) {
        DocumentReference userRef = FirebaseUtils.getUserReference(uid);

        return userRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    return document.toObject(UserModel.class);
                } else {
                    // Handle the case where the document does not exist
                    return null;
                }
            } else {
                // Handle exceptions if necessary
                return null;
            }
        });
    }

    public static Task<Void> updateUserModel(UserModel userModel) {
        DocumentReference userRef = FirebaseUtils.getUserReference(userModel.getUserId());
        return addUserToFirestore(userRef, userModel);
    }

    public static Task<Void> updateAge(String userId, int age) {
        DocumentReference userRef = FirebaseUtils.getUserReference(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("age", age);

        return userRef.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firestore", "User age updated successfully");
                    } else {
                        Log.e("Firestore", "Error updating user age", task.getException());
                    }
                });
    }

    public static Task<Void> updateBio(String userId, String bio) {
        DocumentReference userRef = FirebaseUtils.getUserReference(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("bio", bio);

        return userRef.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firestore", "User bio updated successfully");
                    } else {
                        Log.e("Firestore", "Error updating user bio", task.getException());
                    }
                });
    }

    public static Task<Void> updateLocation(String userId, GeoPoint geoPoint) {
        DocumentReference userRef = FirebaseUtils.getUserReference(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("currentLocation", geoPoint);

        return userRef.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Log or perform any other action upon success if needed
                        Log.d("Firestore", "User location updated successfully");
                    } else {
                        // Handle the error
                        Log.e("Firestore", "Error updating user location", task.getException());
                    }
                });
    }

    public static Task<Void> updateGender(String userId, String gender) {
        DocumentReference userRef = FirebaseUtils.getUserReference(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("gender", gender);

        return userRef.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firestore", "User gender updated successfully");
                    } else {
                        Log.e("Firestore", "Error updating user gender", task.getException());
                    }
                });
    }

    public static Task<Void> updateHoroscopeSign(String userId, String horoscopeSign) {
        DocumentReference userRef = FirebaseUtils.getUserReference(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("horoscopeSign", horoscopeSign);

        return userRef.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firestore", "User horoscope sign updated successfully");
                    } else {
                        Log.e("Firestore", "Error updating user horoscope sign", task.getException());
                    }
                });
    }

    public static Task<Void> updatePremium(String userId) {
        DocumentReference userRef = FirebaseUtils.getUserReference(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("premium", true);

        return userRef.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firestore", "User gets premium package");
                    } else {
                        Log.e("Firestore", "Error updating user premium package", task.getException());
                    }
                });
    }

    public static Task<Void> updateStatus(String userId, Boolean isBanned) {
        DocumentReference userRef = FirebaseUtils.getUserReference(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("banned", isBanned);

        return userRef.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firestore", "Update status successfully");
                    } else {
                        Log.e("Firestore", "Update status fail", task.getException());
                    }
                });
    }

    public static void updateUserImageList(String userId, List<ImageModel> imageList) {
        DocumentReference userRef = FirebaseUtils.getUserReference(userId);

        // Update the "imageList" field in the user's document
        userRef.update("image_list", imageList).addOnSuccessListener(aVoid -> {
            // Update successful
            Log.d("Firestore", "User image list updated successfully");
        }).addOnFailureListener(e -> {
            // Handle the failure to update
            Log.e("Firestore", "Error updating user image list", e);
        });
    }

    public static void addOneImageToUser(String userId, String firstImageUrl) {
        updateNumOfImages(userId, 1, firstImageUrl);
    }

    public static Task<Void> subtractOneImageFromUser(String userId, String firstImageUrl) {
        return updateNumOfImages(userId, -1, firstImageUrl);
    }

    private static Task<Void> updateNumOfImages(String userId, int numOfImagesChange, String firstImageUrl) {
        DocumentReference userRef = FirebaseUtils.getUserReference(userId);

        // Use FieldValue.increment to atomically update the numOfImages
        Map<String, Object> updates = new HashMap<>();
        updates.put("numOfImages", FieldValue.increment(numOfImagesChange));

        return userRef.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firestore", "User numOfImages updated successfully");
                        checkAndUpdatePhotoUrl(userRef, firstImageUrl);
                    } else {
                        Log.e("Firestore", "Error updating user numOfImages", task.getException());
                    }
                });
    }

    private static void checkAndUpdatePhotoUrl(DocumentReference userRef, String firstImageUrl) {
        userRef.get().addOnCompleteListener(snapshotTask -> {
            if (snapshotTask.isSuccessful()) {
                DocumentSnapshot document = snapshotTask.getResult();
                if (document.exists()) {
                    // Assuming "photoUrl" and "profile" are the field names
                    int numOfImages = Objects.requireNonNull(document.getLong("numOfImages")).intValue();
                    if (numOfImages == 0) {
                        String profileString = document.getString("profile");
                        // Update the "photoUrl" field with the string from "profile"
                        userRef.update("photoUrl", profileString)
                                .addOnSuccessListener(aVoid -> Log.d("FirebaseUserUtils", "photoUrl updated successfully"))
                                .addOnFailureListener(e -> Log.e("FirebaseUserUtils", "Error updating photoUrl", e));
                    } else{
                        userRef.update("photoUrl", firstImageUrl)
                                .addOnSuccessListener(aVoid -> Log.d("FirebaseUserUtils", "photoUrl updated successfully"))
                                .addOnFailureListener(e -> Log.e("FirebaseUserUtils", "Error updating photoUrl", e));
                    }
                } else {
                    Log.e("FirebaseUserUtils", "Document does not exist");
                }
            } else {
                Log.e("FirebaseUserUtils", "Error getting document", snapshotTask.getException());
            }
        });
    }

    public static void addUserToLikedList(String currentUserId, String likedUserId) {
        // Get reference to the current user's document in the "users" collection
        DocumentReference currentUserRef = FirebaseUtils.getUserReference(currentUserId);

        // Use arrayUnion to add the likedUserId to the liked_list if not already present
        currentUserRef.update("liked_list", FieldValue.arrayUnion(likedUserId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseUserUtils", "User added to liked_list");
                    // Remove from disliked_list if user is present
                    removeUserFromDislikedList(currentUserId, likedUserId);
                })
                .addOnFailureListener(e -> Log.e("FirebaseUserUtils", "Error adding user to liked_list", e));
    }


    public static void addUserToSwipedList(String currentUserId, String swipedUserId) {
        // Get reference to the current user's document in the "users" collection
        DocumentReference currentUserRef = FirebaseUtils.getUserReference(currentUserId);

        // Use arrayUnion to add the swipedUserId to the swiped_list if not already present
        currentUserRef.update("swiped_list", FieldValue.arrayUnion(swipedUserId))
                .addOnSuccessListener(aVoid -> Log.d("FirebaseUserUtils", "User added to swiped_list"))
                .addOnFailureListener(e -> Log.e("FirebaseUserUtils", "Error adding user to swiped_list", e));
    }

    public static void addUserToDisLikedList(String currentUserId, String dislikedUserId) {
        // Get reference to the current user's document in the "users" collection
        DocumentReference currentUserRef = FirebaseUtils.getUserReference(currentUserId);

        // Use arrayUnion to add the dislikedUserId to the disliked_list if not already present
        currentUserRef.update("disliked_list", FieldValue.arrayUnion(dislikedUserId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseUserUtils", "User added to disliked_list");
                    // Remove from liked_list if user is present
                    removeUserFromLikedList(currentUserId, dislikedUserId);
                })
                .addOnFailureListener(e -> Log.e("FirebaseUserUtils", "Error adding user to disliked_list", e));
    }


    public static void removeUserFromLikedList(String currentUserId, String unlikedUserId) {
        // Get reference to the current user's document in the "users" collection
        DocumentReference currentUserRef = FirebaseUtils.getUserReference(currentUserId);

        // Atomically remove the unlikedUserId from the liked_list
        currentUserRef.update("liked_list", FieldValue.arrayRemove(unlikedUserId))
                .addOnSuccessListener(aVoid -> Log.d("FirebaseUserUtils", "User removed from liked_list"))
                .addOnFailureListener(e -> Log.e("FirebaseUserUtils", "Error removing user from liked_list", e));
    }

    public static void removeUserFromDislikedList(String currentUserId, String dislikedUserId) {
        // Get reference to the current user's document in the "users" collection
        DocumentReference currentUserRef = FirebaseUtils.getUserReference(currentUserId);

        // Atomically remove the dislikedUserId from the disliked_list if present
        currentUserRef.update("disliked_list", FieldValue.arrayRemove(dislikedUserId))
                .addOnSuccessListener(aVoid -> Log.d("FirebaseUserUtils", "User removed from disliked_list"))
                .addOnFailureListener(e -> Log.e("FirebaseUserUtils", "Error removing user from disliked_list", e));
    }

}
