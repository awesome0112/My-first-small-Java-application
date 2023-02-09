package Controller;

import Main.DatabaseConnection;
import Main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

public class AddressController {

    private int addressId;

    @FXML
    private Label generalAddress;

    @FXML
    private Label specificAddress;

    @FXML
    private Label defaultLabel;

    @FXML
    void setDAButtonClicked(ActionEvent event) throws IOException {
        Connection connectionDB = DatabaseConnection.getInstance();
        String updateQuery = "UPDATE customers SET defaultAddress = " + addressId +
                " WHERE customerNumber = " + Main.USER.getCustomerNumber();

        try {
            Statement statement = connectionDB.createStatement();
            statement.execute(updateQuery);
        } catch(Exception e) {
            e.printStackTrace();
        }

        Main.USER.getDefaultAddress().setId(addressId);

        // Refresh scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/addressList.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void setData(String generalAddress, String specificAddress, int addressID) {
        this.generalAddress.setText(generalAddress);
        this.specificAddress.setText(specificAddress);
        this.addressId = addressID;
        if(addressID == Main.USER.getDefaultAddress().getId()) defaultLabel.setText("(Default)");
        else defaultLabel.setText("");
    }

}
