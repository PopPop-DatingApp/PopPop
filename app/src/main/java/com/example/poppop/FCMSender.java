package com.example.poppop;

import androidx.annotation.NonNull;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    public static void sendNotification(String deviceToken, String title, String message) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject data = new JSONObject();
        try {
            data.put("title", title);
            data.put("body", message);
        } catch (JSONException e) {
            e.printStackTrace();
            return; // Return or handle the exception as per your requirement
        }

        JSONObject body = new JSONObject();
        try {
            body.put("to", deviceToken);
            body.put("notification", data);
        } catch (JSONException e) {
            e.printStackTrace();
            return; // Return or handle the exception as per your requirement
        }

        RequestBody requestBody = RequestBody.create(body.toString(), JSON );
        Request request = new Request.Builder()
                .url(FCM_API)
                .header("Authorization", "Bearer " + SERVER_KEY)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle failure, such as network issues or API call errors
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Handle response from FCM API
                if (response.isSuccessful()) {
                    // Request was successful (HTTP 200-299)
                    String responseBody = response.body().string();
                    System.out.println("FCM Response: " + responseBody);
                    // Add your custom logic here to handle the response
                } else {
                    // Request failed
                    System.err.println("FCM Request Failed: " + response.code() + " - " + response.message());
                }
            }
        });
    }
}
