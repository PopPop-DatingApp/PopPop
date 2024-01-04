package com.example.poppop;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    String token6 = "dkfVuBqJQ2CjcPbUX8b1kO:APA91bE0O48rikT4UMZ-xUk_yxvePV39JNkM1PwKFHhOO-VJt-RvyeC5xvum4_iw6DHqNeyJwP00te5JfN8ug3h9K_f6LsWOg2jAJ02RXUuB3MTvzEkqcoFRDkeVRMvSHGvNe7zVRtwq";
    Button btn6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Check for notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        btn6 = findViewById(R.id.button6);
        btn6.setOnClickListener(v -> {
//            FCMSender.sendNotification(token6, "Test", "Hello");
            FCMSender.sendPushToSingleInstance(this,token6,"Test", "Hello");
        });

        getFCMToken();
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("token", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get the token
                    String token = task.getResult();
                    Log.d("token", token);

                    // TODO: Send the token to your server or perform any other actions
                });
    }
}