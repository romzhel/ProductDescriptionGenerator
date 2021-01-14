package ru.romzhel.app.utils;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.nodes.*;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.GlossaryService;
import ru.romzhel.app.services.TemplateService;

public class ContextMenuFactory {
    public static final Logger logger = LogManager.getLogger(ContextMenuFactory.class);
    private static ContextMenuFactory instance;

    private ContextMenuFactory() {
    }

    public static ContextMenuFactory getInstance() {
        if (instance == null) {
            instance = new ContextMenuFactory();
        }
        return instance;
    }

    public MenuItem createDeleteMenuItem(TreeItem<?> treeItem) {
        MenuItem menuItem = new MenuItem("Удалить    ");
        menuItem.setOnAction(event -> {
            if (treeItem instanceof TemplateNode) {
                TemplateService.getInstance().getTemplateMap().remove(((TemplateNode) treeItem).getName());
                logger.trace("Удален шаблон: {}", ((TemplateNode) treeItem).getName());
            } else if (treeItem instanceof GlossaryNode) {
                GlossaryService.getInstance().getGlossaryMap().remove(((GlossaryNode) treeItem).getName());
                logger.trace("Удален словарь: {}", ((GlossaryNode) treeItem).getName());
            } else if (treeItem instanceof FileNode) {
                ExcelFileService.getInstance().getFileMap().remove(((FileNode) treeItem).getName());
                logger.trace("Удален файл: {}", ((FileNode) treeItem).getName());
            } else if (treeItem instanceof PropertyNode) {
                ExcelInputFile excelInputFile = ((FileNode) treeItem.getParent()).getData();
                Node<PropertyNode> nodeForDelete = (Node<PropertyNode>) treeItem;
                Property removedProperty = excelInputFile.getPropertyMap().remove(nodeForDelete.getName());
                logger.trace("Удалено свойство: {} - {}", excelInputFile.getFileName(), removedProperty);
            }
            treeItem.getParent().getChildren().remove(treeItem);
        });
        return menuItem;
    }

    public MenuItem createGenerateSingleTemplateResultMenuItem(TreeItem<?> treeItem) {
        if (!(treeItem instanceof TemplateNode)) {
            return null;
        }


        MenuItem menuItem = new MenuItem("Сгенерировать     ");
        menuItem.setOnAction(event -> {
            try {
                new DescriptionGenerator().generate(((TemplateNode) treeItem));
            } catch (Exception e) {
                logger.error("Ошибка генерации описаний: {}", e.getMessage(), e);
                Dialogs.showMessage("Ошибка генерации описаний", e.getMessage());
            }
        });
        return menuItem;
    }
}
