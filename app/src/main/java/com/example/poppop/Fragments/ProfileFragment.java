package com.example.poppop.Fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.poppop.LoginActivity;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.example.poppop.Utils.LocationUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

public class ProfileFragment extends Fragment implements LocationUtils.GeoPointResultListener {
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
                        requestUserLocation();
                    }
                    else{
                        // Handle profile exit
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            }
        });
    }

    private void requestUserLocation() {
        LocationUtils.getCurrentLocation(requireActivity(), requireContext(), this);
    }

    @Override
    public void onGeoPointResult(GeoPoint geoPoint) {
        if(geoPoint != null){
            //update to firestore
            FirestoreUserUtils.updateLocation(userModel.getUserId(), geoPoint)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Handle success
                            Toast.makeText(requireContext(), "Update location successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle error
                            Toast.makeText(requireContext(), "Update location fail", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else{
            Toast.makeText(requireContext(), "Fail to get location", Toast.LENGTH_SHORT).show();

        }
    }
}