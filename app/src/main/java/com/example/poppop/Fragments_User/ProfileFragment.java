package com.example.poppop.Fragments_User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.poppop.Activities.CheckoutActivity;
import com.example.poppop.Activities.EditProfileActivity;
import com.example.poppop.Activities.LoginActivity;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.example.poppop.Utils.Utils;
import com.example.poppop.ViewModel.UserViewModel;
import com.example.poppop.ViewModel.UserViewModelFactory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment{
    private final String TAG = "ProfileFragment";
    FirebaseUser firebaseUser;
    UserModel userModel;
    TextView userName;
    Button logoutBtn;
    ImageButton editProfileBtn;
    ImageView avatar, checked_image;
    Button premiumButton;
    private UserViewModel userViewModel;

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
            // If the user is logged in, display user details
            userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(new FirestoreUserUtils())).get(UserViewModel.class);
            userViewModel.startListeningToUserData(firebaseUser.getUid());

            // Observe changes in the user model
            userViewModel.getUserLiveData().observe(this, userModel -> {
                if (userModel != null) {
                    // User data has changed, update your UI accordingly
                    Log.d(TAG, userModel.getName());
                    updateUI(userModel);
                } else {
                    // Handle the case where the user data is null or not found
                    Log.d(TAG, "User data is null");
                }
            });
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

    private void updateUI(UserModel userModel) {
        this.userModel = userModel;
        // Update UI elements based on the userModel
        String name = userModel.getName() + (userModel.getAge() == null ? "" : ", " + userModel.getAge());
        userName.setText(name);

        if (userModel.getPremium()) {
            premiumButton.setText("View your premium package");
            checked_image.setVisibility(View.VISIBLE);
        } else {
            premiumButton.setText("Upgrade to premium");
            checked_image.setVisibility(View.GONE);
        }

        Utils.setProfilePic(requireContext(), userModel.getPhotoUrl(), avatar);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (userViewModel != null) {
            userViewModel.stopListeningToUserData();
        }
    }
}