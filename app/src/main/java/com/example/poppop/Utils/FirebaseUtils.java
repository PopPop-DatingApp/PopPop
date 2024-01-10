package com.example.poppop.Utils;

import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FirebaseUtils {
    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static String currentUserName(){
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }
    public static DocumentReference getUserReference(String userId){
        return FirebaseFirestore.getInstance().collection("users").document(userId);
    }

    public static CollectionReference getAllChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtils.currentUserId())){
            return getUserReference(userIds.get(1));
        }else{
            return getUserReference(userIds.get(0));
        }
    }

    public static String getChatroomId(String userId1, String userId2){
        if(userId1.hashCode() < userId2.hashCode()){
            return userId1+"_"+userId2;
        }else {
            return userId2+"_"+userId1;
        }
    }

    public static Task<String> getOtherProfilePic(String otherUserId) {
        Log.d("qwerty", "start to get ref");
        Task<DocumentSnapshot> task = FirebaseUtils.getUserReference(otherUserId).get();

        return task.continueWith(task1 -> {
            if (task1.isSuccessful()) {
                Log.d("qwerty", "Finish get ref");
                DocumentSnapshot documentSnapshot = task1.getResult();
                if (documentSnapshot.exists()) {
                    Log.d("qwerty", "Document exists");
                    // Get the profile field value
                    String profileUrl = documentSnapshot.getString("profile");
                    // Now you have the profileUrl, you can use it as needed
                    if (profileUrl != null) {
                        // Do something with the profile URL
                        return profileUrl;
                    } else {
                        // Handle the case where the "profile" field is not present or is null
                        throw new NullPointerException("Profile URL is null or not present");
                    }
                } else {
                    // Handle the case where the document does not exist
                    throw new Exception("Document does not exist");
                }
            } else {
                // Handle exceptions if necessary
                throw Objects.requireNonNull(task1.getException());
            }
        });
    }


    public static StorageReference getCurrentPicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("images")
                .child(FirebaseUtils.currentUserId());
    }

    public static StorageReference getOtherPicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("images")
                .child(otherUserId);
    }

}
