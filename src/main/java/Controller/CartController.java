package Controller;

import Entity.Product;
import Main.DatabaseConnection;
import Main.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CartController implements Initializable {

    private List<List<Product>> cartProducts = new ArrayList<>();
    private List<CartPageController> cartPageControllers = new ArrayList<>();
    private List<AnchorPane> anchorPanePage = new ArrayList<>();
    private double tmpTotalPayment = 0;

    @FXML
    private Label userFullNameLabel;

    @FXML
    private Pagination cartPage;

    @FXML
    private Label errorLabel;

    @FXML
    private CheckBox buyAllCheckBox;

    @FXML
    private Label totalPayment;

    @FXML
    private Label totalProduct;

    @FXML
    void changeAddressClicked(ActionEvent event) throws IOException {
        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/addressList.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void buyButtonClicked(ActionEvent event) {
        errorLabel.setText("");

        List<CartProductController> controllers = new ArrayList<>();

        for(int i = 0; i < cartPageControllers.size(); i++) {
            for (int j = 0; j < cartPageControllers.get(i).getControllers().size(); j++) {
                CartProductController tmp = cartPageControllers.get(i).getControllers().get(j);
                if(tmp.isTicked()) controllers.add(tmp);
            }
        }
        if(controllers.isEmpty()) {
            errorLabel.setText("You need to select at least one item");
            return;
        }

        Connection connectionDB = DatabaseConnection.getInstance();
        String selectQuery = "SELECT now() AS orderDate, c.customerNumber, defaultAddress FROM customers c INNER JOIN address a ON c.customerNumber = a.customerNumber " +
                "WHERE c.defaultAddress = a.addressId AND c.customerNumber = " + Main.USER.getCustomerNumber();

        try {
            Statement statement = connectionDB.createStatement();
            ResultSet output = statement.executeQuery(selectQuery);
            if(output.next()) {
                String insertQuery = "INSERT INTO orders (orderDate, status, customerNumber, totalPayment, addressId) " +
                        "VALUES ('" + output.getString("orderDate") + "', 'Ordered', " + Main.USER.getCustomerNumber() +
                        ", "  + Double.parseDouble(totalPayment.getText().substring(1)) + ", " + output.getInt("defaultAddress") + ");";
                statement.execute(insertQuery);

                // Get orderNumber
                selectQuery = "SELECT orderNumber FROM orders ORDER BY orderNumber DESC LIMIT 1;";
                output = statement.executeQuery(selectQuery);

                if(output.next()) {
                    int orderNumber = output.getInt("orderNumber");
                    // Insert into order detail
                    for (CartProductController i : controllers) {
                        insertQuery = "INSERT INTO orderDetails (orderNumber, productCode, quantity) " +
                                "VALUES (" + orderNumber + ", '" + i.getCode() + "', " + i.getQuantity() + ");";
                        statement.execute(insertQuery);
                    }
                }

                // Delete bought products
                for(CartProductController i : controllers) {
                    i.deleteCartProduct(i.getCode());
                }

                //Switch scene to orders
                Parent root = FXMLLoader.load(getClass().getResource("/views/orders.fxml"));
                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
                stage.setScene(scene);
                stage.show();

            } else {
                errorLabel.setText("You need to set a default address");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void backButtonClicked(MouseEvent event) throws IOException {
        // Update cart product quantity
        Connection connectionDB = DatabaseConnection.getInstance();
        for(int i = 0; i < cartPageControllers.size(); i++) {
            for(int j = 0; j < cartPageControllers.get(i).getControllers().size(); j++) {
                CartProductController tmp = cartPageControllers.get(i).getControllers().get(j);
                String query = "UPDATE cartDetails SET quantity = " + tmp.getQuantity() +
                        " WHERE productCode = '" + tmp.getCode() + "';";
                try {
                    Statement statement = connectionDB.createStatement();
                    statement.execute(query);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/mainCustomer.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void createAnchorPanePage() {
        for(int i = 0; i < cartProducts.size(); i++) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/cartPage.fxml"));
            AnchorPane pane = null;
            try {
                pane = loader.load();
            } catch(Exception e) {
                e.printStackTrace();
            }
            cartPageControllers.add(loader.getController());
            cartPageControllers.get(i).setData(cartProducts.get(i));
            anchorPanePage.add(pane);
        }
    }

    private Parent createPage(int pageIndex) {
        if(anchorPanePage.size() - 1 < pageIndex) return null;
        else return anchorPanePage.get(pageIndex);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userFullNameLabel.setText(Main.USER.getFullName());
        errorLabel.setText("");

        Connection connectionDB = DatabaseConnection.getInstance();
        String connectionQuery = "SELECT * FROM cartDetails cd INNER JOIN products p ON p.productCode = cd.productCode " +
                "WHERE cartNumber = " + Main.USER.getCustomerNumber();

        try {
            Statement statement = connectionDB.createStatement();
            ResultSet output = statement.executeQuery(connectionQuery);
            int countProduct = 0;
            int countPage = -1;
            while(output.next()) {
                if(countProduct % 5 == 0) {
                    cartProducts.add(new ArrayList<>());
                    countPage++;
                }
                cartProducts.get(countPage).add(new Product(output.getString("productCode"), output.getString("productName"),
                        output.getString("productLine"), output.getDouble("buyPrice"),
                        output.getString("image"), output.getInt("quantity")));
                countProduct++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        createAnchorPanePage();

        cartPage.setPageFactory(this::createPage);

        buyAllCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                setProductsCheckBox(buyAllCheckBox.isSelected());
            }
        });

        DecimalFormat df = new DecimalFormat(".#######");
        for(int i = 0; i < cartPageControllers.size(); i++) {
            for (int j = 0; j < cartPageControllers.get(i).getControllers().size(); j++) {
                CartProductController tmpController = cartPageControllers.get(i).getControllers().get(j);
                CheckBox tmpCheckBox = tmpController.getProductCheckBox();
                tmpCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                        int oldTotalProduct = Integer.parseInt(totalProduct.getText().charAt(1) + "");
                        if (tmpCheckBox.isSelected()) {
                            tmpTotalPayment = Double.parseDouble(df.format(tmpTotalPayment + tmpController.getTotalPrice()));
                            totalProduct.setText("(" + (oldTotalProduct + 1) + " Products)");
                        } else {
                            tmpTotalPayment = Double.parseDouble(df.format(tmpTotalPayment - tmpController.getTotalPrice()));
                            totalProduct.setText("(" + (oldTotalProduct - 1) + " Products)");
                        }
                        totalPayment.setText("$" + tmpTotalPayment);
                    }
                });

                Spinner<Integer> tmpSpinner = tmpController.getQuantitySpinner();
                tmpSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
                    int previousQuantity = tmpSpinner.getValue();
                    @Override
                    public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                        if(tmpCheckBox.isSelected()) {
                            if (tmpSpinner.getValue() > previousQuantity) {
                                tmpTotalPayment = Double.parseDouble(df.format(tmpTotalPayment + tmpController.getNomalizedPrice()));

                            } else if (tmpSpinner.getValue() < previousQuantity) {
                                tmpTotalPayment = Double.parseDouble(df.format(tmpTotalPayment - tmpController.getNomalizedPrice()));
                            }
                            totalPayment.setText("$" + tmpTotalPayment);
                        }
                        previousQuantity = tmpSpinner.getValue();

                        // Change totalPrice
                        DecimalFormat df = new DecimalFormat(".#######");
                        double normalizedPrice = Double.parseDouble(tmpController.getPrice().getText().substring(1));
                        double tmpPrice = Double.parseDouble(df.format(normalizedPrice * tmpSpinner.getValue()));
                        tmpController.setTotalPrice("$" + (tmpPrice));
                    }
                });
            }
        }
    }

    private void setProductsCheckBox(boolean isTicked) {
        for(int i = 0; i < cartPageControllers.size(); i++) {
            for(int j = 0; j < cartPageControllers.get(i).getControllers().size(); j++) {
                CartProductController tmp = cartPageControllers.get(i).getControllers().get(j);
                tmp.setProductCheckBox(isTicked);
            }
        }
    }
}
