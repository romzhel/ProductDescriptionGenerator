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
    public String getNode() {
        return "";
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getWorkPanePath() {
        return "/fxml/glossary_editor.fxml";
    }
}
