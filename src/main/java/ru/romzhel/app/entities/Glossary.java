package ru.romzhel.app.entities;

public interface Glossary {
    String getNext();

    String getNext(String text);

    void add(String text);

    void remove(String text);

    void edit(String oldValue, String newValue);
}
