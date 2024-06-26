package org.store;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import org.store.utils.Settings;


public class SettingsController {

    @FXML
    private ToggleButton themeToggleButton;

    @FXML
    private ChoiceBox<String> languageChoiceBox;

    @FXML
    private ChoiceBox<String> fontSizeChoiceBox;

    @FXML
    private Button closeButton;

    @FXML
    void initialize() {
        // Initialize choice boxes and toggle button with current settings
        themeToggleButton.setSelected(Settings.getTheme().equals("Dark"));

        // Add items to the language choice box
        languageChoiceBox.getItems().addAll("English", "Croatian");
        languageChoiceBox.setValue(Settings.getLanguage().equals("en") ? "English" : "Croatian");

        // Add items to the font size choice box
        fontSizeChoiceBox.getItems().addAll("12px", "14px", "16px");
        fontSizeChoiceBox.setValue(Settings.getFontSize());

        themeToggleButton.setOnAction(_ -> updateSettings());
        languageChoiceBox.setOnAction(_ -> updateSettings());
        fontSizeChoiceBox.setOnAction(_ -> updateSettings());
    }

    private void updateSettings() {
        String theme = themeToggleButton.isSelected() ? "Dark" : "Light";
        String language = languageChoiceBox.getValue().equals("English") ? "en" : "hr";
        String fontSize = fontSizeChoiceBox.getValue();

        Settings.saveSettings(theme, language, fontSize);
    }

    @FXML
    private void closeSettingsDialog() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}