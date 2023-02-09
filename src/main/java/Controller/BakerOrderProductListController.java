package Controller;

import Entity.Product;
import Main.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class BakerOrderProductListController implements Initializable {

    @FXML
    private Label orderNumberLabel;

    @FXML
    private Label bakerName;

    @FXML
    private GridPane grid;

    @FXML
    void backButtonClicked(MouseEvent event) throws IOException {
        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/mainBaker.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bakerName.setText(Main.BAKER.getFirstName());

        Connection connectionDB = DatabaseConnection.getInstance();
        String selectQuery = "SELECT * FROM orderDetails od INNER JOIN products p ON od.productCode = p.productCode " +
                "WHERE orderNumber = " + orderNumberLabel.getText();

        try {
            Statement statement = connectionDB.createStatement();
            ResultSet output = statement.executeQuery(selectQuery);

            int index = 0;

            while(output.next()) {
                Product product = new Product(output.getString("productCode"), output.getString("productName"),
                        output.getString("productLine"), output.getDouble("buyPrice"),
                        output.getString("image"), output.getInt("quantity"));

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/bakerOrderProduct.fxml"));
                AnchorPane pane = null;
                try {
                    pane = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                BakerOrderProductController controller = loader.getController();
                controller.setData(product);

                grid.add(pane, 0, index++);
                GridPane.setMargin(pane, new Insets(7));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(String orderNumber) {
        this.orderNumberLabel.setText(orderNumber);
    }
}
