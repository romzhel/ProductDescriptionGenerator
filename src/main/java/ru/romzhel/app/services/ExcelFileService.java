package ru.romzhel.app.services;

import lombok.Data;
import ru.romzhel.app.utils.ExcelFile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

@Data
@XmlRootElement(name = "files")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExcelFileService {
    private static ExcelFileService instance;
    private Map<String, ExcelFile> fileMap = new HashMap<>();

    private ExcelFileService() {
    }

    public static ExcelFileService getInstance() {
        if (instance == null) {
            instance = new ExcelFileService();
        }
        return instance;
    }
}
