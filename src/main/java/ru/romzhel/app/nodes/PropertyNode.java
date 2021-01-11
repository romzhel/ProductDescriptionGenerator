package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public class PropertyNode extends TreeItem<Node<?>> implements Node<String> {
    private String data;

    public PropertyNode(String propertyName) throws Exception {
        super();
        data = propertyName;
        setValue(this);
    }

    @Override
    public String getName() {
        return "Файлы";
    }

    @Override
    public ContextMenu getContextMenu() {
        return null;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data;
    }

    @Override
    public String getWorkPanePath() {
        return null;
    }
}
