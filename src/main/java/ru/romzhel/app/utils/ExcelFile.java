package ru.romzhel.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;

public class ExcelFile {
    public static final Logger logger = LogManager.getLogger(ExcelFile.class);
    protected Workbook workbook;
    protected Sheet sheet;
    protected String fileName;

    public void open(File file) throws Exception {
        workbook = WorkbookFactory.create(file);
        sheet = workbook.getSheetAt(0);
        fileName = file.getName();
    }

    public void close() {
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return fileName;
    }
}
