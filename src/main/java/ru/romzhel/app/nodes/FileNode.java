package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import ru.romzhel.app.services.PropertyService;
import ru.romzhel.app.ui_components.ContextMenuFactory;
import ru.romzhel.app.utils.ExcelInputFile;

import java.io.File;

public class FileNode extends AbstractNode<ExcelInputFile> {

    public FileNode(File file) throws Exception {
        super();
        data = new ExcelInputFile();
        data.open(file);
        PropertyService.getInstance().parseTitles(data);
        PropertyService.getInstance().checkPropertiesFilling(data);
        data.close();
        setValue(getName());
    }

    public FileNode(ExcelInputFile excelInputFile) throws Exception {
        super();
        data = excelInputFile;
        data.open();
        PropertyService.getInstance().checkPropertiesFilling(data);
        data.close();
        setValue(getName());
    }

    @Override
    public String getName() {
        return data.getFileName();
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
        return "-fx-text-fill: saddleBrown;";
    }
}
