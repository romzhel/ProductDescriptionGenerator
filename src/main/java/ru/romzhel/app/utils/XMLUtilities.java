package ru.romzhel.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.controllers.MainAppController;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.nodes.FileNode;
import ru.romzhel.app.nodes.GlossaryNode;
import ru.romzhel.app.nodes.PropertyNode;
import ru.romzhel.app.nodes.TemplateNode;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.GlossaryService;
import ru.romzhel.app.services.PropertyService;
import ru.romzhel.app.services.TemplateService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class XMLUtilities {
    public static final Logger logger = LogManager.getLogger(XMLUtilities.class);
    public static final String TEMPLATE_FILE = "templates.xml";
    public static final String GLOSSARY_FILE = "glossaries.xml";
    public static final String FILES_FILE = "files.xml";

    public static void saveAll(MainAppController mainAppController) throws JAXBException {
        saveToXml(TemplateService.class, TEMPLATE_FILE, TemplateService.getInstance());
        saveToXml(GlossaryService.class, GLOSSARY_FILE, GlossaryService.getInstance());
        saveToXml(ExcelFileService.class, FILES_FILE, ExcelFileService.getInstance());
    }

    public static void saveToXml(Class<?> clazz, String fileName, Object service) throws JAXBException {
        JAXBContext jaxb = JAXBContext.newInstance(clazz);
        Marshaller jaxbMarshaller = jaxb.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        String path = System.getProperty("user.dir") + "\\" + fileName;
        File xmlFile = new File(path);
        jaxbMarshaller.marshal(service, xmlFile);
        logger.trace("настройки сохранены в файл: '{}'", xmlFile);
    }

    public static Object loadFromXml(Class<?> clazz, String fileName) throws JAXBException {
        File xmlFile = new File(System.getProperty("user.dir") + "\\" + fileName);

        if (!xmlFile.exists()) {
            logger.warn("не найден файл настроек '{}'", xmlFile);
            return null;
        }

        JAXBContext jaxbTemplates = JAXBContext.newInstance(clazz);
        Unmarshaller jaxbUnmarshaller = jaxbTemplates.createUnmarshaller();
        return jaxbUnmarshaller.unmarshal(xmlFile);
    }

    public static void loadAll(MainAppController mainAppController) throws Exception {
        loadTemplates(mainAppController);
        loadGlossaries(mainAppController);
        loadFilesInfo(mainAppController);
    }

    private static void loadTemplates(MainAppController mainAppController) throws Exception {
        TemplateService templateService = (TemplateService) XMLUtilities.loadFromXml(TemplateService.class, TEMPLATE_FILE);
        if (templateService != null) {
            templateService.getTemplateMap().entrySet().forEach(descriptionTemplateEntry ->
                    mainAppController.getNavigationTree().getTemplateRootNode().getChildren().add(new TemplateNode(descriptionTemplateEntry.getValue())));
            TemplateService.getInstance().setTemplateMap(templateService.getTemplateMap());
            logger.trace("шаблоны загружены");
        }
    }

    private static void loadGlossaries(MainAppController mainAppController) throws Exception {
        GlossaryService glossaryService = (GlossaryService) XMLUtilities.loadFromXml(GlossaryService.class, GLOSSARY_FILE);
        if (glossaryService != null) {
            glossaryService.getGlossaryMap().entrySet().forEach(glossaryEntry ->
                    mainAppController.getNavigationTree().getGlossaryRootNode().getChildren().add(new GlossaryNode(glossaryEntry.getValue())));
            GlossaryService.getInstance().setGlossaryMap(glossaryService.getGlossaryMap());
            logger.trace("словари загружены");
        }
    }

    private static void loadFilesInfo(MainAppController mainAppController) throws Exception {
        ExcelFileService excelFileService = (ExcelFileService) XMLUtilities.loadFromXml(ExcelFileService.class, FILES_FILE);
        if (excelFileService != null) {
            excelFileService.getFileMap().entrySet().forEach(fileEntry -> {
                try {
                    FileNode fileNode = new FileNode(fileEntry.getValue());
                    for (Property property : PropertyService.getInstance().getPropertiesByOrder(fileNode.getData())) {
                        fileNode.getChildren().add(new PropertyNode(property));
                    }

                    mainAppController.getNavigationTree().getFileRootNode().getChildren().add(fileNode);
                } catch (Exception e) {
                    logger.error("Ошибка парсинга столбцов: '{}'", e.getMessage(), e);
                }
            });
            ExcelFileService.getInstance().setFileMap(excelFileService.getFileMap());
            logger.trace("информация о файлах загружена");
        }
    }

}
