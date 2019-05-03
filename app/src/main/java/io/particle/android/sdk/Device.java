package io.particle.android.sdk;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by user562 on 2019-04-03.
 */

public class Device implements Serializable {
    private String name;                // device이름
    private int type;                   //0:led 1:blind 2:fan 3:temp
    private int roomNum;
    private int[] room = new int[3];    //0:기본 1~2추가
    private String state;               // device상태
    private Drawable deviceImgDrawable; // device image
    private boolean isFirst;             //fist인지
    private boolean cBoxState;            //checkbox상태

    public String getDeviceName() {
        return name;
    }
    public void setDeviceName(String _name) {
        this.name = _name;
    }

    public int getDeviceType() {
        return type;
    }
    public void setDeviceType(int _type) {
        this.type = _type;
    }

    public String getDeviceState() {
        return state;
    }
    public void setDeviceState(String _state) {
        this.state = _state;
    }

    public int getDeviceRoom(int roomNum) {
        return room[roomNum];
    }
    public void setDeviceRoom(int basicRoomNum, int addedRoomNum) {
        room[basicRoomNum] = addedRoomNum;
    }

    public int getDeviceRoomNum() {return roomNum;}
    public void setDeviceRoomNum(int roomNum) { this.roomNum = roomNum; }

    public Drawable getDeviceImgDrawable() {
        return deviceImgDrawable;
    }
    public void setDeviceImgDrawable(Drawable _deviceImgDrawable) {
        this.deviceImgDrawable = _deviceImgDrawable;
    }

    public boolean getIsFirst(){ return isFirst;};
    public void setIsFirst(Boolean _isFirst){
        this.isFirst = _isFirst;
    }

    public boolean getCBoxState(){ return cBoxState;};
    public void setCBoxState(Boolean _cBoxState){
        this.cBoxState = _cBoxState;
    }
}
