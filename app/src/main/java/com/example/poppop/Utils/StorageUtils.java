package com.example.poppop.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.poppop.Model.UserModel;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StorageUtils {


    public static void uploadImageToStorage(Context context, UserModel userModel, Uri uri, ImageView imageView) {
        // Generate a unique ID based on timestamp and random component
        String uniqueId = System.currentTimeMillis() + "_" + UUID.randomUUID().toString();

        // Create the StorageReference with the unique ID
        StorageReference imageRef = FirebaseUtils.getCurrentPicStorageRef().child(uniqueId + ".jpg");
        UploadTask uploadTask = imageRef.putFile(uri);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Retrieve the download URL
                imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                    // Add the download URL to the user's image list
                    List<String> imageList = userModel.getImage_list();
                    if (imageList == null) {
                        imageList = new ArrayList<>();
                    }
                    imageList.add(downloadUrl.toString());
                    userModel.setImage_list(imageList);

                    // Update the user's image list in Firestore
                    FirestoreUserUtils.updateUserImageList(userModel.getUserId(), imageList);

                    // Add one to the number of images of the user
                    FirestoreUserUtils.addOneImageToUser(userModel.getUserId());

                    // Display the uploaded image using Glide
                    Glide.with(context).load(uri).into(imageView);
                }).addOnFailureListener(e -> {
                    // Handle the failure to get download URL
                    Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
                });
            } else {
                // Handle failures
                Log.e("Image", "Fail to upload image", task.getException());
                Toast.makeText(context, "Fail to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // this function below get all image Url from a reference in storage
//    public static void getAllImageUrlsFromStorage(String userId) {
//        long startTime = System.currentTimeMillis();
//        StorageReference storageRef = FirebaseUtils.getOtherPicStorageRef(userId);
//        storageRef.listAll()
//                .addOnSuccessListener(listResult -> {
//                    for (StorageReference item : listResult.getItems()) {
//                        // Get the download URL for each item
//                        item.getDownloadUrl()
//                                .addOnSuccessListener(downloadUrl -> {
//                                    // Handle the download URL (e.g., store or display it)
//                                    String imageUrl = downloadUrl.toString();
//                                    Log.d("FirebaseStorage", "Image URL: " + imageUrl);
//                                })
//                                .addOnFailureListener(e -> {
//                                    // Handle the failure to get download URL
//                                    Log.e("FirebaseStorage", "Error getting download URL", e);
//                                });
//                    }
//                    long endTime = System.currentTimeMillis();
//                    Log.d("FirebaseStorage", "Function execution time: " + (endTime - startTime) + " milliseconds");
//                })
//                .addOnFailureListener(e -> {
//                    // Handle the failure to list items
//                    Log.e("FirebaseStorage", "Error listing items", e);
//                });
//    }

}
