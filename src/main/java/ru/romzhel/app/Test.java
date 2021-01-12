package ru.romzhel.app;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        String input = "{клапан/регулирующий} используется в системах {ОВК} зданий для управления потоком теплоносителя. " +
                "Клапан соответствует европейским стандартам и характеризуется низким показателем утечки. Основное " +
                "применение клапана: ИТП и узлы регулирования вентиляционных установок.\n" +
                "Клапан {переменная1} характеризуется следующими значениями: пропускная способность Kvs={переменная 2}, " +
                "номинальный диаметр {переменная 3}.";

        Pattern pattern = Pattern.compile("[{](.+?)[}]");
        //for (String s : spec) {
        System.out.println(input + ": ");
        Matcher matcher = pattern.matcher(input);
            /*if (matcher.matches()) {
                for (int i = 0; i < matcher.groupCount(); i++) {
                    System.out.print("\t" + i + ". " + matcher.group(i) + "\n");
                }
                System.out.println();
            } else {
                System.out.println("не совпало");
            }*/
        //}

        System.out.println();
        String s = input.replaceAll("[{](.+?)[}]", "%s");
//        System.out.println(input.replaceAll("[{](.+?)[}]", "%s"));

        List<String> vars = new ArrayList<>();
        while (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
//                System.out.println(i + " " + matcher.group(i));
                vars.add(matcher.group(1));
            }
        }

        System.out.println(String.format(s, vars.toArray()));
    }
}
