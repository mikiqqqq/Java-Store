import org.store.WindowsRegistryReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;


public class Database {
    public static Connection connect;
    public static boolean activeConnectionWithDatabase = false;

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

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
            databaseURL = WindowsRegistryReader.readStringValue(keyPath, "databaseURL");
            databaseUsername = WindowsRegistryReader.readStringValue(keyPath, "databaseUsername");
            databasePassword = WindowsRegistryReader.readStringValue(keyPath, "databasePassword");
        } else {
            Properties config = new Properties();
            config.load(new FileReader("dat/database.properties"));
            databaseURL = config.getProperty("databaseURL");
            databaseUsername = config.getProperty("databaseUsername");
            databasePassword = config.getProperty("databasePassword");
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

    public static void addItemToFactory(Item item, Factory factory) throws SQLException, IOException, InterruptedException {
        Connection connection = getConnect();

        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO FACTORY_ITEM (FACTORY_ID, ITEM_ID) VALUES(?, ?)");

        statement.setLong(1, factory.getId());
        statement.setLong(2, item.getId());

        statement.executeUpdate();

        connection.close();
    }

    public static void disconnectFromDatabase() throws SQLException {
        System.out.printf("\n[%s] disconnected from the Database", Thread.currentThread().getName());
        connect.close();
    }
}