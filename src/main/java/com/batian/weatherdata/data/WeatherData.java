package com.batian.weatherdata.data;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Created by Ricky on 2018/3/2
 *
 * @author Administrator
 */
public class WeatherData {
    @Test
    public void test() throws Exception {
        String[] keyArr = new String[]{"101010100", "101040100"};
        for (String key : keyArr) {
            String result;
            result = getCityWeather( key );
            System.out.println(result);

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
