package Controller;

import Entity.Product;
import Main.DatabaseConnection;
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
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainCustomerController implements Initializable {

    private List<Product> bread = new ArrayList<>();
    private List<Product> cake = new ArrayList<>();
    private List<Product> cookie = new ArrayList<>();
    private List<Product> dessert = new ArrayList<>();
    private List<Product> frozen = new ArrayList<>();

    @FXML
    private HBox breadButton;

    @FXML
    private HBox cakeButton;

    @FXML
    private Label category;

    @FXML
    private HBox cookieButton;

    @FXML
    private HBox dessertButton;

    @FXML
    private HBox frozenButton;

    @FXML
    private GridPane grid;

    @FXML
    private TextField searchField;

    @FXML
    private MenuButton userFullNameLabel;

    @FXML
    void breadButtonClicked(MouseEvent event) {
        resetProducts(bread);
        breadButton.setStyle("-fx-background-color:  #b8e994");
        category.setText("Bread");
    }

    @FXML
    void cakeButtonClicked(MouseEvent event) {
        resetProducts(cake);
        cakeButton.setStyle("-fx-background-color:  #b8e994");
        category.setText("Cake");
    }

    @FXML
    void cookieButtonClicked(MouseEvent event) {
        resetProducts(cookie);
        cookieButton.setStyle("-fx-background-color:  #b8e994");
        category.setText("Cookie");
    }

    @FXML
    void dessertButtonClicked(MouseEvent event) {
        resetProducts(dessert);
        dessertButton.setStyle("-fx-background-color:  #b8e994");
        category.setText("Dessert");
    }

    @FXML
    void frozenButtonClicked(MouseEvent event) {
        resetProducts(frozen);
        frozenButton.setStyle("-fx-background-color:  #b8e994");
        category.setText("Frozen");
    }

    @FXML
    void profileClicked(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/profile.fxml"));
        AnchorPane pane = null;
        try {
            pane = loader.load();
            ProfileController profileController = loader.getController();
            profileController.setData();
            Scene scene = new Scene(pane);
            scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
            Main.primaryStage.setScene(scene);
            Main.primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ordersClicked(ActionEvent event) throws IOException {
        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/orders.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        Main.primaryStage.setScene(scene);
        Main.primaryStage.show();
    }

    @FXML
    void addressClicked(ActionEvent event) throws IOException {
        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/addressList.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        Main.primaryStage.setScene(scene);
        Main.primaryStage.show();
    }

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
    void enterKeyPressed(KeyEvent event) {
        String key = event.getCode().toString();
        if(key.equals("ENTER")) {
            List<Product> search = search();
            if(!Algorithm.normalize(searchField.getText()).isEmpty()) {
                if(search.isEmpty()) category.setText("No result found");
                else category.setText("Search");
                resetProducts(search);
            }
        }
    }

    @FXML
    void cartButtonClicked(MouseEvent event) throws IOException {
        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/cart.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private List<Product> search() {
        String text = Algorithm.normalize(searchField.getText().toLowerCase());
        List<Product> searchedProducts = new ArrayList<>();
        if(!text.isEmpty()) {
            Connection connectionDB = DatabaseConnection.getInstance();
            String words[] = text.split(" +");
            String query = "SELECT * FROM products WHERE lower(productName) LIKE ";

            for(int i = 0; i < words.length; i++) {
                query += "'%" + words[i] + "%'";
                if(i != words.length - 1) query += " OR lower(productName) LIKE ";
            }
            try {
                Statement statement = connectionDB.createStatement();
                ResultSet queryOutput = statement.executeQuery(query);

                while (queryOutput.next()) {
                    searchedProducts.add(new Product(queryOutput.getString("productCode"), queryOutput.getString("productName"),
                            queryOutput.getString("productLine"), queryOutput.getDouble("buyPrice"), queryOutput.getString("image")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return searchedProducts;
    }
    private void resetProducts(List<Product> products) {
        cakeButton.setStyle("-fx-background-color:  transparent");
        breadButton.setStyle("-fx-background-color:  transparent");
        cookieButton.setStyle("-fx-background-color:  transparent");
        dessertButton.setStyle("-fx-background-color:  transparent");
        frozenButton.setStyle("-fx-background-color:  transparent");

        ObservableList<Node> tmpProducts = grid.getChildren();
        tmpProducts.clear();
        int col = 0;
        int row = 0;
        for(int i = 0; i < products.size(); i++) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/product.fxml"));
            AnchorPane pane = null;
            try {
                pane = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ProductController productController = loader.getController();
            productController.setData(products.get(i));

            // ALIGNMENT = 24

            if(col == 3) {
                col = 0;
                row++;
            }

            grid.add(pane, col++, row);
            GridPane.setMargin(pane, new Insets(24));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userFullNameLabel.setText(Main.USER.getFullName());

        Connection connectionDB = DatabaseConnection.getInstance();
        String connectionQuery = "SELECT * FROM products;";
        try {
            Statement statement = connectionDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectionQuery);

            while (queryOutput.next()) {
                String productLine = queryOutput.getString("productLine");
                if(productLine.equals("Bread")) {
                    bread.add(new Product(queryOutput.getString("productCode"), queryOutput.getString("productName"),
                            queryOutput.getString("productLine"), queryOutput.getDouble("buyPrice"), queryOutput.getString("image")));
                } else if(productLine.equals("Cake")) {
                    cake.add(new Product(queryOutput.getString("productCode"), queryOutput.getString("productName"),
                            queryOutput.getString("productLine"), queryOutput.getDouble("buyPrice"), queryOutput.getString("image")));
                } else if(productLine.equals("Cookie")) {
                    cookie.add(new Product(queryOutput.getString("productCode"), queryOutput.getString("productName"),
                            queryOutput.getString("productLine"), queryOutput.getDouble("buyPrice"), queryOutput.getString("image")));
                } else if(productLine.equals("Dessert")) {
                    dessert.add(new Product(queryOutput.getString("productCode"), queryOutput.getString("productName"),
                            queryOutput.getString("productLine"), queryOutput.getDouble("buyPrice"), queryOutput.getString("image")));
                } else if(productLine.equals("Frozen")) {
                    frozen.add(new Product(queryOutput.getString("productCode"), queryOutput.getString("productName"),
                            queryOutput.getString("productLine"), queryOutput.getDouble("buyPrice"), queryOutput.getString("image")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        category.setText("Cake");
        cakeButton.setStyle("-fx-background-color:  #b8e994");

        int col = 0;
        int row = 0;
        for(int i = 0; i < cake.size(); i++) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/product.fxml"));
            AnchorPane pane = null;
            try {
                pane = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ProductController productController = loader.getController();
            productController.setData(cake.get(i));

            // ALIGNMENT = 24

            if(col == 3) {
                col = 0;
                row++;
            }

            grid.add(pane, col++, row);
            GridPane.setMargin(pane, new Insets(24));
        }
    }
}
