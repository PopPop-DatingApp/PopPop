package com.example.poppop.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.poppop.Model.ReportCaseModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.example.poppop.Utils.Utils;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class ReportCaseAdapter extends RecyclerView.Adapter<ReportCaseAdapter.ReportCaseViewHolder> {
    private final List<ReportCaseModel> reportCaseList;
    private final OnReportCaseClickListener onReportCaseClickListener;

    public ReportCaseAdapter(List<ReportCaseModel> reportCaseList, OnReportCaseClickListener onReportCaseClickListener) {
        this.reportCaseList = reportCaseList;
        this.onReportCaseClickListener = onReportCaseClickListener;
    }

    @NonNull
    @Override
    public ReportCaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_case, parent, false);
        return new ReportCaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportCaseViewHolder holder, int position) {
        ReportCaseModel reportCase = reportCaseList.get(position);

        holder.caseId.setText(reportCase.getReportCaseId());
        holder.caseTime.setText(Utils.timestampToString(reportCase.getReportTime()));

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            if (onReportCaseClickListener != null) {
                onReportCaseClickListener.onReportCaseClick(reportCase);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportCaseList.size();
    }

    static class ReportCaseViewHolder extends RecyclerView.ViewHolder {
        TextView caseId, caseTime, offender, reporter;

        public ReportCaseViewHolder(@NonNull View itemView) {
            super(itemView);
            caseId = itemView.findViewById(R.id.caseId);
            caseTime = itemView.findViewById(R.id.reportTime);
            offender = itemView.findViewById(R.id.offenderName);
            reporter = itemView.findViewById(R.id.reporterName);
        }
    }

    public interface OnReportCaseClickListener {
        void onReportCaseClick(ReportCaseModel reportCase);
    }
}

