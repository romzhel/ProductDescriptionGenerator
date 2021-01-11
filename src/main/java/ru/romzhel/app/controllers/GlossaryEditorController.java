package ru.romzhel.app.controllers;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class GlossaryEditorController implements Initializable, NodeController<MainAppController> {
    private MainAppController mainAppController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void injectMainController(MainAppController mainController) {
        this.mainAppController = mainController;
    }
}
