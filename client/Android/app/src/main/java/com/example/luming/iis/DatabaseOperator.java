package com.example.luming.iis;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luming on 2018/11/23.
 */

public class DatabaseOperator {
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    public DatabaseOperator(Context context)
    {
        dbHelper = new MyDatabaseHelper(context,"deviceDB",null,1);
        db = dbHelper.getWritableDatabase();
    }
    public void add(Device device)
    {
        db.execSQL("insert into device_table(name,ip,port) values(?,?,?)",new Object[]{device.getName(),device.getIp(),device.getPort()});
    }
    public List<Device> queryAll()
    {
        ArrayList<Device> list = new ArrayList<Device>();
        Cursor c = db.rawQuery("select name,ip,port from device_table", null);
        while (c.moveToNext()) {
            Device device  = new Device(c.getString(0),c.getString(1),c.getInt(2));
            list.add(device);
        }
        c.close();
        return list;
    }
}
