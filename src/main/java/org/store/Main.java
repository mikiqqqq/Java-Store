package org.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import org.store.model.StageManager;

import java.io.IOException;

public class Main extends Application {

    private static Stage mainStage;
    private static Main mainApp;
    private static StageManager stageManager;

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    @Override
    public void start(Stage stage) throws IOException {
        if (isWindows()) {
            WindowsRegistryController.writeRegistryValuesFromProperties();
        }

        mainStage = stage;
        mainApp = this;
        stageManager = new StageManager(stage);
        showLoginDialog();
    }

    public void showLoginDialog() throws IOException {
        closeCurrentStage();
        stageManager.showDialog("/org/store/login.fxml", "Login");
    }

    public void showMainView() throws IOException {
        stageManager.switchScene("/org/store/main.fxml", "Java Application");
    }

    public void showRegisterDialog() throws IOException {
        stageManager.showDialog("/org/store/register.fxml", "Register");
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Main getMainApp() { return mainApp; }

    public static StageManager getStageManager() { return stageManager; }

    private void closeCurrentStage() {
        if (mainStage != null) {
            mainStage.close();
        }
    }
}
