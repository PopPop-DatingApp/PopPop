package com.example.poppop.boardingpages;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.example.poppop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class hobbyBoarding extends AppCompatActivity {
    private TextView tempDataTextView, tempDataTextView2, TempDataTextView3, TempDataTextView4;
    private String userName, userGender, userHoro;
    private Integer userAge;

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

        //---------------------END OF SECTION---------------------//
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("interests")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}