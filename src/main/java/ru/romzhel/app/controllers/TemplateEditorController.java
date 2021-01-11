package ru.romzhel.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.net.URL;
import java.util.ResourceBundle;

public class TemplateEditorController implements Initializable, NodeController<MainAppController> {
    @FXML
    TextArea taContent;
    @FXML
    Button btnSave;
    private MainAppController mainAppController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        taContent.setOnDragOver((DragEvent e) -> {
            if (e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.ANY);
            }

            e.consume();
        });


        taContent.setOnDragDropped((DragEvent e) -> {
            System.out.println("drag dropped event");
            Dragboard dragboard = e.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                taContent.appendText("{" + dragboard.getString() + "}");
                success = true;
            }

            e.setDropCompleted(success);

            e.consume();
        });
    }

    @Override
    public void injectMainController(MainAppController mainController) {
        this.mainAppController = mainController;
    }
}
