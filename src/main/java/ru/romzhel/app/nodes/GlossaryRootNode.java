package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public class GlossaryRootNode extends TreeItem<Node<?>> implements Node<String> {

    public GlossaryRootNode() {
        super();
        setValue(this);
    }

    @Override
    public String getName() {
        return "Словари";
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
        return "/fxml/glossary_editor.fxml";
    }

    @Override
    public String getStyle() {
        return "-fx-text-fill: black; -fx-font-weight: bold;";
    }
}
