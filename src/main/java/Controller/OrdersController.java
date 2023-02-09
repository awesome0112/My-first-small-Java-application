package Controller;

import Entity.Address;
import Entity.Order;
import Main.DatabaseConnection;
import Main.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrdersController implements Initializable {

    private List<OrderDetailController> tmpController = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();

    @FXML
    private GridPane grid;

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
    void profileClicked(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/profile.fxml"));
        AnchorPane pane = null;
        try {
            pane = loader.load();
            ProfileController profileController = loader.getController();
            profileController.setData();
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(pane);
            scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connection connectionDB = DatabaseConnection.getInstance();
        String selectQuery = "SELECT orderNumber, orderDate, shippedDate, status, totalPayment, p.name AS province,\n" +
                "d.name AS district, w.name AS ward, a.specificAddress FROM orders o\n" +
                "INNER JOIN customers c ON o.customerNumber = c.customerNumber\n" +
                "INNER JOIN address a ON o.addressId = a.addressId\n" +
                "INNER JOIN provinces p ON a.provinceId = p.id\n" +
                "INNER JOIN districts d ON a.districtId = d.id\n" +
                "INNER JOIN wards w ON a.wardId = w.id\n" +
                "WHERE c.customerNumber = " + Main.USER.getCustomerNumber() +
                " ORDER BY o.shippedDate;";

        try {
            Statement statement = connectionDB.createStatement();
            ResultSet output = statement.executeQuery(selectQuery);

            int index = 0;

            while(output.next()) {
                orders.add(new Order(output.getInt("orderNumber"), output.getString("orderDate"), output.getString("shippedDate"),
                        output.getString("status"), output.getDouble("totalPayment"),
                        new Address(output.getString("province"), output.getString("district"),
                                output.getString("ward"), output.getString("specificAddress"))));

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/orderDetail.fxml"));
                AnchorPane pane = null;
                try {
                    pane = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                tmpController.add(loader.getController());
                tmpController.get(index).setData(orders.get(index));

                grid.add(pane, 0, index++);
                GridPane.setMargin(pane, new Insets(10));

            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
