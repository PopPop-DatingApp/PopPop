package com.example.poppop.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poppop.Fragments_Admin.ReportCaseViewModel;
import com.example.poppop.Fragments_Admin.ReportCaseViewModelFactory;
import com.example.poppop.Model.ReportCaseModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.example.poppop.Utils.ReportCaseUtils;
import com.example.poppop.ViewModel.UserViewModel;
import com.example.poppop.ViewModel.UserViewModelFactory;

public class ReportCaseDetailActivity extends AppCompatActivity {
    private final String TAG = "ReportCaseDetailActivity";
    ImageButton backBtn;
    TextView reportedMsg, offender, reporter, contextDetail, resultText;

    Button chatReporterBtn, chatOffenderBtn, banOffenderBtn, solveCaseBtn;
    private ReportCaseViewModel reportCaseViewModel;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_case_detail);

        //Text View
        reportedMsg = findViewById(R.id.textReportedMsg);
        offender = findViewById(R.id.textOffender);
        reporter = findViewById(R.id.textReporter);
        contextDetail = findViewById(R.id.textContextDetail);
        resultText = findViewById(R.id.resultText);

        //Button
        chatReporterBtn = findViewById(R.id.btnChatReporter);
        chatOffenderBtn = findViewById(R.id.btnChatOffender);
        banOffenderBtn = findViewById(R.id.btnBanOffender);
        solveCaseBtn = findViewById(R.id.btnResolveCase);


        // Get the report case ID from the Intent
        String reportCaseId = getIntent().getStringExtra("reportCaseId");
        Log.d(TAG, reportCaseId);

        // Initialize ViewModel
        reportCaseViewModel = new ViewModelProvider(this, new ReportCaseViewModelFactory(new ReportCaseUtils())).get(ReportCaseViewModel.class);
        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(new FirestoreUserUtils())).get(UserViewModel.class);
        // Observe changes to the single report case
        reportCaseViewModel.listenToSingleReportCase(reportCaseId);
        reportCaseViewModel.getSingleReportCase().observe(this, this::updateUI);

        solveCaseBtn.setOnClickListener(v -> resolveCase(reportCaseViewModel.getSingleReportCase()));

        backBtn = findViewById(R.id.detailReport_backBtn);
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void resolveCase(LiveData<ReportCaseModel> reportCaseModel) {
        ReportCaseUtils.updateReportCaseDoneStatus(reportCaseModel.getValue().getReportCaseId(),true, task -> {
            if(task.isSuccessful()){
                Toast.makeText(this, "You have resolve this case", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Something wrong. Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(ReportCaseModel reportCaseModel) {
        if (reportCaseModel != null) {
            reportedMsg.setText(reportCaseModel.getReportedMsg());
            if(reportCaseModel.getDone()){
                solveCaseBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.btn_gray));
                solveCaseBtn.setText("Case Resolved");
                solveCaseBtn.setClickable(false);
                resultText.setVisibility(View.VISIBLE);
                banOffenderBtn.setClickable(false);
            }

            FirestoreUserUtils.getUserModelByUid(reportCaseModel.getOffenderId())
                    .addOnSuccessListener(userModel -> {
                        if (userModel != null) {
                            offender.setText("Offender: " + userModel.getName());
                            userViewModel.startListeningToUserData(userModel.getUserId());
                            userViewModel.getUserLiveData().observe(this, userModel1 -> {
                                // Update your UI or perform actions based on the changes in user data
                                if (userModel1 != null) {
                                    // Handle the user data
                                    if(userModel1.getBanned()){
                                        banOffenderBtn.setText("Unban offender");
                                        banOffenderBtn.setOnClickListener(v -> banUser(userModel.getUserId(), false));
                                    } else {
                                        banOffenderBtn.setText("Ban offender");
                                        banOffenderBtn.setOnClickListener(v -> banUser(userModel.getUserId(), true));
                                    }
                                } else {
                                    // Handle the case when user data is null
                                    finish();
                                }
                            });
                        }
                    });

            FirestoreUserUtils.getUserModelByUid(reportCaseModel.getReporterId())
                    .addOnSuccessListener(userModel -> {
                        if (userModel != null) {
                            reporter.setText("Reporter: " + userModel.getName());

                        }
                    });
            contextDetail.setText(reportCaseModel.getContextDetail());
        }
    }

    private void banUser(String userId, boolean b) {
        Log.d(TAG, "Ban " + b);
        FirestoreUserUtils.updateStatus(userId,b);
    }

}