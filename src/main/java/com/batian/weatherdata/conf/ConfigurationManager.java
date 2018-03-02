package com.batian.weatherdata.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Ricky on 2018/3/2
 *
 * @author Administrator
 */
public class ConfigurationManager {
    private static Properties prop = new Properties();

    static {
        try {
            InputStream in = ConfigurationManager.class
                    .getClassLoader().getResourceAsStream( "weather.properties" );
            prop.load( in );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get value By key
     * @param key
     * @return
     */

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }


    /**
     * get Integer value By key
     * @param key
     * @return value
     */
    public static Integer getInteger(String key) {
        String value = getProperty(key);
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * get Boolean value By key
     * @param key
     * @return value
     */
    public static Boolean getBoolean(String key) {
        String value = getProperty(key);
        try {
            return Boolean.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * get Loog value By key
     * @param key
     * @return
     */
    public static Long getLong(String key) {
        String value = getProperty(key);
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
