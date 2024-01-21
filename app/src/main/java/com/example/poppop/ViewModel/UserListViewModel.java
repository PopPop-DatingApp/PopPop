package com.example.poppop.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.poppop.Model.UserModel;
import com.example.poppop.Utils.FirebaseUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserListViewModel extends ViewModel {
    private static final String TAG = "UserListViewModel";
    private final FirebaseUtils firebaseUtils;
    private MutableLiveData<List<UserModel>> userList = new MutableLiveData<List<UserModel>>();

    private static final double EARTH_RADIUS = 6371.0;

    public UserListViewModel(FirebaseUtils firebaseUtils) {
        this.firebaseUtils = firebaseUtils;
    }

    public LiveData<List<UserModel>> getAllUser() {
        return userList;
    }

    public void listenToUserList() {
        FirebaseUtils.getAllUsersCollectionReference().addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Listen failed.", e);
                userList.setValue(null);
                return;
            }

            List<UserModel> userModelList = new ArrayList<>();

            if (queryDocumentSnapshots != null) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.contains("isAdmin"))
                        continue;
                    UserModel userModel = documentSnapshot.toObject(UserModel.class);
                    userModelList.add(userModel);
                }

                userList.setValue(userModelList);
            } else {
                Log.d(TAG, "Current data: null");
                userList.setValue(null);
            }
        });
    }

    public LiveData<List<UserModel>> getUserListWithPref(UserModel userModel, GeoPoint userLocation, String genderPref, int maxDist, List<Integer> ageRangePref) {
        Log.d("getUserList", "userLocation: " + userLocation + ", genderPref: " + genderPref +
                ", maxDist: " + maxDist + ", ageRangePref: " + ageRangePref);
        if (userList == null) {
            userList = new MutableLiveData<>();
            loadUserList(userModel, userLocation, genderPref, maxDist, ageRangePref);
        } else if (userList.getValue() == null || userList.getValue().isEmpty()) {
            // Fetch data only if the list is empty
            loadUserList(userModel, userLocation, genderPref, maxDist, ageRangePref);
        }
        return userList;
    }

    private void loadUserList(UserModel userModel, GeoPoint userLocation, String genderPref, int maxDist, List<Integer> ageRangePref) {

        if (userList == null || userList.getValue() == null || userList.getValue().isEmpty()) {
            userList.postValue(new ArrayList<>());
            // Fetch data only if it hasn't been loaded before or if the list is empty
            CollectionReference usersRef = FirebaseUtils.getAllUsersCollectionReference();

            getUsersWithPref(usersRef, userModel, userLocation, genderPref, maxDist, ageRangePref);
        }
    }

    private void getUsersWithPref(CollectionReference usersCollection, UserModel userModel, GeoPoint myLocation, String genderPref, int maxDist, List<Integer> ageRangePref) {
        int minAge = ageRangePref.get(0);
        int maxAge = ageRangePref.get(1);
        double maxDistFloat = 0.0 + maxDist;

        Query userQuery = (genderPref.equals("Everyone"))
                ? usersCollection
                : usersCollection.whereEqualTo("gender", genderPref);
        Log.d("getUserList", userModel.getUserId());

        userQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<UserModel> nearbyUsers = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    UserModel user = document.toObject(UserModel.class);
                    if (user.getAdmin() == null || user.getAdmin())
                        if (shouldSkipUser(user, userModel.getUserId(), myLocation)) {
                            Log.d("Skip", "Skipped user: " + user.getName());
                        } else if (!isInLikedList(user.getUserId(), userModel) &&
                                (isWithinRadius(user.getCurrentLocation(), myLocation, maxDist) ||
                                        isWithinAgeRange(user.getAge(), minAge, maxAge))) {
                            nearbyUsers.add(user);
                        }
                }

                // Sort the list based on swiped_list
                Collections.sort(nearbyUsers, (user1, user2) -> {
                    boolean user1Swiped = isInSwipedList(user1.getUserId(), userModel);
                    boolean user2Swiped = isInSwipedList(user2.getUserId(), userModel);

                    // Users with IDs in swiped_list come last
                    return Boolean.compare(user2Swiped, user1Swiped);
                });

                Log.d("getUserList", "Nearby Users count: " + nearbyUsers.size());
                userList.setValue(nearbyUsers);
            } else {
                Log.e("getUserList", "Error getting users: ", task.getException());
                // Handle the error
            }
        });
    }

    private boolean shouldSkipUser(UserModel user, String currentUserId, GeoPoint myLocation) {
        return (user.getCurrentLocation() == null || user.getAge() == null
                || user.getUserId().equals(currentUserId) || myLocation == null);
    }

    private boolean isInLikedList(String userId, UserModel userModel) {
        return userModel != null && userModel.getLiked_list() != null && userModel.getLiked_list().contains(userId);
    }

    private boolean isInSwipedList(String userId, UserModel userModel) {
        return userModel != null && userModel.getSwiped_list() != null && userModel.getSwiped_list().contains(userId);
    }


    private static boolean isWithinRadius(GeoPoint location1, GeoPoint location2, double radiusInKm) {
        double distance = calculateDistance(location1.getLatitude(), location1.getLongitude(),
                location2.getLatitude(), location2.getLongitude());
        Log.d("Distance", ": " + distance);
        Log.d("Distance", ": " + (distance <= radiusInKm));
        return distance <= radiusInKm;
    }

    private static boolean isWithinAgeRange(Integer age, int minAge, int maxAge) {
        return age != null && age >= minAge && age <= maxAge;
    }

    private static double calculateDistance(double userLat, double userLng, double locLat, double locLng) {
        try {
            double latDistance = Math.toRadians(locLat - userLat);
            double lngDistance = Math.toRadians(locLng - userLng);

            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(locLat))
                    * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return EARTH_RADIUS * c; // Distance in kilometers
        } catch (IllegalArgumentException e) {
            // Handle the exception (e.g., log it or return a default value)
            e.printStackTrace(); // Print the exception details (for demonstration)
            return -1; // Or any other default value that suits your use case
        }
    }


    public void deleteTopUser() {
        List<UserModel> currentList = userList.getValue();
        if (currentList != null && !currentList.isEmpty()) {
            currentList.remove(0); // Remove the first user
            userList.setValue(currentList);
        } else {
            // Handle empty user list
            Log.e("ViewModel", "User list is empty.");
        }

        // Add more function to user liked-list...
    }
}