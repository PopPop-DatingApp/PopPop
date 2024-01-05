package com.example.poppop;

import static com.example.poppop.Utils.FirebaseUtils.checkIfUserExistsThenAdd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.os.Build;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView textView;
    private Button signInBtn;
    private Button signOutBtn;
    private Button msgBtn;
    private static final int RC_SIGN_IN = 40;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        signInBtn = findViewById(R.id.button);
        signOutBtn = findViewById(R.id.button2);
        msgBtn = findViewById(R.id.msgBtn);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        msgBtn.setOnClickListener(v -> {
            FCMSender fcmSender = new FCMSender(this);
            fcmSender.sendPushToSingleInstance(MainActivity.this,
                    "d1hoNECBRjeYPWyzMRJO8H:APA91bFKLb1eZCrRM4BlC_OWsFG1921eziXVAUz47n9QelJsmYXXFaJkAOgIKYbM81Y5VvDQqATz3ZtA_WrZzv1KETYXi57r0-5Zm2a_n-4RnyP6CeQWIMZmrIRdkH9P6lHRjp8AauLy",
                    "Hello", "Test");
        });
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
//                .requestServerAuthCode(getString(R.string.default_web_client_id), true)
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
//                Log.e("qwerty", Objects.requireNonNull(task.getResult().getServerAuthCode()));
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void buttonGoogleSignin(View view) {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void firebaseAuth(String idToken){
        Log.d("tag", "ready to firebaseAuth");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        checkIfUserExistsThenAdd(user).addOnCompleteListener(new OnCompleteListener<UserModel>() {
                            @Override
                            public void onComplete(@NonNull Task<UserModel> task) {
                                if (task.isSuccessful()) {
                                    UserModel userModel = task.getResult();
                                    // Handle userModel
                                    textView.setText("Hello " + userModel.getName());
                                    Toast.makeText(MainActivity.this, "Save info successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Exception exception = task.getException();
                                    // Handle failure
                                    assert exception != null;
                                    Log.e("exception", exception.toString());
                                    Toast.makeText(MainActivity.this, "Fail to save", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Fail to sign in", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}