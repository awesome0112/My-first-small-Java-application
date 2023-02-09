package Controller;

import Entity.Product;
import Main.DatabaseConnection;
import Main.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ProductController {

    @FXML
    private ImageView image;

    @FXML
    private Label name;

    @FXML
    private Label price;

    private String productCode;

    public void setData(Product product) {
        image.setImage(new Image(getClass().getResource(product.getImagePath()).toExternalForm()));
        name.setText(product.getName());
        price.setText("$" + product.getPrice());
        productCode = product.getCode();
    }

    @FXML
    void buyNowButtonClicked(MouseEvent event) {
        Connection connectionDB = DatabaseConnection.getInstance();
        // SELECT kiểm tra xem mặt hàng có productCode đã có ở trong bảng shoppingCarts chưa
        String query = "SELECT * FROM shoppingCarts sc INNER JOIN cartDetails cd on sc.cartNumber = cd.cartNumber " +
                "AND sc.cartNumber = " + Main.USER.getCustomerNumber() +
                " AND cd.productCode = '" + productCode + "';";
        try {
            Statement statement = connectionDB.createStatement();
            ResultSet output = statement.executeQuery(query);
            // Nếu mặt hàng có productCode đã có ở trong bảng shoppingCarts thì tăng quantity trong cartDetails lên 1
            // Nếu chưa thì thêm vào cartDetails mặt hàng đó với quantity là 1.
            if(output.next()) {
                int newQuantity = output.getInt("quantity") + 1;
                String updateQuery = "UPDATE cartDetails SET quantity = " + newQuantity +
                        " WHERE cartNumber = " + Main.USER.getCustomerNumber() +
                        " AND productCode = '" + productCode + "';";
                statement.execute(updateQuery);
            } else {
                String insertQuery = "INSERT INTO cartDetails (cartNumber, productCode, quantity) VALUES (" +
                        Main.USER.getCustomerNumber() + ", '" + productCode + "', " + 1 + ");";
                statement.execute(insertQuery);
                // Tăng totalQuantity trong shoppingCarts thêm 1 đơn vị.
                Main.USER.addQuantity(1);
                String updateQuery = "UPDATE shoppingCarts SET totalQuantity = " + Main.USER.getCartTotalQuantity() +
                        " WHERE cartNumber = " + Main.USER.getCustomerNumber() + ";";
                statement.execute(updateQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showProductClicked(MouseEvent event) {
        Connection connectionDB = DatabaseConnection.getInstance();
        String connectQuery = "SELECT * FROM products WHERE productCode = '" + productCode + "';";
        try {
            Statement statement = connectionDB.createStatement();
            ResultSet output = statement.executeQuery(connectQuery);
            if(output.next()) {
                // Switch scene
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/productDetail.fxml"));
                AnchorPane pane = null;
                try {
                    pane = loader.load();
                    ProductDetailController controller = loader.getController();
                    controller.setData(new Product(output.getString("productCode"), output.getString("productName"),
                            output.getString("productLine"), output.getDouble("buyPrice"), output.getString("image"),
                            output.getString("productDescription")));
                    Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(pane);
                    scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
