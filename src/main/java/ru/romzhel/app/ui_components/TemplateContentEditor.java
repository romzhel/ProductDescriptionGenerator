package ru.romzhel.app.ui_components;

import javafx.concurrent.Task;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import ru.romzhel.app.entities.DescriptionTemplate;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.entities.StringGlossary;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.GlossaryService;
import ru.romzhel.app.services.PropertyService;
import ru.romzhel.app.utils.ExcelInputFile;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateContentEditor extends CodeArea {
    public static final Logger logger = LogManager.getLogger(TemplateContentEditor.class);
    private final Pattern PATTERN = Pattern.compile("[{](.+?)[}]");
    private ExecutorService executorService;
    private DescriptionTemplate template;
    private Subscription cleanupWhenDone;

    public TemplateContentEditor(AnchorPane parent) {
        super();
        executorService = Executors.newSingleThreadExecutor();
        setWrapText(true);
        parent.getChildren().add(this);
        AnchorPane.setLeftAnchor(this, 28.0);
        AnchorPane.setRightAnchor(this, 27.0);
        AnchorPane.setBottomAnchor(this, 55.0);
        AnchorPane.setTopAnchor(this, 118.0);
        getStyleClass().clear();
        getStyleClass().add("rtx-content");//todo рамка при фокусе светло-голубая, отличается

        parentProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof AnchorPane) {
                logger.trace("syntax highlighting is active");

                cleanupWhenDone = multiPlainChanges()
                        .successionEnds(Duration.ofMillis(5))
                        .supplyTask(this::computeHighlightingAsync)
                        .awaitLatest(multiPlainChanges())
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

        setOnDragOver((DragEvent e) -> {
            if (e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.ANY);
            }

            e.consume();
        });

        setOnDragDropped((DragEvent e) -> {
            Dragboard dragboard = e.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                appendText("{" + dragboard.getString() + "}");
                success = true;
            }

            e.setDropCompleted(success);
            e.consume();
        });
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String variableText = matcher.group(1);
            StringGlossary glossary = GlossaryService.getInstance().getGlossaryMap().get(variableText);

            String fileName = template.getLinkedNodeName();
            ExcelInputFile excelInputFile = (ExcelInputFile) ExcelFileService.getInstance().getFileMap().get(fileName);
            Property property = excelInputFile != null ?
                    PropertyService.getInstance().getProperty(excelInputFile, variableText) : null;

            String styleClass = glossary != null ? "glossary" :
                    excelInputFile == null || property == null ? "error" :
                            property.getOccurrencesCount() == property.getMaxOccurrencesCount() ? "property-full" : "property-partial";
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = getText();
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
        setStyleSpans(0, highlighting);
    }

    public void stopHighlightingService() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    public void linkTemplate(DescriptionTemplate template) {
        this.template = template;
    }
}
