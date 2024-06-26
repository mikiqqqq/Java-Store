package org.store;

import database.repository.UserRepo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.store.model.User;
import org.store.utils.UserSession;

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

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (validateInput(email, password)) {
            try {
                String hashedPassword = hashPasswordWithSalt(password, generateSalt(email));
                if (verifyUser(email, hashedPassword)) {
                    User user = UserRepo.getUserByEmail(email);
                    System.out.println(user.getId());
                    UserSession.getInstance().setUser(user);
                    Main.getMainApp().showMainView();
                    Stage loginStage = (Stage) emailField.getScene().getWindow();
                    loginStage.close();
                } else {
                    passwordErrorLabel.setText("Invalid email or password.");
                }
            } catch (SQLException | IOException e) {
                passwordErrorLabel.setText("Error connecting to the database.");
                System.err.println("Error connecting to the database: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRegister() {
        try {
            Main.getMainApp().showRegisterDialog();
        } catch (IOException e) {
            emailErrorLabel.setText("Error opening the register view.");
            e.printStackTrace();
        }
    }

    private boolean validateInput(String email, String password) {
        boolean valid = true;
        if (!email.contains("@")) {
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
}