package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.ui_components.ContextMenuFactory;

import static ru.romzhel.app.enums.PropertyFilling.FULL;

public class PropertyNode extends TreeItem<Node<?>> implements Node<Property> {
    private Property data;

    public PropertyNode(Property property) throws Exception {
        super();
        data = property;
        setValue(this);
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
    public Property getData() {
        return data;
    }

    @Override
    public void setData(Property data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data.getName();
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
