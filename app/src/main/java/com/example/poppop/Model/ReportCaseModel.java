package com.example.poppop.Model;


import com.google.firebase.Timestamp;

public class ReportCaseModel {
    private String reportCaseId;
    private String reporterId;
    private String offenderId;
    private String reportedMsg;
    private Timestamp reportTime;
    private Timestamp reportedMsgTime;
    private String contextDetail;
    private Boolean isDone = false;

    public ReportCaseModel() {
    }

    // Constructors
    public ReportCaseModel(String reportCaseId, String reporterId, String offenderId,
                           String reportedMsg, Timestamp reportTime, Timestamp reportedMsgTime, String contextDetail, Boolean isDone) {
        this.reportCaseId = reportCaseId;
        this.reporterId = reporterId;
        this.offenderId = offenderId;
        this.reportedMsg = reportedMsg;
        this.reportTime = reportTime;
        this.reportedMsgTime = reportedMsgTime;
        this.contextDetail = contextDetail;
        this.isDone = isDone;
    }

    public ReportCaseModel(String reporterId, String offenderId, String reportedMsg, Timestamp reportTime, Timestamp reportedMsgTime, String contextDetail) {
        this.reporterId = reporterId;
        this.offenderId = offenderId;
        this.reportedMsg = reportedMsg;
        this.reportTime = reportTime;
        this.reportedMsgTime = reportedMsgTime;
        this.contextDetail = contextDetail;
    }

    // Getters and Setters
    public String getReportCaseId() {
        return reportCaseId;
    }

    public void setReportCaseId(String reportCaseId) {
        this.reportCaseId = reportCaseId;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getOffenderId() {
        return offenderId;
    }

    public void setOffenderId(String offenderId) {
        this.offenderId = offenderId;
    }

    public String getReportedMsg() {
        return reportedMsg;
    }

    public void setReportedMsg(String reportedMsg) {
        this.reportedMsg = reportedMsg;
    }

    public Timestamp getReportTime() {
        return reportTime;
    }

    public void setReportTime(Timestamp reportTime) {
        this.reportTime = reportTime;
    }

    public Timestamp getReportedMsgTime() {
        return reportedMsgTime;
    }

    public void setReportedMsgTime(Timestamp reportedMsgTime) {
        this.reportedMsgTime = reportedMsgTime;
    }

    public String getContextDetail() {
        return contextDetail;
    }

    public void setContextDetail(String contextDetail) {
        this.contextDetail = contextDetail;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }
}
