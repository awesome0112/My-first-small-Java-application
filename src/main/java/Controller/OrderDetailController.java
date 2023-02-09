package Controller;

import Entity.Address;
import Entity.Order;
import Main.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

public class OrderDetailController {

    private int orderNumber;

    @FXML
    private Label orderDate;

    @FXML
    private Label shippedDate;

    @FXML
    private Label fullAddress;

    @FXML
    private Label status;

    @FXML
    private Label totalPayment;

    @FXML
    private Button receivedButton;

    @FXML
    void receivedButtonClicked(ActionEvent event) throws IOException {
        Connection connectionDb = DatabaseConnection.getInstance();
        String updateQuery = "UPDATE orders SET status = 'Received' WHERE orderNumber = " + this.orderNumber;

        try {
            Statement statement = connectionDb.createStatement();
            statement.execute(updateQuery);
            updateQuery = "UPDATE orders SET shippedDate = NOW() WHERE orderNumber = " + orderNumber;
            statement.execute(updateQuery);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Refresh scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/orders.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void setData(Order order) {
        orderNumber = order.getOrderNumber();
        orderDate.setText(order.getOrderDate());
        status.setText("(" + order.getStatus() + ")");
        totalPayment.setText("$" + order.getTotalPayment());

        Address tmpAddress = order.getAddress();
        String fullAddress = tmpAddress.getSpecificAddress() + ", " + tmpAddress.getWard() + ", " +
                tmpAddress.getDistrict() + ", " + tmpAddress.getProvince();
        this.fullAddress.setText(fullAddress);

        if(!order.getStatus().equals("Delivering")) {
            receivedButton.setDisable(true);
        }
        if(order.getShippedDate() != null) shippedDate.setText(order.getShippedDate());
        else shippedDate.setText("");

    }

}
