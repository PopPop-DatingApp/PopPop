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

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    //SERVER_KEY taken from firebase
    private static final String SERVER_KEY = "AAAArrybYaM:APA91bFDhtZWU9ROTW69oPdP9mJfsFzWqlgQtMi7G2BX7nHEXIdERq-ORB31KJarcuo0AsAt5l7mp3DDVddWHqIZM7Jmlmye6oX5hfFBC724ulvdsXJrH-W6AP279uHIS7qS8ulPuWai"; // Replace with your Server Key

//    public static void sendNotification(String deviceToken, String title, String message){
//        OkHttpClient client = new OkHttpClient();
//
//        JSONObject data = new JSONObject();
//        try {
//            data.put("title", title);
//            data.put("body", message);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return; // Return or handle the exception as per your requirement
//        }
//
//        JSONObject body = new JSONObject();
//        try {
//            body.put("to", deviceToken);
//            body.put("notification", data);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return; // Return or handle the exception as per your requirement
//        }
//
//        RequestBody requestBody = RequestBody.create(body.toString(), JSON );
//        Request request = new Request.Builder()
//                .url(FCM_API)
//                .header("Authorization", "Bearer " + SERVER_KEY)
//                .post(requestBody)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                // Handle failure, such as network issues or API call errors
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                // Handle response from FCM API
//                if (response.isSuccessful()) {
//                    // Request was successful (HTTP 200-299)
//                    String responseBody = response.body().string();
//                    System.out.println("FCM Response: " + responseBody);
//                    // Add your custom logic here to handle the response
//                } else {
//                    // Request failed
//                    System.err.println("FCM Request Failed: " + response.code() + " - " + response.message());
//                }
//            }
//        });
//    }

    private void sendNotificationToOwner(Context activity,String ownerToken, String newParticipantName, String locationName) {
        String title = "New Participant!!!";
        String body = newParticipantName + " has joined your location: " + locationName;
        sendPushToSingleInstance(activity, ownerToken, title, body);
    }

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
