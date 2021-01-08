package ru.romzhel.app.entities;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ProductGroup {
    private String name;
    private Map<String, Integer> properties;

    public ProductGroup(String name) {
        this.name = name;
        properties = new HashMap<>();
    }
}
