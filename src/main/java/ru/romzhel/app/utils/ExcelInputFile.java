package ru.romzhel.app.utils;

import lombok.Data;
import ru.romzhel.app.entities.Property;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;


@Data
@XmlRootElement(name = "file")
public class ExcelInputFile extends ExcelFile {
    private Map<String, Property> propertyMap = new HashMap<>();

    public ExcelInputFile() {
        super();
    }

    @Override
    public String toString() {
        return getFileName();
    }
}
