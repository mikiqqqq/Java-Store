package org.store.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.store.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class StageManager {

    private final Stage primaryStage;
    public StageManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void switchScene(String fxmlPath, String title) throws IOException {
        URL fxmlUrl = getClass().getResource(fxmlPath);

        Scene scene = Settings.applySettings(fxmlUrl, null);
        scene.setUserData(fxmlUrl); // Store the FXML URL in user data

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showDialog(String fxmlPath, String title) throws IOException {
        URL fxmlUrl = getClass().getResource(fxmlPath);

        Scene scene = Settings.applySettings(fxmlUrl, null);
        scene.setUserData(fxmlUrl); // Store the FXML URL in user data

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(primaryStage);
        dialogStage.setTitle(title);
        dialogStage.setScene(scene);

        dialogStage.showAndWait();
    }
}
