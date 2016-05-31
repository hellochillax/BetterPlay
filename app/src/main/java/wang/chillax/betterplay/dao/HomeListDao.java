package wang.chillax.betterplay.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import wang.chillax.betterplay.bmob.GroupFriend;
import wang.chillax.betterplay.bmob.TopImage;

/**
 * Created by MAC on 16/4/3.
 */
public class HomeListDao {

    private static final String insertSql="insert into homelist(_id,_name,_price,_note,_logo,_priority) values(?,?,?,?,?,?)";
    private HomeListHelper mHelper;
    private Context mContext;

    public HomeListDao(Context context){
        mContext=context;
        mHelper=new HomeListHelper(context);
    }
    public void insert(GroupFriend group){
        if (group==null)return;
        SQLiteDatabase db=mHelper.getWritableDatabase();
        if(db.isOpen()){
            db.execSQL(insertSql,new Object[]{group.getId(),group.getName(),group.getPrice(),group.getNote(),group.getLogoUrl()!=null?group.getLogoUrl():group.getLogo().getFileUrl(mContext),group.getPriority()});
            db.close();
        }
    }
    public boolean isEmpty() {
        boolean empty = true;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if (db.isOpen()) {
            if (mHelper.getReadableDatabase()
                    .rawQuery("select * from homelist", null).getCount() > 0) {
                empty = false;
            }
            db.close();
        }
        return empty;
    }
    public void clear() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from homelist");
            db.close();
        }
    }

    public List<GroupFriend> getHomeLists() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        if (db.isOpen()) {
            List<GroupFriend> lists=new ArrayList<>();
            Cursor c=db.rawQuery("select * from homelist",null);
            c.moveToFirst();
            while (!c.isAfterLast()){
                lists.add(new GroupFriend(c.getInt(0),c.getString(1),c.getDouble(2),c.getString(3),c.getString(4),c.getInt(5)));
                c.moveToNext();
            }
            db.close();
            if(lists.size()>0)return lists;
            return null;
        }
        return null;
    }
}
