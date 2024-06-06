package org.store.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class StageManager {

    private final Stage primaryStage;

    public StageManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void switchScene(String fxmlPath, String title) throws IOException {
        Locale locale = new Locale(Settings.getLanguage());
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.language", locale);
        URL fxmlUrl = getClass().getResource(fxmlPath);
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl, bundle);

        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);

        scene.setUserData(fxmlUrl); // Store the FXML URL in user data
        Settings.applySettings(scene);

        primaryStage.show();
    }

    public void showDialog(String fxmlPath, String title, Stage owner) throws IOException {
        Locale locale = new Locale(Settings.getLanguage());
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.language", locale);
        URL fxmlUrl = getClass().getResource(fxmlPath);
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl, bundle);

        Scene scene = new Scene(fxmlLoader.load());
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(owner);
        dialogStage.setTitle(title);
        dialogStage.setScene(scene);

        scene.setUserData(fxmlUrl); // Store the FXML URL in user data
        Settings.applySettings(scene);

        dialogStage.showAndWait();
    }
}
