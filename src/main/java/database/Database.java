package database;

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

    public static void disconnectFromDatabase() throws SQLException {
        System.out.printf("\n[%s] disconnected from the Database", Thread.currentThread().getName());
        connect.close();
    }
}