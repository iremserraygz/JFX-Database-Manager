/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbbrowser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static Connection connection;

   public static Connection connect(String url, String username, String password) throws SQLException {
    if (connection != null && !connection.isClosed()) {
        connection.close();
    }
    connection = DriverManager.getConnection(url, username, password);
    return connection;
}


    public static Connection getConnection() {
        return connection;
    }
}