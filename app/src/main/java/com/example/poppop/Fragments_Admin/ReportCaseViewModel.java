package com.example.poppop.Fragments_Admin;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.poppop.Model.ReportCaseModel;
import com.example.poppop.Model.UserModel;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.example.poppop.Utils.ReportCaseUtils;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReportCaseViewModel extends ViewModel {
    private final String TAG = "ReportCaseViewModel";
    private final ReportCaseUtils reportCaseUtils;

    private final MutableLiveData<List<ReportCaseModel>> reportCaseList = new MutableLiveData<>();
    private final MutableLiveData<ReportCaseModel> singleReportCase = new MutableLiveData<>();

    // ListenerRegistration for the snapshot listener
    private ListenerRegistration reportCaseListListenerRegistration;

    public ReportCaseViewModel(ReportCaseUtils reportCaseUtils) {
        this.reportCaseUtils = reportCaseUtils;
    }

    public void listenToReportCaseList() {
        reportCaseListListenerRegistration = FirebaseUtils.getAllReportCasesCollectionReference().addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Listen failed.", e);
                reportCaseList.setValue(null);
                return;
            }

            List<ReportCaseModel> reportCases = new ArrayList<>();

            if (queryDocumentSnapshots != null) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    ReportCaseModel reportCaseModel = documentSnapshot.toObject(ReportCaseModel.class);
                    reportCases.add(reportCaseModel);
                }

                reportCaseList.setValue(reportCases);
            } else {
                Log.d(TAG, "Current data: null");
                reportCaseList.setValue(null);
            }
        });
    }

    // Method to listen to a single report case by its ID
    public void listenToSingleReportCase(String reportCaseId) {
        FirebaseUtils.getReportCaseReference(reportCaseId).addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Listen failed.", e);
                singleReportCase.setValue(null);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                ReportCaseModel reportCaseModel = documentSnapshot.toObject(ReportCaseModel.class);
                singleReportCase.setValue(reportCaseModel);
            } else {
                Log.d(TAG, "Current data: null");
                singleReportCase.setValue(null);
            }
        });
    }

    public MutableLiveData<List<ReportCaseModel>> getReportCaseList() {
        return reportCaseList;
    }

    public LiveData<ReportCaseModel> getSingleReportCase() {
        return singleReportCase;
    }

    // Clean up the listener when the ViewModel is no longer used
    @Override
    protected void onCleared() {
        if (reportCaseListListenerRegistration != null) {
            reportCaseListListenerRegistration.remove();
        }
    }
}
