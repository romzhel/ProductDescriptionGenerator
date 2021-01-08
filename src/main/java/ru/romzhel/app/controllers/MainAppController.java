package ru.romzhel.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import ru.romzhel.app.utils.Dialogs;
import ru.romzhel.app.utils.ExcelInputFile;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainAppController implements Initializable {
    private TreeItem<Object> rootTreeItem = new TreeItem<>("Обзор");
    private TreeItem<Object> templatesTreeItem = new TreeItem<>("Шаблоны");
    private TreeItem<Object> glossariesTreeItem = new TreeItem<>("Словари");
    private TreeItem<Object> filesTreeItem = new TreeItem<>("Файлы");


    @FXML
    TreeView<Object> tvNavi;
    @FXML
    TextField tf;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootTreeItem.getChildren().addAll(templatesTreeItem, glossariesTreeItem, filesTreeItem);
        tvNavi.setRoot(rootTreeItem);

        tvNavi.setOnMouseClicked(event -> {
            Object obj = tvNavi.getFocusModel().getFocusedItem();
            System.out.println(obj + ", " + ((TreeItem) obj).getValue().getClass().getName());
        });

        tvNavi.setOnMousePressed(e -> System.out.println("source: pressed"));
        tvNavi.setOnMouseDragged(e -> System.out.println("source: dragged"));
        tvNavi.setOnDragDetected(e -> System.out.println("source: drag detected"));
        tvNavi.setOnMouseReleased(e -> System.out.println("source: released"));

        tf.setOnMouseDragEntered(e -> System.out.println("target: drag entered"));
        tf.setOnMouseDragOver(e -> System.out.println("target: drag over"));
        tf.setOnMouseDragReleased(e -> System.out.println("target: drag released"));
        tf.setOnMouseDragExited(e -> System.out.println("target: drag exited"));


    }

    public void openFile() {
        try {
            Stage stage = (Stage) tvNavi.getScene().getWindow();
            List<File> files = new Dialogs().selectAnyFile(stage, "Выбор файла", Dialogs.EXCEL_FILES, null);
            ExcelInputFile excelInputFile = new ExcelInputFile();
            excelInputFile.open(files.get(0));

            TreeItem<Object> fileItem = new TreeItem<>(excelInputFile);
            for (int i = 0; i < excelInputFile.getTitlesIndexes().size(); i++) {
                fileItem.getChildren().add(new TreeItem<>(excelInputFile.getTitlesIndexes().get(i)));
            }

            filesTreeItem.getChildren().add(fileItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
