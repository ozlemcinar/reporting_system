package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.repo.ExcelRepositoryImpl;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ExcelGenerationController {

    private static final Logger log = LoggerFactory.getLogger(ExcelGenerationController.class);
    ExcelService excelService;

    @Autowired
    public ExcelGenerationController(ExcelService excelService) {
        this.excelService = excelService;
    }

    /**
     *
     * @param request
     * @return ResponseEntity<ExcelResponse>
     * @throws IOException
     *
     * This function is used to Generate Excel which has a sheet.
     */

    @PostMapping("/excel")
    @ApiOperation("Generate Excel")
    public ResponseEntity<ExcelResponse> createExcel(@RequestBody @Validated ExcelRequest request) throws IOException {
        ExcelResponse response = new ExcelResponse();
        ExcelFile file= excelService.createHelper(request);
        try{
            response.setFileId(file.getFileId());
            response.setSubmitter(file.getSubmitter());
        }catch (Exception IOException){
            log.error("File couldn't be created!");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     *
     * @param request
     * @return ResponseEntity<ExcelResponse>
     * @throws IOException
     *
     * This function is used to Generate Excel which has multi sheets
     */

    @PostMapping("/excel/auto")
    @ApiOperation("Generate Multi-Sheet Excel Using Split field")
    public ResponseEntity<ExcelResponse> createMultiSheetExcel(@RequestBody @Validated MultiSheetExcelRequest request) throws IOException {
        ExcelResponse response = new ExcelResponse();
        ExcelFile excelFile=excelService.createMultiSheetHelper(request);
        try {
            response.setFileId(excelFile.getFileId());
            response.setSubmitter(excelFile.getSubmitter());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch(Exception IOExceotion){
            log.error("File couldn't be created!");
            return null;
        }
    }

    /**
     *
     * @return ResponseEntity<List<ExcelResponse>>
     *
     * This function is used to list saved Excel files.
     */

    @GetMapping("/excel")
    @ApiOperation("List all existing files")
    public ResponseEntity<List<ExcelResponse>> listExcels() {
        var response = new ArrayList<ExcelResponse>();
        response=  excelService.listHelper();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     *
     * @param id
     * @param response
     * @throws IOException
     *
     * This function is used to download Excel file.
     */
    @GetMapping("/excel/{id}/content")
    public void downloadExcel(@PathVariable String id, HttpServletResponse response) throws IOException {
        InputStream fis = excelService.getExcelBodyById(id);
        response.setHeader("Content-Type","application/vnd.ms-excel");
        response.setHeader("Content-Disposition","attachment; filename=\"name_of_excel_file.xls\""); // TODO: File name cannot be hardcoded here
        FileCopyUtils.copy(fis, response.getOutputStream());
    }

    /**
     *
     * @param id
     * @return ResponseEntity<ExcelResponse>
     *
     * This function is used to delete desired Excel File.
     */

    @DeleteMapping("/excel/{id}")
    public ResponseEntity<ExcelResponse> deleteExcel(@PathVariable String id) {
        var response = new ExcelResponse();
        try {
            ExcelFile excelFile = excelService.deleteHelper(id);
            response.setFileId(excelFile.getFileId());
            response.setSubmitter(excelFile.getSubmitter());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch(Exception IOException){
            return null;
        }
    }
}