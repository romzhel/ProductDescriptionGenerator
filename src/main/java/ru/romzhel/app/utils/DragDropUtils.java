package ru.romzhel.app.utils;

import javafx.scene.control.TreeCell;
import ru.romzhel.app.nodes.GlossaryNode;
import ru.romzhel.app.nodes.ProductGroupNode;
import ru.romzhel.app.nodes.PropertyNode;

import java.util.HashMap;
import java.util.Map;

public class DragDropUtils {
    public static final String DELIMITER = ">->";
    public static final String SOURCE_PARENT_TYPE = "parent_type";
    public static final String SOURCE_PARENT_NAME = "parent_name";
    public static final String SOURCE_NODE_TYPE = "node_type";
    public static final String VALUE = "value";

    public String buildDragData(TreeCell<String> treeCell) {
        String nodeType = treeCell.getTreeItem().getClass().getSimpleName();
        String parentType = treeCell.getTreeItem().getParent().getClass().getSimpleName();
        String parentName = treeCell.getTreeItem().getParent().getValue();

        return String.join(DELIMITER, new String[]{parentType, parentName, nodeType, treeCell.getText()});
    }

    public Map<String, String> parseDropData(String dragData) {
        Map<String, String> map = new HashMap<>();
        String[] parts = dragData.split(DELIMITER);
        map.put(SOURCE_PARENT_TYPE, parts[0]);
        map.put(SOURCE_PARENT_NAME, parts[1]);
        map.put(SOURCE_NODE_TYPE, parts[2]);
        map.put(VALUE, parts[3]);

        return map;
    }

    public boolean dropTreeCellFilter(TreeCell<String> target, String dragData) {
        String targetType = target.getTreeItem().getClass().getSimpleName();
        String targetName = target.getTreeItem().getValue();
        String parentType = target.getTreeItem().getParent().getClass().getSimpleName();
        String parentName = target.getTreeItem().getParent().getValue();
        String[] dragDataParts = dragData.split(DELIMITER);

        return targetType.equals(dragDataParts[2]) && parentName.equals(dragDataParts[1]) || dragDataParts[1].equals(targetName);
    }

    public boolean dropContentEditorFilter(String dragData) {
        String[] dragDataParts = dragData.split(DELIMITER);
        return dragDataParts[2].equals(PropertyNode.class.getSimpleName()) || dragDataParts[2].equals(GlossaryNode.class.getSimpleName());
    }

    public boolean dropLinkFilter(String dragData) {
        String[] dragDataParts = dragData.split(DELIMITER);
        return dragDataParts[2].equals(ProductGroupNode.class.getSimpleName());
    }

    /*public boolean dropContentEditorFilter(String dragData) {

    }*/
}
