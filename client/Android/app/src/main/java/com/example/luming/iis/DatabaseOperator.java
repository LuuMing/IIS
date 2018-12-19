package com.example.luming.iis;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        db.execSQL("insert into device(name,ip,port) values(?,?,?)",new Object[]{device.getName(),device.getIp(),device.getPort()});
    }
    public List<Device> queryAllDevice()
    {
        ArrayList<Device> list = new ArrayList<Device>();
        Cursor c = db.rawQuery("select name,ip,port from device", null);
        while (c.moveToNext()) {
            Device device  = new Device(c.getString(0),c.getString(1),c.getInt(2));
            list.add(device);
        }
        c.close();
        return list;
    }
    public void addLog(String name, String send, String value)
    {
        db.execSQL("insert into data (module_name,send,value) values(?,?,?)",new Object[]{name,send,value});
    }
    public List<Pair<String,Float>> queryLog(String module_name,String num)
    {
        ArrayList<Pair<String,Float>> list = new ArrayList<>();
        Cursor c = db.rawQuery("select  time,value from data  where module_name = \"" + module_name+"\" order by time desc limit " + num ,null);
        while (c.moveToNext())
        {
            list.add(new Pair<>(c.getString(0),c.getFloat(1)));
        }
        return list;
    }
    public String queryRecentTime()
    {
        Cursor c = db.rawQuery("select time from data order by time desc limit 1",null);
        while (c.moveToNext())
        {
            return c.getString(0);
        }
        return "NULL";
    }
    public String queryRecentData(String start)
    {
        String sql;
        if(start.equals("NULL"))
            sql = "select * from data";
        else
            sql = "select * from data where time > \""+start + "\"";
        Log.e("sql",sql);
        Cursor c = db.rawQuery(sql,null);
        int num = c.getColumnCount();
        JSONArray array=new JSONArray();
        while(c.moveToNext())
        {
            JSONObject mapOfColValues = new JSONObject();
            for(int i = 0; i < num; ++i) {
                try {
                    mapOfColValues.put(c.getColumnName(i),c.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            array.put(mapOfColValues);
        }
        Log.e("json",array.toString());
        return array.toString();
    }

}
