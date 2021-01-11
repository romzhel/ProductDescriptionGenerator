package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public class PropertyNode extends TreeItem<Node<?>> implements Node<String> {
    private String node;

    public PropertyNode(String propertyName) throws Exception {
        super();
        node = propertyName;
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
    public String getNode() {
        return node;
    }

    @Override
    public String toString() {
        return node;
    }

    @Override
    public String getWorkPanePath() {
        return null;
    }
}
