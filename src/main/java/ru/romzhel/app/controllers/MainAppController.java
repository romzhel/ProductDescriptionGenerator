package ru.romzhel.app.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.romzhel.app.nodes.*;
import ru.romzhel.app.utils.Dialogs;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainAppController implements Initializable {
    private final RootNode rootNode = new RootNode();
    private final TemplateRootNode templateRootNode = new TemplateRootNode();
    private final GlossaryRootNode glossaryRootNode = new GlossaryRootNode();
    private final FileRootNode fileRootNode = new FileRootNode();

    @FXML
    TreeView<Node<?>> tvNavi;
    @FXML
    TextField tf;
    @FXML
    AnchorPane apWorkPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootNode.getChildren().addAll(templateRootNode, glossaryRootNode, fileRootNode);
        tvNavi.setRoot(rootNode);
        tvNavi.setShowRoot(false);

        tvNavi.setOnMouseClicked(event -> {
            TreeItem<Node<?>> node = tvNavi.getFocusModel().getFocusedItem();

            if (node == null) {
                return;
            }

            String workPanePath = ((Node<?>) node).getWorkPanePath();
            if (workPanePath != null && !workPanePath.isEmpty()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(workPanePath));
                    AnchorPane anchorPane = loader.load();
                    apWorkPane.getChildren().clear();
                    apWorkPane.getChildren().addAll(anchorPane.getChildren());

                    NodeController<MainAppController> controller = loader.getController();
                    controller.injectMainController(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(node.getValue().toString() + ", " + (node.getClass().getSimpleName()));
        });

        tvNavi.setCellFactory(new Callback<TreeView<Node<?>>, TreeCell<Node<?>>>() {
            public TreeCell<Node<?>> call(TreeView<Node<?>> param) {
                final TreeCell<Node<?>> source = new TreeCell<Node<?>>() {
                    @Override
                    protected void updateItem(Node<?> item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            setText(item.toString());
                        } else {
                            setText(null);
                        }
                    }
                };

                source.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        System.out.println("onDragDetected");

                        Dragboard dragboard = source.startDragAndDrop(TransferMode.LINK);

                        ClipboardContent content = new ClipboardContent();
                        content.putString(source.getText());
                        dragboard.setContent(content);

                        System.out.println("added: " + source.getText());

                        event.consume();
                    }
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
                for (int i = 0; i < fileNode.getNode().getTitlesIndexes().size(); i++) {
                    fileNode.getChildren().add(new PropertyNode(fileNode.getNode().getTitlesIndexes().get(i)));
                }
                fileRootNode.getChildren().add(fileNode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
