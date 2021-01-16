package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import ru.romzhel.app.ui_components.ContextMenuFactory;

public class FileRootNode extends AbstractNode<String> {

    public FileRootNode() {
        super();
        setValue(getName());
    }

    @Override
    public String getName() {
        return "Файлы";
    }

    @Override
    public ContextMenu getContextMenu() {
        return new ContextMenu(ContextMenuFactory.getInstance().createAddFilesMenuItem(this));
    }

    @Override
    public String getWorkPaneFxmlPath() {
        return null;
    }

    @Override
    public String getStyle() {
        return "-fx-text-fill: saddleBrown; -fx-font-weight: bold;";
    }
}
