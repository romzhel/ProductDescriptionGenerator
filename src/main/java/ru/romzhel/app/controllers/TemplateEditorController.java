package ru.romzhel.app.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import ru.romzhel.app.entities.DescriptionTemplate;
import ru.romzhel.app.entities.StringGlossary;
import ru.romzhel.app.nodes.Node;
import ru.romzhel.app.nodes.TemplateNode;
import ru.romzhel.app.nodes.TemplateRootNode;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.GlossaryService;
import ru.romzhel.app.services.TemplateService;
import ru.romzhel.app.utils.ExcelInputFile;

import java.net.URL;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEditorController implements Initializable, NodeController<MainAppController> {
    public static final Logger logger = LogManager.getLogger(TemplateEditorController.class);
    //    private static final Pattern PATTERN = Pattern.compile("(\\{.*?\\})");
    private final Pattern PATTERN = Pattern.compile("[{](.+?)[}]");
    @FXML
    TextField tfTemplateName;
    @FXML
    TextField tfTemplateLink;
    /*@FXML
    TextArea taContent;*/
    @FXML
    Button btnSave;
    private CodeArea rtxContent;
    private ExecutorService executorService;
    private MainAppController mainAppController;
    private Node<?> instigatorNode;
    private Subscription cleanupWhenDone;

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        logger.trace("finding matching...");
        while (matcher.find()) {
            String variableText = matcher.group(1);
            StringGlossary glossary = GlossaryService.getInstance().getGlossaryMap().get(variableText);
            String fileName = ((TemplateNode) instigatorNode).getData().getLinkedFileName();
            ExcelInputFile excelInputFile = (ExcelInputFile) ExcelFileService.getInstance().getFileMap().get(fileName);
            int colIndex = excelInputFile != null ? excelInputFile.getTitles().getOrDefault(variableText, -1) : -1;

            String styleClass = glossary != null || colIndex >= 0 ? "brace" : "brace-error";
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executorService = Executors.newSingleThreadExecutor();
        rtxContent = new CodeArea();
        rtxContent.setWrapText(true);
        rtxContent.setBorder(Border.EMPTY);
        ((AnchorPane) btnSave.getParent()).getChildren().add(rtxContent);
        AnchorPane.setLeftAnchor(rtxContent, 28.0);
        AnchorPane.setRightAnchor(rtxContent, 27.0);
        AnchorPane.setBottomAnchor(rtxContent, 55.0);
        AnchorPane.setTopAnchor(rtxContent, 118.0);
        rtxContent.getStyleClass().clear();
        rtxContent.getStyleClass().add("rtx-content");//todo рамка при фокусе светло-голубая, отличается

        rtxContent.parentProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof AnchorPane) {
                logger.trace("syntax highlighting is active");

                cleanupWhenDone = rtxContent.multiPlainChanges()
                        .successionEnds(Duration.ofMillis(500))
                        .supplyTask(this::computeHighlightingAsync)
                        .awaitLatest(rtxContent.multiPlainChanges())
                        .filterMap(t -> {
                            if (t.isSuccess()) {
                                return Optional.of(t.get());
                            } else {
                                t.getFailure().printStackTrace();
                                return Optional.empty();
                            }
                        })
                        .subscribe(this::applyHighlighting);
            } else {
                if (cleanupWhenDone != null) {
                    logger.trace("syntax highlighting is not active");

                    cleanupWhenDone.unsubscribe();
                    executorService.shutdownNow();
                }
            }
        });

        rtxContent.setOnDragOver((DragEvent e) -> {
            if (e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.ANY);
            }

            e.consume();
        });

        rtxContent.setOnDragDropped((DragEvent e) -> {
            Dragboard dragboard = e.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                rtxContent.appendText("{" + dragboard.getString() + "}");
                success = true;
            }

            e.setDropCompleted(success);
            e.consume();
        });

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
            descriptionTemplate.setContent(rtxContent.getText());

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
            rtxContent.replaceText(descriptionTemplate.getContent());
        }

        mainController.closeProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue && executorService != null) {
                executorService.shutdownNow();
            }
        });
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = rtxContent.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executorService.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        rtxContent.setStyleSpans(0, highlighting);
    }
}
