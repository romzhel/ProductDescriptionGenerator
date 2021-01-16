package ru.romzhel.app.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.controllers.MainAppController;
import ru.romzhel.app.controllers.NodeController;
import ru.romzhel.app.nodes.Node;

import java.io.IOException;

@Getter
public class NavigationTreeService {
    public static final Logger logger = LogManager.getLogger(NavigationTreeService.class);
    private static NavigationTreeService instance;
    private MainAppController mainAppController;

    private NavigationTreeService() {
    }

    public static void init(MainAppController mainAppController) {
        instance = new NavigationTreeService();
        instance.mainAppController = mainAppController;
    }

    public static NavigationTreeService getInstance() throws RuntimeException {
        if (instance == null || instance.mainAppController == null) {
            throw new RuntimeException("NavigationTreeService не был инициализирован");
        }

        return instance;
    }

    public void navigateTo(Node<?> node) {
        if (node == null || node.getWorkPaneFxmlPath() == null || node.getWorkPaneFxmlPath().isEmpty()) {
            return;
        }

        logger.trace("выбран node '{}'", node);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(node.getWorkPaneFxmlPath()));
            AnchorPane anchorPane = loader.load();
            mainAppController.getApWorkPane().getChildren().clear();
            mainAppController.getApWorkPane().getChildren().addAll(anchorPane.getChildren());

            NodeController<MainAppController> controller = loader.getController();
            controller.injectMainController(mainAppController, node);
        } catch (IOException e) {
            logger.error("Ошибка загрузки UI: {}", e.getMessage(), e);
        }
    }

}
