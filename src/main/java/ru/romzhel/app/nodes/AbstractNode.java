package ru.romzhel.app.nodes;

import javafx.scene.control.TreeItem;
import lombok.Data;

@Data
public abstract class AbstractNode<T> extends TreeItem<String> implements Node<T> {
    protected T data;

    @Override
    public String toString() {
        return getName();
    }
}
