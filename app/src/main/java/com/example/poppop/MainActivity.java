package com.example.poppop;

import static android.content.ContentValues.TAG;
import static com.example.poppop.GoogleTokenFetcher.fetchAccessToken;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.poppop.Model.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView textView;
    private Button signInBtn;
    private Button signOutBtn;
    private static final int RC_SIGN_IN = 40;

    private String Oauth2Token;

    private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/cloud-platform");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        signInBtn = findViewById(R.id.button);
        signOutBtn = findViewById(R.id.button2);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Oauth2Token = getAccessToken(MainActivity.this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonGoogleSignin(v); // Call buttonGoogleSignin function when signIn button is clicked
            }
        });

        signOutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // Update UI after signing out
                    textView.setText("Sign in to continue");
                }
            });
        });

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestServerAuthCode(getString(R.string.default_web_client_id), true)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    textView.setText("Hello " + user.getDisplayName());
                } else {
                    // User is signed out
                    textView.setText("Sign in to continue");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.e("qwerty", Objects.requireNonNull(task.getResult().getServerAuthCode()));
                firebaseAuth(account.getIdToken(),task.getResult().getServerAuthCode());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void buttonGoogleSignin(View view) {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void firebaseAuth(String idToken, String serverAuthCode){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    UserModel userModel = new UserModel();
                    userModel.setUserId(user.getUid());
                    userModel.setName(user.getDisplayName());
                    userModel.setProfile(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);

                    textView.setText("Hello " + user.getDisplayName());
                    FCMSender.sendPushToSingleInstance(Oauth2Token,MainActivity.this,"dkfVuBqJQ2CjcPbUX8b1kO:APA91bE0O48rikT4UMZ-xUk_yxvePV39JNkM1PwKFHhOO-VJt-RvyeC5xvum4_iw6DHqNeyJwP00te5JfN8ug3h9K_f6LsWOg2jAJ02RXUuB3MTvzEkqcoFRDkeVRMvSHGvNe7zVRtwq",
                            "Test", "hello");
                }
                else{
                    Toast.makeText(MainActivity.this, "Fail to sign in", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static String getAccessToken(Context context) throws IOException {
        String type = context.getResources().getString(R.string.service_account_type);
        String projectId = context.getResources().getString(R.string.service_account_project_id);
        String privateKeyId = context.getResources().getString(R.string.service_account_private_key_id);
        String privateKey = context.getResources().getString(R.string.service_account_private_key);
        String clientEmail = context.getResources().getString(R.string.service_account_client_email);
        String clientId = context.getResources().getString(R.string.service_account_client_id);
        String authUri = context.getResources().getString(R.string.service_account_auth_uri);
        String tokenUri = context.getResources().getString(R.string.service_account_token_uri);
        String authProviderX509CertUrl = context.getResources().getString(R.string.service_account_auth_provider_x509_cert_url);
        String clientX509CertUrl = context.getResources().getString(R.string.service_account_client_x509_cert_url);
        String universeDomain = context.getResources().getString(R.string.service_account_universe_domain);

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