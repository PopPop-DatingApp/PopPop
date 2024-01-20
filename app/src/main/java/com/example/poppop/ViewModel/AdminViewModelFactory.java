package com.example.poppop.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.poppop.Utils.AdminUtils;

public class AdminViewModelFactory implements ViewModelProvider.Factory {
    private final AdminUtils adminUtils;

    public AdminViewModelFactory(AdminUtils adminUtils) {
        this.adminUtils = adminUtils;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AdminViewModel.class)) {
            //noinspection unchecked
            return (T) new AdminViewModel(adminUtils);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
