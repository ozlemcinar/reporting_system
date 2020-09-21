package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    ExcelRepository excelRepository;
    private static final Logger log = LoggerFactory.getLogger(ExcelServiceImpl.class);

    public ExcelRepository getExcelRepository() {
        return excelRepository;
    }

    public void setExcelRepository(ExcelRepository excelRepository) {
        this.excelRepository = excelRepository;
    }

    @Override

    public InputStream getExcelBodyById(String id) {

        Optional<ExcelFile> fileInfo = excelRepository.getFileById(id);
            File file = new File(id+".xlsx");
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        return null;
    }

    /**
     *
     * @param id
     * @return ExcelFile
     *
     * Helper function to delete Excel file
     */
    public ExcelFile deleteFile(String id){
        if(id==null){
            log.error("validation error: You need to enter valid file name");
        }
        ExcelFile excelFile=excelRepository.deleteFile(id);
        String projectFolderStr = System.getProperty("user.dir");
        try{
            String fileName=projectFolderStr+"/"+excelFile.getFileId()+".xlsx".toString();
            File file=new File(fileName);
            file.delete();
            log.info("file is deleted");
            return excelFile;
        }catch (Exception IOException){
            log.info("validation: enter valid file name");
        }
        return null;
    }

    /**
     *
     * @return ArrayList<ExcelResponse>
     *
     * Helper function to list Excel files.
     */
    public ArrayList<ExcelResponse>list (){
        ArrayList <ExcelResponse> excelResponses=new ArrayList<ExcelResponse>();
        List<ExcelFile> excelFiles=excelRepository.getFiles();
        if(excelFiles.isEmpty()){
            log.info("Any excel file is not created yet.");
            return excelResponses;
        }
        for(ExcelFile file:excelFiles){
            ExcelResponse excelResponse=new ExcelResponse();
            excelResponse.setFileId(file.getFileId());
            excelResponse.setSubmitter(file.getSubmitter());
            excelResponses.add(excelResponse);
        }
        log.info("list is returned");
        return excelResponses;
    }

    /**
     *
     * @param request
     * @return ExcelFile
     * @throws IOException
     *
     * Helper function to create an Excel file which has 1 sheet.
     */
    public ExcelFile create(ExcelRequest request) throws IOException {
        ExcelFile excelFile=new ExcelFile();
        ExcelData excelData = new ExcelData();
        ExcelDataSheet sheet = new ExcelDataSheet();
        List<ExcelDataSheet> sheets = new ArrayList<ExcelDataSheet>();
        List<ExcelDataHeader> headers = new ArrayList<ExcelDataHeader>();
        List<List<Object>> dataRows = request.getData();
        if(request.getHeaders()==null){
            log.error("validation error: headers can't be empty.");
            return excelFile;
        }
        for (int i = 0; i < request.getData().size(); i ++) {
            if(request.getData().get(i).size()!=request.getHeaders().size()) {
                log.error("validation error: header size and data size have to be the same.");
                return null;
            }
        }
        for (String h: request.getHeaders()) {
            ExcelDataHeader header = new ExcelDataHeader();
            header.setName(h);
            headers.add(header);
        }
        sheet.setTitle("Sheet1");
        sheet.setHeaders(headers);
        sheet.setDataRows(dataRows);
        sheets.add(sheet);
        excelData.setSheets(sheets);
        excelData.setTitle(request.getDescription());

        excelData.setFileId(excelFile.getFileId());
        excelFile.setSubmitter(request.getSubmitter());
        excelFile.setExcelData(excelData);
        try {
            ExcelGenerationServiceImpl excelGenerationServiceImpl = new ExcelGenerationServiceImpl();
            excelGenerationServiceImpl.generateExcelReport(excelData);
            excelRepository.saveFile(excelFile);
            log.info("file is created");
            return excelFile;
        }catch (IOException e){
            log.error("file couldn't be generated");
            return null;
        }
    }

    /**
     *
     * @param request
     * @return ExcelFile
     * @throws IOException
     *
     * Helper function to create an Excel file which multi-sheets
     */
    public ExcelFile createMultiSheet(MultiSheetExcelRequest request) throws IOException {
        ExcelFile excelFile =new ExcelFile();
        ExcelData excelData=new ExcelData();
        List<ExcelDataSheet> sheets = new ArrayList<ExcelDataSheet>();
        List<ExcelDataHeader> headers = new ArrayList<ExcelDataHeader>();
        if(request.getHeaders()==null){
            log.error("validation error: headers can't be empty.");
            return null;
        }
        for (int i = 0; i < request.getData().size(); i ++) {
            if(request.getData().get(i).size()!=request.getHeaders().size()) {
                log.error("validation error: header size and data size have to be the same.");
                return null;
            }
        }
        if(request.getSplitBy()==null || request.getSplitBy().isEmpty()){
            log.error("validation error: splitBy can be empty or need to be defined");
            return null;
        }
        for (String h: request.getHeaders()) {
            ExcelDataHeader header = new ExcelDataHeader();
            header.setName(h);
            headers.add(header);
        }
        
        String splitBy = request.getSplitBy();
        int splitIndex = request.getHeaders().indexOf(splitBy);
        HashMap<String, List<List<Object>>> hMap =new HashMap<>();
        for (int i = 0; i < request.getData().size(); i ++) {
            String name=request.getData().get(i).get(splitIndex).toString();
            if(hMap.isEmpty()){
                ExcelDataSheet sheet = new ExcelDataSheet();
                sheet.setTitle(name);
                sheet.setHeaders(headers);

                List<Object> rows=new ArrayList<>();
                rows=request.getData().get(i);
                List<List<Object> >allSheets=new ArrayList<>();
                allSheets.add(rows);
                hMap.put(name,allSheets);
            }
            else{
                if(hMap.containsKey(name)){
                    hMap.get(name).add(request.getData().get(i));
                }
                else{
                    ExcelDataSheet sheet = new ExcelDataSheet();
                    sheet.setTitle(name);
                    sheet.setHeaders(headers);
                    List<Object> rows=new ArrayList<>();
                    rows=request.getData().get(i);
                    List<List<Object> >allSheets=new ArrayList<>();
                    allSheets.add(rows);
                    hMap.put(name,allSheets);
                }
            }
        }
        for(String eds:hMap.keySet()){
            ExcelDataSheet excelDataSheet=new ExcelDataSheet();
            excelDataSheet.setTitle(eds);
            List<List<Object>> allSheets=new ArrayList<>();
            allSheets=hMap.get(eds);
            excelDataSheet.setDataRows(allSheets);
            excelDataSheet.setHeaders(headers);
            sheets.add(excelDataSheet);
        }
        excelData.setSheets(sheets);
        excelData.setFileId(excelFile.getFileId());
        excelFile.setSubmitter(request.getSubmitter());
        excelFile.setExcelData(excelData);
        try {
            ExcelGenerationServiceImpl excelGenerationServiceImpl = new ExcelGenerationServiceImpl();
            excelGenerationServiceImpl.generateExcelReport(excelData);
            excelRepository.saveFile(excelFile);
            log.info("file is created");
            return excelFile;
        }catch(IOException e){
            log.error("file couldn't be generated");
            return null;
        }

    }
}
