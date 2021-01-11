package ru.romzhel.app.nodes;

import javafx.scene.control.ContextMenu;

public interface Node<T> {
    String getName();

    ContextMenu getContextMenu();

    T getNode();

    String toString();

    String getWorkPanePath();
}
