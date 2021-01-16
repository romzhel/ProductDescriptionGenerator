package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import ru.romzhel.app.entities.DescriptionTemplate;
import ru.romzhel.app.ui_components.ContextMenuFactory;

public class TemplateNode extends TreeItem<Node<?>> implements Node<DescriptionTemplate> {
    protected DescriptionTemplate descriptionTemplate;

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
        return new ContextMenu(ContextMenuFactory.getInstance().createDeleteMenuItem(this),
                ContextMenuFactory.getInstance().createGenerateSingleTemplateResultMenuItem(this));
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
    public String getWorkPaneFxmlPath() {
        return "/fxml/template_editor.fxml";
    }

    @Override
    public String getStyle() {
        return "";
    }
}
