package Controller;

import Entity.Product;
import Main.DatabaseConnection;
import Main.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class CartProductController {

    private String code;

    @FXML
    private Spinner<Integer> quantity;

    @FXML
    private CheckBox productCheckBox;

    @FXML
    private Label category;

    @FXML
    private ImageView image;

    @FXML
    private Label name;

    @FXML
    private Label price;

    @FXML
    private Label totalPrice;


    @FXML
    void deleteButtonClicked(MouseEvent event) throws IOException {

        deleteCartProduct(code);

        // Refresh cart scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/cart.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void deleteCartProduct(String productCode) {
        Connection connectionDB = DatabaseConnection.getInstance();
        String deleteQuery = "DELETE FROM cartDetails WHERE cartNumber = " + Main.USER.getCustomerNumber() +
                " AND productCode = '" + productCode + "';";
        Main.USER.setTotalQuantity(Main.USER.getCartTotalQuantity() - 1);
        String alterQuantityQuery = "UPDATE shoppingCarts SET totalQuantity = " + Main.USER.getCartTotalQuantity() +
                " WHERE cartNumber = " + Main.USER.getCustomerNumber();

        try {
            Statement statement = connectionDB.createStatement();
            statement.execute(deleteQuery);
            statement.execute(alterQuantityQuery);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(Product product) {
        // Set normal information
        image.setImage(new Image(getClass().getResource(product.getImagePath()).toExternalForm()));
        name.setText(product.getName());
        price.setText("$" + product.getPrice());
        category.setText(product.getProductLine());
        code = product.getCode();

        // Set spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50);
        valueFactory.setValue(product.getQuantityOrder());
        quantity.setValueFactory(valueFactory);

        // Set total price
        totalPrice.setText("$" + getTotalPrice());
    }

    public int getQuantity() {
        return this.quantity.getValue();
    }

    public String getCode() {
        return code;
    }

    public CheckBox getProductCheckBox() {
        return productCheckBox;
    }

    public Spinner<Integer> getQuantitySpinner() {
        return quantity;
    }

    public Label getPrice() {
        return price;
    }

    public void setTotalPrice(String tmp) {
        totalPrice.setText(tmp);
    }

    public double getTotalPrice() {
        DecimalFormat df = new DecimalFormat(".#######");
        double normalizedPrice = Double.parseDouble(price.getText().substring(1));
        return Double.parseDouble(df.format(normalizedPrice * quantity.getValue()));
    }

    public double getNomalizedPrice() {
        return Double.parseDouble(price.getText().substring(1));
    }

    public void setProductCheckBox(boolean isTicked) {
        productCheckBox.setSelected(isTicked);
    }

    public boolean isTicked() {
        return productCheckBox.isSelected();
    }
}
