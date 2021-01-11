package ru.romzhel.app.controllers;

import ru.romzhel.app.nodes.Node;

public interface NodeController<T> {
    void injectMainController(T mainController, Node<?> instigatorNode);
}
