package com.example.poppop.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainFragment extends Fragment {
    FirebaseUser firebaseUser;
    TextView main_name;
    UserModel userModel;
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
                    }
//                    else{
//                        // Handle profile exit
//                        getActivity().getSupportFragmentManager().popBackStack();
//                    }
                }
            }
        });
    }


}