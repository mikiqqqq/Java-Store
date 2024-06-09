package database.repository;

import database.Database;
import org.store.model.User;

import java.io.IOException;
import java.sql.*;

public class UserRepo {

    public static void addUser(User user) throws SQLException, IOException {
        Connection connection = Database.getConnect();

        String query = "INSERT INTO \"USERS\" (EMAIL, PASSWORD_HASH, SALT, NAME, AUTHORIZATION_LEVEL_ID) VALUES (?, ?, ?, ?, ?)";
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
        Connection connection = Database.getConnect();
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
        Connection connection = Database.getConnect();
        User user = null;

        String query = "SELECT U.\"ID\", U.\"NAME\", U.\"EMAIL\", U.\"PASSWORD_HASH\", AL.\"TITLE\" AS AUTHORIZATION_LEVEL " +
                "FROM \"USERS\" U " +
                "JOIN \"AUTHORIZATION_LEVEL\" AL ON U.\"AUTHORIZATION_LEVEL_ID\" = AL.\"ID\" " +
                "WHERE U.\"EMAIL\" = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            user = new User();
            user.setId(resultSet.getInt("ID"));
            user.setFullName(resultSet.getString("NAME"));
            user.setEmail(resultSet.getString("EMAIL"));
            user.setPasswordHash(resultSet.getString("PASSWORD_HASH"));
            user.setAuthorizationLevel(resultSet.getString("AUTHORIZATION_LEVEL"));
        }

        connection.close();
        return user;
    }
}
