package com.example.poppop.Utils;

import android.util.Log;

import com.example.poppop.Model.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

public class FirebaseUtils {
    public static Task<UserModel> checkIfUserExistsThenAdd(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(user.getUid());
        return userRef.get().continueWith(new Continuation<DocumentSnapshot, UserModel>() {
            @Override
            public UserModel then(@NonNull Task<DocumentSnapshot> task) throws Exception {
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
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "User added successfully to Firestore!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error adding user to Firestore", e);
                    }
                });
    }
    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static String getChatroomId(String userId1, String userId2){
        if(userId1.hashCode() < userId2.hashCode()){
            return userId1+"_"+userId2;
        }else {
            return userId2+"_"+userId1;
        }
    }
}
