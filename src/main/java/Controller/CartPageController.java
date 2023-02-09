package Controller;

import Entity.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CartPageController {

    @FXML
    private GridPane grid;
    private List<CartProductController> cartProductControllers = new ArrayList<>();

    public List<CartProductController> getControllers() {
        return cartProductControllers;
    }

    public void setData(List<Product> products) {
        for(int i = 0; i < products.size(); i++) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/cartProduct.fxml"));
            AnchorPane pane = null;
            try {
                pane = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            cartProductControllers.add(loader.getController());
            cartProductControllers.get(i).setData(products.get(i));

            grid.add(pane, 0, i);
            GridPane.setMargin(pane, new Insets(7));
        }
    }

}

