package ru.romzhel.app.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.nodes.AbstractNode;
import ru.romzhel.app.services.NavigationTreeService;
import ru.romzhel.app.ui_components.Dialogs;
import ru.romzhel.app.ui_components.NavigationTree;
import ru.romzhel.app.utils.XMLUtilities;

import javax.xml.bind.JAXBException;
import java.net.URL;
import java.util.ResourceBundle;

@Data
public class MainAppController implements Initializable {
    public static final Logger logger = LogManager.getLogger();
    public BooleanProperty closeProperty = new SimpleBooleanProperty(false);

    @FXML
    AnchorPane apNavi;
    @FXML
    AnchorPane apWorkPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        NavigationTreeService.init(this);
        NavigationTree.init(apNavi);

        NavigationTree.getInstance().setOnMouseClicked(event -> {
            AbstractNode<?> node = (AbstractNode<?>) NavigationTree.getInstance().getSelectedItem();
            NavigationTreeService.getInstance().navigateTo(node);
        });

        try {
            XMLUtilities.loadAll();
        } catch (Exception e) {
            logger.error("Ошибка загрузки сохраненных данных: {}", e.getMessage(), e);
            Dialogs.showMessage("Ошибка загрузки данных рабочей среды", e.getMessage());
        }
    }

    public void saveWorkEnvToDisk() throws JAXBException {
        logger.trace("сохранение рабочей среды в xml файлы через меню");
        XMLUtilities.saveAll();
    }
}
