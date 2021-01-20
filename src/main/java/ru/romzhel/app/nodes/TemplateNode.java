package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import ru.romzhel.app.entities.DescriptionTemplate;
import ru.romzhel.app.ui_components.MenuItemDelete;
import ru.romzhel.app.ui_components.MenuItemTreatSingleTemplate;

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
        return new ContextMenu(new MenuItemTreatSingleTemplate(this), new SeparatorMenuItem(),
                new MenuItemDelete(this));
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
