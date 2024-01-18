package com.example.poppop.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.poppop.CheckoutActivity;
import com.example.poppop.EditProfileActivity;
import com.example.poppop.LoginActivity;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileFragment extends Fragment{
    FirebaseUser firebaseUser;

    UserModel userModel;
    TextView userName;
    Button logoutBtn;
    ImageButton editProfileBtn;
    ImageView avatar, checked_image;

    Button premiumButton;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            // If user is logged in, display user details
            displayUserDetails(firebaseUser.getUid());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userName = view.findViewById(R.id.profile_username);
        avatar = view.findViewById(R.id.profile_avatar);
        logoutBtn = view.findViewById(R.id.profile_logout);
        editProfileBtn = view.findViewById(R.id.profile_editBtn);
        checked_image = view.findViewById(R.id.checked_image);
        premiumButton = view.findViewById(R.id.premiereBtn);
        premiumButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CheckoutActivity.class);
            intent.putExtra("userModel", userModel);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            // Optionally, sign out from Google as well
             GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN);
             googleSignInClient.signOut().addOnCompleteListener(task -> {
                 // Update UI after signing out
                 Intent intent = new Intent(getActivity(), LoginActivity.class);
                 startActivity(intent);
                 getActivity().finish();
             });
        });

        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("userModel",userModel);
            startActivity(intent);
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
                        String name = userModel.getName() + (userModel.getAge() == null ? "" : ", " + userModel.getAge());
                        userName.setText(name);
                        if(userModel.getPremium()){
                            premiumButton.setText("View your premium package");
                            checked_image.setVisibility(View.VISIBLE);
                        }
                        Utils.setProfilePic(requireContext(),userModel.getPhotoUrl(),avatar);
                    }
                    else{
                        // Handle profile exit
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            }
        });
    }
}