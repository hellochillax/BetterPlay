package wang.chillax.betterplay.utils;

import android.content.Context;


import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import wang.chillax.betterplay.bmob.GroupFriend;
import wang.chillax.betterplay.config.Keys;

/**
 * Created by MAC on 15/12/7.
 */
public class ActionUtils {
    /**
     * 查询活动列表
     */
    public static void quary(Context context, int currPage, final QuaryListener listener){
        BmobQuery<GroupFriend> query=new BmobQuery<>();
        query.setLimit(Keys.PAGE_LIMIT);
        query.setSkip(Keys.PAGE_LIMIT*currPage);
        query.findObjects(context, new FindListener<GroupFriend>() {
            @Override
            public void onSuccess(List<GroupFriend> list) {
                if(listener!=null){
                    listener.onSuccess(list);
                }
            }

            @Override
            public void onError(int i, String s) {
                if(listener!=null){
                    listener.onError(i,s);
                }
            }
        });
    }
    public interface QuaryListener{
        void onSuccess(List<GroupFriend> list);
        void onError(int i, String s);
    }

}
