package org.post.dao;

import org.post.database.Connexion;
import org.post.models.User;
import org.post.utils.Hashing;
import org.post.utils.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final String tableName;
    private final Connection connection;


    public UserDAO(String tableName) throws SQLException {
        this.tableName = tableName;
        try {
            this.connection = Connexion.getConnexion();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (!tableExists()) createTableSQL();

    }

    public boolean tableExists() throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"});

        return resultSet.next();
    }

    public void createTableSQL() {
        String tablesql = "CREATE TABLE " + this.tableName +
                "(" + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "email VARCHAR(255) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "active BOOLEAN DEFAULT false NOT NULL)";


        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(tablesql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Boolean insertUser(String name, String email, String password, Boolean active) throws SQLException {
        String insertSQL = "INSERT INTO users(name,email,password,active) VALUES (?,?,?,?)";

        // Creation d'un objet PreparedStatement pour l'insertion
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, email);
        preparedStatement.setString(3, Hashing.hashPassword(password));
        preparedStatement.setBoolean(4, false);

        // Executez la requete d'insertion
        int rowsInserted = preparedStatement.executeUpdate();
        preparedStatement.close();

        return rowsInserted > 0;


    }

    public boolean confirmAccount(String email) throws SQLException {

        PreparedStatement preparedStatement;
        String UpdateSQL = "Update users set active=true where email = ?";
        preparedStatement = this.connection.prepareStatement(UpdateSQL);
        preparedStatement.setString(1, email);

        int rowsInserted = preparedStatement.executeUpdate();
        preparedStatement.close();

        return rowsInserted > 0;

    }

    public User getByEmail(String email) throws SQLException {
        PreparedStatement preparedStatement;
        String sql = "SELECT * FROM users WHERE email = ?";
        preparedStatement = this.connection.prepareStatement(sql);
        preparedStatement.setString(1, email);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            boolean active = resultSet.getBoolean("active");

            return new User(id, name, email, active);
        } else {
            return null;
        }
    }

    public boolean resetPassword(String email, String newPassword) throws SQLException {
        User user = getByEmail(email);

        if (user != null) {
            String updateSQL = "UPDATE users SET password = ? WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
            preparedStatement.setString(1, Hashing.hashPassword(newPassword));
            preparedStatement.setString(2, email);

            int rowsUpdated = preparedStatement.executeUpdate();
            preparedStatement.close();

            return rowsUpdated > 0;
        }

        return false;
    }


    public List<User> findByStatement(PreparedStatement preparedStatement) throws SQLException {
        List<User> users = new ArrayList<>();

        ResultSet resultSet;

        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String email = resultSet.getString("email");
            boolean active = resultSet.getBoolean("active");
            // Add more columns as needed
            User user = new User(id, name, email, active);
            users.add(user);


        }

        resultSet.close();
        preparedStatement.close();

        return users;

    }

    public List<User> getAllUsers() throws SQLException {

        PreparedStatement preparedStatement;

        String sql = "SELECT * FROM users";
        preparedStatement = this.connection.prepareStatement(sql);

        return findByStatement(preparedStatement);
    }


    public Pair<Boolean, User> loginUser(String email, String password) throws SQLException {
        String sql = "SELECT id,name,password,active FROM users WHERE email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, email);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String hashedPasswordFromDB = resultSet.getString("password");
            boolean active = resultSet.getBoolean("active");

            User user = new User(id, name, email, active);

            // Vérifier le mot de passe et si le compte est actif
            Boolean verified = Hashing.verifyPassword(password, hashedPasswordFromDB) && active;
            return new Pair<>(verified, user);
        }

        // Aucun utilisateur trouvé avec cet email
        return new Pair<>(false, new User());
    }


}
