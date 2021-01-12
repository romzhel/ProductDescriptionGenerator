package ru.romzhel.app.utils;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import ru.romzhel.app.nodes.TemplateNode;

public class ContextMenuFactory {
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
                e.printStackTrace();
            }
        });
        return menuItem;
    }
}
