package ru.romzhel.app.services;

import lombok.Data;
import ru.romzhel.app.entities.DescriptionTemplate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

@Data
@XmlRootElement(name = "templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class TemplateService {
    private static TemplateService instance;
    private Map<String, DescriptionTemplate> templateMap = new HashMap<>();

    private TemplateService() {
    }

    public static TemplateService getInstance() {
        if (instance == null) {
            instance = new TemplateService();
        }
        return instance;
    }
}
