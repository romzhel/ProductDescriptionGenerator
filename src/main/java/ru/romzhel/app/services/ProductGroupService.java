package ru.romzhel.app.services;

import org.apache.poi.ss.usermodel.Row;
import ru.romzhel.app.entities.ProductGroup;
import ru.romzhel.app.entities.Property;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ProductGroupService {
    private static ProductGroupService instance;


    private ProductGroupService() {
    }

    public static ProductGroupService getInstance() {
        if (instance == null) {
            instance = new ProductGroupService();
        }
        return instance;
    }

    public Map<String, ProductGroup> extractPropertyGroups(Map<String, Set<Row>> rowMap, int groupColumn) throws Throwable {
        Map<String, ProductGroup> productGroupMap = new HashMap<>();
        Row titleRow = rowMap.get(ExcelFileService.TITLE_ROW).stream().findFirst()
                .orElseThrow((Supplier<Throwable>) () -> new RuntimeException("Не найдена строка с заголовками"));
        List<String> titles = ExcelFileService.getInstance().parseTitles(titleRow);

        for (Map.Entry<String, Set<Row>> rowGroupEntry : rowMap.entrySet()) {
            if (rowGroupEntry.getKey().equals(ExcelFileService.TITLE_ROW)) {
                continue;
            }

            ProductGroup productGroup = parseProductGroup(groupColumn, titles, rowGroupEntry);
            productGroupMap.put(productGroup.getName(), productGroup);
        }
        return productGroupMap;
    }

    private ProductGroup parseProductGroup(int groupColumn, List<String> titles, Map.Entry<String, Set<Row>> rowGroupEntry) {
        String simpleGroupName = groupColumn < 0 ? ExcelFileService.DEFAULT_GROUP_NAME : titles.get(groupColumn);
        String productGroupName = simpleGroupName.concat(": ").concat(rowGroupEntry.getKey());
        ProductGroup productGroup = new ProductGroup(productGroupName);
        for (int colIndex = 0; colIndex < titles.size(); colIndex++) {
            if (colIndex == groupColumn) {
                continue;
            }

            Property property = new Property(titles.get(colIndex), colIndex);
            property.setOrder(colIndex);

            for (Row row : rowGroupEntry.getValue()) {
                if (row.getCell(colIndex) != null && !row.getCell(colIndex).toString().trim().isEmpty()) {
                    property.increaseOccurrencesCount(1);
                }
            }

            if (property.getOccurrencesCount() > 0) {
                property.setMaxOccurrencesCount(rowGroupEntry.getValue().size());
                productGroup.getPropertyMap().put(property.getName(), property);
            }
        }
        return productGroup;
    }
}
