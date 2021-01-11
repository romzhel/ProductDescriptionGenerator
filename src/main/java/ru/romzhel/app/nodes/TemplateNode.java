package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import ru.romzhel.app.entities.DescriptionTemplate;

public class TemplateNode extends TreeItem<Node<?>> implements Node<DescriptionTemplate> {
    private DescriptionTemplate descriptionTemplate;

    public TemplateNode(DescriptionTemplate descriptionTemplate) {
        super();
        this.descriptionTemplate = descriptionTemplate;
        setValue(this);
    }

    @Override
    public String getName() {
        return descriptionTemplate.getName();
    }

    @Override
    public ContextMenu getContextMenu() {
        return null;
    }

    @Override
    public DescriptionTemplate getData() {
        return descriptionTemplate;
    }

    @Override
    public void setData(DescriptionTemplate data) {
        this.descriptionTemplate = data;
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
