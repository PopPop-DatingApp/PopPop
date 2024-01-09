package com.example.poppop.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.example.poppop.Utils.AccessTokenUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FCMSender {
    private final String Oauth2Token;

    public String getOauth2Token() {
        return Oauth2Token;
    }

    public FCMSender(Context context) {
        try {
            Oauth2Token = AccessTokenUtil.getAccessToken(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPushToSingleInstance(final Context activity, final String instanceIdToken, final String title, final String body) {
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
                headers.put("Authorization", "Bearer " + Oauth2Token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(activity).add(myReq);
    }

    public static CompletableFuture<String> getFCMToken() {
        CompletableFuture<String> future = new CompletableFuture<>();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        Log.d("token", token);
                        future.complete(token);
                    } else {
                        Log.e("FCM Token", "Failed to get token");
                        future.completeExceptionally(new RuntimeException("Failed to get token"));
                    }
                });
        return future;
    }
}
