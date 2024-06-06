package org.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


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

        try {
            loginStage.getIcons().add(new Image("file:icons/javaIcon.png"));
        } catch (IllegalArgumentException ex) {
            System.out.println("Icon path invalid!");
        }

        LoginController controller = fxmlLoader.getController();
        controller.setMainApp(this); // Injecting the main application reference

        loginStage.showAndWait();
    }

    public void showMainView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        Scene mainScene = new Scene(fxmlLoader.load(), 500, 500);

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

    public void showRegisterView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("register.fxml"));
        Scene registerScene = new Scene(fxmlLoader.load());

        Stage registerStage = new Stage();
        registerStage.initModality(Modality.APPLICATION_MODAL);
        registerStage.initOwner(mainStage);
        registerStage.setTitle("Register");
        registerStage.setScene(registerScene);

        try {
            registerStage.getIcons().add(new Image("file:icons/javaIcon.png"));
        } catch (IllegalArgumentException ex) {
            System.out.println("Icon path invalid!");
        }

        RegisterController controller = fxmlLoader.getController();
        controller.setMainApp(this); // Injecting the main application reference

        registerStage.showAndWait();
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