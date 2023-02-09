package Controller;

import Entity.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.text.DecimalFormat;

public class BakerOrderProductController {

    private String code;

    @FXML
    private Label category;

    @FXML
    private ImageView image;

    @FXML
    private Label name;

    @FXML
    private Label price;

    @FXML
    private Label quantity;

    @FXML
    private Label totalPrice;

    public void setData(Product product) {
        // Set normal information
        image.setImage(new Image(getClass().getResource(product.getImagePath()).toExternalForm()));
        name.setText(product.getName());
        price.setText("$" + product.getPrice());
        category.setText(product.getProductLine());
        code = product.getCode();
        quantity.setText(product.getQuantityOrder() + "");
        totalPrice.setText("$" + getTotalPrice(product.getQuantityOrder()));

    }

    public double getTotalPrice(int quantityOrdered) {
        DecimalFormat df = new DecimalFormat(".#######");
        double normalizedPrice = Double.parseDouble(price.getText().substring(1));
        return Double.parseDouble(df.format(normalizedPrice * quantityOrdered));
    }

}
