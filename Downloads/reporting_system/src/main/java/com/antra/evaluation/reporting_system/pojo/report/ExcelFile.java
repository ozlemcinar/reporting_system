package com.antra.evaluation.reporting_system.pojo.report;

import org.apache.commons.lang3.RandomStringUtils;

import java.sql.Timestamp;
import java.util.Date;

public class ExcelFile {
    private String submitter;
    private String FileId;
    private ExcelData excelData;

    public ExcelFile() {
        String fileId = generateUniqueFileName();
        System.out.println(fileId);
        this.FileId=fileId;
    }
    String generateUniqueFileName() {
        String filename = "";
        long millis = System.currentTimeMillis();
        String datetime = new Date().toString();
        datetime = datetime.replace(" ", "");
        datetime = datetime.replace(":", "");
        String rndchars = RandomStringUtils.randomAlphanumeric(16);
        filename = rndchars + "" + datetime + "" + millis;
        return filename;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getFileId() {
        return FileId;
    }

    public void setFileId(String fileId) {
        FileId = fileId;
    }

    public ExcelData getExcelData() {
        return excelData;
    }
    public void setExcelData(ExcelData excelData) {
        this.excelData = excelData;
    }
}
