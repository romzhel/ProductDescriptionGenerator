package ru.romzhel.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.entities.DescriptionTemplate;
import ru.romzhel.app.nodes.Node;
import ru.romzhel.app.nodes.TemplateNode;
import ru.romzhel.app.nodes.TemplateRootNode;
import ru.romzhel.app.services.TemplateService;
import ru.romzhel.app.utils.TemplateContentEditor;

import java.net.URL;
import java.util.ResourceBundle;

public class TemplateEditorController implements Initializable, NodeController<MainAppController> {
    public static final Logger logger = LogManager.getLogger(TemplateEditorController.class);
    @FXML
    TextField tfTemplateName;
    @FXML
    TextField tfTemplateLink;
    /*@FXML
    TextArea taContent;*/
    @FXML
    Button btnSave;
    private MainAppController mainAppController;
    private Node<?> instigatorNode;
    private TemplateContentEditor contentEditor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contentEditor = new TemplateContentEditor((AnchorPane) btnSave.getParent());

        tfTemplateLink.setOnDragOver((DragEvent e) -> {
            if (e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.ANY);
            }

            e.consume();
        });

        tfTemplateLink.setOnDragDropped((DragEvent e) -> {
            Dragboard dragboard = e.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                tfTemplateLink.setText(dragboard.getString());
                success = true;
            }

            e.setDropCompleted(success);
            e.consume();
        });

        btnSave.setOnAction(e -> {
            DescriptionTemplate descriptionTemplate = new DescriptionTemplate();
            descriptionTemplate.setName(tfTemplateName.getText());
            descriptionTemplate.setLinkedFileName(tfTemplateLink.getText());
            descriptionTemplate.setContent(contentEditor.getText());

            if (instigatorNode instanceof TemplateRootNode) {
                mainAppController.templateRootNode.getChildren().add(new TemplateNode(descriptionTemplate));
                TemplateService.getInstance().getTemplateMap().put(descriptionTemplate.getName(), descriptionTemplate);
            } else if (instigatorNode instanceof TemplateNode) {
                ((TemplateNode) instigatorNode).setData(descriptionTemplate);
                TemplateService.getInstance().getTemplateMap().put(descriptionTemplate.getName(), descriptionTemplate);
            }
        });
    }

    @Override
    public void injectMainController(MainAppController mainController, Node<?> instigatorNode) {
        this.mainAppController = mainController;
        this.instigatorNode = instigatorNode;

        if (instigatorNode instanceof TemplateNode) {
            DescriptionTemplate descriptionTemplate = ((TemplateNode) instigatorNode).getData();
            tfTemplateName.setText(descriptionTemplate.getName());
            tfTemplateLink.setText(descriptionTemplate.getLinkedFileName());
            contentEditor.linkTemplate(descriptionTemplate);
            logger.debug("link ContentEditor with: {}", descriptionTemplate.getName());
            contentEditor.replaceText(descriptionTemplate.getContent());
        }

        mainController.closeProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                contentEditor.stopHighlightingService();
            }
        });
    }
}
