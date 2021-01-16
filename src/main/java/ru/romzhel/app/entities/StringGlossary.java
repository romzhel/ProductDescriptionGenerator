package ru.romzhel.app.entities;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

@Data
@XmlRootElement(name = "glossary")
@XmlAccessorType(XmlAccessType.FIELD)
public class StringGlossary implements Glossary {
    private List<String> glossaryItems;
    @XmlTransient
    private int nextIndex;
    private String name;

    public StringGlossary() {
        this.glossaryItems = new ArrayList<>();
    }

    @Override
    public String getNext() {
        return glossaryItems.get(nextIndex++ % glossaryItems.size());
    }

    @Override
    public void add(String text) {
        glossaryItems.add(text);
    }

    @Override
    public void remove(String text) {
        glossaryItems.remove(text);
    }

    @Override
    public void edit(String oldValue, String newValue) {
        int editedIndex = glossaryItems.indexOf(oldValue);
        if (editedIndex >= 0) {
            glossaryItems.set(editedIndex, newValue);
        }
    }
}
