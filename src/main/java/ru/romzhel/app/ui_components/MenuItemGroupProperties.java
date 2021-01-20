package ru.romzhel.app.ui_components;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.nodes.FileNode;
import ru.romzhel.app.nodes.Node;
import ru.romzhel.app.nodes.PropertyNode;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.NavigationTreeService;

public class MenuItemGroupProperties extends MenuItem {
    private static final Logger logger = LogManager.getLogger(MenuItemGroupProperties.class);

    public MenuItemGroupProperties(TreeItem<?> treeItem) {
        super("Группировать позиции     ");

        setOnAction(event -> {
            logger.trace("Выбрано контекстное меню 'Группировать' для узла {}", treeItem.getClass().getSimpleName());
            try {
                if (!(treeItem instanceof PropertyNode)) {
                    throw new RuntimeException("Неверный тип node для группирования: " + ((Node<?>) treeItem).getClass());
                }

                Property property = ((PropertyNode) treeItem).getData();
                FileNode fileNode = ((FileNode) treeItem.getParent().getParent());

                fileNode.getData().open();

                ExcelFileService.getInstance().parseProductGroupMap(fileNode.getData(), property.getColumnIndex());
                NavigationTreeService.getInstance().display(fileNode);

                fileNode.getData().close();
            } catch (Throwable throwable) {
                logger.error("Ошибка группирования: {}", throwable.getMessage(), throwable);
                Dialogs.showMessage("Ошибка группирования", throwable.getMessage());
            }
        });


    }
}
