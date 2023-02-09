package Main;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static Connection databaseLink;

    public static Connection getInstance() {
        if(databaseLink == null) {
            return getConnection();
        } else return databaseLink;
    }

    private static Connection getConnection() {
        String databaseName = "application";
        String databaseUser = "root";
        String databasePassword = "mothaiba123...";
        String url = "jdbc:mysql://localhost:3307/" + databaseName;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return databaseLink;
    }
}
