package ru.romzhel.app.utils;

import lombok.Data;
import org.apache.poi.ss.usermodel.Row;
import ru.romzhel.app.entities.ProductGroup;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


@Data
@XmlRootElement(name = "file")
public class ExcelInputFile extends ExcelFile {
    @XmlTransient
    private Map<Integer, String> titlesIndexes;
    @XmlTransient
    private Map<String, Integer> titles;
    @XmlTransient
    private Map<String, ProductGroup> productGroupsMap;

    public void analyze(File file) throws Exception {
        super.open(file);

        Row firstRow = sheet.getRow(0);
        titlesIndexes = new HashMap<>();
        titles = new HashMap<>();
        for (int colIndex = 0; colIndex < firstRow.getLastCellNum(); colIndex++) {
            titlesIndexes.put(colIndex, firstRow.getCell(colIndex).getStringCellValue());
            titles.put(titlesIndexes.get(colIndex), colIndex);
        }

        productGroupsMap = new HashMap<>();

        close();
    }

    private Map<String, ProductGroup> extractGroups(int groupColumn) {
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
    }

    @Override
    public String toString() {
        return getFileName();
    }
}
