package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public class RootNode extends TreeItem<Node<?>> implements Node<String> {

    public RootNode() {
        super();
        setValue(this);
    }

    @Override
    public String getName() {
        return "Обзор";
    }

    @Override
    public ContextMenu getContextMenu() {
        return null;
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
    public String getWorkPanePath() {
        return null;
    }

    @Override
    public String getStyle() {
        return null;
    }


}
