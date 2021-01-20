package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;

public interface Node<T> {
    String getName();

    ContextMenu getContextMenu();

    T getData();

    void setData(T data);

    String toString();

    String getWorkPaneFxmlPath();

    String getStyle();

    String changeDisplayedText(String text);
}
