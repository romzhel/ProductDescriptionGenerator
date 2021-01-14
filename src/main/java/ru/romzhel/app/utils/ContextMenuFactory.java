package ru.romzhel.app.utils;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.nodes.FileNode;
import ru.romzhel.app.nodes.GlossaryNode;
import ru.romzhel.app.nodes.TemplateNode;
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
            treeItem.getParent().getChildren().remove(treeItem);
            if (treeItem instanceof TemplateNode) {
                TemplateService.getInstance().getTemplateMap().remove(((TemplateNode) treeItem).getName());
            } else if (treeItem instanceof GlossaryNode) {
                GlossaryService.getInstance().getGlossaryMap().remove(((GlossaryNode) treeItem).getName());
            } else if (treeItem instanceof FileNode) {
                ExcelFileService.getInstance().getFileMap().remove(((FileNode) treeItem).getName());
            }

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
