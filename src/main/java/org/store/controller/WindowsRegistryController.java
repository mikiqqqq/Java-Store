package org.store.controller;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class WindowsRegistryController {

    private static final Logger logger = LoggerFactory.getLogger(WindowsRegistryController.class);

    public static void writeStringValue(String keyPath, String valueName, String value) {
        try {
            Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, keyPath);
            Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, keyPath, valueName, value);
            System.out.println("Value written to registry successfully.");
            logger.info("Value written to registry successfully.");
        } catch (Exception e) {
            System.err.println("Error writing to registry: " + e.getMessage());
            logger.error("Error writing to registry: " + e.getMessage());
        }
    }

    public static String readStringValue(String keyPath, String valueName) {
        try {
            return Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, keyPath, valueName);
        } catch (Exception e) {
            System.err.println("Error reading from registry: " + e.getMessage());
            logger.error("Error reading from registry: " + e.getMessage());
            return null;
        }
    }

    public static void writeRegistryValuesFromProperties() {
        String keyPath = "SOFTWARE\\MyApp";
        Properties properties = new Properties();

        try (FileReader reader = new FileReader("dat/database.properties")) {
            properties.load(reader);

            String databaseURL = properties.getProperty("databaseURL");
            String databaseUsername = properties.getProperty("databaseUsername");
            String databasePassword = properties.getProperty("databasePassword");

            writeStringValue(keyPath, "databaseURL", databaseURL);
            writeStringValue(keyPath, "databaseUsername", databaseUsername);
            writeStringValue(keyPath, "databasePassword", databasePassword);
        } catch (IOException e) {
            logger.error("Error reading from properties file: ", e);
        }
    }
}