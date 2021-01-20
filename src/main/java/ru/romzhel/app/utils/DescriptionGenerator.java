package ru.romzhel.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import ru.romzhel.app.entities.ProductGroup;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.entities.StringGlossary;
import ru.romzhel.app.nodes.TemplateNode;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.GlossaryService;
import ru.romzhel.app.ui_components.Dialogs;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.STRING;

public class DescriptionGenerator {
    public static final Logger logger = LogManager.getLogger(DescriptionGenerator.class);
    public static final String EMPTY = "#EMPTY#";
    private TemplateContentCorrector templateContentCorrector;

    public DescriptionGenerator() {
        templateContentCorrector = new TemplateContentCorrector();
    }

    public void generate(TemplateNode templateNode) throws Exception {
        String[] linkParts = templateNode.getData().getLinkedNodeName().split(" > ");
        ExcelInputFile excelInputFile = ExcelFileService.getInstance().getFileMap().get(linkParts[0]);

        excelInputFile.open();
        int articleColumnIndex = ExcelFileService.getInstance().getArticleColumnIndex(excelInputFile);
        if (articleColumnIndex < 0) {
            throw new RuntimeException("Не найден столбец-идентификатор с Артикулом");
        }

        List<String> parsedContent = new TemplateContentParser().parseSubstitutions(templateNode.getData().getContent());
        List<String> values = new ArrayList<>();

        ExcelFile excelOutputFile = new ExcelFile();
        excelOutputFile.create();

        Row inputRow;
        Row outputRow;
        int outputRowIndex = 0;

        outputRow = excelOutputFile.sheet.createRow(outputRowIndex++);
        Cell cell = outputRow.createCell(0, STRING);
        cell.setCellValue("Артикул [ARTICLE]");
        cell = outputRow.createCell(1, STRING);
        cell.setCellValue("Детальное описание (html)");


        String groupPropertyName = linkParts[1].split("\\:\\s")[0];
        String groupPropertyValue = linkParts[1].split("\\:\\s")[1];
        List<String> titles = ExcelFileService.getInstance().parseTitles(excelInputFile.getSheet().getRow(0));
        int groupColIndex = titles.indexOf(groupPropertyName);

        for (int i = 1; i < excelInputFile.sheet.getLastRowNum(); i++) {
            inputRow = excelInputFile.sheet.getRow(i);

            //отвечает ли позиция
            if (groupColIndex >= 0 && !inputRow.getCell(groupColIndex).toString().equals(groupPropertyValue)) {
                continue;
            }

            ExcelUtils excelUtils = new  ExcelUtils();

            values.clear();
            for (int v = 1; v < parsedContent.size(); v++) {
                String treatedVariable = parsedContent.get(v);
                StringGlossary glossary = GlossaryService.getInstance().getGlossaryMap().get(treatedVariable);
                if (glossary != null) {
                    values.add(glossary.getNext());
                    continue;
                }

                ProductGroup productGroup = excelInputFile.getProductGroupMap().get(linkParts[1]);
                Property property = productGroup.getPropertyMap().get(treatedVariable);
                if (property == null) {
                    logger.warn("Неизвестная переменная: '{}'", treatedVariable);
                    values.add("?" + treatedVariable + "?");
                    continue;
                }

                int colIndex = property.getColumnIndex();

                if ((cell = inputRow.getCell(colIndex)) != null && !cell.toString().isEmpty()) {
                    values.add(templateContentCorrector.correctStringProperty(excelUtils.getStringValue(cell)));
                } else {
                    values.add(EMPTY);
                }
            }
            String description = String.format(parsedContent.get(0), values.toArray());
            description = templateContentCorrector.correctDescription(description);
            outputRow = excelOutputFile.sheet.createRow(outputRowIndex++);
            cell = outputRow.createCell(0, STRING);
            cell.setCellValue(inputRow.getCell(articleColumnIndex).toString());
            cell = outputRow.createCell(1, STRING);
            cell.setCellValue(description);
        }

        excelInputFile.close();
        try {
            excelOutputFile.save(new Dialogs().selectAnyFile(null, "Сохранение результата",
                    Dialogs.EXCEL_FILES, String.format("descriptions_%s.xlsx", templateNode.getData().getName())).get(0));
            Desktop.getDesktop().open(excelOutputFile.file);
        } catch (Exception e) {
            logger.warn("Ошибка сохранения: {}", e.getMessage());
        }
    }
}
