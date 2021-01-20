package ru.romzhel.app.ui_components;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.entities.DescriptionTemplate;
import ru.romzhel.app.entities.ProductGroup;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.entities.StringGlossary;
import ru.romzhel.app.nodes.*;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.GlossaryService;
import ru.romzhel.app.services.NavigationTreeService;
import ru.romzhel.app.services.TemplateService;

public class MenuItemDelete extends MenuItem {
    private static final Logger logger = LogManager.getLogger(MenuItemDelete.class);

    public MenuItemDelete(TreeItem<?> treeItem) {
        super("Удалить     ");

        setOnAction(event -> {
            logger.trace("Выбрано контекстное меню 'Удалить' для узла {}", treeItem.getClass().getSimpleName());

            if (!Dialogs.confirm("Удаление элементов", "Действительно желаете удалить выбранные элементы?")) {
                return;
            }

            ChangeListener<TreeItem<String>> changeSelectionListener = (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    NavigationTreeService.getInstance().navigateTo((AbstractNode<?>) newValue);
                }
            };

            NavigationTree.getInstance().getSelectionModel().selectedItemProperty().addListener(changeSelectionListener);

            if (treeItem instanceof TemplateNode) {
                DescriptionTemplate deletedTemplate = TemplateService.getInstance().getTemplateMap().remove(((TemplateNode) treeItem).getName());
                logger.trace("Удален шаблон: {}", deletedTemplate);
            } else if (treeItem instanceof GlossaryNode) {
                StringGlossary deletedGlossary = GlossaryService.getInstance().getGlossaryMap().remove(((GlossaryNode) treeItem).getName());
                logger.trace("Удален словарь: {}", deletedGlossary);
            } else if (treeItem instanceof FileNode) {
                ExcelFileService.getInstance().getFileMap().remove(((FileNode) treeItem).getName());
                logger.trace("Удален файл: {}", ((FileNode) treeItem).getName());
            } else if (treeItem instanceof ProductGroupNode) {
                FileNode fileNode = ((FileNode) treeItem.getParent());
                ProductGroupNode nodeForDelete = (ProductGroupNode) treeItem;
                fileNode.getChildren().remove(nodeForDelete);
                ProductGroup deletedProductGroup = fileNode.getData().getProductGroupMap().remove(nodeForDelete.getName());
                logger.trace("Удалена группа продукции: {} - {}", fileNode.getName(), deletedProductGroup.getName());
            } else if (treeItem instanceof PropertyNode) {
                ProductGroupNode productGroupNode = ((ProductGroupNode) treeItem.getParent());
                PropertyNode nodeForDelete = (PropertyNode) treeItem;
                Property removedProperty = productGroupNode.getData().getPropertyMap().remove(nodeForDelete.getName());
                logger.trace("Удалено свойство: {} - {} - {}", productGroupNode.getParent(), productGroupNode, removedProperty);
            }

            treeItem.getParent().getChildren().remove(treeItem);
            NavigationTree.getInstance().getSelectionModel().selectedItemProperty().removeListener(changeSelectionListener);
        });
    }
}
