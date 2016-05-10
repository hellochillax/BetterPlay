package wang.chillax.betterplay.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MAC on 16/4/3.
 */
public class TopImageHelper extends SQLiteOpenHelper {

    private static final String tableName="wang.chillax.betterplay.dao.TopImageHelper";
    private static final String createTable="create table topimages(_id varchar(15) primary key,_url text,_title text,_address text);";

    public TopImageHelper(Context context) {
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
