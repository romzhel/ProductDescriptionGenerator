package ru.romzhel.app.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.romzhel.app.utils.DescriptionGenerator.EMPTY;

public class TemplateContentCorrector {

    public String emptyCorrector(String text) {
        //1. проверяем наличие в тексте !EMPTY!
        if (!text.contains(EMPTY)) {
            return text;
        }

        //2. бьём по предложениям и находим проблемные
        StringBuilder sb = new StringBuilder();
//        Matcher matcher = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS).matcher(text);
        Matcher matcher = Pattern.compile("[^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS).matcher(text);

        while (matcher.find()) {
            sb.append(matcher.group().contains(EMPTY) ? correctSentence(matcher.group()) : matcher.group());
        }

        return sb.toString();
    }

    public String correctSentence(String sentence) {
        StringBuilder sb = new StringBuilder();
//        System.out.println("input:" + sentence);
        Matcher matcher = Pattern.compile("(\n?.*\\:)").matcher(sentence);//проверка основной части предложения
        if (matcher.find()) {
            sb.append(matcher.group());
        } else {
            return "";
        }

        sentence = sentence.replaceAll(matcher.group(), "");
        matcher = Pattern.compile("[^,]([^,]*?" + EMPTY + ".*?)[.,!?]").matcher(sentence);//проверка характеристик
        while (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
//                System.out.println(i + ": " + matcher.group(i));
                sentence = sentence.replaceAll(matcher.group(), "");
            }
        }

//        System.out.println("output:" + sentence + "\n\n");
        return sentence.trim().isEmpty() ? "" : sb.append(sentence.replaceAll(",$", ".")).toString();
    }
}
