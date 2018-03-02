package com.batian.weatherdata.jdbc;

import com.batian.weatherdata.conf.ConfigurationManager;
import com.batian.weatherdata.constant.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * JDBC 辅助组件
 * Created by Ricky on 2018/3/2
 *
 * @author Administrator
 */
public class JDBCHelper {

    /**
     * Database Driver
     */
    static {
        try {
            String driver = ConfigurationManager.getProperty( Constants.JDBC_DRIVER );
            Class.forName( driver );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Single case
     */
    private static JDBCHelper instance = null;

    /**
     * get single
     */

    public static JDBCHelper getInstance() {
        if (instance == null) {
            synchronized (JDBCHelper.class) {
                if (instance == null) {
                    instance = new JDBCHelper();
                }
            }
        }
        return instance;
    }

    /**
     * database connection pool
     */
    private LinkedList<Connection> datasource = new LinkedList<Connection>();

    /**
     * instantiation single mode,create only connection pool
     */
    private JDBCHelper() {
        //set database connection number
        int datasourceSize = ConfigurationManager.getInteger( Constants.JDBC_DATASOURCE_SIZE );

        //create specify the number of connections
        for (int i = 0; i < datasourceSize; i++) {
            String url = ConfigurationManager.getProperty( Constants.JDBC_URL );
            String user = ConfigurationManager.getProperty( Constants.JDBC_USER );
            String password = ConfigurationManager.getProperty( Constants.JDBC_PASSWORD );
            try {
                Connection conn = DriverManager.getConnection( url, user, password );
                datasource.push( conn );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get Connection function
     */
    public synchronized Connection getConnection() {
        while (datasource.size() == 0) {
            try {
                Thread.sleep( 10 );
            } catch (InterruptedException e) {
                e.getStackTrace();
            }
        }
        return datasource.poll();
    }

    /**
     * close databases connection
     */
    public synchronized void returnConnection(Connection conn) {
        if (conn != null) {
            try {
                if (conn.isClosed()) {
                    String url = ConfigurationManager.getProperty( Constants.JDBC_URL );
                    String user = ConfigurationManager.getProperty( Constants.JDBC_USER );
                    String password = ConfigurationManager.getProperty( Constants.JDBC_PASSWORD );
                    try {
                        Connection conn2 = DriverManager.getConnection( url, user, password );
                        datasource.push( conn2 );
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            datasource.push( conn );
        }
    }



}
