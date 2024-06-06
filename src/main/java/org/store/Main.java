package org.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import org.store.model.StageManager;

import java.io.IOException;

public class Main extends Application {

    private static Stage mainStage;
    private StageManager stageManager;
    private String userAuthorizationLevel;
    public static Main mainApp;

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
        showLoginView();
    }

    public void showLoginView() throws IOException {
        LoginController.setMainApp(mainApp);
        stageManager.showDialog("/org/store/login.fxml", "Login", mainStage);
    }

    public void showMainView() throws IOException {
        MainController.setMainApp(mainApp);
        stageManager.switchScene("/org/store/main.fxml", "Java Application");
    }

    public void showRegisterView(Stage loginStage) throws IOException {
        stageManager.showDialog("/org/store/register.fxml", "Register", loginStage);
    }

    public void showAdminView() throws IOException {
        stageManager.showDialog("/org/store/admin.fxml", "Admin View", mainStage);
    }

    public void showCartView() throws IOException {
        stageManager.showDialog("/org/store/cart.fxml", "Cart", mainStage);
    }

    public void showContactDialog() throws IOException {
        stageManager.showDialog("/org/store/contact.fxml", "Contact", mainStage);
    }

    public void showOrderHistoryView() throws IOException {
        stageManager.showDialog("/org/store/orders.fxml", "Order History", mainStage);
    }

    public void showSettingsDialog() throws IOException {
        stageManager.showDialog("/org/store/settings.fxml", "Settings", mainStage);
    }

    public void showUserInfoView() throws IOException {
        stageManager.showDialog("/org/store/user_information.fxml", "User Information", mainStage);
    }

    public void setUserAuthorizationLevel(String level) {
        this.userAuthorizationLevel = level;
    }

    public String getUserAuthorizationLevel() {
        return userAuthorizationLevel;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getStage() {
        return mainStage;
    }
}
