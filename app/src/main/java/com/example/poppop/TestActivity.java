package com.example.poppop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.poppop.Adapters.ImageGridAdapter;
import com.example.poppop.Model.UserModel;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.StorageUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private ImageGridAdapter imageGridAdapter;
    private GridView gridView;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        displayUserDetails();
    }

    // Replace this method with your logic to populate image URLs
    private List<String> yourImageUrls() {
        List<String> urls = new ArrayList<>();
        // Add your image URLs to the list
        urls.add("https://firebasestorage.googleapis.com/v0/b/poppop-datingapp.appspot.com/o/images%2FmBxywJd9FuSyyCmeaP3a9XGH0Xo2%2F1705465645606_e52f463c-ac37-43bf-9957-eceb2b82e963.jpg?alt=media&token=18fe164b-a1cc-4a39-bdcf-eda6af77c2c1");
        urls.add("https://firebasestorage.googleapis.com/v0/b/poppop-datingapp.appspot.com/o/images%2FmBxywJd9FuSyyCmeaP3a9XGH0Xo2%2F1705472715775_ad6d6716-2822-464a-8a2e-87e40cee9410.jpg?alt=media&token=642d951e-30cb-4f12-8299-cb4e3a5e5d25");
        return urls;
    }

    private void displayUserDetails() {
        DocumentReference userDocRef = FirebaseUtils.getUserReference("upLS5Wg2DCSCZcukzojxBehSdVk1");
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    userModel = documentSnapshot.toObject(UserModel.class);
                    if (userModel != null) {
                        if(userModel.getImage_list() == null){
                            userModel.setImage_list(new ArrayList<>());
                        }
                        //set up UI
                        imageGridAdapter = new ImageGridAdapter(this, userModel.getImage_list(), this, userModel);
                        // Set the adapter on your GridView
                        gridView.setAdapter(imageGridAdapter);
                    } else {
                        // Handle the case where the userModel is null
                    }
                }
            }
        });
    }

    public void startImagePicker() {
        // Start the image picker activity
        ImagePicker.with(this)
                .crop()
                .maxResultSize(1080, 1080)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result from the ImagePicker
        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            // Handle the result

            StorageUtils.uploadImageToStorage(TestActivity.this, userModel, fileUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Image uploaded successfully, and task.getResult() contains the download URL
                            StorageUtils.ImageUploadResult uploadResult = task.getResult();
                            String uniqueId = uploadResult.getUniqueId();
                            String downloadUrl = uploadResult.getDownloadUrl();
                            Log.d("Image upload", uniqueId);
                            Log.d("Image upload", downloadUrl);
                            imageGridAdapter.notifyDataSetChanged();
                            Log.d("Image", ": " + userModel.getImage_list().get(userModel.getImage_list().size()-1).getName());
                            Log.d("Image", ": " + userModel.getImage_list().get(userModel.getImage_list().size()-1).getUrl());
                        } else {
                            // Handle failure
                            Exception exception = task.getException();
                            // Handle the exception
                        }
                    });
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
