package wang.chillax.betterplay.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import wang.chillax.betterplay.bmob.TopImage;

/**
 * Created by MAC on 16/4/3.
 */
public class TopImageDao {

    private static final String insertSql="insert into topimages(_id,_url,_title,_address) values(?,?,?,?)";
    private TopImageHelper mHelper;
    private Context mContext;

    public TopImageDao(Context context){
        mContext=context;
        mHelper=new TopImageHelper(context);
    }
    public void insert(TopImage image){
        if (image==null)return;
        SQLiteDatabase db=mHelper.getWritableDatabase();
        if(db.isOpen()){
            db.execSQL(insertSql,new Object[]{image.getObjectId(),image.getImageUrl()!=null?image.getImageUrl():image.getImage().getFileUrl(mContext),image.getTitle(),image.getAddress()});
            db.close();
        }
    }
    public boolean isEmpty() {
        boolean empty = true;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if (db.isOpen()) {
            if (mHelper.getReadableDatabase()
                    .rawQuery("select * from topimages", null).getCount() > 0) {
                empty = false;
            }
            db.close();
        }
        return empty;
    }
    public void clear() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from topimages");
            db.close();
        }
    }

    public List<TopImage> getTopImages() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        if (db.isOpen()) {
            List<TopImage> lists=new ArrayList<>();
            Cursor c=db.rawQuery("select * from topimages",null);
            c.moveToFirst();
            while (!c.isAfterLast()){
                lists.add(new TopImage(c.getString(1),c.getString(2),c.getString(3)));
                c.moveToNext();
            }
            db.close();
            if(lists.size()>0)return lists;
            return null;
        }
        return null;
    }
}
