package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import ru.romzhel.app.entities.ProductGroup;
import ru.romzhel.app.ui_components.MenuItemDelete;

public class ProductGroupNode extends AbstractNode<ProductGroup> {

    public ProductGroupNode(ProductGroup productGroup) {
        this.data = productGroup;
        setValue(data.getName());
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
    public String getStyle() {
        return "-fx-text-fill: saddleBrown; -fx-font-style: italic; -fx-font-weight: bold;";
    }
}
