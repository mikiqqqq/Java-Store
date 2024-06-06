package org.store.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Settings {
    private static final String SETTINGS_FILE_PATH = "src/main/resources/dat/settings.ini";
    private static final String SECTION_NAME = "Settings";
    private static final String THEME_KEY = "Theme";
    private static final String LANGUAGE_KEY = "Language";
    private static final String FONT_SIZE_KEY = "FontSize";

    private static final String DEFAULT_THEME = "Dark";
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String DEFAULT_FONT_SIZE = "16px";

    private static Ini ini;

    static {
        checkAndCreateIniFile();
    }

    private static void checkAndCreateIniFile() {
        try {
            File iniFile = new File(SETTINGS_FILE_PATH);
            if (!iniFile.exists()) {
                iniFile.getParentFile().mkdirs(); // Create directories if they do not exist
                iniFile.createNewFile(); // Create the file if it does not exist
                ini = new Ini(iniFile);
                ini.put(SECTION_NAME, THEME_KEY, DEFAULT_THEME);
                ini.put(SECTION_NAME, LANGUAGE_KEY, DEFAULT_LANGUAGE);
                ini.put(SECTION_NAME, FONT_SIZE_KEY, DEFAULT_FONT_SIZE);
                ini.store();
            } else {
                ini = new Ini(iniFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSettings(String theme, String language, String fontSize) {
        try {
            ini.put(SECTION_NAME, THEME_KEY, theme);
            ini.put(SECTION_NAME, LANGUAGE_KEY, language);
            ini.put(SECTION_NAME, FONT_SIZE_KEY, fontSize);
            ini.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
        applySettingsToOpenStages();
    }

    public static String getTheme() {
        return ini.get(SECTION_NAME, THEME_KEY, String.class);
    }

    public static String getLanguage() {
        return ini.get(SECTION_NAME, LANGUAGE_KEY, String.class);
    }

    public static String getFontSize() {
        return ini.get(SECTION_NAME, FONT_SIZE_KEY, String.class);
    }

    public static void applySettingsToOpenStages() {
        ObservableList<Stage> stages = Stage.getWindows().filtered(window -> window instanceof Stage).stream()
                .map(window -> (Stage) window)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        stages.forEach(stage -> {
            Scene scene = stage.getScene();
            if (scene != null) {
                applySettings(scene);
            }
        });
    }

    public static void applySettings(Scene scene) {
        try {
            String theme = getTheme();
            String language = getLanguage();
            String fontSize = getFontSize();

            // Apply language
            Locale locale = new Locale(language);
            ResourceBundle bundle = ResourceBundle.getBundle("bundles.language", locale);
            // Apply language
            if (scene.getUserData() instanceof URL fxmlUrl) {

                FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl, bundle);
                scene.setRoot(fxmlLoader.load());
            }

            // Apply font size
            scene.getRoot().setStyle("-fx-font-size: " + fontSize + ";");

            // Apply theme
            if (theme.equals("Dark")) {
                scene.getRoot().getStyleClass().remove("light-theme");
                scene.getRoot().getStyleClass().add("dark-theme");
            } else {
                scene.getRoot().getStyleClass().remove("dark-theme");
                scene.getRoot().getStyleClass().add("light-theme");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}