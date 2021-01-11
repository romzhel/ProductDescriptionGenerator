package ru.romzhel.app.controllers;

public interface NodeController<T> {
    void injectMainController(T mainController);
}
