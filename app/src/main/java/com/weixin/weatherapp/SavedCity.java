package com.weixin.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SavedCity extends AppCompatActivity {

    ListView save_city_listview;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        save_city_listview = (ListView)findViewById(R.id.saved_city_listview);
        sharedPreferences = getSharedPreferences("savedInfo",MODE_PRIVATE);
        int count = sharedPreferences.getInt("number_of_saved_citys",-1);
        if (count == -1|| count ==0) Toast.makeText(SavedCity.this, "No city has been saved!", Toast.LENGTH_SHORT).show();
        else {
            String list_of_city[] = new String[count];
            for (int i =0;i < count;i++){
                String city_name = "city" + i;
                list_of_city[i]=sharedPreferences.getString(city_name,"");
            }
            ArrayAdapter adapter = new ArrayAdapter(SavedCity.this,android.R.layout.simple_list_item_1,list_of_city);
            save_city_listview.setAdapter(adapter);
            save_city_listview.setOnItemClickListener(itemClickedListener);

        }
    }
    private AdapterView.OnItemClickListener itemClickedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String city_name_ID = "city_ID"+position;
            Long city_ID = sharedPreferences.getLong(city_name_ID,-1);
            SavedCity.this.getIntent().putExtra("city_ID", city_ID);
            Intent i = new Intent(Intent.ACTION_PICK);
            i.putExtra("city_ID", city_ID);
            setResult(Activity.RESULT_OK,i);
            SavedCity.this.finish();

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_in_saved_cities, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            sharedPreferences.edit().putInt("number_of_saved_citys",0).commit();
            save_city_listview.setVisibility(View.INVISIBLE);
            Toast.makeText(SavedCity.this, "All saved cities have been cleared", Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }

}
