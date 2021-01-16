package ru.romzhel.app.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.entities.Property;
import ru.romzhel.app.nodes.FileNode;
import ru.romzhel.app.nodes.Node;
import ru.romzhel.app.nodes.PropertyNode;
import ru.romzhel.app.services.ExcelFileService;
import ru.romzhel.app.services.PropertyService;
import ru.romzhel.app.ui_components.Dialogs;
import ru.romzhel.app.ui_components.NavigationTree;
import ru.romzhel.app.utils.XMLUtilities;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Data
public class MainAppController implements Initializable {
    public static final Logger logger = LogManager.getLogger();
    public BooleanProperty closeProperty = new SimpleBooleanProperty(false);
    @FXML
    TreeView<Node<?>> tvNavi;
    @FXML
    AnchorPane apNavi;
    @FXML
    AnchorPane apWorkPane;
    private NavigationTree navigationTree;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navigationTree = new NavigationTree(apNavi);

        navigationTree.setOnMouseClicked(event -> {
            Node<?> node = (Node<?>) navigationTree.getSelectionModel().getSelectedItem();
            logger.trace("выбран node '{}'", node);

            if (node == null || node.getWorkPaneFxmlPath() == null || node.getWorkPaneFxmlPath().isEmpty()) {
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(node.getWorkPaneFxmlPath()));
                AnchorPane anchorPane = loader.load();
                apWorkPane.getChildren().clear();
                apWorkPane.getChildren().addAll(anchorPane.getChildren());

                NodeController<MainAppController> controller = loader.getController();
                controller.injectMainController(this, node);
            } catch (IOException e) {
                logger.error("Ошибка загрузки UI: {}", e.getMessage(), e);
            }
        });

        try {
            XMLUtilities.loadAll(this);
        } catch (Exception e) {
            logger.error("Ошибка загрузки сохраненных данных: {}", e.getMessage(), e);
            Dialogs.showMessage("Ошибка загрузки данных рабочей среды", e.getMessage());
        }
    }

    public void openFile() {
        try {
            Stage stage = (Stage) tvNavi.getScene().getWindow();
            List<File> files = new Dialogs().selectAnyFile(stage, "Выбор файла", Dialogs.EXCEL_FILES, null);

            for (int f = 0; f < files.size(); f++) {
                try {
                    FileNode fileNode = new FileNode(files.get(f));
                    for (Property property : PropertyService.getInstance().getPropertiesByOrder(fileNode.getData())) {
                        fileNode.getChildren().add(new PropertyNode(property));
                    }
                    navigationTree.getFileRootNode().getChildren().add(fileNode);
                    ExcelFileService.getInstance().getFileMap().put(fileNode.getData().getFileName(), fileNode.getData());
                } catch (Exception e) {
                    logger.error("Ошибка открытия файла: '{}");
                    Dialogs.showMessage("Ошибка открытия файла", e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveToFile() throws JAXBException {
        logger.trace("сохранение рабочей среды в xml файлы");
        XMLUtilities.saveAll(this);
    }
}
