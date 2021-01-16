package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.ui_components.ContextMenuFactory;

import static ru.romzhel.app.enums.PropertyFilling.FULL;

public class PropertyNode extends AbstractNode<Property> {

    public PropertyNode(Property property) throws Exception {
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
        return new ContextMenu(ContextMenuFactory.getInstance().createDeleteMenuItem(this));
    }

    @Override
    public String getWorkPaneFxmlPath() {
        return null;
    }

    @Override
    public String getStyle() {
        return new StringBuilder()
                .append("-fx-text-fill: ")
                .append(data.getFilling() == FULL ? "saddleBrown;" : "salmon;")
                .append("-fx-font-style: italic;")
                .toString();
    }
}
