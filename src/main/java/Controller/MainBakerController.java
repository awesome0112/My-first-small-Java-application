package Controller;

import Entity.Address;
import Entity.Order;
import Main.DatabaseConnection;
import Main.Main;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainBakerController implements Initializable {

    @FXML
    private MenuButton bakerNameLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private ImageView backButton;

    @FXML
    private GridPane grid;

    @FXML
    void signOutClicked(ActionEvent event) throws IOException {
        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/signIn.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        Main.primaryStage.setScene(scene);
        Main.primaryStage.show();
    }

    @FXML
    void historyClicked(ActionEvent event) throws IOException {
        setHistoryOrders();
    }

    @FXML
    void backButtonClicked(MouseEvent event) {
        setExistingOrders();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bakerNameLabel.setText(Main.BAKER.getFirstName());

        setExistingOrders();
    }

    private void setHistoryOrders() {
        backButton.setVisible(true);
        titleLabel.setText("History Orders");
        resetGrid();

        Connection connectionDB = DatabaseConnection.getInstance();
        String selectQuery = "SELECT tor.bakerNumber, tor.orderNumber, o.orderDate, o.shippedDate, o.status, o.totalPayment, p.name AS province,\n" +
                "d.name AS district, w.name AS ward, a.specificAddress FROM taken_orders tor \n" +
                "INNER JOIN bakers b ON tor.bakerNumber = b.bakerNumber\n" +
                "INNER JOIN orders o ON o.orderNumber = tor.orderNumber\n" +
                "INNER JOIN address a ON o.addressId = a.addressId\n" +
                "INNER JOIN provinces p ON a.provinceId = p.id\n" +
                "INNER JOIN districts d ON a.districtId = d.id\n" +
                "INNER JOIN wards w ON a.wardId = w.id\n" +
                "WHERE tor.bakerNumber = " + Main.BAKER.getBakerNumber();
        try {
            Statement statement = connectionDB.createStatement();
            ResultSet output = statement.executeQuery(selectQuery);

            int index = 0;

            while(output.next()) {

                Order order = new Order(output.getInt("orderNumber"), output.getString("orderDate"),
                        output.getString("shippedDate"), output.getString("status"),
                        output.getDouble("totalPayment"), new Address(output.getString("province"),
                        output.getString("district"), output.getString("ward"), output.getString("specificAddress")));

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/bakerOrderDetail.fxml"));
                AnchorPane pane = null;
                try {
                    pane = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                BakerOrderDetailController controller = loader.getController();
                controller.setData(order);

                grid.add(pane, 0, index++);
                GridPane.setMargin(pane, new Insets(10));

            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void setExistingOrders() {
        backButton.setVisible(false);
        titleLabel.setText("Existing Orders");
        resetGrid();

        Connection connectionDB = DatabaseConnection.getInstance();
        String selectQuery = "SELECT orderNumber, orderDate, shippedDate, status, totalPayment, p.name AS province,\n" +
                "d.name AS district, w.name AS ward, a.specificAddress FROM orders o\n" +
                "INNER JOIN customers c ON o.customerNumber = c.customerNumber\n" +
                "INNER JOIN address a ON o.addressId = a.addressId\n" +
                "INNER JOIN provinces p ON a.provinceId = p.id\n" +
                "INNER JOIN districts d ON a.districtId = d.id\n" +
                "INNER JOIN wards w ON a.wardId = w.id\n" +
                "WHERE o.status != 'Received' ORDER BY status DESC, orderDate";

        try {
            Statement statement = connectionDB.createStatement();
            ResultSet output = statement.executeQuery(selectQuery);

            int index = 0;

            while(output.next()) {
                Order order = new Order(output.getInt("orderNumber"), output.getString("orderDate"),
                        output.getString("shippedDate"), output.getString("status"),
                        output.getDouble("totalPayment"), new Address(output.getString("province"),
                        output.getString("district"), output.getString("ward"), output.getString("specificAddress")));

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/bakerOrderDetail.fxml"));
                AnchorPane pane = null;
                try {
                    pane = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                BakerOrderDetailController controller = loader.getController();
                controller.setData(order);

                grid.add(pane, 0, index++);
                GridPane.setMargin(pane, new Insets(10));

            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void resetGrid() {
        ObservableList<Node> tmpProducts = grid.getChildren();
        tmpProducts.clear();
    }
}
