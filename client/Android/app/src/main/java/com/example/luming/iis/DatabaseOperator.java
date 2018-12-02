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
    private static MyDatabaseHelper dbHelper;
    private static SQLiteDatabase db;
    private static DatabaseOperator databaseOperator;
    private DatabaseOperator(Context context)
    {
        dbHelper = new MyDatabaseHelper(context,"IIS_DB",null,1);
        db = dbHelper.getWritableDatabase();
    }
    public static DatabaseOperator getInstance(Context context)
    {
        if(databaseOperator == null)
        {
            databaseOperator = new DatabaseOperator(context);
        }
        return databaseOperator;
    }

    public void addDevice(Device device)
    {
        db.execSQL("insert into device_table(name,ip,port) values(?,?,?)",new Object[]{device.getName(),device.getIp(),device.getPort()});
    }
    public List<Device> queryAllDevice()
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
