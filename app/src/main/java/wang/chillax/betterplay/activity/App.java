package wang.chillax.betterplay.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Process;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

import wang.chillax.betterplay.bmob.GroupFriend;
import wang.chillax.betterplay.service.CrashService;
import wang.chillax.betterplay.utils.CrashHandler;
import wang.chillax.betterplay.utils.LogUtils;

/**
 * Created by MAC on 15/12/1.
 */
public class App extends MultiDexApplication{

//    /**
//     * 活动列表的数据List
//     */
//    public static List<GroupFriend> actionList;

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if(!Environment.getExternalStorageDirectory().exists()){
            //如果手机没有内存卡，直接退出。
            Process.killProcess(Process.myPid());
        }
    }

}
