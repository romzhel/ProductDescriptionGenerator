package ru.romzhel.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import ru.romzhel.app.entities.StringGlossary;
import ru.romzhel.app.nodes.GlossaryNode;
import ru.romzhel.app.nodes.GlossaryRootNode;
import ru.romzhel.app.nodes.Node;
import ru.romzhel.app.services.GlossaryService;

import java.net.URL;
import java.util.ResourceBundle;

public class GlossaryEditorController implements Initializable, NodeController<MainAppController> {
    @FXML
    TextField tfName;
    @FXML
    ListView<String> lvItems;
    @FXML
    TextField tfNew;
    @FXML
    Button btnAdd;
    @FXML
    Button btnSave;
    private MainAppController mainAppController;
    private Node<?> instigatorNode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnAdd.setOnAction(e -> {
            if (tfNew.getText() != null && !tfNew.getText().isEmpty()) {
                lvItems.getItems().add(tfNew.getText());
                tfNew.setText("");
            }
        });

        btnSave.setOnAction(e -> {
            StringGlossary stringGlossary = new StringGlossary();
            stringGlossary.setName(tfName.getText());
            lvItems.getItems().forEach(stringGlossary::add);

            if (instigatorNode instanceof GlossaryRootNode) {
                mainAppController.glossaryRootNode.getChildren().add(new GlossaryNode(stringGlossary));
                GlossaryService.getInstance().getGlossaryMap().put(stringGlossary.getName(), stringGlossary);
            } else if (instigatorNode instanceof GlossaryNode) {
                ((GlossaryNode) instigatorNode).setData(stringGlossary);
                GlossaryService.getInstance().getGlossaryMap().put(stringGlossary.getName(), stringGlossary);
            }
        });

        lvItems.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                lvItems.getItems().remove(lvItems.getSelectionModel().getSelectedItem());
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
            lvItems.getItems().addAll(stringGlossary.getGlossaryItems());
        }
    }
}
