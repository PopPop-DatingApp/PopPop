package com.example.poppop.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poppop.Model.ImageModel;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.example.poppop.Utils.StorageUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Objects;

public class MainFragment extends Fragment {
    FirebaseUser firebaseUser;
    TextView main_name;
    UserModel userModel;
    ImageView imageView;
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
           displayUserDetails(firebaseUser.getUid());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        main_name = view.findViewById(R.id.main_name);
        Button pickBtn = view.findViewById(R.id.button2);
        Button deleteBtn = view.findViewById(R.id.button);
        imageView = view.findViewById(R.id.imageView);
        pickBtn.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .crop()
//                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .createIntent(intent -> {
                        startForProfileImageResult.launch(intent);
                        return null;
                    });
        });

        deleteBtn.setOnClickListener(v -> {
            if (userModel.getNumOfImages() > 0) {
                StorageUtils.deleteImageFromStorage(userModel, userModel.getImage_list().get(userModel.getNumOfImages() - 1))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Image deleted successfully, now update the UI
                                displayUserDetails(firebaseUser.getUid());
                                Toast.makeText(requireContext(), "Delete successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle failure
                                Toast.makeText(requireContext(), "Failed to delete image: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(requireContext(), "There is no image to delete", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void displayUserDetails(String userId) {
        DocumentReference userDocRef = FirebaseUtils.getUserReference(userId);
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    userModel = documentSnapshot.toObject(UserModel.class);
                    if(userModel != null){
                        //set up UI
                        main_name.setText(userModel.getName());

                        // retrieve the list of image url for display
                        List<ImageModel> imageList = userModel.getImage_list();
                    }
//                    else{
//                        // Handle profile exit
//                        getActivity().getSupportFragmentManager().popBackStack();
//                    }
                }
            }
        });
    }
    private final ActivityResultLauncher<Intent> startForProfileImageResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();

                        if (resultCode == Activity.RESULT_OK && data != null) {
                            Uri fileUri = data.getData();
                            // Handle the result
//                            imageView.setImageURI(fileUri);
                            StorageUtils.uploadImageToStorage(requireContext(),userModel,fileUri, imageView)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Image uploaded successfully, and task.getResult() contains the download URL
                                            displayUserDetails(firebaseUser.getUid());
                                        } else {
                                            // Handle failure
                                            Exception exception = task.getException();
                                            // Handle the exception
                                        }
                                    });
                        } else if (resultCode == ImagePicker.RESULT_ERROR) {
                            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });



}