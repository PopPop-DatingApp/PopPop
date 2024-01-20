package com.example.poppop.ViewModel;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.poppop.Model.AdminModel;
import com.example.poppop.Model.UserModel;
import com.example.poppop.Utils.AdminUtils;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

public class AdminViewModel extends ViewModel {
    private final String TAG = "AdminViewModel";
    private final AdminUtils adminUtils;
    private final MutableLiveData<AdminModel> adminLiveData = new MutableLiveData<>();
    private ListenerRegistration adminDataListenerRegistration;

    public AdminViewModel(AdminUtils adminUtils) {
        this.adminUtils = adminUtils;
    }

    public LiveData<AdminModel> getAdminLiveData() {
        return adminLiveData;
    }

    public void startListeningToAdminData(String adminId) {
        adminDataListenerRegistration = adminUtils.listenToAdminData(adminId, (documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Listen failed.", e);
                adminLiveData.setValue(null);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                AdminModel adminModel = documentSnapshot.toObject(AdminModel.class);
                adminLiveData.setValue(adminModel);
            } else {
                Log.d(TAG, "Current data: null");
                adminLiveData.setValue(null);
            }
        });
    }

    public void stopListeningToAdminData() {
        if (adminDataListenerRegistration != null) {
            adminDataListenerRegistration.remove();
            adminDataListenerRegistration = null;
        }
    }
}

