package ru.romzhel.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;

@XmlRootElement
public class ExcelFile {
    public static final Logger logger = LogManager.getLogger(ExcelFile.class);
    protected Workbook workbook;
    protected Sheet sheet;
    @XmlElement
    protected File file;

    public void open(File file) throws Exception {
        logger.trace("книга '{}' открыта", file);
        workbook = WorkbookFactory.create(file);
        sheet = workbook.getSheetAt(0);
        this.file = file;
    }

    public void close() {
        try {
            workbook.close();
            logger.trace("книга '{}' закрыта", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return file.getName();
    }
}
