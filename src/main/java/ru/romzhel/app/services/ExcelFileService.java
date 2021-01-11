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
    private Map<String, ExcelFile> fileMap = new HashMap<>();
}
