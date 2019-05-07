package io.particle.android.sdk;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.particle.sdk.app.R;

/**
 * Created by user562 on 2019-04-02.
 */

public class addViewAdapter extends BaseAdapter{

   // public AddActivity.OnCheckedChange onCheckedChange = new AddActivity.OnCheckedChange();
    Context context;
    ArrayList<Device> addDevices;
    LayoutInflater layoutInflater = null;
    int roomNum;
    int[] img;
    String [] roomName;


    public addViewAdapter(Context context, ArrayList<Device> init){
        this.context = context;
        this.roomNum = roomNum;
        addDevices = init;
        layoutInflater = LayoutInflater.from(context);

        img = new int[4];
        img[0] = R.drawable.light;
        img[1] = R.drawable.blind;
        img[2] = R.drawable.fan;
        img[3] = R.drawable.temperature;

        roomName = new String[4];
        roomName[0] = "Living Room";
        roomName[1] = "Bathroom";
        roomName[2] = "Bedroom";
        roomName[3] = "Study Room";
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View itemlayout = layoutInflater.inflate(R.layout.addlist_node,null);
        final Device device = addDevices.get(position);
        Log.d("super","position : " + position );
        TextView room_txtv = (TextView)itemlayout.findViewById(R.id.roomName);

       if( device.getIsFirst() == true){
            room_txtv.setText(roomName[device.getDeviceRoom(0)]);
        }
        else {
            room_txtv.setHeight(0);
        }
        ImageView imgv = (ImageView) itemlayout.findViewById(R.id.imageicon);
        TextView txtv = (TextView) itemlayout.findViewById(R.id.textview);
        imgv.setImageResource(img[device.getDeviceType()]);
        txtv.setText(device.getDeviceName());

        CheckBox cbox = (CheckBox) itemlayout.findViewById(R.id.checkbox);

        cbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("super","GG "+position);
                AddActivity.addNum[position] = isChecked;
                buttonView.setChecked(isChecked);
                notifyDataSetChanged();
            }
        });
        cbox.setChecked(AddActivity.addNum[position]);


        return itemlayout;

    }


}
