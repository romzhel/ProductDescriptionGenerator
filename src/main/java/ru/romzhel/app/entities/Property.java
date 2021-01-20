package ru.romzhel.app.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Property {
    private String name;
    private int columnIndex;
    private int order;
    private int occurrencesCount;
    private int maxOccurrencesCount;

    public Property(String name, int columnIndex) {
        this.name = name;
        this.columnIndex = columnIndex;
    }

    public Property(String name, int columnIndex, int order) {
        this.name = name;
        this.columnIndex = columnIndex;
        this.order = order;
    }

    public void increaseOccurrencesCount(int value) {
        occurrencesCount += value;
    }

    public void increaseMaxOccurrencesCount(int value) {
        maxOccurrencesCount += value;
    }
}
