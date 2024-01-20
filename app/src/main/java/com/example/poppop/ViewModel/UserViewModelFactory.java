package com.example.poppop.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.poppop.Utils.FirestoreUserUtils;

public class UserViewModelFactory implements ViewModelProvider.Factory {

    private final FirestoreUserUtils firestoreUserUtils;

    public UserViewModelFactory(FirestoreUserUtils firestoreUserUtils) {
        this.firestoreUserUtils = firestoreUserUtils;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(firestoreUserUtils);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

