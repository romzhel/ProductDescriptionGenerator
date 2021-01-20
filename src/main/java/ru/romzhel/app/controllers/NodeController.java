package ru.romzhel.app.controllers;

import ru.romzhel.app.nodes.AbstractNode;

public interface NodeController<T> {
    void injectMainController(T mainController, AbstractNode<?> instigatorNode);
}
