package com.example.poppop.Utils;

import com.example.poppop.Model.ReportCaseModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firestore.v1.WriteResult;

public class ReportCaseUtils {
    private static final String COLLECTION_NAME = "reportCases";

    public static void addReportCase(ReportCaseModel reportCase, final OnCompleteListener<DocumentReference> onCompleteListener) {
        // Add a new document with a generated ID to the "reportCases" collection
        FirebaseUtils.getAllReportCasesCollectionReference()
                .add(reportCase)
                .addOnCompleteListener(onCompleteListener);
    }

    public static Task<ReportCaseModel> getReportCaseById(String reportCaseId) {
        // Retrieve a document from the "reportCases" collection based on reportCaseId
        DocumentReference docRef = FirebaseUtils.getAllReportCasesCollectionReference().document(reportCaseId);
        return docRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    return document.toObject(ReportCaseModel.class);
                }
            }
            return null;
        });
    }
}
