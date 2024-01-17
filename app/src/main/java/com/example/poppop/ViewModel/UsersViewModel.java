package com.example.poppop.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.poppop.Model.UserModel;
import com.example.poppop.Utils.FirebaseUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends ViewModel {
    private MutableLiveData<List<UserModel>> userList;

    public LiveData<List<UserModel>> getUserList() {
        if (userList == null) {
            userList = new MutableLiveData<>();
            loadUserList();
        } else if (userList.getValue() == null || userList.getValue().isEmpty()) {
            // Fetch data only if the list is empty
            loadUserList();
        }
        return userList;
    }

    private void loadUserList() {
        if (userList == null || userList.getValue() == null || userList.getValue().isEmpty()) {
            // Fetch data only if it hasn't been loaded before or if the list is empty
            CollectionReference usersRef = FirebaseUtils.getAllUsersCollectionReference();

            usersRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        List<UserModel> users = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserModel user = document.toObject(UserModel.class);
                            users.add(user);
                        }
                        userList.setValue(users);
                    } else {
                        // Handle the case when task.getResult() is null
                        Log.e("ViewModel", "Query result is null.");
                    }
                } else {
                    // Handle errors
                    Log.e("ViewModel", "Error getting documents: ", task.getException());
                }
            });
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