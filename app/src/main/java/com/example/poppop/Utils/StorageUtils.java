package com.example.poppop.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.poppop.Model.ImageModel;
import com.example.poppop.Model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StorageUtils {


//    public static Task<String> uploadImageToStorage(Context context, UserModel userModel, Uri uri, ImageView imageView) {
//        // Generate a unique ID based on timestamp and random component
//        String uniqueId = System.currentTimeMillis() + "_" + UUID.randomUUID().toString();
//
//        // Create the StorageReference with the unique ID
//        StorageReference imageRef = FirebaseUtils.getCurrentPicStorageRef().child(uniqueId + ".jpg");
//        UploadTask uploadTask = imageRef.putFile(uri);
//
//        // Create a TaskCompletionSource to manually complete the task
//        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();
//
//        uploadTask.addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                // Retrieve the download URL
//                imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
//                    ImageModel imageModel = new ImageModel(downloadUrl.toString(), uniqueId);
//                    // Add the download URL to the user's image list
//                    List<ImageModel> imageList = userModel.getImage_list();
//                    if (imageList == null) {
//                        imageList = new ArrayList<>();
//                    }
//                    imageList.add(imageModel);
//                    userModel.setImage_list(imageList);
//
//                    // Update the user's image list in Firestore
//                    FirestoreUserUtils.updateUserImageList(userModel.getUserId(), imageList);
//
//                    // Add one to the number of images of the user
//                    FirestoreUserUtils.addOneImageToUser(userModel.getUserId(), imageList.get(0).getUrl());
//
//                    // Display the uploaded image using Glide
//                    Glide.with(context).load(uri).into(imageView);
//
//                    // Complete the task with the download URL
//                    taskCompletionSource.setResult(downloadUrl.toString());
//                }).addOnFailureListener(e -> {
//                    // Handle the failure to get download URL
//                    Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
//                    // Complete the task with an error
//                    taskCompletionSource.setException(e);
//                });
//            } else {
//                // Handle failures
//                Log.e("Image", "Fail to upload image", task.getException());
//                Toast.makeText(context, "Fail to upload image", Toast.LENGTH_SHORT).show();
//                // Complete the task with an error
//                taskCompletionSource.setException(task.getException());
//            }
//        });
//
//        // Return the task associated with the TaskCompletionSource
//        return taskCompletionSource.getTask();
//    }

    public static Task<ImageUploadResult> uploadImageToStorage(Context context, UserModel userModel, Uri uri) {
        // Generate a unique ID based on timestamp and random component
        String uniqueId = System.currentTimeMillis() + "_" + UUID.randomUUID().toString();

        // Create the StorageReference with the unique ID
        StorageReference imageRef = FirebaseUtils.getCurrentPicStorageRef().child(uniqueId + ".jpg");
        UploadTask uploadTask = imageRef.putFile(uri);

        // Create a TaskCompletionSource to manually complete the task
        TaskCompletionSource<ImageUploadResult> taskCompletionSource = new TaskCompletionSource<>();

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Retrieve the download URL
                imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                    ImageModel imageModel = new ImageModel(downloadUrl.toString(), uniqueId);
                    // Add the download URL to the user's image list
                    List<ImageModel> imageList = userModel.getImage_list();
                    if (imageList == null) {
                        imageList = new ArrayList<>();
                    }
                    imageList.add(imageModel);
                    userModel.setImage_list(imageList);

                    // Update the user's image list in Firestore
                    FirestoreUserUtils.updateUserImageList(userModel.getUserId(), imageList);

                    // Add one to the number of images of the user
                    FirestoreUserUtils.addOneImageToUser(userModel.getUserId(), imageList.get(0).getUrl());
                    ImageUploadResult uploadResult = new ImageUploadResult(uniqueId, downloadUrl.toString());
                    // Complete the task with the download URL
                    taskCompletionSource.setResult(uploadResult);
                }).addOnFailureListener(e -> {
                    // Handle the failure to get download URL
                    Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
                    // Complete the task with an error
                    taskCompletionSource.setException(e);
                });
            } else {
                // Handle failures
                Log.e("Image", "Fail to upload image", task.getException());
                Toast.makeText(context, "Fail to upload image", Toast.LENGTH_SHORT).show();
                // Complete the task with an error
                taskCompletionSource.setException(task.getException());
            }
        });

        // Return the task associated with the TaskCompletionSource
        return taskCompletionSource.getTask();
    }

    public static Task<Void> deleteImageFromStorage(UserModel userModel, ImageModel imageModel) {
        // Get the StorageReference for the image
        StorageReference imageRef = FirebaseUtils.getCurrentPicStorageRef().child(imageModel.getName() + ".jpg");

        // Create a TaskCompletionSource to manually complete the task
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        // Delete the image from storage
        imageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // File deleted successfully
                    Log.d("Delete", "File Deleted successfully");

                    // Remove image from the image list
                    List<ImageModel> imageList = userModel.getImage_list();
                    imageList.remove(imageModel);

                    // Update the new image list
                    FirestoreUserUtils.updateUserImageList(userModel.getUserId(), imageList);

                    // Subtract one number of image from user
                    FirestoreUserUtils.subtractOneImageFromUser(userModel.getUserId(), imageList.isEmpty() ? null : imageList.get(0).getUrl());

                    // Complete the task successfully
                    taskCompletionSource.setResult(null);
                })
                .addOnFailureListener(exception -> {
                    // Uh-oh, an error occurred!
                    Log.e("Delete", "File Deletion failed", exception);

                    // Complete the task with failure
                    taskCompletionSource.setException(exception);
                });

        // Return the task associated with the TaskCompletionSource
        return taskCompletionSource.getTask();
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

    public static class ImageUploadResult {
        private final String uniqueId;
        private final String downloadUrl;

        public ImageUploadResult(String uniqueId, String downloadUrl) {
            this.uniqueId = uniqueId;
            this.downloadUrl = downloadUrl;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }
    }
}


