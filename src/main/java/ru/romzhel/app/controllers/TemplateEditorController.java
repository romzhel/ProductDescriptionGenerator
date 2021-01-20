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
import ru.romzhel.app.nodes.AbstractNode;
import ru.romzhel.app.nodes.TemplateNode;
import ru.romzhel.app.nodes.TemplateRootNode;
import ru.romzhel.app.services.NavigationTreeService;
import ru.romzhel.app.services.TemplateService;
import ru.romzhel.app.ui_components.Dialogs;
import ru.romzhel.app.ui_components.TemplateContentEditor;

import java.net.URL;
import java.util.ResourceBundle;

public class TemplateEditorController implements Initializable, NodeController<MainAppController> {
    public static final Logger logger = LogManager.getLogger(TemplateEditorController.class);
    @FXML
    TextField tfTemplateName;
    @FXML
    TextField tfTemplateLink;
    @FXML
    Button btnSave;
    private MainAppController mainAppController;
    private AbstractNode<?> instigatorNode;
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

        tfTemplateName.textProperty().addListener((observable, oldValue, newValue) -> {
            ((TemplateNode) instigatorNode).getData().setName(newValue);
        });

        tfTemplateLink.textProperty().addListener((observable, oldValue, newValue) -> {
            ((TemplateNode) instigatorNode).getData().setLinkedNodeName(newValue);
            String content = contentEditor.getText();
            contentEditor.clear();
            contentEditor.appendText(content);
        });

        contentEditor.textProperty().addListener((observable, oldValue, newValue) ->
                ((TemplateNode) instigatorNode).getData().setContent(newValue));

        btnSave.setOnAction(e -> {
            DescriptionTemplate descriptionTemplate = ((TemplateNode) instigatorNode).getData();

            if (descriptionTemplate.getName().isEmpty() || TemplateService.getInstance().getTemplateMap().get(descriptionTemplate.getName()) != null) {
                Dialogs.showMessage("Добавление шаблона", "Шаблону не задано имя или шаблон с таким именем уже существует");
                return;
            }

            if (instigatorNode instanceof TemplateRootNode) {//todo переделать на TemplateService как сделано в GlossaryService
                TemplateNode newNode = new TemplateNode(descriptionTemplate);
                NavigationTreeService.getInstance().addTemplateNode(newNode);
                TemplateService.getInstance().getTemplateMap().put(descriptionTemplate.getName(), descriptionTemplate);

                ((TemplateRootNode) instigatorNode).setData(new DescriptionTemplate());
                NavigationTreeService.getInstance().navigateTo(instigatorNode);
            }
        });
    }

    @Override
    public void injectMainController(MainAppController mainController, AbstractNode<?> instigatorNode) {
        this.mainAppController = mainController;
        this.instigatorNode = instigatorNode;

        if (instigatorNode instanceof TemplateNode) {
            DescriptionTemplate descriptionTemplate = ((TemplateNode) instigatorNode).getData();
            contentEditor.linkTemplate(descriptionTemplate);
            logger.debug("link ContentEditor with: {}", descriptionTemplate.getName());
            contentEditor.replaceText(descriptionTemplate.getContent());
            tfTemplateName.setText(descriptionTemplate.getName());
            tfTemplateLink.setText(descriptionTemplate.getLinkedNodeName());
        }

        tfTemplateName.setEditable(instigatorNode instanceof TemplateRootNode);
        btnSave.setDisable(!(instigatorNode instanceof TemplateRootNode));

        mainController.closeProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                contentEditor.stopHighlightingService();
            }
        });
    }
}
