package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.ui_components.MenuItemDelete;
import ru.romzhel.app.utils.ExcelInputFile;

import java.io.File;

public class FileNode extends AbstractNode<ExcelInputFile> {

    public FileNode(File file) throws Throwable {
        super();
        data = new ExcelInputFile();
        data.open(file);
        ExcelFileService.getInstance().parseSingleProductGroupMap(data);
        data.close();
        setValue(getName());
    }

    public FileNode(ExcelInputFile excelInputFile) throws Throwable {
        super();
        data = excelInputFile;
        setValue(getName());
    }

    @Override
    public String getName() {
        return data.getFileName();
    }

    @Override
    public ContextMenu getContextMenu() {
        return new ContextMenu(new MenuItemDelete(this));
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
