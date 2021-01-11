package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import ru.romzhel.app.entities.StringGlossary;

public class GlossaryNode extends TreeItem<Node<?>> implements Node<StringGlossary> {
    private StringGlossary data;

    public GlossaryNode(StringGlossary stringGlossary) {
        super();
        this.data = stringGlossary;
        setValue(this);
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public ContextMenu getContextMenu() {
        return null;
    }

    @Override
    public StringGlossary getData() {
        return data;
    }

    @Override
    public void setData(StringGlossary data) {
        this.data = data;
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
