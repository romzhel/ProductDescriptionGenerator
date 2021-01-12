package ru.romzhel.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.controllers.MainAppController;
import ru.romzhel.app.utils.XMLUtilities;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.Properties;

public class MainApp extends Application {
    public static final Logger logger = LogManager.getLogger(MainApp.class);
    private static final String PROP_FILE_NAME = "application.properties";

    public static void main(String[] args) {
        InputStream propFile = MainApp.class.getClassLoader().getResourceAsStream(PROP_FILE_NAME);
        Properties properties = new Properties();
        try {
            properties.load(propFile);
            propFile.close();
        } catch (Exception e) {
            logger.fatal("Ошибка запуска программы: {}", e.getMessage(), e);
            Platform.exit();
        }

        logger.trace("Версия приложения '{}'",
                String.format("v.%s от %s", properties.getProperty("app_version"), properties.getProperty("app_date")));
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_app.fxml"));
        Pane root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/java-keywords.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Генератор описаний");
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            ((MainAppController) loader.getController()).closeProperty.set(true);
            try {
                XMLUtilities.saveAll(loader.getController());
                logger.trace("Рабочая среда сохранена");
            } catch (JAXBException e) {
                logger.warn("Ошибка сохранения");
            }
            logger.trace("Выход из приложения");
        });


    }
}
