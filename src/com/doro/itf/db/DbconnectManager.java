package com.doro.itf.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbconnectManager {

    private static DbconnectManager instance = null;

    public static DbconnectManager getInstance() {
        if (instance == null) {
            if (instance == null) {
                instance = new DbconnectManager();
            }
        }
        return instance;
    }

    private DbconnectManager() {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Class.forName("org.apache.commons.dbcp.PoolingDriver");
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }
    }

    // dbconnection
    public Connection getConnection() throws SQLException {
  
        //return DriverManager.getConnection("jdbc:apache:commons:dbcp:/pool_test");
        return DriverManager.getConnection("jdbc:apache:commons:dbcp:/pool_RevIFMain");

    }

}
