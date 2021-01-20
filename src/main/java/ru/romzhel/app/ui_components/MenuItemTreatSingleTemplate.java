package ru.romzhel.app.ui_components;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.romzhel.app.nodes.TemplateNode;
import ru.romzhel.app.utils.DescriptionGenerator;

public class MenuItemTreatSingleTemplate extends MenuItem {
    private static final Logger logger = LogManager.getLogger(MenuItemTreatSingleTemplate.class);

    public MenuItemTreatSingleTemplate(TreeItem<?> treeItem) {
        super("Сгенерировать     ");

        if (!(treeItem instanceof TemplateNode)) {
            return;
        }

        setOnAction(event -> {
            logger.trace("Выбрано контекстное меню 'Сгенерировать' для узла {}", treeItem.getClass().getSimpleName());
            try {
                new DescriptionGenerator().generate(((TemplateNode) treeItem));
            } catch (Exception e) {
                logger.error("Ошибка генерации описаний: {}", e.getMessage(), e);
                Dialogs.showMessage("Ошибка генерации описаний", e.getMessage());
            }
        });
    }
}
