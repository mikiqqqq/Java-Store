package org.store.model;

import javafx.scene.Scene;
import org.ini4j.Ini;
import org.store.Main;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Settings {
    private static final String SETTINGS_FILE_PATH = "dat/settings.ini";
    private static final String SECTION_NAME = "Settings";
    private static final String THEME_KEY = "Theme";
    private static final String LANGUAGE_KEY = "Language";
    private static final String FONT_SIZE_KEY = "FontSize";

    private static final String DEFAULT_THEME = "Light";
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String DEFAULT_FONT_SIZE = "14px";

    private static Ini ini;

    static {
        checkAndCreateIniFile();
    }

    private static void checkAndCreateIniFile() {
        try {
            File iniFile = new File(SETTINGS_FILE_PATH);
            if (!iniFile.exists()) {
                iniFile.getParentFile().mkdirs();
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
        applySettings();
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

    public static void applySettings() {
        try {
            String theme = getTheme();
            String language = getLanguage();
            String fontSize = getFontSize();

            // Apply theme
            Scene scene = Main.getStage().getScene();
            if (theme.equals("Dark")) {
                scene.getStylesheets().add(Settings.class.getResource("/styles/dark-theme.css").toExternalForm());
            } else {
                scene.getStylesheets().add(Settings.class.getResource("/styles/light-theme.css").toExternalForm());
            }

            // Apply language
            Locale locale = new Locale(language);
            ResourceBundle bundle = ResourceBundle.getBundle("bundles.MyBundle", locale);
            // Update all UI components with the new bundle (implementation depends on your UI structure)

            // Apply font size
            scene.getRoot().setStyle("-fx-font-size: " + fontSize + ";");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}