package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public class TemplateRootNode extends TreeItem<Node<?>> implements Node<String> {

    public TemplateRootNode() {
        super();
        setValue(this);
    }

    @Override
    public String getName() {
        return "Шаблоны";
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
        return "/fxml/template_editor.fxml";
    }
}
