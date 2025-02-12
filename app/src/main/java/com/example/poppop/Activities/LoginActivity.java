package com.example.poppop.Activities;

// LoginActivity.java

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.poppop.MainActivity;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.example.poppop.boardingpages.boardingName;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.stripe.android.PaymentConfiguration;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 40;

    TextView usernameRegisterText;
    TextView passwordRegisterText;
    Button loginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Updated layout resource
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51OXKSzEP1gGhSTU9IBjjvSKHnLbvHLfP7VtvYjE6MA1KEVaWU9jvbTgFCdoHe85D2ddpHGi63E7mcjtTuUuG3EN500TXV8w8PW"
        );
        mAuth = FirebaseAuth.getInstance();

        usernameRegisterText = findViewById(R.id.usernameRegisterText);
        passwordRegisterText = findViewById(R.id.passwordRegisterText);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
            String username = usernameRegisterText.getText().toString();
            String password = passwordRegisterText.getText().toString();
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password))
                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    FirestoreUserUtils.checkIfUserExistsThenAdd(user).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            //Toast.makeText(LoginActivity.this, "Info saved successfully", Toast.LENGTH_SHORT).show();
                                            UserModel userModel = task1.getResult();
                                            if (userModel != null && userModel.getAdmin() != null && userModel.getAdmin()) {
                                                // Handle admin login
                                                // ...
                                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                            } else if (userModel != null) {
                                                Log.d(TAG, userModel.getUserId());
                                                // Handle regular user login
                                                if (userModel.getAge() == null) {
                                                    startActivity(new Intent(LoginActivity.this, boardingName.class));
                                                } else {
                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                                FirestoreUserUtils.updateFCMTokenForUser(userModel)
                                                        .thenAccept(result -> {
                                                            Log.d(TAG, "FCM Token saved successfully");
                                                        })
                                                        .exceptionally(throwable -> {
                                                            Log.e("FCM token update", "Failed: " + throwable.getMessage());
                                                            //Toast.makeText(LoginActivity.this, "Fail to save FCM Token", Toast.LENGTH_SHORT).show();
                                                            return null;
                                                        });
                                            }

                                        } else {
                                            Exception exception = task1.getException();
                                            Log.e("exception", exception.toString());
                                            Toast.makeText(LoginActivity.this, "Fail to save", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Fail to sign in", Toast.LENGTH_SHORT).show();
                            }
                        });
        });
        // Updated variable name
        ImageButton loginBtn = findViewById(R.id.GoogleButton); // Updated button ID
        TextView registerLink = findViewById(R.id.RegisterTextView);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginBtn.setOnClickListener(v -> {
            Intent intent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
        });
        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED)
                return;
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    FirestoreUserUtils.checkIfUserExistsThenAdd(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Info saved successfully", Toast.LENGTH_SHORT).show();
                            UserModel userModel = task1.getResult();
                            if (userModel.getAge() == null) {
                                startActivity(new Intent(LoginActivity.this, boardingName.class));
                            } else {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                            FirestoreUserUtils.updateFCMTokenForUser(userModel)
                                    .thenAccept(result -> {
                                        Log.d("FCM token update", "success");
                                    })
                                    .exceptionally(throwable -> {
                                        Log.e("FCM token update", "Failed: " + throwable.getMessage());
                                        //Toast.makeText(LoginActivity.this, "Fail to save FCM Token", Toast.LENGTH_SHORT).show();
                                        return null;
                                    });
                        } else {
                            Exception exception = task1.getException();
                            Log.e("exception", exception.toString());
                            //Toast.makeText(LoginActivity.this, "Fail to save", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(LoginActivity.this, "Fail to sign in", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
