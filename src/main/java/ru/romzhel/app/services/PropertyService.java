package ru.romzhel.app.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.utils.ExcelInputFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PropertyService {
    public static final Logger logger = LogManager.getLogger(PropertyService.class);
    private static PropertyService instance;

    private PropertyService() {
    }

    public static PropertyService getInstance() {
        if (instance == null) {
            instance = new PropertyService();
        }
        return instance;
    }


    public List<Property> getPropertiesByOrder(Map<String, Property> propertyMap) {
        return propertyMap.values().stream()
                .sorted((o1, o2) -> o1.getOrder() - o2.getOrder())
                .collect(Collectors.toList());
    }

    public void increaseOccurrencesCount(Property property) {
        property.setOccurrencesCount(property.getOccurrencesCount() + 1);
    }

    public void increaseMaxOccurrencesCount(Property property) {
        property.setMaxOccurrencesCount(property.getMaxOccurrencesCount() + 1);
    }

    public Property getProperty(ExcelInputFile excelInputFile, String propertyName) {
        return excelInputFile.getProductGroupMap().values().stream()
                .map(productGroup -> productGroup.getPropertyMap().get(propertyName))
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }

}
