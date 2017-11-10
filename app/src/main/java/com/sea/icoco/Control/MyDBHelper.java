package com.sea.icoco.Control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper
{
    private final static String DATABASE_NAME = "icoco";
    private final static int DATABASE_VERSION = 1;

    public MyDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sql = "CREATE TABLE `message_aa` (" +
                "  `_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  `from` INTEGER DEFAULT NULL," +
                "  `to` INTEGER DEFAULT NULL," +
                "  `money` INTEGER DEFAULT NULL," +
                "  `time` datetime DEFAULT NULL," +
                "  `read` INTEGER DEFAULT '0'" +
                ");";
        db.execSQL(sql);
        Log.d("debug MySQLite","create db success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }
}