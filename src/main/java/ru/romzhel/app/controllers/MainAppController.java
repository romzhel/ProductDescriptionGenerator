package ru.romzhel.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.Data;
import ru.romzhel.app.nodes.*;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.GlossaryService;
import ru.romzhel.app.services.TemplateService;
import ru.romzhel.app.utils.Dialogs;
import ru.romzhel.app.utils.XMLUtilities;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Data
public class MainAppController implements Initializable {
    final RootNode rootNode = new RootNode();
    public final TemplateRootNode templateRootNode = new TemplateRootNode();
    public final GlossaryRootNode glossaryRootNode = new GlossaryRootNode();
    public final FileRootNode fileRootNode = new FileRootNode();
    public final TemplateService templateService = new TemplateService();
    public final GlossaryService glossaryService = GlossaryService.getInstance();
    public final ExcelFileService excelFileService = ExcelFileService.getInstance();

    @FXML
    TreeView<Node<?>> tvNavi;
    @FXML
    AnchorPane apWorkPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootNode.getChildren().addAll(templateRootNode, glossaryRootNode, fileRootNode);
        tvNavi.setRoot(rootNode);
        tvNavi.setShowRoot(false);

        try {
            XMLUtilities.loadAll(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvNavi.setOnMouseClicked(event -> {
            Node<?> node = (Node<?>) tvNavi.getFocusModel().getFocusedItem();

            if (node == null) {
                return;
            }

            String workPanePath = node.getWorkPanePath();
            if (workPanePath != null && !workPanePath.isEmpty()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(workPanePath));
                    AnchorPane anchorPane = loader.load();
                    apWorkPane.getChildren().clear();
                    apWorkPane.getChildren().addAll(anchorPane.getChildren());

                    NodeController<MainAppController> controller = loader.getController();
                    controller.injectMainController(this, node);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        tvNavi.setCellFactory(new Callback<TreeView<Node<?>>, TreeCell<Node<?>>>() {
            public TreeCell<Node<?>> call(TreeView<Node<?>> param) {
                final TreeCell<Node<?>> source = new TreeCell<Node<?>>() {
                    @Override
                    protected void updateItem(Node<?> item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            setText(item.toString());
                            setContextMenu(item.getContextMenu());
                        } else {
                            setText(null);
                            setContextMenu(null);
                        }
                    }
                };

                String.format("%s dfdfd %s dfdfdfd %s", new String[]{"", "", ""});

                source.setOnDragDetected(event -> {
                    Dragboard dragboard = source.startDragAndDrop(TransferMode.LINK);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(source.getText());
                    dragboard.setContent(content);

                    event.consume();
                });

                return source;
            }
        });

    }

    public void openFile() {
        try {
            Stage stage = (Stage) tvNavi.getScene().getWindow();
            List<File> files = new Dialogs().selectAnyFile(stage, "Выбор файла", Dialogs.EXCEL_FILES, null);

            for (int f = 0; f < files.size(); f++) {
                FileNode fileNode = new FileNode(files.get(f));
                for (int i = 0; i < fileNode.getData().getTitlesIndexes().size(); i++) {
                    fileNode.getChildren().add(new PropertyNode(fileNode.getData().getTitlesIndexes().get(i)));
                }
                fileRootNode.getChildren().add(fileNode);
                excelFileService.getFileMap().put(fileNode.getData().getFileName(), fileNode.getData());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveToFile() throws JAXBException {
        XMLUtilities.saveAll(this);
    }
}
