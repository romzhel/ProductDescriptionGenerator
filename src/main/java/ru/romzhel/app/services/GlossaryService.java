package ru.romzhel.app.services;

import lombok.Data;
import ru.romzhel.app.entities.StringGlossary;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

@Data
@XmlRootElement(name = "glossaries")
@XmlAccessorType(XmlAccessType.FIELD)
public class GlossaryService {
    private Map<String, StringGlossary> glossaryMap = new HashMap<>();
}