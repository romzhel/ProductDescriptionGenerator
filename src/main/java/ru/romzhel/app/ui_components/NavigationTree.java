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
import ru.romzhel.app.services.NavigationTreeService;
import ru.romzhel.app.utils.DragDropUtils;

import java.util.Objects;

@Getter
public class NavigationTree extends TreeView<String> {
    public static final Logger logger = LogManager.getLogger(NavigationTree.class);
    private static final String DROP_HINT_STYLE = "-fx-border-color: #eea82f; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3;";
    private static NavigationTree instance;
    private final TemplateRootNode templateRootNode = new TemplateRootNode();
    private final GlossaryRootNode glossaryRootNode = new GlossaryRootNode();
    private final FileRootNode fileRootNode = new FileRootNode();
    private final RootNode rootNode = new RootNode();
    private TreeItem<String> draggedItem;
    private TreeCell<String> dropZone;

    private NavigationTree(Pane parent) {
        super();
        rootNode.getChildren().addAll(templateRootNode, glossaryRootNode, fileRootNode);
        setRoot(rootNode);
        setShowRoot(false);
        parent.getChildren().add(this);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setTopAnchor(this, 0.0);

        DragDropUtils dragDropUtils = new DragDropUtils();

        setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            public TreeCell<String> call(TreeView<String> param) {
                final TreeCell<String> treecell = new TreeCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
//                            logger.debug("Отображение в дереве элемента {}: {}", getTreeItem().getClass().getSimpleName(), item);
                            setText(((AbstractNode<?>) getTreeItem()).changeDisplayedText(item));
                            setContextMenu(((AbstractNode<?>) getTreeItem()).getContextMenu());
                            setStyle(((AbstractNode<?>) getTreeItem()).getStyle());
                        } else {
                            setText(null);
                            setContextMenu(null);
                        }
                    }
                };

                treecell.setOnDragDetected(event -> {
                    logger.trace("navi tree OnDragDetected");
                    draggedItem = treecell.getTreeItem();
                    Dragboard dragboard = treecell.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    String dragContent = dragDropUtils.buildDragData(treecell);
                    logger.trace("drag & drop info: {}", dragContent);
                    content.putString(dragContent);
                    dragboard.setContent(content);
                    dragboard.setDragView(treecell.snapshot(null, null));

                    event.consume();
                });

                treecell.setOnDragOver((DragEvent event) -> {
                    if (!event.getDragboard().hasString()) {
                        return;
                    }

                    TreeItem<String> thisItem = treecell.getTreeItem();
                    if (draggedItem == null || thisItem == null || draggedItem == thisItem ||
                            !dragDropUtils.dropTreeCellFilter(treecell, event.getDragboard().getString())) {
                        return;
                    }

                    event.acceptTransferModes(TransferMode.MOVE);
                    if (!Objects.equals(dropZone, treecell)) {
                        clearDropLocation();
                        dropZone = treecell;
                        dropZone.setStyle(dropZone.getStyle().concat(" ").concat(DROP_HINT_STYLE));
                    }
                });

                treecell.setOnDragDropped((DragEvent event) -> {
                    logger.trace("navi tree OnDragDropped");
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (!db.hasString()) {
                        return;
                    }

                    TreeItem<String> thisItem = treecell.getTreeItem();
                    TreeItem<String> droppedItemParent = draggedItem.getParent();

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

                treecell.setOnDragDone((DragEvent event) -> {
                    logger.trace("navi tree OnDragDone");
                    clearDropLocation();
                    if (treecell.getTreeItem() instanceof PropertyNode) {
                        NavigationTreeService.getInstance().reorderPropertyNodes((ProductGroupNode) treecell.getTreeItem().getParent());
                    }
                });

                return treecell;
            }
        });
    }

    public static NavigationTree init(Pane parent) {
        instance = new NavigationTree(parent);
        return instance;
    }

    public static NavigationTree getInstance() throws RuntimeException {
        if (instance == null) {
            throw new RuntimeException("Навигационное дерево не инициализировано!!!");
        }
        return instance;
    }

    private void clearDropLocation() {
        if (dropZone != null) {
            dropZone.setStyle(dropZone.getStyle().replaceAll(" " + DROP_HINT_STYLE, ""));
        }
    }

    public TreeItem<String> getSelectedItem() {
        return getSelectionModel().getSelectedItem();
    }
}
