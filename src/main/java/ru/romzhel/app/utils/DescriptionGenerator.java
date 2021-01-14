package ru.romzhel.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.entities.StringGlossary;
import ru.romzhel.app.nodes.TemplateNode;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.GlossaryService;
import ru.romzhel.app.services.PropertyService;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DescriptionGenerator {
    public static final Logger logger = LogManager.getLogger(DescriptionGenerator.class);
    public static final String EMPTY = "#EMPTY#";

    public void generate(TemplateNode templateNode) throws Exception {
        ExcelInputFile excelInputFile = (ExcelInputFile) ExcelFileService.getInstance().getFileMap().get(templateNode.getData().getLinkedFileName());

        int articleColumnIndex = PropertyService.getInstance().getArticleColumnIndex(excelInputFile);
        if (articleColumnIndex < 0) {
            throw new RuntimeException("Не найден столбец-идентификатор с Артикулом");
        }

        List<String> parsedContent = new TemplateContentParser().parseContent(templateNode.getData().getContent());
        List<String> values = new ArrayList<>();

        excelInputFile.open();
        ExcelFile excelOutputFile = new ExcelFile();
        excelOutputFile.create();

        Row inputRow;
        Row outputRow;
        int outputRowIndex = 0;

        for (int i = 1; i < excelInputFile.sheet.getLastRowNum(); i++) {
            inputRow = excelInputFile.sheet.getRow(i);

            values.clear();
            for (int v = 1; v < parsedContent.size(); v++) {
                String treatedVariable = parsedContent.get(v);
                StringGlossary glossary = GlossaryService.getInstance().getGlossaryMap().get(treatedVariable);
                if (glossary != null) {
                    values.add(glossary.getNext());
                    continue;
                }

                Property property = excelInputFile.getPropertyMap().get(treatedVariable);
                if (property == null) {
                    logger.warn("Неизвестная переменная: '{}'", treatedVariable);
                    values.add("?" + treatedVariable + "?");
                    continue;
                }

                int colIndex = property.getColumnIndex();

                if (inputRow.getCell(colIndex) != null && !inputRow.getCell(colIndex).toString().isEmpty()) {
                    values.add(inputRow.getCell(colIndex).toString());
                } else {
                    values.add(EMPTY);
                }
            }
            String description = String.format(parsedContent.get(0), values.toArray());
            description = new TemplateContentCorrector().correct(description);
            outputRow = excelOutputFile.sheet.createRow(outputRowIndex++);
            Cell cell = outputRow.createCell(0, CellType.STRING);
            cell.setCellValue(inputRow.getCell(articleColumnIndex).toString());
            cell = outputRow.createCell(1, CellType.STRING);
            cell.setCellValue(description);
        }

        excelInputFile.close();
        try {
            excelOutputFile.save(new Dialogs().selectAnyFile(null, "Сохранение результата",
                    Dialogs.EXCEL_FILES, "descriptions.xlsx").get(0));
            Desktop.getDesktop().open(excelOutputFile.file);
        } catch (Exception e) {
            logger.warn("Ошибка сохранения: {}", e.getMessage());
        }
    }
}
