package ru.romzhel.app.services;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import ru.romzhel.app.entities.ProductGroup;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.utils.ExcelInputFile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@Data
@XmlRootElement(name = "files")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExcelFileService {
    public static final int SINGLE_GROUP = -1;
    public static final String DEFAULT_GROUP_NAME = "Группа по-умолчанию";
    public static final String TITLE_ROW = "Заголовки";
    private static final Logger logger = LogManager.getLogger(ExcelFileService.class);
    private static final String[] articleTitles = {"Артикул", "Артикул [ARTICLE]"};
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

    public int getArticleColumnIndex(ExcelInputFile excelInputFile) {
        return Arrays.stream(articleTitles)
                .map(s -> PropertyService.getInstance().getProperty(excelInputFile, s))
                .filter(property -> property != null && property.getColumnIndex() >= 0)
                .findFirst().orElse(new Property("", -1)).getColumnIndex();
    }

    public List<String> parseTitles(Row row) {
        List<String> titleList = new ArrayList<>();
        row.cellIterator().forEachRemaining(cell -> titleList.add(cell == null ? "" : cell.toString()));
        return titleList;
    }

    public Map<String, Set<Row>> groupRowByColumnValue(ExcelInputFile excelInputFile, int colIndex) {
        Map<String, Set<Row>> rowMap = new HashMap<>();
        Row row;
        Cell cell = null;

        rowMap.put(TITLE_ROW, Collections.singleton(excelInputFile.getSheet().getRow(0)));

        for (int rowIndex = 1; rowIndex < excelInputFile.getSheet().getLastRowNum(); rowIndex++) {
            if ((row = excelInputFile.getSheet().getRow(rowIndex)) == null ||
                    colIndex >= 0 && ((cell = row.getCell(colIndex)) == null || cell.toString().trim().isEmpty())) {
                continue;
            }

            String groupName = colIndex == SINGLE_GROUP ? DEFAULT_GROUP_NAME : cell.toString();
            Set<Row> rowSet = rowMap.getOrDefault(groupName, new HashSet<>());
            rowSet.add(row);
            rowMap.putIfAbsent(groupName, rowSet);
        }
        return rowMap;
    }

    public Map<String, ProductGroup> parseSingleProductGroupMap(ExcelInputFile excelInputFile) throws Throwable {
        Map<String, Set<Row>> rowMap = groupRowByColumnValue(excelInputFile, SINGLE_GROUP);
        excelInputFile.setProductGroupMap(ProductGroupService.getInstance().extractPropertyGroups(rowMap, SINGLE_GROUP));
        return excelInputFile.getProductGroupMap();
    }

    public Map<String, ProductGroup> parseProductGroupMap(ExcelInputFile excelInputFile, int colIndex) throws Throwable {
        Map<String, Set<Row>> rowMap = groupRowByColumnValue(excelInputFile, colIndex);
        excelInputFile.setProductGroupMap(ProductGroupService.getInstance().extractPropertyGroups(rowMap, colIndex));
        return excelInputFile.getProductGroupMap();
    }


}
