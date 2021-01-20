package ru.romzhel.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.romzhel.app.entities.StringGlossary;
import ru.romzhel.app.nodes.AbstractNode;
import ru.romzhel.app.nodes.GlossaryNode;
import ru.romzhel.app.nodes.GlossaryRootNode;
import ru.romzhel.app.services.GlossaryService;
import ru.romzhel.app.ui_components.Dialogs;

import java.net.URL;
import java.util.ResourceBundle;

public class GlossaryEditorController implements Initializable, NodeController<MainAppController> {
    @FXML
    TextField tfName;
    @FXML
    TextArea taItems;
    @FXML
    Button btnSave;
    private MainAppController mainAppController;
    private AbstractNode<?> instigatorNode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSave.setOnAction(e -> {
            if (tfName.getText().isEmpty() || GlossaryService.getInstance().getGlossaryMap().get(tfName.getText()) != null) {
                Dialogs.showMessage("Добавление словаря", "Словарю не задано имя или словарь с таким именем уже существует");
                return;
            }

            if (instigatorNode instanceof GlossaryRootNode) {
                GlossaryService.getInstance().addGlossaryFromRootData((GlossaryRootNode) instigatorNode);
            }
        });

        taItems.textProperty().addListener((observable, oldValue, newValue) -> {
            ((GlossaryNode) instigatorNode).getData().setGlossaryItems(GlossaryService.getInstance().convertToList(taItems.getText()));
        });

        tfName.textProperty().addListener((observable, oldValue, newValue) -> {
            ((GlossaryNode) instigatorNode).getData().setName(newValue);
            instigatorNode.setValue(newValue);
        });
    }

    @Override
    public void injectMainController(MainAppController mainController, AbstractNode<?> instigatorNode) {
        this.mainAppController = mainController;
        this.instigatorNode = instigatorNode;

        if (instigatorNode instanceof GlossaryNode) {
            StringGlossary stringGlossary = ((GlossaryNode) instigatorNode).getData();

            tfName.setText(stringGlossary.getName());
            taItems.setText(String.join("\n", stringGlossary.getGlossaryItems()));
        }

        tfName.setEditable(instigatorNode instanceof GlossaryRootNode);//todo реализовать изменение имени с защитой от дублирования
        btnSave.setDisable(!(instigatorNode instanceof GlossaryRootNode));
    }
}
