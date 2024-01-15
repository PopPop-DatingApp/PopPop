package com.example.poppop.boardingpages;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.poppop.LoginActivity;
import com.example.poppop.MainActivity;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class hobbyBoarding extends AppCompatActivity {
    private TextView tempDataTextView, tempDataTextView2, TempDataTextView3, TempDataTextView4;
    private String userName, userGender, userHoro;
    private Integer userAge;
    private UserModel newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CHECK THE PREV DATA (WILL BE DELETED AFTER FUNCTIONS DONE)
        setContentView(R.layout.activity_hobby_boarding);
        tempDataTextView = findViewById(R.id.tempDataTextViewgend);
        tempDataTextView2 = findViewById(R.id.tempDataTextViewgend2);
        TempDataTextView3 = findViewById(R.id.tempDataTextViewgend3);
        TempDataTextView4 = findViewById(R.id.tempDataTextViewgend4);

        userName = getIntent().getStringExtra("userName");
        userAge = getIntent().getIntExtra("userAge", 18);
        userGender = getIntent().getStringExtra("userGender");
        userHoro = getIntent().getStringExtra("userHoro");

        tempDataTextView.setText("Temp Data: " + userName);
        tempDataTextView2.setText("Temp Data: " + userAge);
        TempDataTextView3.setText("Temp Data: " + userGender);
        TempDataTextView4.setText("Temp Data: " + userHoro);

        Button save = findViewById(R.id.saveBtn);
        save.setOnClickListener(v -> {
            FirestoreUserUtils.getUserModelByUid(FirebaseUtils.currentUserId())
                    .addOnSuccessListener(userModel -> {
                        // This method is called when UserModel is successfully retrieved
                        newUser = userModel;

                        if (newUser != null) {
                            newUser.setName(userName);
                            newUser.setAge(userAge);
                            newUser.setGender(userGender);
                            newUser.setHoroscopeSign(userHoro);

                            FirestoreUserUtils.updateUserModel(newUser)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // If the update is successful, start the new activity
                                            startActivity(new Intent(hobbyBoarding.this, MainActivity.class));
                                        } else {
                                            // Handle errors here
                                            Exception exception = task.getException();
                                            if (exception != null) {
                                                exception.printStackTrace();
                                            }
                                        }
                                    });
                        } else {
                            Log.e("ChatActivity", "otherUser is null");
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure case
                        Log.e("Firestore", "Error retrieving UserModel: " + e.getMessage());
                        finish();
                    });
        });


        //---------------------END OF SECTION---------------------//

        CollectionReference inter = FirebaseUtils.getAllInterestsCollectionReference();
        inter.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Access the document ID and print it
                    String documentId = document.getId();
                    Log.d("interest", documentId);

                    // If you want to access the document data, you can use:
                    // Map<String, Object> data = document.getData();
                    // Now 'data' contains the fields of the document
                }
            } else {
                // Handle errors
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }
}