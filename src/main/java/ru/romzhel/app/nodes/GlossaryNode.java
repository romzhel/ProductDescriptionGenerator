package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import ru.romzhel.app.entities.StringGlossary;
import ru.romzhel.app.ui_components.MenuItemDelete;

public class GlossaryNode extends AbstractNode<StringGlossary> {

    public GlossaryNode(StringGlossary stringGlossary) {
        super();
        this.data = stringGlossary;
        setValue(getName());
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public ContextMenu getContextMenu() {
        return new ContextMenu(new MenuItemDelete(this));
    }

    @Override
    public String getWorkPaneFxmlPath() {
        return "/fxml/glossary_editor.fxml";
    }

    @Override
    public String getStyle() {
        return "-fx-text-fill: mediumBlue;";
    }
}
