package ru.romzhel.app.ui_components;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.entities.DescriptionTemplate;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.entities.StringGlossary;
import ru.romzhel.app.nodes.*;
import ru.romzhel.app.services.*;
import ru.romzhel.app.utils.DescriptionGenerator;
import ru.romzhel.app.utils.ExcelInputFile;

import java.io.File;
import java.util.List;

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

    public MenuItem createDeleteMenuItem(TreeItem<?> treeItem) {//todo очистка/отображение новых данных в UI компонентах после удаления
        MenuItem menuItem = new MenuItem("Удалить    ");
        menuItem.setOnAction(event -> {
            if (!Dialogs.confirm("Удаление элементов", "Действительно желаете удалить выбранные элементы?")) {
                return;
            }

            ChangeListener<TreeItem<Node<?>>> changeSelectionListener = (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    NavigationTreeService.getInstance().navigateTo((Node<?>) newValue);
                }
            };

            NavigationTree navigationTree = NavigationTreeService.getInstance().getMainAppController().getNavigationTree();
            navigationTree.getSelectionModel().selectedItemProperty().addListener(changeSelectionListener);

            if (treeItem instanceof TemplateNode) {
                DescriptionTemplate deletedTemplate = TemplateService.getInstance().getTemplateMap().remove(((TemplateNode) treeItem).getName());
                logger.trace("Удален шаблон: {}", deletedTemplate);
            } else if (treeItem instanceof GlossaryNode) {
                StringGlossary deletedGlossary = GlossaryService.getInstance().getGlossaryMap().remove(((GlossaryNode) treeItem).getName());
                logger.trace("Удален словарь: {}", deletedGlossary);
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
            navigationTree.getSelectionModel().selectedItemProperty().removeListener(changeSelectionListener);
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

    public MenuItem createAddFilesMenuItem(TreeItem<?> treeItem) {
        if (!(treeItem instanceof FileRootNode)) {
            return null;
        }

        MenuItem menuItem = new MenuItem("Добавить файлы     ");
        menuItem.setOnAction(event -> {
            List<File> files = new Dialogs().selectAnyFile(null, "Выбор файла", Dialogs.EXCEL_FILES, null);

            for (int f = 0; f < files.size(); f++) {
                try {
                    FileNode fileNode = new FileNode(files.get(f));
                    for (Property property : PropertyService.getInstance().getPropertiesByOrder(fileNode.getData())) {
                        fileNode.getChildren().add(new PropertyNode(property));
                    }
                    ((FileRootNode) treeItem).getChildren().add(fileNode);
                    ExcelFileService.getInstance().getFileMap().put(fileNode.getData().getFileName(), fileNode.getData());
                } catch (Exception e) {
                    logger.error("Ошибка открытия файла: '{}");
                    Dialogs.showMessage("Ошибка открытия файла", e.getMessage());
                }
            }
        });
        return menuItem;
    }
}
