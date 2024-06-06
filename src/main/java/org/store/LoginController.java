package org.store;

import database.repository.UserRepo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.store.model.User;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private Label passwordErrorLabel;

    private Main mainApp;
    private Stage loginStage;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (validateInput(email, password)) {
            try {
                String hashedPassword = hashPasswordWithSalt(password, generateSalt(email));
                if (verifyUser(email, hashedPassword)) {
                    mainApp.setUserAuthorizationLevel(getAuthorizationLevel(email));
                    mainApp.showMainView();
                    loginStage.close();
                } else {
                    passwordErrorLabel.setText("Invalid email or password.");
                }
            } catch (SQLException | IOException e) {
                passwordErrorLabel.setText("Error connecting to the database.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRegister() {
        try {
            mainApp.showRegisterView(loginStage); // Call method to show the register dialog
        } catch (IOException e) {
            emailErrorLabel.setText("Error opening the register view.");
            e.printStackTrace();
        }
    }

    private boolean validateInput(String email, String password) {
        boolean valid = true;
        if (email.isEmpty() || !email.contains("@")) {
            emailErrorLabel.setText("Invalid email.");
            valid = false;
        } else {
            emailErrorLabel.setText("");
        }

        if (password.isEmpty()) {
            passwordErrorLabel.setText("Password cannot be empty.");
            valid = false;
        } else {
            passwordErrorLabel.setText("");
        }

        return valid;
    }

    // Concatenate the ASCII values of each character in the email and divide it with the total number of characters in the email
    private String generateSalt(String email) {
        StringBuilder asciiValues = new StringBuilder();
        for (char c : email.toCharArray()) {
            asciiValues.append((int) c);
        }
        BigInteger sumOfAsciiValues = new BigInteger(asciiValues.toString());
        return sumOfAsciiValues.divide(BigInteger.valueOf(email.length())).toString();
    }

    private String hashPasswordWithSalt(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPassword = salt + password;
            byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private boolean verifyUser(String email, String hashedPassword) throws SQLException, IOException {
        User user = UserRepo.getUserByEmail(email);
        return user != null && user.getPasswordHash().equals(hashedPassword);
    }

    private String getAuthorizationLevel(String email) throws SQLException, IOException {
        User user = UserRepo.getUserByEmail(email);
        return user != null ? user.getAuthorizationLevel() : null;
    }
}