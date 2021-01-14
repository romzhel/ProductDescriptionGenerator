package ru.romzhel.app.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.romzhel.app.utils.DescriptionGenerator.EMPTY;

public class TemplateContentCorrector {

    public String correct(String text) {
        StringBuilder resultBuilder = new StringBuilder();                                          //1. бьём по абзацам
        for (String para : text.split("\n")) {
            StringBuilder sb = new StringBuilder();                       //2. бьём по предложениям и находим проблемные
//        [^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)
            Matcher matcher = Pattern.compile("[^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)",
                    Pattern.MULTILINE | Pattern.COMMENTS).matcher(para);

            while (matcher.find()) {
                String found = matcher.group();
                String treated = StringUtils.capitalize(matcher.group().contains(EMPTY) ?
                        correctSentence(matcher.group()) : matcher.group());
                sb.append(treated);
            }

            resultBuilder.append(resultBuilder.toString().isEmpty() ? sb : "\n" + sb);
        }

        return resultBuilder.toString();
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
