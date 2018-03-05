package com.batian.weatherdata.jdbc;

import com.batian.weatherdata.conf.ConfigurationManager;
import com.batian.weatherdata.constant.Constants;

import java.sql.*;
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

    /**
     * 增删改查
     * 1.增删改SQL语句的执行方法
     */
    public int executeUpdata(String sql ,Object[] params) {
        int rtn = 0;
        Connection conn = null;
        PreparedStatement pstmt;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            rtn = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (conn != null) {
                datasource.push(conn);
            }
        }

        return rtn;
    }

    /**
     * execute select SQL
     */
    public void executeQuery(String sql, Object[] params, QueryCallback callback) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            rs = pstmt.executeQuery();
            callback.process(rs );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                datasource.push(conn);
            }
        }
    }

    /**
     * 静态内部类:查询回调接口
     */
    public static interface QueryCallback {
        /**
         *         handle query result
         *         @param rs
         */
        void process(ResultSet rs) throws Exception;
    }

}
