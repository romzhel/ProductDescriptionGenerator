package ru.romzhel.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.romzhel.app.entities.StringGlossary;
import ru.romzhel.app.nodes.GlossaryNode;
import ru.romzhel.app.nodes.GlossaryRootNode;
import ru.romzhel.app.nodes.Node;
import ru.romzhel.app.services.GlossaryService;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class GlossaryEditorController implements Initializable, NodeController<MainAppController> {
    @FXML
    TextField tfName;
    @FXML
    TextArea taItems;
    @FXML
    Button btnSave;
    private MainAppController mainAppController;
    private Node<?> instigatorNode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSave.setOnAction(e -> {
            StringGlossary stringGlossary = new StringGlossary();
            stringGlossary.setName(tfName.getText());
            Arrays.stream(taItems.getText().split("\n"))
                    .filter(s -> !String.valueOf(s).isEmpty())
                    .forEach(s -> stringGlossary.add(String.valueOf(s)));

            if (instigatorNode instanceof GlossaryRootNode) {
                mainAppController.getNavigationTree().getGlossaryRootNode().getChildren().add(new GlossaryNode(stringGlossary));
                GlossaryService.getInstance().getGlossaryMap().put(stringGlossary.getName(), stringGlossary);
            } else if (instigatorNode instanceof GlossaryNode) {
                ((GlossaryNode) instigatorNode).setData(stringGlossary);
                GlossaryService.getInstance().getGlossaryMap().put(stringGlossary.getName(), stringGlossary);
            }
        });
    }

    @Override
    public void injectMainController(MainAppController mainController, Node<?> instigatorNode) {
        this.mainAppController = mainController;
        this.instigatorNode = instigatorNode;

        if (instigatorNode instanceof GlossaryNode) {
            StringGlossary stringGlossary = ((GlossaryNode) instigatorNode).getData();

            tfName.setText(stringGlossary.getName());
            taItems.setText(String.join("\n", stringGlossary.getGlossaryItems()));
        }
    }
}
