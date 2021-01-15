package ru.romzhel.app.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.romzhel.app.utils.DescriptionGenerator.EMPTY;

public class TemplateContentCorrector {
    public static final Logger logger = LogManager.getLogger(TemplateContentCorrector.class);

    public String correctDescription(String text) {
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

    public String correctStringProperty(String inputText) {
//        logger.debug("input:  {}", inputText);
        if (inputText.matches("^[\\d]+\\,?\\.?[\\d]+$")) {
            Matcher matcher = Pattern.compile("([,.]0+)$").matcher(inputText);
            if (matcher.find()) {
                String match = matcher.group();
                String res = inputText.replaceAll(match, "");
//                logger.debug("cut decimal {} = {}", match, res);
                return res;
            }
//            logger.debug("output: {}", inputText.replaceAll("\\.", ","));
            return inputText.replaceAll("\\.", ",");
        } else {
            if (inputText.contains(";")) {
                String[] parts = inputText.split(";");

                for (int i = 0; i < parts.length; i++) {    //делаем строчной первую букву
                    if (parts[i].matches("^[\\D]")) {
                        parts[i] = StringUtils.uncapitalize(parts[i]);
                    }
                }

                String result = String.join(", ", Arrays.copyOf(parts, parts.length - 1))
                        .concat(" и ").concat(parts[parts.length - 1]);
//                logger.debug("output: {}", result);
                return result;
            } else {
//                logger.debug("output: {}", inputText);
                return inputText;
            }
        }
    }
}
