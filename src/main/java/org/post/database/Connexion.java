package org.post.database;

import org.post.config.Settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion {

    public static Connection getConnexion() throws SQLException {
        Settings settings = new Settings();

        try {
            // Register the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(
                    settings.getUrl(), settings.getUser(), settings.getPassword());
        } catch (ClassNotFoundException | SQLException e) {
            throw new SQLException("Connexion impossible", e);
        }
    }

}

