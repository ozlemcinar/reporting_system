package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.*;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.repo.ExcelRepositoryImpl;
import com.antra.evaluation.reporting_system.service.ExcelGenerationService;
import com.antra.evaluation.reporting_system.service.ExcelService;
import com.antra.evaluation.reporting_system.service.ExcelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportingSystemApplicationTests {

    @Autowired
    ExcelGenerationService reportService;
    ExcelRequest excelRequest = new ExcelRequest();
    ExcelServiceImpl excelService =new ExcelServiceImpl();
    ExcelRepository excelRepository = new ExcelRepositoryImpl();
    ExcelData data = new ExcelData();
    MultiSheetExcelRequest multiSheetExcelRequest = new MultiSheetExcelRequest();

    @BeforeEach // We are using JUnit 5, @Before is replaced by @BeforeEach
    public void setUpData() {
        excelService.setExcelRepository(excelRepository);
        data.setTitle("Test book");
        data.setGeneratedTime(LocalDateTime.now());

        var sheets = new ArrayList<ExcelDataSheet>();
        var sheet1 = new ExcelDataSheet();
        sheet1.setTitle("First Sheet");

        var headersS1 = new ArrayList<ExcelDataHeader>();
        ExcelDataHeader header1 = new ExcelDataHeader();
        header1.setName("NameTest");
        header1.setWidth(10000);
        header1.setType(ExcelDataType.STRING);
        headersS1.add(header1);

        ExcelDataHeader header2 = new ExcelDataHeader();
        header2.setName("Age");
        header2.setWidth(10000);
        header2.setType(ExcelDataType.NUMBER);
        headersS1.add(header2);

        ArrayList<String> headerList = new ArrayList<>();
        String f_header = "Name";
        String s_header = "Age";
        headerList.add(f_header);
        headerList.add(s_header);
        List<List<Object>> dataRows = new ArrayList<>();
        List<Object> row1 = new ArrayList<>();
        row1.add("Ozlem");
        row1.add(12);
        List<Object> row2 = new ArrayList<>();
        row2.add("Cinar");
        row2.add(23);
        dataRows.add(row1);
        dataRows.add(row2);

        sheet1.setDataRows(dataRows);
        sheet1.setHeaders(headersS1);
        sheets.add(sheet1);
        data.setSheets(sheets);

        var sheet2 = new ExcelDataSheet();
        sheet2.setTitle("second Sheet");
        sheet2.setDataRows(dataRows);
        sheet2.setHeaders(headersS1);
        sheets.add(sheet2);

        excelRequest.setData(dataRows);
        excelRequest.setDescription("ozlem sheet");
        excelRequest.setHeaders(headerList);
        excelRequest.setSubmitter("Ozlem Cinar");

        multiSheetExcelRequest.setData(dataRows);
        multiSheetExcelRequest.setDescription("ozlem sheet");
        multiSheetExcelRequest.setHeaders(headerList);
        multiSheetExcelRequest.setSubmitter("Ozlem Cinar");
        multiSheetExcelRequest.setSplitBy(f_header);

    }

    @Test
    public void testExcelGeneration() {
        File file = null;
        try {
            file = reportService.generateExcelReport(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(file != null);
    }

    @Test
    public void testCreateExcel(){
        ExcelFile excelFile = null;
        try{
            excelFile=excelService.create(excelRequest);

        }catch (Exception e){
            e.printStackTrace();
        }
        assertTrue(excelFile.getFileId() != null);
    }

    @Test
    public void testCreateMultiExcel(){
        ExcelFile excelFile = null;
        try{
            excelFile = excelService.createMultiSheet(multiSheetExcelRequest);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        assertTrue(excelFile.getFileId()!=null);

    }
    @Test
    public void testListExcel(){
        ArrayList<ExcelResponse> listOfList = new ArrayList<ExcelResponse>();
        ExcelFile excelFile = new ExcelFile();
        try{
            excelFile=excelService.create(excelRequest);
            listOfList = excelService.list();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        assertTrue(listOfList!=null);
    }


    @Test
    public void testDeleteExcel() throws IOException {
        ExcelFile excelFile = new ExcelFile();
        try{
            excelFile = excelService.create(excelRequest);
            String id = excelFile.getFileId();
            excelService.deleteFile(id);
        }catch (Exception e){
            e.printStackTrace();
        }
        assertTrue(excelFile.getFileId()!=null);
    }
}
