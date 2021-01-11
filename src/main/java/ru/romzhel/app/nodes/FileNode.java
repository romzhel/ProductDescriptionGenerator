package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import ru.romzhel.app.utils.ExcelInputFile;

import java.io.File;

public class FileNode extends TreeItem<Node<?>> implements Node<ExcelInputFile> {
    private ExcelInputFile node;

    public FileNode(File file) throws Exception {
        super();
        node = new ExcelInputFile();
        node.open(file);
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
    public ExcelInputFile getNode() {
        return node;
    }

    @Override
    public String toString() {
        return node.getFileName();
    }

    @Override
    public String getWorkPanePath() {
        return null;
    }
}
