package com.example.poppop.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.poppop.R;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import static android.content.ContentValues.TAG;
import org.json.JSONException;
import org.json.JSONObject;

public class AccessTokenUtil {
    private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/cloud-platform");

    public static String getAccessToken(Context context) throws IOException {
        Resources resources = context.getResources();

        String type = resources.getString(R.string.service_account_type);
        String projectId = resources.getString(R.string.service_account_project_id);
        String privateKeyId = resources.getString(R.string.service_account_private_key_id);
        String privateKey = resources.getString(R.string.service_account_private_key);
        String clientEmail = resources.getString(R.string.service_account_client_email);
        String clientId = resources.getString(R.string.service_account_client_id);
        String authUri = resources.getString(R.string.service_account_auth_uri);
        String tokenUri = resources.getString(R.string.service_account_token_uri);
        String authProviderX509CertUrl = resources.getString(R.string.service_account_auth_provider_x509_cert_url);
        String clientX509CertUrl = resources.getString(R.string.service_account_client_x509_cert_url);
        String universeDomain = resources.getString(R.string.service_account_universe_domain);

        Log.d(TAG, "Type: " + type);
        Log.d(TAG, "Project ID: " + projectId);
        Log.d(TAG, "Private Key ID: " + privateKeyId);
        Log.d(TAG, "Private Key: " + privateKey);
        Log.d(TAG, "Client Email: " + clientEmail);
        Log.d(TAG, "Client ID: " + clientId);
        Log.d(TAG, "Auth URI: " + authUri);
        Log.d(TAG, "Token URI: " + tokenUri);
        Log.d(TAG, "Auth Provider X509 Cert URL: " + authProviderX509CertUrl);
        Log.d(TAG, "Client X509 Cert URL: " + clientX509CertUrl);
        Log.d(TAG, "Universe Domain: " + universeDomain);

        // Construct JSON using JSONObject
        JSONObject serviceAccountJson = new JSONObject();
        try {
            serviceAccountJson.put("type", type);
            serviceAccountJson.put("project_id", projectId);
            serviceAccountJson.put("private_key_id", privateKeyId);
            serviceAccountJson.put("private_key", privateKey);
            serviceAccountJson.put("client_email", clientEmail);
            serviceAccountJson.put("client_id", clientId);
            serviceAccountJson.put("auth_uri", authUri);
            serviceAccountJson.put("token_uri", tokenUri);
            serviceAccountJson.put("auth_provider_x509_cert_url", authProviderX509CertUrl);
            serviceAccountJson.put("client_x509_cert_url", clientX509CertUrl);
            serviceAccountJson.put("universe_domain", universeDomain);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Service Account JSON: " + serviceAccountJson.toString());
        InputStream inputStream = new ByteArrayInputStream(serviceAccountJson.toString().getBytes());
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(inputStream).createScoped(SCOPES);
        googleCredentials.refresh();
        Log.d(TAG, "getAccessToken: " + googleCredentials.getAccessToken().getTokenValue());


        return googleCredentials.getAccessToken().getTokenValue();
    }
}

