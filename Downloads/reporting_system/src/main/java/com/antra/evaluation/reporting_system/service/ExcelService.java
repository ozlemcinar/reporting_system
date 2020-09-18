package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public interface ExcelService {
    InputStream getExcelBodyById(String id);
    ExcelFile createHelper(ExcelRequest excelRequest) throws IOException;
    ArrayList<ExcelResponse> listHelper();
    ExcelFile deleteHelper(String id);
    ExcelFile createMultiSheetHelper(MultiSheetExcelRequest request) throws IOException;

}
