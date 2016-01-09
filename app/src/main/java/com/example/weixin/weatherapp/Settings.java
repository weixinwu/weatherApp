package com.example.weixin.weatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final String setting_menu[] ={"Units","Saved locations","Version"};
        lv = (ListView)findViewById(R.id.listview_settings);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listview_settings,setting_menu);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (setting_menu[position].equals("Units")) {
                    ListView listView = new ListView(Settings.this);
                    listView.setAdapter(new ArrayAdapter<String>(Settings.this, android.R.layout.simple_list_item_1, new String[]{"Celsius", "Fahrenheit",}));
                    AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                    builder.setTitle("Select an unit");
                    builder.setView(listView);
                    final Dialog dialog = builder.create();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                Toast.makeText(Settings.this, "Celsius", Toast.LENGTH_SHORT).show();
                                getSharedPreferences("savedInfo",MODE_PRIVATE).edit().putInt("cel_or_fah", 1).commit();
                                dialog.hide();
                                Settings.this.finish();
                                setResult(Activity.RESULT_OK);
                                //sharepreference to store the units
                            } else {
                                Toast.makeText(Settings.this, "Fahrenheit", Toast.LENGTH_SHORT).show();
                                getSharedPreferences("savedInfo",MODE_PRIVATE).edit().putInt("cel_or_fah", 0).commit();
                                dialog.hide();
                                Settings.this.finish();
                                setResult(Activity.RESULT_OK);
                            }
                        }
                    });
                    dialog.show();
                }
            }
        });

    }

}