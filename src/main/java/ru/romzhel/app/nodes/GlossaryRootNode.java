package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import ru.romzhel.app.entities.StringGlossary;

public class GlossaryRootNode extends GlossaryNode {

    public GlossaryRootNode() {
        super(new StringGlossary());
        setValue(getName());
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
    public String getWorkPaneFxmlPath() {
        return "/fxml/glossary_editor.fxml";
    }

    @Override
    public String getStyle() {
        return "-fx-text-fill: mediumBlue; -fx-font-weight: bold;";
    }
}
