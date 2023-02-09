package Controller;

import Entity.Baker;
import Entity.Customer;
import Main.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import Main.DatabaseConnection;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class SignInController implements Initializable {

    @FXML
    private Label errorLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField userNameField;

    @FXML
    private CheckBox rememberAccountCheckBox;

    @FXML
    void signUpButtonClicked(ActionEvent event) throws IOException {
        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/signUp.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void signInButtonClicked(ActionEvent event) {
        errorLabel.setText("");
        String userName = Algorithm.normalize(userNameField.getText());
        String password = passwordField.getText();

        if(userName.equals("") || password.equals("")) {
            errorLabel.setText("All information must contain letter(s)");
            return;
        }

        if(isCustomerUserName(userName)) customerSignIn(userName, password, event);
        else bakerSignIn(userName, password, event);
    }

    private void customerSignIn(String userName, String password, ActionEvent event) {
        Connection connectionDB = DatabaseConnection.getInstance();
        String connectQuery = "SELECT * FROM customers c INNER JOIN shoppingCarts sc ON c.customerNumber = sc.cartNumber" +
                " WHERE c.userName = '" + userName + "';";
        try {
            Statement statement = connectionDB.createStatement();
            ResultSet output = statement.executeQuery(connectQuery);
            while(output.next()) {

                // Salt + Provided Password order
                String databasePassword = Algorithm.getHashedString( output.getString("salt") + password);

                if(!databasePassword.equals(output.getString("passwords"))) {
                    errorLabel.setText("Incorrect password");
                } else {
                    // Set information
                    Main.USER = new Customer(output.getInt("customerNumber"), output.getString("userName"),
                            output.getString("customerFirstName"), output.getString("customerLastName"),
                            output.getString("phoneNumbers"), output.getInt("defaultAddress"));
                    Main.USER.setTotalQuantity(output.getInt("totalQuantity"));

                    if(rememberAccountCheckBox.isSelected()) rememberAccount(userName, password);
                    else rememberAccount("", "");

                    // Switch scene
                    Parent root = FXMLLoader.load(getClass().getResource("/views/mainCustomer.fxml"));
                    Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
                    stage.setResizable(false);
                    stage.setScene(scene);
                    stage.show();
                }
                return;
            }
            errorLabel.setText("Incorrect user name or password");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void bakerSignIn(String userName, String password, ActionEvent event) {
        Connection connectionDB = DatabaseConnection.getInstance();
        String connectQuery = "SELECT * FROM bakers b WHERE b.userName = '" + userName + "';";
        try {
            Statement statement = connectionDB.createStatement();
            ResultSet output = statement.executeQuery(connectQuery);
            while(output.next()) {

                // Salt + Provided Password order
                String databasePassword = Algorithm.getHashedString( output.getString("salt") + password);

                if(!databasePassword.equals(output.getString("password"))) {
                    errorLabel.setText("Incorrect password");
                } else {
                    // Set information
                    Main.BAKER = new Baker(output.getInt("bakerNumber"), output.getString("userName"),
                            output.getString("bakerName"));

                    if(rememberAccountCheckBox.isSelected()) rememberAccount(userName, password);
                    else rememberAccount("", "");

                    // Switch scene
                    Parent root = FXMLLoader.load(getClass().getResource("/views/mainBaker.fxml"));
                    Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
                    stage.setResizable(false);
                    stage.setScene(scene);
                    stage.show();
                }
                return;
            }
            errorLabel.setText("Incorrect user name or password");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isCustomerUserName(String userName) {
        if(userName.length() < 5) return true;
        return !userName.substring(0, 5).equals("baker");
    }

    public void setData(String userName, String password) {
        userNameField.setText(userName);
        passwordField.setText(password);
    }

    private void rememberAccount(String userName, String password) {
        try {
//            FileWriter writer = new FileWriter(new File(
//                    "src/main/resources/account.txt").toURI().toString()
//            );
            FileWriter writer = new FileWriter("E:/Applicaition/src/main/resources/account.txt");
            writer.write(userName + "\n");
            writer.write(password + "\n");
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rememberAccountCheckBox.setSelected(true);

        try {
            BufferedReader reader = new BufferedReader(new FileReader("E:/Applicaition/src/main/resources/account.txt"));
            setData(reader.readLine(), reader.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}