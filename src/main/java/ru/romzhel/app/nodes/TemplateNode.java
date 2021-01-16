package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import ru.romzhel.app.entities.DescriptionTemplate;
import ru.romzhel.app.ui_components.ContextMenuFactory;

public class TemplateNode extends AbstractNode<DescriptionTemplate> {

    public TemplateNode(DescriptionTemplate descriptionTemplate) {
        super();
        this.data = descriptionTemplate;
        setValue(getName());
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public ContextMenu getContextMenu() {
        return new ContextMenu(ContextMenuFactory.getInstance().createDeleteMenuItem(this),
                ContextMenuFactory.getInstance().createGenerateSingleTemplateResultMenuItem(this));
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
