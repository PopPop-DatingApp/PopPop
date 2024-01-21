package com.example.poppop.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.FirestoreUserUtils;

public class UserListViewModelFactory implements ViewModelProvider.Factory {

    private final FirebaseUtils firebaseUtils;


    public UserListViewModelFactory(FirebaseUtils firebaseUtils) {
        this.firebaseUtils = firebaseUtils;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListViewModel.class)) {
            return (T) new UserListViewModel(firebaseUtils);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

