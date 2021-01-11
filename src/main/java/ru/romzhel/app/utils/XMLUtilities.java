package ru.romzhel.app.utils;

import ru.romzhel.app.controllers.MainAppController;
import ru.romzhel.app.nodes.FileNode;
import ru.romzhel.app.nodes.GlossaryNode;
import ru.romzhel.app.nodes.PropertyNode;
import ru.romzhel.app.nodes.TemplateNode;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.GlossaryService;
import ru.romzhel.app.services.TemplateService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class XMLUtilities {
    public static final String TEMPLATE_FILE = "templates.xml";
    public static final String GLOSSARY_FILE = "glossaries.xml";
    public static final String FILES_FILE = "files.xml";

    public static void saveToXml(Class<?> clazz, String fileName, Object service) throws JAXBException {
        JAXBContext jaxb = JAXBContext.newInstance(clazz);
        Marshaller jaxbMarshaller = jaxb.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        String path = System.getProperty("user.dir") + "\\" + fileName;
        File xmlFile = new File(path);
        jaxbMarshaller.marshal(service, xmlFile);
    }

    public static Object loadFromXml(Class<?> clazz, String fileName) throws JAXBException {
        File xmlFile = new File(System.getProperty("user.dir") + "\\" + fileName);

        if (!xmlFile.exists()) {
            return null;
        }

        JAXBContext jaxbTemplates = JAXBContext.newInstance(clazz);
        Unmarshaller jaxbUnmarshaller = jaxbTemplates.createUnmarshaller();
        return jaxbUnmarshaller.unmarshal(xmlFile);
    }

    public static void loadAll(MainAppController mainAppController) throws Exception {
        TemplateService templateService = (TemplateService) XMLUtilities.loadFromXml(TemplateService.class, TEMPLATE_FILE);
        GlossaryService glossaryService = (GlossaryService) XMLUtilities.loadFromXml(GlossaryService.class, GLOSSARY_FILE);
        ExcelFileService excelFileService = (ExcelFileService) XMLUtilities.loadFromXml(ExcelFileService.class, FILES_FILE);

        if (templateService != null) {
            templateService.getTemplateMap().entrySet().forEach(descriptionTemplateEntry ->
                    mainAppController.templateRootNode.getChildren().add(new TemplateNode(descriptionTemplateEntry.getValue())));
            mainAppController.templateService.setTemplateMap(templateService.getTemplateMap());
        }
        if (glossaryService != null) {
            glossaryService.getGlossaryMap().entrySet().forEach(glossaryEntry ->
                    mainAppController.glossaryRootNode.getChildren().add(new GlossaryNode(glossaryEntry.getValue())));
            mainAppController.glossaryService.setGlossaryMap(glossaryService.getGlossaryMap());
        }
        if (excelFileService != null) {
            excelFileService.getFileMap().entrySet().forEach(fileEntry -> {
                try {
                    FileNode fileNode = new FileNode(fileEntry.getValue().file);
                    fileEntry.setValue(fileNode.getData());
                    for (int i = 0; i < fileNode.getData().getTitlesIndexes().size(); i++) {
                        fileNode.getChildren().add(new PropertyNode(fileNode.getData().getTitlesIndexes().get(i)));
                    }

                    mainAppController.fileRootNode.getChildren().add(fileNode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            mainAppController.excelFileService.setFileMap(excelFileService.getFileMap());
        }
    }

}
