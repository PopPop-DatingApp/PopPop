package com.example.poppop;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.poppop.Model.UserModel;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
    private static final String BACKEND_URL = "https://poppoppayment-production.up.railway.app";

    String paymentIntentClientSecret;
    PaymentSheet paymentSheet;
    ImageView imagePayment;
    TextView premiumTitle;
    Button payButton;
    UserModel userModel;
    AppCompatButton backBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        imagePayment = findViewById(R.id.image_payment);
        premiumTitle = findViewById(R.id.premiumTitle);
        backBtn = findViewById(R.id.checkOut_BackBtn);
        payButton = findViewById(R.id.pay_button);

        backBtn.setOnClickListener(v -> {
            finish();
        });

        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51OXKSzEP1gGhSTU9IBjjvSKHnLbvHLfP7VtvYjE6MA1KEVaWU9jvbTgFCdoHe85D2ddpHGi63E7mcjtTuUuG3EN500TXV8w8PW"
        );
        Intent intent = getIntent();
        userModel = intent.getParcelableExtra("userModel");
        if(userModel == null){
            startActivity(new Intent(CheckoutActivity.this, MainActivity.class));
            finish();
        }else if(!userModel.getPremium()){
            payButton.setOnClickListener(this::onPayClicked);
            payButton.setEnabled(false);
            paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
            fetchPaymentIntent();
        }else{
            displayUI();
        }
    }

    private void showAlert(String title, @Nullable String message) {
        runOnUiThread(() -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Ok", null)
                    .create();
            dialog.show();
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    private void fetchPaymentIntent() {

        Request request = new Request.Builder()
                .url(BACKEND_URL + "/payment")
                .get()
                .build();

        new OkHttpClient()
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        showAlert("Failed to load data", "Error: " + e.toString());
                    }

                    @Override
                    public void onResponse(
                            @NonNull Call call,
                            @NonNull Response response
                    ) throws IOException {
                        if (!response.isSuccessful()) {
                            showAlert(
                                    "Failed to load page",
                                    "Error: " + response.toString()
                            );
                        } else {
                            final JSONObject responseJson = parseResponse(response.body());
                            paymentIntentClientSecret = responseJson.optString("clientSecret").replaceAll("\"", "");
                            Log.i(TAG, "Retrieved PaymentIntent: " + paymentIntentClientSecret);
                            runOnUiThread(() -> payButton.setEnabled(true));
                        }
                    }
                });
    }

    private JSONObject parseResponse(ResponseBody responseBody) {
        if (responseBody != null) {
            try {
                return new JSONObject(responseBody.string());
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error parsing response", e);
            }
        }

        return new JSONObject();
    }

    private void onPayClicked(View view) {
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("PopPop, Inc.");
        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
    }

    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            showToast("Payment complete!");
            FirestoreUserUtils.updatePremium(userModel.getUserId())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Firestore update was successful, proceed with UI display
                            displayUI();
                        } else {
                            // Handle the case where Firestore update fails
                            Log.e(TAG, "Error updating premium status in Firestore", task.getException());
                            Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.i(TAG, "Payment canceled!");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Throwable error = ((PaymentSheetResult.Failed) paymentSheetResult).getError();
            showAlert("Payment failed", error.getLocalizedMessage());
        }
    }

    private void displayUI(){
        imagePayment.setImageResource(R.drawable.checked);
        // Set the title to "Purchased Success"
        premiumTitle.setText("Purchased Success");
        premiumTitle.setTextColor(Color.parseColor("#008000"));
        payButton.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);
    }
}