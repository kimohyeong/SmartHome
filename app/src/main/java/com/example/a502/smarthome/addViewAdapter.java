package com.example.a502.smarthome;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user562 on 2019-04-02.
 */

public class addViewAdapter extends BaseAdapter{

    Context context;
    ArrayList<Device> addDevices;
    LayoutInflater layoutInflater = null;
    int[] img;

    public addViewAdapter(Context context, ArrayList<Device> init){
        this.context = context;
        addDevices = init;
        layoutInflater = LayoutInflater.from(context);

        img = new int[4];
        img[0] = R.drawable.light;
        img[1] = R.drawable.blind;
        img[2] = R.drawable.fan;
        img[3] = R.drawable.temperature;
    }
    @Override
    public int getCount() {
        return addDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return addDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemlayout = layoutInflater.inflate(R.layout.addlist_node,null);
        Device device = addDevices.get(position);
        ImageView imgv = (ImageView)itemlayout.findViewById(R.id.imageicon);
        TextView txtv = (TextView)itemlayout.findViewById(R.id.textview);
        imgv.setImageResource(img[device.type]);
        txtv.setText(device.name);
        return itemlayout;
    }
}
