package org.store;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
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

        // Set the application icon for the login stage
        try {
            loginStage.getIcons().add(new Image("file:icons/javaIcon.png"));
        } catch (IllegalArgumentException ex) {
            System.out.println("Icon path invalid!");
        }

        LoginController controller = fxmlLoader.getController();
        controller.setMainApp(this);

        loginStage.showAndWait();
    }

    public void showMainView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
            Scene mainScene = new Scene(fxmlLoader.load(), 500, 500);

            mainStage.setTitle("Java Application");
            mainStage.setScene(mainScene);

            // Set the application icon for the main stage
            try {
                mainStage.getIcons().add(new Image("file:icons/javaIcon.png"));
            } catch (IllegalArgumentException ex) {
                System.out.println("Icon path invalid!");
            }

            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getStage(){
        return mainStage;
    }
}