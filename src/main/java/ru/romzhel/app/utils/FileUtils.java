package ru.romzhel.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.ui_components.Dialogs;

import java.awt.*;
import java.io.File;

public class FileUtils {
    private static final Logger logger = LogManager.getLogger(FileUtils.class);

    public static void openFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            logger.error("Не удалось открыть файл: {}", file);
            Dialogs.showMessage("Открытие файла", "При открытии файла произошла ошибка:\n\n" + e.getMessage());
        }
    }
}
