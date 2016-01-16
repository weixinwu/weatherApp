package com.example.weixin.weatherapp;


import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Weixin on 2016-01-06.
 */
public class WeatherData {

    private Location location;
    private InputStream inputStream;
    private String result;
    private long sunrise,sunset;
    String sunrise_sunset[];
    private double temp[];
    private String city_name,country,description;
    private String forecast_main_condition[];


    public String parse(String addr)  {
        addr = addr.replaceAll(" ","%20");

        URL url;
        String return_value = null;
        URLConnection urlConnection;
        InputStream in = null;

        try {
            url = new URL(addr);
            urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            in = httpURLConnection.getInputStream();
            return_value = inputStreamToString(in);
            return return_value;
        } catch (Exception e) {
            Log.d("J","in the exception in parse method");
            return null;
        }

    }


    public String getIcon(JSONObject input)  {
        JSONArray jsonArray = null;
        try {
            jsonArray = input.getJSONArray("weather");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            return jsonObject.getString("icon");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("J", "exception in getIcon");
            return null;
        }

    }
    public int[] getForecast(JSONObject input)  {
        int return_val[] = new int[14];
        forecast_main_condition = new String[7];
        JSONArray jsonArray = null;
        try {
            jsonArray = input.getJSONArray("list");
            int length = 7;
            if (length>jsonArray.length()) length = jsonArray.length();
            for (int i = 0;i < length;i++ ){
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                JSONObject jsonObject_temp = jsonObject.getJSONObject("temp");
                JSONArray jsonArray1_weather = jsonObject.getJSONArray("weather");
                forecast_main_condition[i] = jsonArray1_weather.optJSONObject(0).getString("main");
                double temp_min = jsonObject_temp.getDouble("min")+273.15;
                double temp_max = jsonObject_temp.getDouble("max")+273.15;

                if (temp_min<0)
                    temp_min= -(Math.floor(Math.abs(temp_min)+0.5));
                else temp_min = (Math.floor((temp_min)+0.5));
                if (temp_max<0)  temp_max=-(Math.floor(Math.abs(temp_max)+0.5));
                else
                    temp_max=(Math.floor((temp_max)+0.5));
                return_val[2*i]= (int) temp_min;
                return_val[2*i+1]= (int) temp_max;
            }
            return return_val;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("J", "exception in getForecast");
            return null;
        }
    }
    public long getCityID(JSONObject input){
        try {
            return input.getLong("id");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public double getWindSpeed(JSONObject input)  {
        double wind_speed = 0;
        try {
            wind_speed = input.getJSONObject("wind").getDouble("speed");
            wind_speed = (Math.floor((wind_speed*10)+0.5))/10;
            return wind_speed;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("J", "exception in getTemp");
            return 0;
        }


    }
    public double[] getTemp(JSONObject input)  {
        double temp[] = new double[5];
        JSONObject jsonObject = null;
        try {
            jsonObject = input.getJSONObject("main");
            temp[0]= jsonObject.getDouble("temp");
            temp[1]=jsonObject.getDouble("temp_min");
            temp[2] = jsonObject.getDouble("temp_max");
            temp[3]=jsonObject.getDouble("pressure");
            temp[4]=jsonObject.getDouble("humidity");
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("J", "exception in getTemp");
            return null;
        }

    }
    public String getDescription(String input)  {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(input);
            String return_val = jsonObject.getJSONArray("weather").optJSONObject(0).getString("description");
            return return_val;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("J", "exception in getDescription");
            return null;
        }
    }
    public String getCountry(JSONObject input)  {
        JSONObject jObject = null;

        try {
            jObject = ((JSONObject) (input.get("sys")));
            return jObject.getString("country");
        } catch (JSONException e) {
            Log.d("J", "exception in getCountry");
            e.printStackTrace();
            return null;
        }
    }
    public String getCityName(JSONObject input) {
        String temp = "";
        String ret = null;
        try {
            ret = input.getString("name");
            return ret;
        } catch (JSONException e) {
            Log.d("J", "exception in getCityName");
            e.printStackTrace();
            return null;
        }

    }

    public String[] getSunrise_sunset() {
        return sunrise_sunset;
    }

    public String[] getSunActs(JSONObject input)  {
        try {
            sunrise = input.getJSONObject("sys").getLong("sunrise");

            sunset = input.getJSONObject("sys").getLong("sunset");
            String return_value[] = new String[2];
            long timeStamp = sunrise * 1000L;
            Date date = new Date(timeStamp);
            return_value[0] = new SimpleDateFormat("hh:mm").format(date) + "AM";
            timeStamp = sunset * 1000L;
            date = new Date(timeStamp);
            return_value[1] = new SimpleDateFormat("hh:mm").format(date) + "PM";
            sunrise_sunset = return_value;

            return null;
        }
            catch (Exception e) {
                Log.d("J", "exception in getSUnact");
                e.printStackTrace();
                return null;
            }
    }
    private String inputStreamToString(InputStream in){

        String return_value = "";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringBuilder = new StringBuilder();
        String next_line;
        try {
            while ((next_line = bufferedReader.readLine())!=null){
                stringBuilder.append(next_line);
            }
            return_value+= stringBuilder.toString();
            return return_value;
        } catch (Exception e) {
            e.printStackTrace();
            return_value=null;
        }
        return null;
    }

    public boolean isCityFound(JSONObject input){
        try {
            if (input.getInt("cod")!=404){
                return true;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String[] getForecast_main_condition() {
        return forecast_main_condition;
    }

    public String getDescription() {
        return description;
    }

    public String getCountry() {
        return country;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }



    public Location getLocation() {
        return location;
    }

    public InputStream getInputStream() {
        return inputStream;
    }


}
