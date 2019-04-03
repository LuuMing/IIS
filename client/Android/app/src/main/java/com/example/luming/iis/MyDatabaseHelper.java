package com.example.luming.iis;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by luming on 2018/11/23.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    public MyDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql ="create table device ( id integer primary key AUTOINCREMENT, name "
                +" TEXT NOT NULL UNIQUE, ip TEXT NOT NULL ,port integer NOT NULL )";
        String sql2 = "create table data ( id integer primary key AUTOINCREMENT, module_name "
                +" TEXT NOT NULL, send TEXT default null ,value TEXT,time TimeStamp DEFAULT(datetime('now', 'localtime') ))";
        db.execSQL(sql);
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}