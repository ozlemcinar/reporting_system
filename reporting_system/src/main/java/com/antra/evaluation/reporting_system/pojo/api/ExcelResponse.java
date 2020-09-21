package com.antra.evaluation.reporting_system.pojo.api;

public class ExcelResponse {
    private String fileId;
    private String submitter;

    public String getSubmitter() {
        return submitter;
    }
    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getFileId() {
        return fileId;
    }
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
