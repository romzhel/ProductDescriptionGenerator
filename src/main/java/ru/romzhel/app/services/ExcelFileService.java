package ru.romzhel.app.services;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.utils.ExcelInputFile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

@Data
@XmlRootElement(name = "files")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExcelFileService {
    public static final Logger logger = LogManager.getLogger(ExcelFileService.class);
    private static ExcelFileService instance;
    private Map<String, ExcelInputFile> fileMap = new HashMap<>();

    private ExcelFileService() {
    }

    public static ExcelFileService getInstance() {
        if (instance == null) {
            instance = new ExcelFileService();
        }
        return instance;
    }





    /*private Map<String, ProductGroup> extractGroups(int groupColumn) {
        Row row = null;
        for (int rowIndex = 1; rowIndex < sheet.getLastRowNum(); rowIndex++) {
            row = sheet.getRow(rowIndex);
            if (row == null || row.getCell(groupColumn) == null) {
                continue;
            }

            String groupName = row.getCell(groupColumn).getStringCellValue();
            if (groupName == null || groupName.isEmpty()) {
                continue;
            }

            if (productGroupsMap.containsKey(groupName)) {
                ProductGroup productGroup = productGroupsMap.get(groupName);
                for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                    if (colIndex != groupColumn) {
                        productGroup.getProperties().putIfAbsent(titlesIndexes.get(colIndex), colIndex);
                    }
                }
            } else {
                ProductGroup productGroup = new ProductGroup(groupName);
                for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                    if (colIndex != groupColumn) {
                        productGroup.getProperties().putIfAbsent(titlesIndexes.get(colIndex), colIndex);
                    }
                }
                productGroupsMap.put(groupName, productGroup);
            }
        }

        return productGroupsMap;
    }*/
}
