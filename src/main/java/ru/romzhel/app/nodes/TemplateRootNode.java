package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import ru.romzhel.app.entities.DescriptionTemplate;

public class TemplateRootNode extends TemplateNode {

    public TemplateRootNode() {
        super(new DescriptionTemplate());
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
    public DescriptionTemplate getData() {
        return descriptionTemplate;
    }

    @Override
    public void setData(DescriptionTemplate descriptionTemplate) {
        this.descriptionTemplate = descriptionTemplate;
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
        return "-fx-text-fill: black; -fx-font-weight: bold;";
    }
}
