package ru.romzhel.app.ui_components;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import ru.romzhel.app.nodes.FileNode;
import ru.romzhel.app.utils.FileUtils;

public class MenuItemOpenFile extends MenuItem {

    public MenuItemOpenFile(TreeItem<String> treeItem) {
        super("Открыть файл     ");

        if (!(treeItem instanceof FileNode)) {
            return;
        }

        setOnAction(event -> {
            FileUtils.openFile(((FileNode) treeItem).getData().getFile());
        });
    }
}
