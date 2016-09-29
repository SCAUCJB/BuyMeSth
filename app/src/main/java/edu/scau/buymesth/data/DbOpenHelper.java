package edu.scau.buymesth.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.scau.buymesth.data.bean.Collect;
import edu.scau.buymesth.data.bean.Follow;

/**
 * Created by John on 2016/9/28.
 */

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    private static final String CREATE_COLLECT = ""
            + "CREATE TABLE " + Collect.TABLE + "("
            + Collect.ID + " INTEGER NOT NULL PRIMARY KEY,"
            + Collect.USER_ID + " TEXT NOT NULL,"
            + Collect.REQUEST_ID + " TEXT NOT NULL "
            + ")";
    private static final String CREATE_FOLLOW = ""
            + "CREATE TABLE " + Follow.TABLE + "("
            + Follow.ID + " INTEGER NOT NULL PRIMARY KEY,"
            + Follow.TO_USER_ID + " TEXT NOT NULL  "
            + Follow.FROM_USER_ID + " TEXT NOT NULL,"
            + ")";

    public DbOpenHelper(Context context) {
        super(context, "request_detail.db", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COLLECT);
        db.execSQL(CREATE_FOLLOW);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
