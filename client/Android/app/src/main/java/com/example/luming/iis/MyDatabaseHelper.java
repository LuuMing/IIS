package com.example.luming.iis;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by luming on 2018/11/23.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "device_table";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String IP = "ip";
    public static final String PORT = "port";
    public MyDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql ="create table "+TABLE_NAME+" ( "+ID+" integer primary key AUTOINCREMENT, "+NAME
                +" TEXT NOT NULL, "+IP+" TEXT NOT NULL ,"+PORT+" integer NOT NULL )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}