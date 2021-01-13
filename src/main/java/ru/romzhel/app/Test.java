package ru.romzhel.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        String[] input = {
                "Клапан регулирующий используется в системах ОВК зданий для управления потока теплоносителя. Клапан " +
                        "соответствует европейским стандартам и характеризуется низким показателем утечки. Основное " +
                        "применение регулирующий клапан: ИТП и регулирования вентиляционных установок.\n" +
                        "Клапан ALI15VAI60/61 характеризуется следующими значениями: пропускная способность Kvs=#EMPTY#, " +
                        "номинальный диаметр #EMPTY#.",

                "Клапан регулирующий используется в системах индивидуальные тепловые пункты зданий для управления потока теплоносителя. " +
                        "Клапан соответствует европейским стандартам и характеризуется малым значением утечки. Основное применение регулирующий " +
                        "клапан: ОВК и регулирования вентиляционных машин.\n" +
                        "Клапан VAG61.40-25 характеризуется следующими значениями: пропускная способность Kvs=#EMPTY#, номинальный диаметр DN40.",

                "Клапан регулирующий используется в системах индивидуальные тепловые пункты зданий для управления потока теплоносителя. " +
                        "Клапан соответствует европейским стандартам и характеризуется малым значением утечки. Основное применение регулирующий " +
                        "клапан: ОВК и регулирования вентиляционных машин.\n" +
                        "Клапан VAG61.40-25-2 характеризуется следующими значениями: пропускная способность Kvs=354, номинальный диаметр #EMPTY#."
        };

        for (String desc : input) {
            System.out.println(correctDescription(desc) + "\n");
//            correctDescription(desc);
        }
    }

    public static String correctDescription(String text) {
        //1. проверяем наличие в тексте !EMPTY!
        if (!text.contains("#EMPTY#")) {
            return text;
        }

        //2. бьём по предложениям и находим проблемные
        StringBuilder sb = new StringBuilder();
//        Matcher matcher = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS).matcher(text);
        Matcher matcher = Pattern.compile("[^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS).matcher(text);

        while (matcher.find()) {
            sb.append(matcher.group().contains("#EMPTY#") ? correctSentence(matcher.group()) : matcher.group());
        }

        return sb.toString();
    }

    public static String correctSentence(String sentence) {
        StringBuilder sb = new StringBuilder();
//        System.out.println("input:" + sentence);
        Matcher matcher = Pattern.compile("(\n?.*\\:)").matcher(sentence);//проверка основной части предложения
        if (matcher.find()) {
            sb.append(matcher.group());
        } else {
            return "";
        }

        sentence = sentence.replaceAll(matcher.group(), "");
        matcher = Pattern.compile("[^,]([^,]*?#EMPTY#.*?)[.,!?]").matcher(sentence);//проверка чарактеристик
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
