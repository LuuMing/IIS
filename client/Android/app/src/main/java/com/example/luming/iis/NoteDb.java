package com.example.luming.iis;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by luming on 2018/11/23.
 */
public class NoteDb extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "device";
    public static final String CONTENT = "content";
    public static final String ID = "_id";
    public static final String TIME = "time";
    public NoteDb(Context context) {
        super(context, "notes", null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql ="create table "+TABLE_NAME+" ( "+ID+" integer primary key AUTOINCREMENT, "+CONTENT
                +" TEXT NOT NULL, "+TIME+" TEXT NOT NULL )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}