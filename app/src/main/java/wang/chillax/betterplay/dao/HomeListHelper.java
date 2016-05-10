package wang.chillax.betterplay.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MAC on 16/4/3.
 */
public class HomeListHelper extends SQLiteOpenHelper {

    private static final String tableName="wang.chillax.betterplay.dao.HomeListHelper";
    private static final String createTable="create table homelist(_id integer primary key,_name text,_price real,_note text,_logo text);";

    public HomeListHelper(Context context) {
        super(context, tableName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
