package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.ui_components.MenuItemDelete;
import ru.romzhel.app.ui_components.MenuItemGroupProperties;

public class PropertyNode extends AbstractNode<Property> {

    public PropertyNode(Property property) {
        super();
        this.data = property;
        setValue(getName());
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public ContextMenu getContextMenu() {
        return new ContextMenu(new MenuItemGroupProperties(this), new SeparatorMenuItem(),
                new MenuItemDelete(this));
    }

    @Override
    public String getWorkPaneFxmlPath() {
        return null;
    }

    @Override
    public String getStyle() {
        return new StringBuilder()
                .append("-fx-text-fill: ")
                .append(data.getOccurrencesCount() == data.getMaxOccurrencesCount() ? "saddleBrown;" : "salmon; ")
                .append("-fx-font-style: italic;")
                .toString();
    }

    @Override
    public String changeDisplayedText(String text) {
        return text.concat(String.format(" (%d из %d)", data.getOccurrencesCount(), data.getMaxOccurrencesCount()));
    }
}
