package Controller;

import Main.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

public class ProfileController {

    private String oldLastName;
    private String oldFirstName;
    private String oldPhoneNumber;
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label errorLabel;

    @FXML
    void backButtonClicked(MouseEvent event) throws IOException {
        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/mainCustomer.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void addressClicked(MouseEvent event) throws IOException {
        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/addressList.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void ordersClicked(MouseEvent event) throws IOException {
        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/orders.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    void saveButtonClicked(ActionEvent event) {
        errorLabel.setText("");
        errorLabel.setStyle("-fx-text-fill: red");
        if(isNullInformation()) {
            errorLabel.setText("Please type in all information");
            return;
        }
        if(isSpaceInformation()) {
            errorLabel.setText("All information must contain letter(s)");
            return;
        }
        if(!isValidPhoneNumber()) {
            errorLabel.setText("Invalid phone number");
            return;
        }

        Main.USER.setFirstName(normalize(firstNameField.getText()));
        Main.USER.setLastName(normalize(lastNameField.getText()));
        Main.USER.setPhoneNumber(phoneNumberField.getText());

        if(isChange()) {
            System.out.println("changed");

            oldFirstName = Main.USER.getFirstName();
            oldLastName = Main.USER.getLastName();
            oldPhoneNumber = Main.USER.getPhoneNumber();

            Connection connectionDB = DatabaseConnection.getInstance();

            String updateQuery = "UPDATE customers SET customerFirstName = '" + Main.USER.getFirstName() + "'," +
                    " customerLastName = '" + Main.USER.getLastName() + "', phoneNumbers = '" + phoneNumberField.getText() + "'" +
                    " WHERE customerNumber = " + Main.USER.getCustomerNumber();

            errorLabel.setStyle("-fx-text-fill: green");
            errorLabel.setText("Updated");

            try {
                Statement statement = connectionDB.createStatement();
                statement.execute(updateQuery);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String normalize(String s) {
        while(!s.isEmpty() && s.charAt(0) == ' ') s = s.substring(1);
        while(!s.isEmpty() && s.charAt(s.length() - 1) == ' ') s = s.substring(0, s.length() - 1);
        return s;
    }

    private boolean isChange() {
        return !(Main.USER.getFirstName().equals(oldFirstName) &&
                Main.USER.getLastName().equals(oldLastName) &&
                Main.USER.getPhoneNumber().equals(oldPhoneNumber));
    }

    private boolean isNullInformation() {
        return (firstNameField.getText().isEmpty() ||
                lastNameField.getText().isEmpty() ||
                phoneNumberField.getText().isEmpty());
    }

    private boolean isValidPhoneNumber() {
        String phoneNumber = phoneNumberField.getText();
        if(phoneNumber.length() != 10
                || phoneNumber.charAt(0) != '0'
                || phoneNumber.charAt(1) == '0') {
            return false;
        } else return true;
    }

    private boolean isSpaceInformation() {
        return (firstNameField.getText().isBlank() ||
                lastNameField.getText().isBlank() ||
                phoneNumberField.getText().isBlank());
    }

    public void setData() {
        errorLabel.setText("");
        errorLabel.setStyle("-fx-text-fill: red");
        firstNameField.setText(Main.USER.getFirstName());
        lastNameField.setText(Main.USER.getLastName());
        phoneNumberField.setText(Main.USER.getPhoneNumber());
        userNameLabel.setText(Main.USER.getUserName());

        oldFirstName = firstNameField.getText();
        oldLastName = lastNameField.getText();
        oldPhoneNumber = phoneNumberField.getText();
    }
}
