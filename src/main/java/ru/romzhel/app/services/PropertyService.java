package ru.romzhel.app.services;

import javafx.scene.control.TreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.nodes.FileNode;
import ru.romzhel.app.nodes.PropertyNode;
import ru.romzhel.app.utils.ExcelInputFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.romzhel.app.enums.PropertyFilling.FULL;
import static ru.romzhel.app.enums.PropertyFilling.PARTIAL;

public class PropertyService {
    public static final Logger logger = LogManager.getLogger(PropertyService.class);
    private static final String[] articleTitles = {"Артикул", "Артикул [ARTICLE]"};
    private static PropertyService instance;

    private PropertyService() {
    }

    public static PropertyService getInstance() {
        if (instance == null) {
            instance = new PropertyService();
        }
        return instance;
    }

    public void parseTitles(ExcelInputFile excelInputFile) throws Exception {
        Row firstRow = excelInputFile.getSheet().getRow(0);
        for (int colIndex = 0; colIndex < firstRow.getLastCellNum(); colIndex++) {
            String propertyName = firstRow.getCell(colIndex).toString();
            excelInputFile.getPropertyMap().put(propertyName, new Property(propertyName, colIndex));
        }
    }

    public void checkPropertiesFilling(ExcelInputFile excelInputFile) {//todo не проверяется наличие столбца с таким свойством
        for (String propertyName : excelInputFile.getPropertyMap().keySet()) {
            boolean partialFilling = false;
            boolean fullFilling = true;
            int colIndex = excelInputFile.getPropertyMap().get(propertyName).getColumnIndex();
            for (int rowIndex = 0; rowIndex < excelInputFile.getSheet().getLastRowNum(); rowIndex++) {
                Cell cell = excelInputFile.getSheet().getRow(rowIndex).getCell(colIndex);
                if (cell == null || cell.toString() == null || cell.toString().isEmpty()) {
                    fullFilling = false;
                    continue;
                }

                partialFilling = true;
            }

            if (!fullFilling && !partialFilling) {
                excelInputFile.getPropertyMap().remove(propertyName);
            }

            excelInputFile.getPropertyMap().get(propertyName).setFilling(fullFilling ? FULL : PARTIAL);
        }
    }

    public int getArticleColumnIndex(ExcelInputFile excelInputFile) {
        return Arrays.stream(articleTitles)
                .map(article -> getPropertyColumnIndex(excelInputFile, article))
                .filter(index -> index >= 0)
                .findFirst().orElse(-1);
    }

    public int getPropertyColumnIndex(ExcelInputFile excelInputFile, String propertyName) {
        return excelInputFile.getPropertyMap().getOrDefault(propertyName, new Property(null, -1)).getColumnIndex();
    }

    public List<Property> getPropertiesByOrder(ExcelInputFile excelInputFile) {
        return excelInputFile.getPropertyMap().values().stream()
                .sorted((o1, o2) -> o1.getOrder() - o2.getOrder())
                .collect(Collectors.toList());
    }

    public void reorderProperties(FileNode fileNode) {
        ExcelInputFile excelInputFile = fileNode.getData();
        int order = 0;
        for (TreeItem<String> childNode : fileNode.getChildren()) {
            excelInputFile.getPropertyMap().get(((PropertyNode) childNode).getName()).setOrder(order++);
        }
    }


}
