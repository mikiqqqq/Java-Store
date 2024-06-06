package org.store;

import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;

public class RegisterController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label fullNameErrorLabel;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Label confirmPasswordErrorLabel;

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (validateInput(fullName, email, password, confirmPassword)) {
            try {
                String salt = generateSalt(email);
                String hashedPassword = hashPasswordWithSalt(password, salt);
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setPasswordHash(salt + "$" + hashedPassword); // Store salt with hash
                newUser.setAuthorizationLevel("USER"); // Default authorization level

                Database.addUser(newUser);
                mainApp.showLoginView(); // Return to login view
            } catch (SQLException | IOException e) {
                confirmPasswordErrorLabel.setText("Error connecting to the database.");
                e.printStackTrace();
            }
        }
    }

    private boolean validateInput(String fullName, String email, String password, String confirmPassword) {
        boolean valid = true;

        if (fullName.isEmpty()) {
            fullNameErrorLabel.setText("Full name cannot be empty.");
            valid = false;
        } else {
            fullNameErrorLabel.setText("");
        }

        if (email.isEmpty() || !email.contains("@")) {
            emailErrorLabel.setText("Invalid email.");
            valid = false;
        } else {
            try {
                if (Database.getUserByEmail(email) != null) {
                    emailErrorLabel.setText("Email already exists.");
                    valid = false;
                } else {
                    emailErrorLabel.setText("");
                }
            } catch (SQLException | IOException e) {
                emailErrorLabel.setText("Error checking email.");
                valid = false;
            }
        }
        if (password.isEmpty()) {
            passwordErrorLabel.setText("Password cannot be empty.");
            valid = false;
        } else {
            passwordErrorLabel.setText("");
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordErrorLabel.setText("Passwords do not match.");
            valid = false;
        } else {
            confirmPasswordErrorLabel.setText("");
        }

        return valid;
    }

    // Concatenate the ASCII values of each character in the email and divide it with the total number of characters in the email
    private String generateSalt(String email) {
        StringBuilder asciiValues = new StringBuilder();
        for (char c : email.toCharArray()) {
            asciiValues.append((int) c);
        }
        long sumOfAsciiValues = Long.parseLong(asciiValues.toString());
        return String.valueOf(sumOfAsciiValues / email.length());
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
}