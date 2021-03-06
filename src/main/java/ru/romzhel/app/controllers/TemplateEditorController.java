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
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.NavigationTreeService;
import ru.romzhel.app.services.TemplateService;
import ru.romzhel.app.ui_components.Dialogs;
import ru.romzhel.app.ui_components.TemplateContentEditor;
import ru.romzhel.app.utils.DragDropUtils;
import ru.romzhel.app.utils.FileUtils;

import java.io.File;
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
    @FXML
    Button btnOpenFile;
    private MainAppController mainAppController;
    private AbstractNode<?> instigatorNode;
    private TemplateContentEditor contentEditor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contentEditor = new TemplateContentEditor((AnchorPane) btnSave.getParent());
        DragDropUtils dragDropUtils = new DragDropUtils();

        tfTemplateLink.setOnDragOver((DragEvent e) -> {
            if (e.getDragboard().hasString() && dragDropUtils.dropLinkFilter(e.getDragboard().getString())) {
                e.acceptTransferModes(TransferMode.ANY);
            }

            e.consume();
        });

        tfTemplateLink.setOnDragDropped((DragEvent e) -> {
            Dragboard dragboard = e.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                tfTemplateLink.setText(String.format("%s > %s",
                        dragDropUtils.parseDropData(dragboard.getString()).get(DragDropUtils.SOURCE_PARENT_NAME),
                        dragDropUtils.parseDropData(dragboard.getString()).get(DragDropUtils.VALUE)));
                success = true;
            }

            e.setDropCompleted(success);
            e.consume();
        });

        tfTemplateName.textProperty().addListener((observable, oldValue, newValue) -> {
            ((TemplateNode) instigatorNode).getData().setName(newValue);
            instigatorNode.setValue(newValue);
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

        btnOpenFile.setOnAction(event -> {
            if (tfTemplateLink.getText().trim().isEmpty()) {
                Dialogs.showMessage("Открытие файла", "Не удалось открыть файл, так как он не указан");
                return;
            }

            String[] linkParts = tfTemplateLink.getText().split(" > ");
            File file = ExcelFileService.getInstance().getFileMap().get(linkParts[0]).getFile();
            FileUtils.openFile(file);
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

        tfTemplateName.setEditable(instigatorNode instanceof TemplateRootNode);//todo реализовать изменение имени с защитой от дублирования
        btnSave.setDisable(!(instigatorNode instanceof TemplateRootNode));

        mainController.closeProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                contentEditor.stopHighlightingService();
            }
        });
    }
}
