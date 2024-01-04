package com.example.poppop;

import static android.content.ContentValues.TAG;
import static com.example.poppop.GoogleTokenFetcher.fetchAccessToken;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
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

import java.io.FileInputStream;
import java.io.IOException;
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

    private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/cloud-platform");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getAccessToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        signInBtn = findViewById(R.id.button);
        signOutBtn = findViewById(R.id.button2);
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
//                    try {
//                        String accessTokenResponse = fetchAccessToken(serverAuthCode);
////                        FCMSender.sendPushToSingleInstance(accessTokenResponse,MainActivity.this,"dkfVuBqJQ2CjcPbUX8b1kO:APA91bE0O48rikT4UMZ-xUk_yxvePV39JNkM1PwKFHhOO-VJt-RvyeC5xvum4_iw6DHqNeyJwP00te5JfN8ug3h9K_f6LsWOg2jAJ02RXUuB3MTvzEkqcoFRDkeVRMvSHGvNe7zVRtwq",
////                                "Test", "hello");
//                        Log.e("accessTokenResponse",accessTokenResponse);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Log.e("qwerty", e.toString());
//                    } catch (GeneralSecurityException e) {
//                        Log.e("qwerty", e.toString());
//                        throw new RuntimeException(e);
//                    }

                }
                else{
                    Toast.makeText(MainActivity.this, "Fail to sign in", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new FileInputStream("java/com/example/poppop/assesets/service-account.json")).createScoped(SCOPES);
        googleCredentials.refresh();
        Log.d(TAG, "getAccessToken: " + googleCredentials.getAccessToken().getTokenValue());
        return googleCredentials.getAccessToken().getTokenValue();
    }

}