package ru.romzhel.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.xml.bind.annotation.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExcelFile {
    public static final Logger logger = LogManager.getLogger(ExcelFile.class);
    @XmlTransient
    protected Workbook workbook;
    @XmlTransient
    protected Sheet sheet;
    @XmlElement
    protected File file;

    public void open(File file) throws Exception {
        this.file = file;
        open();
    }

    public void open() throws Exception {
        logger.trace("книга '{}' открыта", file);
        workbook = WorkbookFactory.create(file);
        sheet = workbook.getSheetAt(0);
    }

    public void create() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Описания");
    }

    public void save(File file) {
        if (file == null) {
            throw new RuntimeException("При сохранении не выбрано имя файла");
        }

        this.file = file;

        try {
            FileOutputStream outFile = new FileOutputStream(file);
            workbook.write(outFile);

            workbook.close();
            outFile.close();
        } catch (Exception e) {
            Dialogs.showMessage("Ошибка создания файла", e.getMessage());
        }
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
