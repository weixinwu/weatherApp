package com.example.weixin.weatherapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    ImageView iv;
    ProgressDialog dialog;
    EditText editText;
    Button search_btn;
    String one_day_forecast_addr;
    String addr_forecast;
    String country;
    String city, description;
    double temp[], windSpeed;
    TextView tv_city, tv_description, tv_temp, tv_min_max_temp;
    ListView lv, lv_for_detail;
    int cel_or_fah;
    long city_ID ;
    int forecast[];
    String forecast_main_condition[];
    WeatherData weatherData;
    ArrayList<String> listItem_for_forecast, listItem_for_detail;
    Location location;
    LocationManager locationManager;
    LocationListener locationListener;
    SharedPreferences sharedpreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (city != null || !(city.equals(""))) {
                    new GetWeatherInfo().execute();
                } else
                    Toast.makeText(MainActivity.this, "Please enter a city and retry", Toast.LENGTH_SHORT).show();
            }
        });
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        tv_city = (TextView) findViewById(R.id.tv_city_name);
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_temp = (TextView) findViewById(R.id.tv_temperature);
        tv_min_max_temp = (TextView) findViewById(R.id.tv_min_max_temp);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        lv = (ListView) findViewById(R.id.list_item_for_forecasts);
        ///set up location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                MainActivity.this.location = location;
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {

            }
            @Override
            public void onProviderDisabled(String provider) {
                Log.d("J","location service is not available");
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        };

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        drawer.setScrimColor(getResources().getColor(R.color.colorPrimary));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ////////////////////////////////////////////////////////
        search_btn = (Button) findViewById(R.id.search_btn);
        iv = (ImageView) findViewById(R.id.imageView_weather_icon);
        editText = (EditText) findViewById(R.id.location_text);
        sharedpreference = getSharedPreferences("savedInfo",MODE_PRIVATE);

        //setting the unit and number of saved city if are not previously set
        if (sharedpreference.getInt("cel_or_fah",-1)== -1) {
            sharedpreference.edit().putInt("cel_or_fah", 1).commit();
        }
        if (sharedpreference.getInt("number_of_saved_citys",-1)==-1){
            sharedpreference.edit().putInt("number_of_saved_citys", 0).commit();
        }
        listItem_for_detail = new ArrayList<String>();
        weatherData = new WeatherData();
        //get_weather_by_GPS();
        //startActivityForResult(new Intent(MainActivity.this,SavedCity.class),15);

    }

    class GetWeatherInfo extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String forecast_result="";
            String result="";
            try {
                listItem_for_forecast = new ArrayList<String>();
                result += weatherData.parse(one_day_forecast_addr);
                forecast_result+=weatherData.parse(addr_forecast);
                Log.d("J", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONObject forecastJsonObject = new JSONObject(forecast_result);
                city=weatherData.getCityName(jsonObject);
                city_ID=weatherData.getCityID(jsonObject);
                country = weatherData.getCountry(jsonObject);
                description = weatherData.getDescription(result);
                temp = weatherData.getTemp(jsonObject);
                //getting detail information
                weatherData.getSunActs(jsonObject);
                listItem_for_detail.add(weatherData.getSunrise_sunset()[0]);
                listItem_for_detail.add(weatherData.getSunrise_sunset()[1]);
                windSpeed = weatherData.getWindSpeed(jsonObject);
                forecast = weatherData.getForecast(forecastJsonObject);
                forecast_main_condition = weatherData.getForecast_main_condition();
                Log.d("J", "after getting the forecast_main");
                String weekDay;
                SimpleDateFormat dayFormat = new SimpleDateFormat("EE", Locale.US);
                Calendar calendar = Calendar.getInstance();
                weekDay = dayFormat.format(calendar.getTime());
                for (int i = 0; i < 7; i++) {
                    String str;
                    weekDay = dayFormat.format(calendar.getTime());
                    calendar.add(Calendar.DAY_OF_WEEK, 1);
                    str = weekDay + ": " + temp_unit(forecast[2 * i]) + "\u00B0c" + " \u007E " + temp_unit(forecast[2 * i + 1]) + "\u00B0c         " + forecast_main_condition[i];
                    listItem_for_forecast.add(str);
                }
                //get weather icon
               return weatherData.getIcon(jsonObject);

            } catch (Exception e) {
                Toast.makeText(getBaseContext(),"Please check the internet and try again",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String v) {
            tv_city.setText(city + ", " + country);
            tv_description.setText(description);
            tv_temp.setText((int) (temp_unit(temp[0])) + "\u00B0c");
            tv_min_max_temp.setText((int) (temp_unit(temp[1])) + "\u00B0c" + " \u007E " + (int) (temp_unit(temp[2])) + "\u00B0c");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, listItem_for_forecast);
            lv.setAdapter(adapter);
            String str[] = {"Wind speed:", "Sunrise: ", "Sunset:", "Humidity:"};
            String str2[] = new String[5];
            str2[0] = windSpeed + " meter/sec";
            str2[1] = weatherData.getSunrise_sunset()[0];
            str2[2] = weatherData.getSunrise_sunset()[1];
            str2[3] = temp[4] + "%";

            Log.d("J", "before setting the adapter for detail");
            ListAdapter adapter_detail = new custom_listviewAdap(getBaseContext(), str, str2);
            lv_for_detail.setAdapter(adapter_detail);

            String weather_icon = "weather_"+v;
            iv.setImageResource(getResources().getIdentifier(weather_icon, "drawable", "com.example.weixin.weatherapp"));
            lv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            dialog.hide();


        }

        @Override
        protected void onPreExecute() {
            dialog.show();

            lv_for_detail = (ListView) findViewById(R.id.detail_listview);


        }
    }

    public int temp_unit(double input) {
        cel_or_fah = sharedpreference.getInt("cel_or_fah",-1);
        if (cel_or_fah == 0) {
            double temp = (input-273.15)*1.80  + 32;
            if (temp<0)
                temp= -(Math.floor(Math.abs(temp)+0.5));
            else temp = (Math.floor((temp)+0.5));
            return (int) temp;
        } else {
            double return_val = (double) Math.round(((input - 273.15) * 10d) / 10d);
            return (int) return_val;
        }
    }

    public void search_btn_onClick(View v) throws Exception {

        city = editText.getText().toString();
        city = city.replaceAll("\\s", "");
        Log.d("J","city is " + city+"end");
        if (city != null && !(city.equals(""))) {
            city = city.substring(0, 1).toUpperCase() + city.substring(1).toLowerCase();
            addr_forecast ="http://api.openweathermap.org/data/2.5/forecast/daily?q="+city+"&units=metric&mode=json&cnt=7&appid=6eb5092a2bd660c2d0830e749f20f99d";
            one_day_forecast_addr = "http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=6eb5092a2bd660c2d0830e749f20f99d";
            new GetWeatherInfo().execute();
        } else {
            Toast.makeText(MainActivity.this, "Please enter a city and retry", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_GPS) {
            get_weather_by_GPS();
        }else if (id ==R.id.action_Add){
            save_the_city();
        }

        return super.onOptionsItemSelected(item);
    }

    public void get_weather_by_GPS() {
        if ((!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))&&(!(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))))
        {
            Toast.makeText(this,"Please enable location service",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
                }
                else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        Log.d("J", "location service is not available");
                        Toast.makeText(MainActivity.this, "Location is not available", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, Settings.class));
                    }
                    locationManager.removeUpdates(locationListener);
                    String geoLocation = "lat=" + location.getLatitude() + "&lon=" + location.getLongitude();
                    one_day_forecast_addr = "http://api.openweathermap.org/data/2.5/weather?" + geoLocation + "&appid=6eb5092a2bd660c2d0830e749f20f99d";
                    addr_forecast = "http://api.openweathermap.org/data/2.5/forecast/daily?" + geoLocation + "&units=metric&mode=json&cnt=7&appid=6eb5092a2bd660c2d0830e749f20f99d";
                    new GetWeatherInfo().execute();
                }
            }

        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode ==10){
            Toast.makeText(MainActivity.this,"Please allow the application to access the location service and try again", Toast.LENGTH_LONG).show();
            return ;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_savedCity) {
            drawer.closeDrawers();
            startActivityForResult(new Intent(MainActivity.this,SavedCity.class),15);
        } else if (id == R.id.Setting) {
            drawer.closeDrawers();
            startActivityForResult(new Intent(MainActivity.this, Settings.class), 16);
            //startActivity(new Intent(MainActivity.this,Settings.class));

        } else if (id == R.id.nav_share) {

            Toast.makeText(getBaseContext(), "sharing..", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    SharedPreferences getSharedPref(){
        return sharedpreference;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 15){
            if (resultCode == Activity.RESULT_OK) {
                if (data.getExtras().containsKey("city_ID")) {
                    city_ID = data.getExtras().getLong("city_ID");
                    String id = "id=" + city_ID;
                    one_day_forecast_addr = "http://api.openweathermap.org/data/2.5/weather?" + id + "&appid=6eb5092a2bd660c2d0830e749f20f99d";
                    addr_forecast = "http://api.openweathermap.org/data/2.5/forecast/daily?" + id + "&units=metric&mode=json&cnt=7&appid=6eb5092a2bd660c2d0830e749f20f99d";
                    new GetWeatherInfo().execute();
                }
            }
        }
        else if (requestCode == 16){
            new GetWeatherInfo().execute();
        }
    }
    private void save_the_city(){
        if (city ==null||city.equals("")){
            Toast.makeText(MainActivity.this, "Please chose search the city first", Toast.LENGTH_SHORT).show();
        }
        else {
            int count = sharedpreference.getInt("number_of_saved_citys", -1);
            boolean isExist = false;
            for (int i =0;i < count ; i ++){
                Long temp = sharedpreference.getLong("city_ID"+i,-1);
                if (temp == city_ID){
                    isExist=true;
                    Toast.makeText(getBaseContext(),"city exists",Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            if (!isExist) {
                String city_name = "city" + count;
                String city_name_ID = "city_ID" + count;
                sharedpreference.edit().putLong(city_name_ID, city_ID).commit();
                sharedpreference.edit().putString(city_name, city).commit();
                count++;
                sharedpreference.edit().putInt("number_of_saved_citys", count).commit();
                Toast.makeText(MainActivity.this, "The current city is saved.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
