package org.store.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import org.store.Main;
import org.store.model.Settings;

import java.util.Locale;
import java.util.ResourceBundle;

public class SettingsController {

    @FXML
    private ToggleButton themeToggleButton;

    @FXML
    private ChoiceBox<String> languageChoiceBox;

    @FXML
    private ChoiceBox<String> fontSizeChoiceBox;

    private Main mainApp;

    @FXML
    private void initialize() {
        // Load current settings
        loadCurrentSettings();

        // Add listeners to update settings
        themeToggleButton.setOnAction(event -> updateSettings());
        languageChoiceBox.setOnAction(event -> updateSettings());
        fontSizeChoiceBox.setOnAction(event -> updateSettings());
    }

    private void loadCurrentSettings() {
        String theme = Settings.getTheme();
        String language = Settings.getLanguage();
        String fontSize = Settings.getFontSize();

        themeToggleButton.setSelected("Dark".equals(theme));
        languageChoiceBox.setValue(language);
        fontSizeChoiceBox.setValue(fontSize);
    }

    private void updateSettings() {
        String theme = themeToggleButton.isSelected() ? "Dark" : "Light";
        String language = languageChoiceBox.getValue();
        String fontSize = fontSizeChoiceBox.getValue();

        Settings.saveSettings(theme, language, fontSize);

        // Apply language change
        Locale locale = new Locale(language.equals("Croatian") ? "hr" : "en");
        ResourceBundle bundle = ResourceBundle.getBundle("org.store.bundle.MyBundle", locale);
        Main.getStage().getScene().setUserData(bundle);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
}
