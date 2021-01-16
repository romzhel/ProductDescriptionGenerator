package ru.romzhel.app.entities;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "template")
@XmlAccessorType(XmlAccessType.FIELD)
public class DescriptionTemplate {
    private String name = "";
    private ProductGroup linkedGroup;
    private String linkedFileName = "";
    private String content = "";
}
