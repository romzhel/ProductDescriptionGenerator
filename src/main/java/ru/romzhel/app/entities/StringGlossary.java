package ru.romzhel.app.entities;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@XmlRootElement(name = "glossary")
@XmlAccessorType(XmlAccessType.FIELD)
public class StringGlossary implements Glossary {
    private Map<String, Integer> glossaryMap;
    private List<String> glossaryItems;
    private int nextIndex;
    private String name;

    public StringGlossary() {
        this.glossaryMap = new HashMap<>();
        this.glossaryItems = new ArrayList<>();
    }

    @Override
    public String getNext() {
        return glossaryItems.get(++nextIndex % glossaryItems.size());
    }

    @Override
    public String getNext(String text) {
        return glossaryItems.get(++nextIndex % glossaryItems.size());
    }

    @Override
    public void add(String text) {
        glossaryItems.add(text);
        glossaryMap.put(text, glossaryItems.indexOf(text));
    }

    @Override
    public void remove(String text) {
        glossaryItems.remove(text);
        glossaryItems.remove(text);
        int removedIndex = glossaryMap.remove(text);
        glossaryMap.replaceAll((s, integer) -> integer > removedIndex ? integer - 1 : integer);
    }

    @Override
    public void edit(String oldValue, String newValue) {
        int removedIndex = glossaryMap.remove(oldValue);
        glossaryMap.put(newValue, removedIndex);
        glossaryItems.set(removedIndex, newValue);
    }
}
