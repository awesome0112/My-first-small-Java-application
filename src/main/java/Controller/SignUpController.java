package Controller;

import Entity.Address;
import Entity.Customer;
import Main.Algorithm;
import Main.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.xml.transform.Source;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SignUpController {

    @FXML
    private Label errorLabel;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField userNameField;

    @FXML
    void signUpButtonClicked(ActionEvent event) throws IOException {
        errorLabel.setText("");
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
        if(!isValidPassword()) return;
        if(!passwordField.getText().equals(confirmPasswordField.getText())) {
            errorLabel.setText("Password and confirm password does not match");
            return;
        }

        Connection connectionDB = DatabaseConnection.getInstance();

        try {
            String userName = Algorithm.normalize(userNameField.getText());
            Statement statement = connectionDB.createStatement();
            String checkUserNameQuery = "SELECT userName FROM customers WHERE userName = '" + userName + "';";
            ResultSet outPut = statement.executeQuery(checkUserNameQuery);

            if(!isValidUserName(userName) || outPut.next() != false) {
                errorLabel.setText("Sorry! User name has already been taken");
                return;
            }

            String databaseSalt = Algorithm.randomString();
            // Salt + Provided Password order
            String databasePassword = Algorithm.getHashedString(databaseSalt + passwordField.getText());

            String insertCustomerQuery = "INSERT INTO customers (customerFirstName, customerLastName, phoneNumbers, userName, passwords, salt) " +
                    "VALUES ('" + Algorithm.normalize(firstNameField.getText()) + "', '" + Algorithm.normalize(lastNameField.getText()) + "', '" +
                    phoneNumberField.getText() + "', '" + userName + "', '" + databasePassword + "', '" + databaseSalt + "');";
            String insertShoppingCartQuery = "INSERT INTO shoppingCarts (totalQuantity) VALUES (0);";
            statement.execute(insertCustomerQuery);
            statement.execute(insertShoppingCartQuery);

            //Switch to sign in scene
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/signIn.fxml"));
            AnchorPane pane = null;
            try {
                pane = loader.load();
                SignInController controller = loader.getController();
                controller.setData(userName, passwordField.getText());
                Scene scene = new Scene(pane);
                scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch(Exception e) {
                e.printStackTrace();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void backToSignInButtonClicked(ActionEvent event) throws IOException {
        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/signIn.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private boolean isValidUserName(String userName) {
        if(userName.length() < 5) return true;
        return !userName.substring(0, 5).equals("baker");
    }

    private boolean isValidPhoneNumber() {
        String phoneNumber = phoneNumberField.getText();
        if(phoneNumber.length() != 10
                || phoneNumber.charAt(0) != '0'
                || phoneNumber.charAt(1) == '0') {
            return false;
        } else return true;
    }

    private boolean isValidPassword() {
        String password = passwordField.getText();
        boolean checkLetter = false;
        boolean checkNumber = false;
        boolean checkCharacter = false;
        for(int i = 0; i < password.length(); i++) {
            char tmp = password.charAt(i);
            if(tmp >= '0' && tmp <= '9') checkNumber = true;
            else if((tmp >= 'a' && tmp <= 'z' || tmp >= 'A' && tmp <= 'Z')) checkLetter = true;
            else checkCharacter = true;
        }
        if(!(checkNumber && checkLetter && checkCharacter)) {
            errorLabel.setText("Password must contain letter(s), number(s), character(s)");
            return false;
        } return true;
    }

    private boolean isNullInformation() {
        return (firstNameField.getText().isEmpty() ||
                lastNameField.getText().isEmpty() ||
                userNameField.getText().isEmpty() ||
                phoneNumberField.getText().isEmpty() ||
                passwordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty());
    }

    private boolean isSpaceInformation() {
        return (firstNameField.getText().isBlank() ||
                lastNameField.getText().isBlank() ||
                userNameField.getText().isBlank() ||
                phoneNumberField.getText().isBlank() ||
                passwordField.getText().isBlank() ||
                confirmPasswordField.getText().isBlank());
    }
}