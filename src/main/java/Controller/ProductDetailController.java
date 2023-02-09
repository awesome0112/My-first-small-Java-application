package Controller;

import Entity.Comment;
import Entity.Product;
import Main.*;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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

public class ProductDetailController implements Initializable {

    private String productCode;

    @FXML
    private Spinner<Integer> amount;

    @FXML
    private Label category;

    @FXML
    private ImageView image;

    @FXML
    private Label name;

    @FXML
    private Label price;

    @FXML
    private Label description;

    @FXML
    private Label userFullNameLabel;

    @FXML
    private TextField commentField;

    @FXML
    private GridPane grid;

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
    void addToCartButtonClicked(ActionEvent event) {
        Connection connectionDB = DatabaseConnection.getInstance();
        // SELECT kiểm tra xem mặt hàng có productCode đã có ở trong bảng shoppingCarts chưa
        String query = "SELECT * FROM shoppingCarts sc INNER JOIN cartDetails cd on sc.cartNumber = cd.cartNumber " +
                "AND sc.cartNumber = " + Main.USER.getCustomerNumber() +
                " AND cd.productCode = '" + productCode + "';";
        try {
            Statement statement = connectionDB.createStatement();
            ResultSet output = statement.executeQuery(query);
            // Nếu mặt hàng có productCode đã có ở trong bảng shoppingCarts thì tăng quantity trong cartDetails lên amount
            // Nếu chưa thì thêm vào cartDetails mặt hàng đó với quantity là amount.
            if(output.next()) {
                int newQuantity = output.getInt("quantity") + amount.getValue();
                String updateQuery = "UPDATE cartDetails SET quantity = " + newQuantity +
                        " WHERE cartNumber = " + Main.USER.getCustomerNumber() +
                        " AND productCode = '" + productCode + "';";
                statement.execute(updateQuery);
            } else {
                String insertQuery = "INSERT INTO cartDetails (cartNumber, productCode, quantity) VALUES (" +
                        Main.USER.getCustomerNumber() + ", '" + productCode + "', " + amount.getValue() + ");";
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
    void goToCartButtonClicked(ActionEvent event) throws IOException {
        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/cart.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void enterPressed(KeyEvent event) throws IOException {
        String key = event.getCode().toString();
        if(key.equals("ENTER")) {
            String comment = normalize(commentField.getText());
            if(!comment.isEmpty()) {
                Connection connectionDB = DatabaseConnection.getInstance();
                String insertQuery = "INSERT INTO comments (productCode, customerNumber, comment) VALUES ('" +
                        productCode + "', '" + Main.USER.getCustomerNumber() + "', '" + comment + "');";
                try {
                    Statement statement = connectionDB.createStatement();
                    statement.execute(insertQuery);
                } catch(Exception e) {
                    e.printStackTrace();
                }

                setComments();
                commentField.setText("");
            }
        }
    }

    private String normalize(String s) {
        while(!s.isEmpty() && s.charAt(0) == ' ') s = s.substring(1);
        while(!s.isEmpty() && s.charAt(s.length() - 1) == ' ') s = s.substring(0, s.length() - 1);
        return s;
    }

    public void setData(Product product) {
        name.setText(product.getName());
        price.setText("$" + product.getPrice());
        category.setText(product.getProductLine());
        image.setImage(new Image(getClass().getResource(product.getImagePath()).toExternalForm()));
        description.setText(product.getDescription());
        userFullNameLabel.setText(Main.USER.getFullName());
        this.productCode = product.getCode();

        setComments();
    }

    private void setComments() {
        Connection connectionDB = DatabaseConnection.getInstance();
        String selectQuery = "SELECT * FROM comments cm INNER JOIN customers cu ON cm.customerNumber = cu.customerNumber " +
                "WHERE productCode = '" + productCode + "';";
        List<Comment> comments = new ArrayList<>();
        try {
            Statement statement = connectionDB.createStatement();
            ResultSet output = statement.executeQuery(selectQuery);
            while (output.next()) {
                comments.add(new Comment(output.getString("customerLastName") + " " + output.getString("customerFirstName"),
                        output.getString("comment")));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Clear comments
        ObservableList<Node> tmpProducts = grid.getChildren();
        tmpProducts.clear();

        // Reset
        for (int i = 0; i < comments.size(); i++) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/comment.fxml"));
            AnchorPane pane = null;
            try {
                pane = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            CommentController tmp = loader.getController();
            tmp.setData(comments.get(i));

            grid.add(pane, 0, i);
            GridPane.setMargin(pane, new Insets(5));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10);
        valueFactory.setValue(1);
        amount.setValueFactory(valueFactory);
    }
}
