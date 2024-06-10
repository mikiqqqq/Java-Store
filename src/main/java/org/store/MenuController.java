package org.store;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.store.utils.StageManager;

import java.io.IOException;

public class MenuController {
    private final StageManager stageManager = Main.getStageManager();

    @FXML
    private Button backToMainButton;

    public void showAdminView() throws IOException {
        backToMainButton.setOpacity(1);
        stageManager.switchScene("/org/store/admin.fxml", "Admin View");
    }

    public void showCartView() throws IOException {
        backToMainButton.setOpacity(1);
        stageManager.switchScene("/org/store/cart.fxml", "Cart");
    }

    public void showContactDialog() throws IOException {
        stageManager.showDialog("/org/store/contact.fxml", "Contact");
    }

    public void showOrderHistoryView() throws IOException {
        backToMainButton.setOpacity(1);
        stageManager.switchScene("/org/store/orders.fxml", "Order History");
    }

    public void showSettingsDialog() throws IOException {
        stageManager.showDialog("/org/store/settings.fxml", "Settings");
    }

    public void showUserInfoView() throws IOException {
        backToMainButton.setOpacity(1);
        stageManager.switchScene("/org/store/user_information.fxml", "User Information");
    }

    public void backToMain() throws IOException {
        backToMainButton.setOpacity(0);
        Main.getMainApp().showMainView();
    }

    public void logOut() throws IOException {
        Main.getMainApp().showLoginDialog();
    }
}
