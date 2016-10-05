package edu.scau.buymesth.notice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jammy on 2016/9/21.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_TABLE = "msgtable";
    public static final String DB_NAME ="chatmsg.db";

    private String DATABASE_CREATE = "create table if not exists " + DATABASE_TABLE + "("
            + "id integer primary key autoincrement,"
            + "objectId varchar,"
            + "orderJson varchar,"
            + "status varchar);";


    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
