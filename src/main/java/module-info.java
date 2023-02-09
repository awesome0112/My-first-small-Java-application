module minh_quan.applicaition {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports Main;
    opens Main to javafx.fxml;
    exports Controller;
    opens Controller to javafx.fxml;
}