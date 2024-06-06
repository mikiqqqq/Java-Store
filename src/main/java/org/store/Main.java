package org.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.store.model.Settings;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;


public class Main extends Application {

    private static Stage mainStage;
    private String userAuthorizationLevel;

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

        showLoginView();
    }

    public void showLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene loginScene = new Scene(fxmlLoader.load());
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.initOwner(mainStage);
        loginStage.setTitle("Login");
        loginStage.setScene(loginScene);

        Settings.applySettings(loginScene);

        LoginController controller = fxmlLoader.getController();
        loginScene.setUserData(controller);
        controller.setMainApp(this);
        controller.setLoginStage(loginStage);

        loginStage.showAndWait();
    }

    public void showMainView() throws IOException {
        Locale locale = new Locale(Settings.getLanguage());
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.language", locale);
        URL fxmlUrl = Main.class.getResource("main.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl, bundle);
        Scene mainScene = new Scene(fxmlLoader.load());


        mainStage.setTitle("Java Application");
        mainStage.setScene(mainScene);

        MainController controller = fxmlLoader.getController();
        mainScene.setUserData(controller); // Store the controller in user data
        controller.setMainApp(this); // Injecting the main application reference


        mainScene.setUserData(controller); // Store the controller in user data

        // Apply settings before storing the controller
        Settings.applySettings(mainScene);

        mainStage.show();
    }

    public void showRegisterView(Stage loginStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("register.fxml"));
        Scene registerScene = new Scene(fxmlLoader.load());

        Stage registerStage = new Stage();
        registerStage.initModality(Modality.APPLICATION_MODAL);
        registerStage.initOwner(loginStage);
        registerStage.setTitle("Register");
        registerStage.setScene(registerScene);

        Settings.applySettings(registerScene);

        RegisterController controller = fxmlLoader.getController();
        controller.setMainApp(this);
        controller.setRegisterStage(registerStage);
        controller.setLoginStage(loginStage);

        registerStage.showAndWait();
    }

    public void showAdminView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("admin.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        Settings.applySettings(scene);
        stage.setTitle("Admin View");
        stage.show();
    }

    public void showCartView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("cart.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        Settings.applySettings(scene);
        stage.setTitle("Cart");
        stage.show();
    }

    public void showContactDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("contact.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        Settings.applySettings(scene);
        stage.setTitle("Contact");
        stage.show();
    }

    public void showOrderHistoryView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("orders.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        Settings.applySettings(scene);
        stage.setTitle("Order History");
        stage.show();
    }

    public void showSettingsDialog() throws IOException {
        Locale locale = new Locale(Settings.getLanguage());
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.language", locale);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("settings.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);

        scene.setUserData(Main.class.getResource("settings.fxml"));

        Settings.applySettings(scene);
        stage.setTitle("Settings");
        stage.show();
    }

    public void showUserInfoView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("user_information.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        Settings.applySettings(scene);
        stage.setTitle("User Information");
        stage.show();
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

    public static Stage getStage(){
        return mainStage;
    }
}