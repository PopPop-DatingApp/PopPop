package com.example.poppop.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.poppop.Model.UserModel;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.google.firebase.firestore.ListenerRegistration;

public class UserViewModel extends ViewModel {
    private final String TAG = "UserViewModel";
    private final FirestoreUserUtils firestoreUserUtils;
    private final MutableLiveData<UserModel> userLiveData = new MutableLiveData<>();
    private ListenerRegistration userDataListenerRegistration;

    public UserViewModel(FirestoreUserUtils firestoreUserUtils) {
        this.firestoreUserUtils = firestoreUserUtils;
    }

    public void setUserModel(UserModel userModel) {
        this.userLiveData.setValue(userModel);
    }

    // Method to save or update user data
    public void saveOrUpdateUser(UserModel userModel) {
        firestoreUserUtils.updateUserModel(userModel)
                .addOnFailureListener(exception -> {
                    String message = exception.getMessage();
                    Log.w(TAG, message);
                });
    }

    public LiveData<UserModel> getUserById(String userId) {
        // Use the provided function to get the user model by UID
        firestoreUserUtils.getUserModelByUid(userId)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserModel userModel = task.getResult();
                        if (userModel == null) {
                            Log.w(TAG, "UserModel is null.");
                            userLiveData.setValue(null);
                        } else {
                            userLiveData.setValue(userModel);
                        }
                    } else {
                        // Handle exceptions if necessary
                        Exception exception = task.getException();
                        Log.e(TAG, "Error getting user data", exception);
                        userLiveData.setValue(null);
                    }
                });

        return userLiveData;
    }

    // Start listening to real-time updates for user data
    public void startListeningToUserData(String userId) {
        userDataListenerRegistration = firestoreUserUtils.listenToUserData(userId, (documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Listen failed.", e);
                userLiveData.setValue(null);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                userLiveData.setValue(userModel);
            } else {
                Log.d(TAG, "Current data: null");
                userLiveData.setValue(null);
            }
        });
    }

    // Stop listening to real-time updates when the ViewModel is no longer needed
    public void stopListeningToUserData() {
        // Check if the listener registration is not null
        if (userDataListenerRegistration != null) {
            // Remove the listener to stop receiving updates
            userDataListenerRegistration.remove();
            userDataListenerRegistration = null; // Set it to null after removal
        }
    }

    // Provide the LiveData for the activity to observe
    public LiveData<UserModel> getUserLiveData() {
        return userLiveData;
    }
}
