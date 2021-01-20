package ru.romzhel.app.ui_components;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.nodes.FileNode;
import ru.romzhel.app.nodes.FileRootNode;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.NavigationTreeService;

import java.io.File;
import java.util.List;

public class MenuItemAddFiles extends MenuItem {
    private static final Logger logger = LogManager.getLogger(MenuItemAddFiles.class);

    public MenuItemAddFiles(TreeItem<?> treeItem) {
        super("Добавить файлы     ");
        if (!(treeItem instanceof FileRootNode)) {
            return;
        }

        setOnAction(event -> {
            logger.trace("Выбрано контекстное меню 'Добавить файлы' для узла {}", treeItem.getClass().getSimpleName());
            List<File> files = new Dialogs().selectAnyFile(null, "Выбор файла", Dialogs.EXCEL_FILES, null);

            for (File file : files) {
                try {
                    FileNode fileNode = new FileNode(file);
                    ExcelFileService.getInstance().getFileMap().put(file.getName(), fileNode.getData());
//                    ExcelFileService.getInstance().parseSingleProductGroupMap(fileNode.getData());
                    NavigationTreeService.getInstance().addFileNode(fileNode);
                } catch (Throwable e) {
                    logger.error("Ошибка открытия файла: '{}'", e.getMessage(), e);
                    Dialogs.showMessage("Ошибка открытия файла", e.getMessage());
                }
            }
        });
    }
}
