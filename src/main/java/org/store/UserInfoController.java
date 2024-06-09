package org.store;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.store.model.User;
import org.store.utils.UserSession;

public class UserInfoController {

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userLastNameLabel;

    @FXML
    private Label userEmailLabel;

    @FXML
    private Label userRoleLabel;

    @FXML
    public void initialize() {
        // Retrieve the current user from the UserSession
        User user = UserSession.getInstance().getUser();
        if (user != null) {
            updateUserInfo(user);
        }
    }

    // Method to update the labels with user information
    private void updateUserInfo(User user) {
        String[] fullNameParts = user.getFullName().split(" ");
        String firstName = fullNameParts.length > 0 ? fullNameParts[0] : "";
        String lastName = fullNameParts.length > 1 ? fullNameParts[1] : "";

        userNameLabel.setText(firstName);
        userLastNameLabel.setText(lastName);
        userEmailLabel.setText(user.getEmail());
        userRoleLabel.setText(user.getAuthorizationLevel());
    }
}