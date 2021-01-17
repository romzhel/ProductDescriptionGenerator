package ru.romzhel.app.services;

import lombok.Data;
import ru.romzhel.app.entities.StringGlossary;
import ru.romzhel.app.nodes.GlossaryNode;
import ru.romzhel.app.nodes.GlossaryRootNode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@XmlRootElement(name = "glossaries")
@XmlAccessorType(XmlAccessType.FIELD)
public class GlossaryService {
    private static GlossaryService instance;
    private Map<String, StringGlossary> glossaryMap = new HashMap<>();

    private GlossaryService() {
    }

    public static GlossaryService getInstance() {
        if (instance == null) {
            instance = new GlossaryService();
        }
        return instance;
    }

    public List<String> convertToList(String planText) {
        return Arrays.stream(planText.split("\n"))
                .filter(line -> !String.valueOf(line).isEmpty())
                .collect(Collectors.toList());
    }

    public void addGlossaryFromRootData(GlossaryRootNode glossaryRootNode) {
        addGlossary(glossaryRootNode.getData());

        glossaryRootNode.setData(new StringGlossary());
        NavigationTreeService.getInstance().navigateTo(glossaryRootNode);
    }

    public void addGlossary(StringGlossary glossary) {
        NavigationTreeService.getInstance().addGlossaryNode(new GlossaryNode(glossary));
        glossaryMap.put(glossary.getName(), glossary);
    }
}
