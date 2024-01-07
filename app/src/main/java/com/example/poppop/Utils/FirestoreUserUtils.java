package com.example.poppop.Utils;


import android.util.Log;

import com.example.poppop.FCMSender;
import com.example.poppop.Model.UserModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class FirestoreUserUtils {
    public static Task<UserModel> checkIfUserExistsThenAdd(FirebaseUser user) {
        DocumentReference userRef = FirebaseUtils.getUserReference(user.getUid());
        return userRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // User exists, retrieve data from Firestore and create UserModel
                    return document.toObject(UserModel.class);
                } else {
                    // User does not exist, create a new UserModel
                    UserModel newUserModel = createNewUserModel(user);
                    addUserToFirestore(userRef, newUserModel);
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
        // Add other fields as needed

        return userModel;
    }
    private static void addUserToFirestore(DocumentReference userRef, UserModel userModel) {
        // Add user data to Firestore
        userRef.set(userModel)
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
}
