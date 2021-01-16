package ru.romzhel.app.ui_components;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.nodes.*;
import ru.romzhel.app.services.PropertyService;

import java.util.Objects;

@Getter
public class NavigationTree extends TreeView<Node<?>> {
    public static final Logger logger = LogManager.getLogger(NavigationTree.class);
    private static final String DROP_HINT_STYLE = "-fx-border-color: #eea82f; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3;";
    private final TemplateRootNode templateRootNode = new TemplateRootNode();
    private final GlossaryRootNode glossaryRootNode = new GlossaryRootNode();
    private final FileRootNode fileRootNode = new FileRootNode();
    private final RootNode rootNode = new RootNode();
    private TreeItem draggedItem;
    private TreeCell dropZone;

    public NavigationTree(Pane parent) {
        super();
        rootNode.getChildren().addAll(templateRootNode, glossaryRootNode, fileRootNode);
        setRoot(rootNode);
        setShowRoot(false);
        parent.getChildren().add(this);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setTopAnchor(this, 0.0);

        setCellFactory(new Callback<TreeView<Node<?>>, TreeCell<Node<?>>>() {
            public TreeCell<Node<?>> call(TreeView<Node<?>> param) {
                final TreeCell<Node<?>> source = new TreeCell<Node<?>>() {
                    @Override
                    protected void updateItem(Node<?> item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            setText(item.toString());
                            setContextMenu(item.getContextMenu());
                            setStyle(item.getStyle());
                        } else {
                            setText(null);
                            setContextMenu(null);
                        }
                    }
                };

                source.setOnDragDetected(event -> {
                    logger.trace("navi tree OnDragDetected");
                    draggedItem = source.getTreeItem();
                    Dragboard dragboard = source.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(source.getText());
                    dragboard.setContent(content);
                    dragboard.setDragView(source.snapshot(null, null));

                    event.consume();
                });

                source.setOnDragOver((DragEvent event) -> {
                    if (!event.getDragboard().hasString()) {
                        return;
                    }

                    TreeItem thisItem = source.getTreeItem();
//                    if (draggedItem == null || !(thisItem instanceof PropertyNode) || draggedItem == thisItem) {
                    if (draggedItem == null || thisItem == null || draggedItem == thisItem) {
                        return;
                    }

                    event.acceptTransferModes(TransferMode.MOVE);
                    if (!Objects.equals(dropZone, source)) {
                        clearDropLocation();
                        dropZone = source;
                        dropZone.setStyle(dropZone.getStyle().concat(" ").concat(DROP_HINT_STYLE));
                    }
                });

                source.setOnDragDropped((DragEvent event) -> {
                    logger.trace("navi tree OnDragDropped");
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (!db.hasString()) {
                        return;
                    }

                    TreeItem thisItem = source.getTreeItem();
                    TreeItem droppedItemParent = draggedItem.getParent();

                    // remove from previous location
                    droppedItemParent.getChildren().remove(draggedItem);

                    // dropping on parent node makes it the first child
                    if (Objects.equals(droppedItemParent, thisItem)) {
                        thisItem.getChildren().add(0, draggedItem);
                        getSelectionModel().select(draggedItem);
                    } else {
                        // add to new location
                        int indexInParent = thisItem.getParent().getChildren().indexOf(thisItem);
                        thisItem.getParent().getChildren().add(indexInParent + 1, draggedItem);
                    }
                    getSelectionModel().select(draggedItem);
                    event.setDropCompleted(success);
                });

                source.setOnDragDone((DragEvent event) -> {
                    logger.trace("navi tree OnDragDone");
                    clearDropLocation();
                    if (source.getTreeItem() instanceof PropertyNode) {
                        PropertyService.getInstance().reorderProperties((FileNode) source.getTreeItem().getParent());
                    }
                });

                return source;
            }
        });
    }

    private void clearDropLocation() {
        if (dropZone != null) {
            dropZone.setStyle(dropZone.getStyle().replaceAll(" " + DROP_HINT_STYLE, ""));
        }
    }
}
