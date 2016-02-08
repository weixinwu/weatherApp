package com.weixin.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
 * Created by Weixin on 2016-01-07.
 */
public class custom_listviewAdap extends ArrayAdapter<String> {
    String[] sec_text;
    public custom_listviewAdap(Context context, String[] str,String []str2) {
        super(context,R.layout.listview_item_for_detail,str);
        this.sec_text = str2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View v = layoutInflater.inflate(R.layout.listview_item_for_detail, parent, false);
        TextView tv1=(TextView)v.findViewById(R.id.tv_left);
        TextView tv2=(TextView)v.findViewById(R.id.tv_right);

        tv1.setText(getItem(position));
        tv2.setText(sec_text[position]);
        return v;
    }
}
