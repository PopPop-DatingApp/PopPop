package com.example.poppop.Utils;

import static com.example.poppop.Utils.FirebaseUtils.getAllUsersCollectionReference;

import android.util.Log;

import com.example.poppop.Model.AdminModel;
import com.example.poppop.Model.UserModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminUtils {
    private static final String TAG = "AdminUtils";

    public static void updateAdminModel(AdminModel adminModel) {
        DocumentReference adminRef = FirebaseFirestore.getInstance().collection("admins").document(adminModel.getAdminId());
        adminRef.set(adminModel)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Admin data updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating admin data", e));
    }

    public ListenerRegistration listenToAdminData(String adminId, EventListener<DocumentSnapshot> listener) {
        DocumentReference adminRef = FirebaseUtils.getAdminReference(adminId);
        return adminRef.addSnapshotListener(listener);
    }

    public static void getAllUsersList(FirestoreListener<List<UserModel>> listener) {
        CollectionReference usersCollectionRef = getAllUsersCollectionReference();
        usersCollectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserModel> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        UserModel userModel = document.toObject(UserModel.class);
                        // Not getting the admin
                        if(userModel.getName() != null)
                            userList.add(userModel);
                    }
                    listener.onSuccess(userList);
                })
                .addOnFailureListener(listener::onFailure);
    }


    public interface FirestoreListener<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

}

