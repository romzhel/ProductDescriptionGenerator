package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;

public class RootNode extends AbstractNode<String> {

    public RootNode() {
        super();
        setValue(getName());
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
    public String getWorkPaneFxmlPath() {
        return null;
    }

    @Override
    public String getStyle() {
        return null;
    }


}
