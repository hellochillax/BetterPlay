package wang.chillax.betterplay.utils;

import android.util.Log;

import wang.chillax.betterplay.config.Keys;

/**
 * Created by MAC on 15/12/12.
 */
public class LogUtils {
    private static String TAG="LogUtils";
    public static void d(String log){
        if(Keys.DEBUG){
            Log.d(TAG,log);
        }
    }
    public static void w(String log){
        if(Keys.DEBUG){
            Log.d(TAG,log);
        }
    }
    public static void e(String log){
        if(Keys.DEBUG){
            Log.e(TAG,log);
        }
    }
}
