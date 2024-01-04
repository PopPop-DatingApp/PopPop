package com.example.poppop;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
* This FCM uses Cloud Messaging API (Legacy)
* which is still available until 20/6/2024
*
* */
public class FCMSender {
    public static void sendPushToSingleInstance(String OauthToken,final Context activity, final String instanceIdToken, final String title, final String body) {
        final String url = "https://fcm.googleapis.com/v1/projects/poppop-datingapp/messages:send";
        // Replace YOUR_PROJECT_ID with your Firebase project ID
        StringRequest myReq = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(activity, "Sent notification to owner", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(activity, "Oops error sending notification!!!", Toast.LENGTH_SHORT).show()) {

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                Map<String, Object> rawParameters = new HashMap<>();
                Map<String, Object> message = new HashMap<>();
                Map<String, Object> notification = new HashMap<>();

                notification.put("body", body);
                notification.put("title", title);

                message.put("token", instanceIdToken);
                message.put("notification", notification);

                rawParameters.put("message", message);

                return new JSONObject(rawParameters).toString().getBytes();
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + OauthToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(activity).add(myReq);
    }
}
