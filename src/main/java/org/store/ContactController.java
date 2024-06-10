package org.store;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

public class ContactController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField emailSubjectField;

    @FXML
    private TextArea messageField;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private Label emailSubjectErrorLabel;

    @FXML
    private Label messageErrorLabel;

    @FXML
    private Button sendButton;

    private final String API_KEY = "1ed21e8a716e73844873c65144f2330f";
    private final String API_SECRET = "a41f27e14b9d226e6d55629a76e8f1a3";
    private final String FROM_EMAIL = "suleivan@protonmail.com";
    private final String FROM_NAME = "Filip M";

    @FXML
    private void handleSend() {
        String recipientEmail = emailField.getText();
        String subject = emailSubjectField.getText();
        String message = messageField.getText();

        if (recipientEmail.isEmpty() || !isValidEmail(recipientEmail)) {
            showAlert("Validation Error", "Please enter a valid recipient email.");
            return;
        }

        if (subject.isEmpty()) {
            showAlert("Validation Error", "Subject cannot be empty.");
            return;
        }

        if (message.isEmpty()) {
            showAlert("Validation Error", "Message cannot be empty.");
            return;
        }

        try {
            sendEmail(recipientEmail, subject, message);
            showAlert("Success", "Email sent successfully.");
        } catch (Exception e) {
            showAlert("Error", "Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) sendButton.getScene().getWindow();
        stage.close();
    }

    private void sendEmail(String recipientEmail, String subject, String message) throws Exception {
        MailjetClient client = new MailjetClient(API_KEY, API_SECRET);
        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", FROM_EMAIL)
                                        .put("Name", FROM_NAME))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", recipientEmail)
                                                .put("Name", "Recipient Name")))
                                .put(Emailv31.Message.SUBJECT, subject)
                                .put(Emailv31.Message.TEXTPART, message)
                                .put(Emailv31.Message.CUSTOMID, "AppGettingStartedTest")));

        MailjetResponse response = client.post(request);
        if (response.getStatus() != 200) {
            throw new Exception("Failed to send email: " + response.getData());
        } else {
            System.out.println(response.getData());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}