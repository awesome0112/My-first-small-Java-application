package Controller;

import Entity.District;
import Entity.Province;
import Entity.Ward;
import Main.Algorithm;
import Main.DatabaseConnection;
import Main.Main;
import javafx.collections.FXCollections;
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

public class AddressListController implements Initializable {

    private List<Province> provinces = new ArrayList<>();
    private List<District> districts = new ArrayList<>();
    private List<Ward> wards = new ArrayList<>();

    private List<String> tmpProvinces = new ArrayList<>();
    private List<String> tmpDistricts = new ArrayList<>();
    private List<String> tmpWards = new ArrayList<>();

    private List<AddressController> tmpAddressControllers = new ArrayList<>();

    private int chosenProvinceId = 0;
    private int chosenDistrictId = 0;
    private int chosenWardId = 0;


    @FXML
    private GridPane grid;

    @FXML
    private ComboBox<String> provinceBox;

    @FXML
    private ComboBox<String> districtBox;

    @FXML
    private ComboBox<String> wardBox;

    @FXML
    private AnchorPane createAddressPane;

    @FXML
    private TextField specificAddressField;

    @FXML
    private Label addressCountLabel;

    @FXML
    private ScrollPane scroll;

    @FXML
    private Button addAddressButton;

    @FXML
    private HBox ordersButton;

    @FXML
    private HBox profileButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Button addButton;

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

    @FXML
    void ordersClicked(MouseEvent event) throws IOException {
        // Switch scene
        Parent root = FXMLLoader.load(getClass().getResource("/views/orders.fxml"));
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
    void addAddressButtonClicked(ActionEvent event) {

        setDisplay(false);

        createAddressPane.setVisible(true);
    }


    @FXML
    void addButtonClicked(ActionEvent event) {
        errorLabel.setText("");
        if(specificAddressField.getText().isBlank()) {
            errorLabel.setText("All information must contain letter(s)");
            return;
        }
        String normalizedInformation = Algorithm.normalize(specificAddressField.getText());

        Connection connectionDB = DatabaseConnection.getInstance();
        String insertQuery = "INSERT INTO address (provinceID, districtID, wardID, specificAddress, customerNumber) " +
                "VALUES (" + chosenProvinceId + ", " + chosenDistrictId + ", " + chosenWardId + ", '" + normalizedInformation +
                "', " + Main.USER.getCustomerNumber() + ");";

        System.out.println(insertQuery);

        try {
            Statement statement = connectionDB.createStatement();
            statement.execute(insertQuery);

            // Refresh scene
            Parent root = FXMLLoader.load(getClass().getResource("/views/addressList.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }

        resetAddressList();

    }

    @FXML
    void ReturnButtonClicked(ActionEvent event) {
        resetAddressBox();
        setDisplay(true);
        createAddressPane.setVisible(false);
    }

    @FXML
    void provinceChosen(ActionEvent event) {
        // Get Province ID
        for(Province i : provinces) {
            if(i.getName().equals(provinceBox.getValue())) {
                chosenProvinceId = i.getId();
                break;
            }
        }

        // Get District corresponding to chosen province
        List<String> newDistricts = new ArrayList<>();
        for(District i : districts) {
            if(i.getProvinceId() == chosenProvinceId) newDistricts.add(i.getName());
        }
        districtBox.setItems(FXCollections.observableList(newDistricts));
        districtBox.setDisable(false);
    }

    @FXML
    void districtChosen(ActionEvent event) {
        // Get District ID
        for(District i : districts) {
            if(i.getName().equals(districtBox.getValue())) {
                chosenDistrictId = i.getId();
                break;
            }
        }

        // Get Ward corresponding to chosen district
        List<String> newWards = new ArrayList<>();
        for(Ward i : wards) {
            if(i.getDistrictId() == chosenDistrictId) newWards.add(i.getName());
        }
        wardBox.setItems(FXCollections.observableList(newWards));
        wardBox.setDisable(false);
    }

    @FXML
    void wardChosen(ActionEvent event) {
        // Get Ward ID
        for(Ward i : wards) {
            if(i.getName().equals(wardBox.getValue())) {
                chosenWardId = i.getId();
                break;
            }
        }

        specificAddressField.setDisable(false);
        addButton.setDisable(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        resetAddressList();

        // Set province, district, ward data
        Connection connectionDB = DatabaseConnection.getInstance();
        try {
            Statement statement = connectionDB.createStatement();

            // Province data
            String query = "SELECT * FROM provinces";
            ResultSet output = statement.executeQuery(query);
            while (output.next()) {
                provinces.add(new Province(output.getInt("id"), output.getString("name")));
                tmpProvinces.add(output.getString("name"));
            }

            //District data
            query = "SELECT * FROM districts";
            output = statement.executeQuery(query);
            while (output.next()) {
                districts.add(new District(output.getInt("id"), output.getString("name"), output.getInt("provinceId")));
                tmpDistricts.add(output.getString("name"));
            }

            //Ward data
            query = "SELECT * FROM wards";
            output = statement.executeQuery(query);
            while (output.next()){
                wards.add(new Ward(output.getInt("id"), output.getString("name"), output.getInt("districtId")));
                tmpWards.add(output.getString("name"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        resetAddressBox();

        createAddressPane.setVisible(false);

    }

    private void setDisplay(boolean isAble) {
        scroll.setDisable(!isAble);
        profileButton.setDisable(!isAble);
        ordersButton.setDisable(!isAble);
        addAddressButton.setDisable(!isAble);
    }

    private void resetAddressList() {
        addressCountLabel.setText("");

        ObservableList<Node> tmpAddress = grid.getChildren();
        tmpAddress.clear();

        Connection connectionDB = DatabaseConnection.getInstance();
        String selectQuery = "SELECT addressId, CONCAT(w.name, ', ', d.name, ', ', p.name) AS generalAddress, specificAddress FROM address a \n" +
                "INNER JOIN provinces p ON a.provinceID = p.id\n" +
                "INNER JOIN districts d ON a.districtID = d.id\n" +
                "INNER JOIN wards w on a.wardId = w.id\n" +
                "WHERE a.customerNumber = " + Main.USER.getCustomerNumber();

        int rowIndex = 0;

        try {
            Statement statement = connectionDB.createStatement();
            ResultSet output = statement.executeQuery(selectQuery);

            while(output.next()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/address.fxml"));
                AnchorPane pane = null;
                try {
                    pane = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                AddressController controller = loader.getController();
                controller.setData(output.getString("generalAddress"), output.getString("specificAddress"),
                        output.getInt("addressId"));

                grid.add(pane, 0, rowIndex++);
                GridPane.setMargin(pane, new Insets(10));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        System.out.println(tmpAddress.size());
        if(tmpAddress.size() == 0) {
            addressCountLabel.setText("You don't have any address yet");
        }

        setDisplay(true);
    }

    private void resetAddressBox() {
        provinceBox.setItems(FXCollections.observableList(tmpProvinces));
        districtBox.setItems(FXCollections.observableList(tmpDistricts));
        wardBox.setItems(FXCollections.observableList(tmpWards));

        provinceBox.setValue(null);
        districtBox.setValue(null);
        wardBox.setValue(null);

        districtBox.setDisable(true);
        wardBox.setDisable(true);
        specificAddressField.setDisable(true);
        addButton.setDisable(true);

        specificAddressField.setText("");
        errorLabel.setText("");
    }

}
