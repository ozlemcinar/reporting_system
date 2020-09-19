package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.endpoint.ExcelGenerationController;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.restassured.http.ContentType;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.apache.tomcat.jni.File;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class APITest {
    @Mock
    ExcelService excelService;
    ExcelFile excelFile;
    ExcelGenerationController excelGenerationController;

    @BeforeEach
    public void configMock() {
        MockitoAnnotations.initMocks(this);
        RestAssuredMockMvc.standaloneSetup(new ExcelGenerationController(excelService));
    }

    @Test
    public void testFileDelete() throws  IOException{
        Mockito.when(excelService.deleteHelper("temp")).thenReturn(excelFile);
        given().delete("/excel/temp")
                .peek()
                .then().assertThat()
                .statusCode(200);

    }

    @Test
    public void testFileDownload() throws IOException {
        ExcelFile excelFile=new ExcelFile();
        InputStream fis = excelService.getExcelBodyById(excelFile.getFileId());
        Mockito.when(excelService.getExcelBodyById(excelFile.getFileId())).thenReturn(fis);
        /*given().accept("application/json").get("/excel/"+excelFile.getFileId()+"/content").peek().
                then().assertThat()
                .statusCode(500);*/
    }

    @Test
    public void testListFiles() throws FileNotFoundException {
        ArrayList <ExcelResponse> excelResponses=new ArrayList<ExcelResponse>();
        Mockito.when(excelService.listHelper()).thenReturn(excelResponses);
        given().accept("application/json").get("/excel").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testExcelGeneration() throws IOException {
        Mockito.when(excelService.createHelper(any())).thenReturn(new ExcelFile());
        given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]],\"submitter\":\"Dawei\", \"description\":\"Description\"  }").post("/excel").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testMultiExcelGeneration() throws IOException {
        Mockito.when(excelService.createMultiSheetHelper(any())).thenReturn(new ExcelFile());
        given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]], \"submitter\":\"Dawei\", \"description\":\"Description\" , \"splitBy\":\"class\"}").post("/excel").peek().
                then().assertThat()
                .statusCode(200);
    }

}
