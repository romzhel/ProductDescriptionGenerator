package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import ru.romzhel.app.utils.ContextMenuFactory;
import ru.romzhel.app.utils.ExcelInputFile;

import java.io.File;

public class FileNode extends TreeItem<Node<?>> implements Node<ExcelInputFile> {
    private ExcelInputFile data;

    public FileNode(File file) throws Exception {
        super();
        data = new ExcelInputFile();
        data.analyze(file);
        setValue(this);
    }

    @Override
    public String getName() {
        return "Файлы";
    }

    @Override
    public ContextMenu getContextMenu() {
        return new ContextMenu(ContextMenuFactory.getInstance().createDeleteMenuItem(this));
    }

    @Override
    public ExcelInputFile getData() {
        return data;
    }

    @Override
    public void setData(ExcelInputFile data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data.getFileName();
    }

    @Override
    public String getWorkPanePath() {
        return null;
    }

    @Override
    public String getStyle() {
        return "-fx-text-fill: black;";
    }
}
