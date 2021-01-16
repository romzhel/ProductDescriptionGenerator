package ru.romzhel.app.nodes;

import javafx.scene.control.TreeItem;
import lombok.Data;
import ru.romzhel.app.controllers.NodeController;

@Data
public abstract class AbstractNode<T> extends TreeItem<Node<T>> implements Node<T> {
    protected T data;
    protected NodeController<T> controller;
}
