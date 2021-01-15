package ru.romzhel.app;

public class Test2 {
    private static final String text = "cell indexed-cell tree-cell node-root-glossary node-glossary";

    public static void main(String[] args) {
        System.out.println(text.replaceAll("\\snode-.+[^\\s]|$", ""));
    }
}
