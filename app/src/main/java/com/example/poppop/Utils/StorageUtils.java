package com.example.poppop.Utils;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.poppop.Model.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

import okhttp3.internal.Util;

public class StorageUtils {
    public static void uploadImageToStorage(Context context, UserModel userModel, Uri uri, ImageView imageView) {
        Integer numOfImages = userModel.getNumOfImages();
        StorageReference imageRef = FirebaseUtils.getCurrentPicStorageRef().child(numOfImages + ".jpg");
        UploadTask uploadTask = imageRef.putFile(uri);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Display the uploaded image using Glide
                Glide.with(context).load(uri).into(imageView);
                // Add one to the number of images of the user
                FirestoreUserUtils.addOneImageToUser(userModel.getUserId());
                Log.d("Umage", "Image uploaded successfully");
            } else {
                // Handle failures
                Log.e("Umage", "Fail to upload image", task.getException());
            }
        });
    }

}
