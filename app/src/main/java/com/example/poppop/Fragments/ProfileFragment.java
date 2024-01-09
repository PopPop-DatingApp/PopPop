package com.example.poppop.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.poppop.LoginActivity;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    FirebaseUser firebaseUser;

    UserModel userModel;
    TextView name;
    Button logoutBtn;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
//            Intent intent = new Intent(getActivity(), LoginActivity.class);
//            startActivity(intent);
//            getActivity().finish();
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
        name = view.findViewById(R.id.profile_username);
        logoutBtn = view.findViewById(R.id.profile_logout);
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
                        name.setText(userModel.getName());
                    }
                    else{
                        // Handle profile exit
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            }
        });
    }
}