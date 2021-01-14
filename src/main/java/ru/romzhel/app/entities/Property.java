package ru.romzhel.app.entities;

import lombok.Data;
import ru.romzhel.app.enums.PropertyFilling;

import static ru.romzhel.app.enums.PropertyFilling.EMPTY;

@Data
public class Property {
    private String name;
    private int columnIndex;
    private int order;
    private PropertyFilling filling = EMPTY;

    public Property() {
    }

    public Property(String name, int columnIndex) {
        this.name = name;
        this.columnIndex = columnIndex;
    }

    public Property(String name, int columnIndex, int order) {
        this.name = name;
        this.columnIndex = columnIndex;
        this.order = order;
    }
}
