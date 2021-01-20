package ru.romzhel.app.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.controllers.MainAppController;
import ru.romzhel.app.controllers.NodeController;
import ru.romzhel.app.entities.ProductGroup;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.nodes.*;
import ru.romzhel.app.ui_components.NavigationTree;

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

    public void navigateTo(AbstractNode<?> node) {
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

    public void addTemplateNode(TemplateNode templateNode) {
        NavigationTree.getInstance().getTemplateRootNode().getChildren().add(templateNode);
    }

    public void addGlossaryNode(GlossaryNode glossaryNode) {
        NavigationTree.getInstance().getGlossaryRootNode().getChildren().add(glossaryNode);
    }

    public void addFileNode(FileNode fileNode) {
        NavigationTree.getInstance().getFileRootNode().getChildren().add(fileNode);
        display(fileNode);
    }

    public void display(FileNode fileNode) {
        fileNode.getChildren().clear();

        for (ProductGroup productGroup : fileNode.getData().getProductGroupMap().values()) {
            ProductGroupNode productGroupNode = new ProductGroupNode(productGroup);
            fileNode.getChildren().add(productGroupNode);

            for (Property property : PropertyService.getInstance().getPropertiesByOrder(productGroup.getPropertyMap())) {
                productGroupNode.getChildren().add(new PropertyNode(property));
            }
        }
    }

    public void reorderPropertyNodes(ProductGroupNode productGroupNode) {
        int order = 0;
        for (TreeItem<String> childNode : productGroupNode.getChildren()) {
            productGroupNode.getData().getPropertyMap().get(((PropertyNode) childNode).getName()).setOrder(order++);
        }
    }
}
