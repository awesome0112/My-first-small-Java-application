package Controller;

import Entity.Address;
import Entity.Order;
import Main.DatabaseConnection;
import Main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

public class BakerOrderDetailController {

    private int orderNumber;

    @FXML
    private Button deliverButton;

    @FXML
    private Label fullAddress;

    @FXML
    private Label orderDate;

    @FXML
    private Label status;

    @FXML
    private Label totalPayment;

    @FXML
    void deliverButtonClicked(ActionEvent event) throws IOException {
        Connection connectionDb = DatabaseConnection.getInstance();
        String updateQuery = "UPDATE orders SET status = 'Delivering' WHERE orderNumber = " + this.orderNumber;
        String insertQuery = "INSERT INTO taken_orders (bakerNumber, orderNumber) VALUES (" +
                Main.BAKER.getBakerNumber() + ", " + orderNumber + ")";

        try {
            Statement statement = connectionDb.createStatement();
            statement.execute(updateQuery);
            statement.execute(insertQuery);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Refresh scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/mainBaker.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void orderProductDetailClicked(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/bakerOrderProductList.fxml"));
        AnchorPane pane = null;
        try {
            pane = loader.load();
            BakerOrderProductListController controller = loader.getController();
            controller.setData(orderNumber + "");
            Scene scene = new Scene(pane);
            scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(Order order) {
        this.orderNumber = order.getOrderNumber();
        orderDate.setText(order.getOrderDate());
        status.setText("(" + order.getStatus() + ")");
        totalPayment.setText("$" + order.getTotalPayment());

        Address tmpAddress = order.getAddress();
        String fullAddress = tmpAddress.getSpecificAddress() + ", " + tmpAddress.getWard() + ", " +
                tmpAddress.getDistrict() + ", " + tmpAddress.getProvince();
        this.fullAddress.setText(fullAddress);

        if(!order.getStatus().equals("Ordered")) {
            deliverButton.setDisable(true);
        }
    }

}
