package org.store.utils;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class StageManager {

    private final Stage primaryStage;
    private final Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/logo.png")));
    public StageManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void switchScene(String fxmlPath, String title) {
        URL fxmlUrl = getClass().getResource(fxmlPath);

        Scene scene = Settings.applySettings(fxmlUrl, null);
        scene.setUserData(fxmlUrl); // Store the FXML URL in user data

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }

    public void showDialog(String fxmlPath, String title) {
        URL fxmlUrl = getClass().getResource(fxmlPath);

        Scene scene = Settings.applySettings(fxmlUrl, null);
        scene.setUserData(fxmlUrl); // Store the FXML URL in user data

        Stage dialogStage = new Stage();
        dialogStage.getIcons().add(icon);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(primaryStage);
        dialogStage.setTitle(title);
        dialogStage.setScene(scene);

        dialogStage.showAndWait();
    }
}
