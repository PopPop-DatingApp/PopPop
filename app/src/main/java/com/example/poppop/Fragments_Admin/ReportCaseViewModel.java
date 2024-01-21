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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReportCaseViewModel extends ViewModel {
    private final String TAG = "ReportCaseViewModel";
    private final ReportCaseUtils reportCaseUtils;

    private final MutableLiveData<List<ReportCaseModel>> reportCaseList = new MutableLiveData<>();
    private final MutableLiveData<List<ReportCaseModel>> completedReportCases = new MutableLiveData<>();
    private final MutableLiveData<List<ReportCaseModel>> pendingReportCases = new MutableLiveData<>();
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

            List<ReportCaseModel> allReportCases = new ArrayList<>();
            List<ReportCaseModel> completedCases = new ArrayList<>();
            List<ReportCaseModel> pendingCases = new ArrayList<>();

            if (queryDocumentSnapshots != null) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    ReportCaseModel reportCaseModel = documentSnapshot.toObject(ReportCaseModel.class);
                    allReportCases.add(reportCaseModel);

                    // Check if the report case is completed or pending
                    if (reportCaseModel.getDone() != null && reportCaseModel.getDone()) {
                        completedCases.add(reportCaseModel);
                    } else {
                        pendingCases.add(reportCaseModel);
                    }
                }

                // Update LiveData for overall report cases
                reportCaseList.setValue(allReportCases);

                // Sort and update LiveData for completed report cases (oldest first)
                completedCases.sort((c1, c2) -> c1.getReportTime().compareTo(c2.getReportTime()));
                completedReportCases.setValue(completedCases);

                // Sort and update LiveData for pending report cases (oldest first)
                pendingCases.sort((c1, c2) -> c1.getReportTime().compareTo(c2.getReportTime()));
                pendingReportCases.setValue(pendingCases);

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

    public MutableLiveData<List<ReportCaseModel>> getCompletedReportCases() {
        return completedReportCases;
    }

    public MutableLiveData<List<ReportCaseModel>> getPendingReportCases() {
        return pendingReportCases;
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
