package database;

import org.store.model.User;
import org.store.WindowsRegistryController;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import static org.store.Main.isWindows;

public class Database {
    public static Connection connect;
    public static boolean activeConnectionWithDatabase = false;

    public synchronized Connection connectToDatabase() throws SQLException, IOException {
        while(Database.activeConnectionWithDatabase) {
            try {
                System.out.printf("\n[%s] is waiting", Thread.currentThread().getName());
                wait(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Database.activeConnectionWithDatabase = true;
        System.out.printf("\n[%s] is communicating with the Database", Thread.currentThread().getName());

        String databaseURL;
        String databaseUsername;
        String databasePassword;

        if (isWindows()) {
            String keyPath = "Software\\MyApp";
            databaseURL = WindowsRegistryController.readStringValue(keyPath, "databaseURL");
            databaseUsername = WindowsRegistryController.readStringValue(keyPath, "databaseUsername");
            databasePassword = WindowsRegistryController.readStringValue(keyPath, "databasePassword");
        } else {
            Properties config = new Properties();
            config.load(new FileReader("dat/database.properties"));
            databaseURL = config.getProperty("databaseURL");
            databaseUsername = config.getProperty("databaseUsername");
            databasePassword = config.getProperty("databasePassword");
            System.out.println(databaseURL + databaseUsername + databasePassword);
        }

        connect = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);

        Database.activeConnectionWithDatabase = false;
        notifyAll();
        return connect;
    }

    public static Connection getConnect() throws SQLException, IOException {
        Database database = new Database();
        return database.connectToDatabase();
    }

    public static void addUser(User user) throws SQLException, IOException {
        Connection connection = getConnect();

        String query = "INSERT INTO \"USER\" (EMAIL, PASSWORD_HASH, SALT, NAME, AUTHORIZATION_LEVEL_ID) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getPasswordHash());
        statement.setString(3, user.getSalt());
        statement.setString(4, user.getFullName());
        statement.setInt(5, getAuthorizationLevelId(user.getAuthorizationLevel()));

        statement.executeUpdate();
        connection.close();
    }

    private static int getAuthorizationLevelId(String authorizationLevel) throws SQLException, IOException {
        Connection connection = getConnect();
        int id = -1;

        String query = "SELECT ID FROM AUTHORIZATION_LEVEL WHERE TITLE = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, authorizationLevel);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            id = resultSet.getInt("ID");
        }

        connection.close();
        return id;
    }

    public static User getUserByEmail(String email) throws SQLException, IOException {
        Connection connection = getConnect();
        User user = null;

        String query = "SELECT U.\"EMAIL\", U.\"PASSWORD_HASH\", AL.\"TITLE\" AS AUTHORIZATION_LEVEL " +
                "FROM \"USER\" U " +
                "JOIN \"AUTHORIZATION_LEVEL\" AL ON U.\"AUTHORIZATION_LEVEL_ID\" = AL.\"ID\" " +
                "WHERE U.\"EMAIL\" = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            user = new User();
            user.setEmail(resultSet.getString("EMAIL"));
            user.setPasswordHash(resultSet.getString("PASSWORD_HASH"));
            user.setAuthorizationLevel(resultSet.getString("AUTHORIZATION_LEVEL"));
        }

        connection.close();
        return user;
    }

    public static void disconnectFromDatabase() throws SQLException {
        System.out.printf("\n[%s] disconnected from the Database", Thread.currentThread().getName());
        connect.close();
    }
}