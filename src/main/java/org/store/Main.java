package org.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.store.controller.LoginController;
import org.store.controller.MainController;
import org.store.controller.RegisterController;
import org.store.controller.WindowsRegistryController;
import org.store.model.Settings;

import java.io.IOException;
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
        applySettings();

        showLoginView();
    }

    private void applySettings() {
        String theme = Settings.getTheme();
        String themeFile = "light-theme.css";
        if ("Dark".equals(theme)) {
            themeFile = "dark-theme.css";
        }
        mainStage.getScene().getStylesheets().add(getClass().getResource(themeFile).toExternalForm());

        // Apply font size
        String fontSize = Settings.getFontSize();
        if (fontSize != null) {
            mainStage.getScene().getRoot().setStyle("-fx-font-size: " + fontSize);
        }

        // Apply language
        String language = Settings.getLanguage();
        Locale locale = new Locale(language.equals("Croatian") ? "hr" : "en");
        ResourceBundle bundle = ResourceBundle.getBundle("org.store.bundle.MyBundle", locale);

        mainStage.getScene().setUserData(bundle);
    }


    public void showLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene loginScene = new Scene(fxmlLoader.load());

        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.initOwner(mainStage);
        loginStage.setTitle("Login");
        loginStage.setScene(loginScene);

        // Set the application icon for the login stage
        try {
            loginStage.getIcons().add(new Image("file:icons/javaIcon.png"));
        } catch (IllegalArgumentException ex) {
            System.out.println("Icon path invalid!");
        }

        LoginController controller = fxmlLoader.getController();
        controller.setMainApp(this);
        controller.setLoginStage(loginStage);

        loginStage.showAndWait();
    }

    public void showMainView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        Scene mainScene = new Scene(fxmlLoader.load());

        System.out.println(fxmlLoader);

        mainStage.setTitle("Java Application");
        mainStage.setScene(mainScene);

        try {
            mainStage.getIcons().add(new Image("file:icons/javaIcon.png"));
        } catch (IllegalArgumentException ex) {
            System.out.println("Icon path invalid!");
        }

        MainController controller = fxmlLoader.getController();
        controller.setMainApp(this); // Injecting the main application reference

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

        // Set the application icon for the register stage
        try {
            registerStage.getIcons().add(new Image("file:icons/javaIcon.png"));
        } catch (IllegalArgumentException ex) {
            System.out.println("Icon path invalid!");
        }

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
        stage.setTitle("Admin View");
        stage.show();
    }

    public void showCartView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("cart.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Cart");
        stage.show();
    }

    public void showContactDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("contact.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Contact");
        stage.show();
    }

    public void showOrderHistoryView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("orders.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Order History");
        stage.show();
    }

    public void showSettingsDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("settings.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Settings");
        stage.show();
    }

    public void showUserInfoView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("user_information.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
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