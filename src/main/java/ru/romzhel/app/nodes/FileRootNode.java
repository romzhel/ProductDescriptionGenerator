package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import ru.romzhel.app.ui_components.ContextMenuFactory;

public class FileRootNode extends TreeItem<Node<?>> implements Node<String> {

    public FileRootNode() {
        super();
        setValue(this);
    }

    @Override
    public String getName() {
        return "Файлы";
    }

    @Override
    public ContextMenu getContextMenu() {
        return new ContextMenu(ContextMenuFactory.getInstance().createAddFilesMenuItem(this));
    }

    @Override
    public String getData() {
        return "";
    }

    @Override
    public void setData(String data) {

    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getWorkPaneFxmlPath() {
        return null;
    }

    @Override
    public String getStyle() {
        return "-fx-text-fill: saddleBrown; -fx-font-weight: bold;";
    }


}
