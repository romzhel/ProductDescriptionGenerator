package ru.romzhel.app.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductGroup {
    private String name;
    private Map<String, Property> propertyMap;

    public ProductGroup(String name) {
        this.name = name;
        propertyMap = new HashMap<>();
    }
}
