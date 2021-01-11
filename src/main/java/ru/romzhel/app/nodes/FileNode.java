package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import ru.romzhel.app.utils.ExcelInputFile;

import java.io.File;

public class FileNode extends TreeItem<Node<?>> implements Node<ExcelInputFile> {
    private ExcelInputFile data;

    public FileNode(File file) throws Exception {
        super();
        data = new ExcelInputFile();
        data.open(file);
        setValue(this);
    }

    @Override
    public String getName() {
        return "Файлы";
    }

    @Override
    public ContextMenu getContextMenu() {
        return null;
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
}
