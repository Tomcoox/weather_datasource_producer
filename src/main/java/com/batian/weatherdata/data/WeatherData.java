package com.batian.weatherdata.data;

import com.batian.weatherdata.conf.ConfigurationManager;
import com.batian.weatherdata.constant.Constants;
import com.batian.weatherdata.jdbc.JDBCHelper;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by Ricky on 2018/3/2
 *
 * @author Administrator
 */
public class WeatherData {
    @Test
    public void test() throws Exception {
        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
        final StringBuffer sb = new StringBuffer();

        jdbcHelper.executeQuery("select city_key from city_coding where city_level=?",
                new Object[]{0},
                new JDBCHelper.QueryCallback() {
                    @Override
                    public void process(ResultSet rs) throws Exception {
                        if (rs.next()) {
                            System.out.println(rs.getString("city_key"));
                            sb.append(rs.toString());
                        }
                    }
                });

        String[] keyArr = new String[]{"101010100", "101040100"};
        for (String key : keyArr) {
            String result;
            result = getCityWeather( key );
            System.out.println(result);

        }
    }

    @Test
    public void test2() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        StringBuffer sb = new StringBuffer();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("connection mysql");
            conn = DriverManager.getConnection(ConfigurationManager.getProperty(Constants.JDBC_URL), ConfigurationManager.getProperty(Constants.JDBC_USER),ConfigurationManager.getProperty(Constants.JDBC_PASSWORD));

            System.out.println("实例化Satement");
            stmt = conn.createStatement();
            String sql;
            sql = "select city_key from city_coding where city_level BETWEEN 0 AND 1";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String city_coding = rs.getString("city_key");
                sb.append(city_coding + ",");
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        String[] result = sb.toString().split(",");
        for (String key : result) {
            String result1;
            result1 = getCityWeather( key );
            System.out.println(result1);

        }
    }

    public static String getCityWeather(String cityKey) throws Exception {

        InputStream is;
        BufferedReader br;
        HttpURLConnection uRLConnection;
        URL url = new URL( "http://wthrcdn.etouch.cn/weather_mini?citykey=" + cityKey );
        uRLConnection = (HttpURLConnection) url.openConnection();
        uRLConnection.setDoOutput( true );
        uRLConnection.setRequestProperty( "Content-Type", "application/json;charset=UTF-8" );
        uRLConnection.connect();
        is = uRLConnection.getInputStream();

        GZIPInputStream gzip = new GZIPInputStream( is );
        br = new BufferedReader( new InputStreamReader( gzip, "UTF-8" ) );
        StringBuffer jsonResult = new StringBuffer();
        String readLine;
        while ((readLine = br.readLine()) != null) {
            jsonResult.append( readLine );
        }
        is.close();
        br.close();
        uRLConnection.disconnect();
        return jsonResult.toString();
    }



}
