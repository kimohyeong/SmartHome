package io.particle.android.sdk.cloudDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.Device;

public class DBhelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "deviceManager";
    private static final String TABLE_DEVICE = "devices";

    /**
     * Columns of Database
     * - String deviceName, deviceState
     * - int type, roomNum
     */
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ROOMNUM = "roomNum";

    private int ID = 0;

    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DEVICES_TABLE = "CREATE TABLE " + TABLE_DEVICE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_ROOMNUM + "INTEGER, " + KEY_TYPE + " INTEGER" + ")";
        db.execSQL(CREATE_DEVICES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
        onCreate(db);
    }

    /**
     * CURD Functions
     */
    public void addDevice(Device device) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, device.getDeviceName());
        values.put(KEY_NAME, device.getDeviceName());
        values.put(KEY_ROOMNUM, device.getDeviceRoomNum());
        values.put(KEY_TYPE, device.getDeviceType());

        db.insert(TABLE_DEVICE, null, values);
        db.close();
    }

    public Device getDevice(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DEVICE, new String[] {
                KEY_ID, KEY_NAME, KEY_ROOMNUM, KEY_TYPE}, KEY_ID + "=?",
                new String[] {String.valueOf(id)},null,null,null,null);
        if(cursor != null)
            cursor.moveToFirst();

        Device device = new Device();
        device.setDeviceId(Integer.parseInt(cursor.getString(0)));
        device.setDeviceName(cursor.getString(1));
        device.setDeviceRoomNum(Integer.parseInt(cursor.getString(2)));
        device.setDeviceType(Integer.parseInt(cursor.getString(3)));

        return device;
    }

    public List<Device> getAllDevices() {
        List<Device> deviceList = new ArrayList<Device>();

        String selectQuery = "SELECT * FROM "+TABLE_DEVICE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                Device device = new Device();
                device.setDeviceId(Integer.parseInt(cursor.getString(0)));
                device.setDeviceName(cursor.getString(1));
                device.setDeviceRoomNum(Integer.parseInt(cursor.getString(2)));
                device.setDeviceType(Integer.parseInt(cursor.getString(3)));

                deviceList.add(device);
            } while(cursor.moveToNext());
        }
        return deviceList;
    }

    public int updateDevice(Device device) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, device.getDeviceName());
        values.put(KEY_ROOMNUM, device.getDeviceRoomNum());
        values.put(KEY_TYPE, device.getDeviceType());

        return db.update(TABLE_DEVICE, values, KEY_ID + " = ?",
                new String[] {String.valueOf(device.getDeviceId())});
    }

    public void deleteDevice(Device device) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DEVICE, KEY_ID + " = ? ",
                new String[] {String.valueOf(device.getDeviceId())});
        db.close();
    }

    public int getDevicesCount() {
        String countQuery = "SELECT * FROM " + TABLE_DEVICE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }
}
