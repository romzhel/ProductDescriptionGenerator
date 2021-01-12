package ru.romzhel.app.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateContentParser {
    private final String PATTERN = "[{](.+?)[}]";

    public List<String> parseContent(String content) {
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(content);

        List<String> result = new ArrayList<>();
        result.add(content.replaceAll(PATTERN, "%s"));

        while (matcher.find()) {
            result.add(matcher.group(1));
        }

        return result;
    }
}
