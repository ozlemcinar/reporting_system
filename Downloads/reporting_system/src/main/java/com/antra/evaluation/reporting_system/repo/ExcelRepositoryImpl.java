package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ExcelRepositoryImpl implements ExcelRepository {

    Map<String, ExcelFile> excelData = new ConcurrentHashMap<>();

    @Override
    public Optional<ExcelFile> getFileById(String id) {
        return Optional.ofNullable(excelData.get(id));
    }

    @Override
    public ExcelFile saveFile(ExcelFile file) {
        excelData.put(file.getFileId(),file);
        return file;
    }

    @Override
    public ExcelFile deleteFile(String id) {
        ExcelFile excelFile=excelData.get(id);
        excelData.remove(id);
        return excelFile;
    }

    @Override
    public List<ExcelFile> getFiles() {
        List<ExcelFile> allFiles=new ArrayList<ExcelFile>();
        Iterator iterator = excelData.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator.next();
            allFiles.add( (ExcelFile) me2.getValue());
        }
        return allFiles;
    }
}

