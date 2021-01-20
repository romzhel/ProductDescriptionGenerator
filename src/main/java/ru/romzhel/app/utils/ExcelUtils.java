package ru.romzhel.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;

import static org.apache.poi.ss.usermodel.CellType.FORMULA;

public class ExcelUtils {
    private static final Logger logger = LogManager.getLogger(ExcelUtils.class);

    public String getStringValue(Cell cell) {
        try {
            if (cell.getCellTypeEnum() == FORMULA) {
                String formulaValue = cell.getStringCellValue();
                logger.debug("формула: {} - {}", cell.toString(), formulaValue);

                return formulaValue;
            } else {
                return cell.toString();
            }
        } catch (Exception e) {
            logger.debug("ошибка получения строчного значения ячейки: {} - {}", cell.getAddress(), cell.toString());
            return cell.toString();
        }
    }
}
