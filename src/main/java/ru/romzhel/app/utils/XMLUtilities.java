package ru.romzhel.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.nodes.FileNode;
import ru.romzhel.app.nodes.GlossaryNode;
import ru.romzhel.app.nodes.TemplateNode;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.GlossaryService;
import ru.romzhel.app.services.NavigationTreeService;
import ru.romzhel.app.services.TemplateService;
import ru.romzhel.app.ui_components.Dialogs;
import ru.romzhel.app.ui_components.NavigationTree;

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

    public static void saveAll() throws JAXBException {
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

    public static void loadAll() throws Exception {
        loadTemplates();
        loadGlossaries();
        loadFilesInfo();
    }

    private static void loadTemplates() throws Exception {
        TemplateService templateService = (TemplateService) XMLUtilities.loadFromXml(TemplateService.class, TEMPLATE_FILE);
        if (templateService != null) {
            templateService.getTemplateMap().forEach((key, value) -> NavigationTree.getInstance().getTemplateRootNode().getChildren().add(new TemplateNode(value)));
            TemplateService.getInstance().setTemplateMap(templateService.getTemplateMap());
            logger.trace("шаблоны загружены");
        }
    }

    private static void loadGlossaries() throws Exception {
        GlossaryService glossaryService = (GlossaryService) XMLUtilities.loadFromXml(GlossaryService.class, GLOSSARY_FILE);
        if (glossaryService != null) {
            glossaryService.getGlossaryMap().forEach((key, value) -> NavigationTree.getInstance().getGlossaryRootNode().getChildren().add(new GlossaryNode(value)));
            GlossaryService.getInstance().setGlossaryMap(glossaryService.getGlossaryMap());
            logger.trace("словари загружены");
        }
    }

    private static void loadFilesInfo() throws Exception {
        ExcelFileService excelFileService = (ExcelFileService) XMLUtilities.loadFromXml(ExcelFileService.class, FILES_FILE);
        if (excelFileService != null) {
            excelFileService.getFileMap().forEach((key, value) -> {
                try {
                    FileNode fileNode = new FileNode(value);
                    NavigationTreeService.getInstance().addFileNode(fileNode);
                } catch (Throwable e) {
                    logger.error("Ошибка парсинга столбцов при работе с файлом: '{}'", e.getMessage(), e);
                    Dialogs.showMessage("Ошибка парсинга столбцов при работе с файлом", e.getMessage());
                }
            });
            ExcelFileService.getInstance().setFileMap(excelFileService.getFileMap());
            logger.trace("информация о файлах загружена");
        }
    }

}
