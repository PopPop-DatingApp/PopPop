package com.example.poppop.Fragments_Admin;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.poppop.Utils.ReportCaseUtils;
import com.example.poppop.ViewModel.HistoryViewModel;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ReportCaseViewModelFactory implements ViewModelProvider.Factory {
    private final ReportCaseUtils reportCaseUtils;

    public ReportCaseViewModelFactory(ReportCaseUtils reportCaseUtils) {
        this.reportCaseUtils = reportCaseUtils;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReportCaseViewModel.class)) {
            // Create an instance of ReportCaseViewModel and inject dependencies
            return (T) new ReportCaseViewModel(reportCaseUtils);
        } else if (modelClass.isAssignableFrom(HistoryViewModel.class)) {
            // Create an instance of HistoryViewModel and inject dependencies
            return (T) new HistoryViewModel(reportCaseUtils);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
