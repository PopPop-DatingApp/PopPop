package com.example.poppop.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.poppop.Model.UserModel;
import com.example.poppop.Utils.FirebaseUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends ViewModel {
    private MutableLiveData<List<UserModel>> userList;

    private static final double EARTH_RADIUS = 6371.0;

    public LiveData<List<UserModel>> getUserList(String currentUserId, GeoPoint userLocation, String genderPref, int maxDist, List<Integer> ageRangePref) {
        Log.d("getUserList", "userLocation: " + userLocation + ", genderPref: " + genderPref +
                ", maxDist: " + maxDist + ", ageRangePref: " + ageRangePref);
        if (userList == null) {
            userList = new MutableLiveData<>();
            loadUserList(currentUserId, userLocation, genderPref, maxDist, ageRangePref);
        } else if (userList.getValue() == null || userList.getValue().isEmpty()) {
            // Fetch data only if the list is empty
            loadUserList(currentUserId, userLocation, genderPref, maxDist, ageRangePref);
        }
        return userList;
    }

    private void loadUserList(String currentUserId, GeoPoint userLocation, String genderPref, int maxDist, List<Integer> ageRangePref) {

        if (userList == null || userList.getValue() == null || userList.getValue().isEmpty()) {
            userList.postValue(new ArrayList<>());
            // Fetch data only if it hasn't been loaded before or if the list is empty
            CollectionReference usersRef = FirebaseUtils.getAllUsersCollectionReference();

            getNearbyMaleUsers(usersRef, currentUserId, userLocation, genderPref, maxDist, ageRangePref);
        }
    }

    private void getNearbyMaleUsers(CollectionReference usersCollection, String currentUserId, GeoPoint myLocation, String genderPref, int maxDist, List<Integer> ageRangePref) {
        int minAge = ageRangePref.get(0);
        int maxAge = ageRangePref.get(1);
        double maxDistFloat = 0.0 + maxDist;
        if (genderPref.equals("Everyone")) {
            usersCollection
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<UserModel> nearbyMaleUsers = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserModel user = document.toObject(UserModel.class);
                                Log.d("username", user.getName());
                                if(user.getCurrentLocation() == null || user.getAge() == null || user.getUserId().equals(currentUserId)) {
                                    Log.d("Skip", "Skip");
                                } else if (isWithinRadius(user.getCurrentLocation(), myLocation, maxDist)
                                        || isWithinAgeRange(user.getAge(), minAge, maxAge)) {
                                    nearbyMaleUsers.add(user);
                                }


                                // Limit to 10 users
                                if (nearbyMaleUsers.size() >= 10) {
                                    break;
                                }
                            }

                            //Sort theo diem

                            userList.setValue(nearbyMaleUsers);

                            // Process the nearbyMaleUsers list
                        } else {
                            // Handle the error
                        }
                    });
        } else {
            usersCollection
                    .whereEqualTo("gender", genderPref)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<UserModel> nearbyMaleUsers = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserModel user = document.toObject(UserModel.class);
                                Log.d("username", user.getName());
                                if(user.getCurrentLocation() == null || user.getAge() == null || user.getUserId().equals(currentUserId)) {
                                    Log.d("Skip", "Skip");
                                } else if (isWithinRadius(user.getCurrentLocation(), myLocation, maxDist)
                                        || isWithinAgeRange(user.getAge(), minAge, maxAge)) {
                                    nearbyMaleUsers.add(user);
                                }


                                // Limit to 10 users
                                if (nearbyMaleUsers.size() >= 10) {
                                    break;
                                }
                            }

                            //Sort theo diem

                            userList.setValue(nearbyMaleUsers);

                            // Process the nearbyMaleUsers list
                        } else {
                            // Handle the error
                        }
                    });
        }
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
        }else {
            // Handle empty user list
            Log.e("ViewModel", "User list is empty.");
        }

        // Add more function to user liked-list...
    }
}