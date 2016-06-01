package wang.chillax.betterplay.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import wang.chillax.betterplay.bmob.CrashLog;
import wang.chillax.betterplay.config.Keys;
import wang.chillax.betterplay.service.CrashService;

/**
 * Created by MAC on 15/12/30.
 * 程序崩溃收据收集。将程序Crash的信息保存到本地，并上传到服务器。
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private final String TAG="CrashHandler";
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private static CrashHandler mInstance=new CrashHandler();

    private CrashHandler(){
        mDefaultCrashHandler=Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashHandler getInstance() {
        return mInstance;
    }

    public void install(Context context){
        mContext=context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
//        recordCrashToServer(ex);

//        Process.killProcess(Process.myPid());
//
        if(Keys.DEBUG){
            mDefaultCrashHandler.uncaughtException(thread,ex);
        }else{
            Process.killProcess(Process.myPid());
        }
    }


    private void recordCrashToServer(Throwable ex) {
        try{
            PackageInfo pi=mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            //App的版本信息
            String appVersion=pi.versionName+"_"+pi.versionCode;
            //设备的Android版本信息
            String deviceVersion= Build.VERSION.RELEASE+"_"+Build.VERSION.SDK_INT;
            //手机制造商和手机型号
            String hardWare=Build.MANUFACTURER+"_"+Build.MODEL;

            Intent intent=new Intent(mContext,CrashService.class);
            intent.putExtra("Crash",new CrashLog(UserUtil.getCurrentUser(mContext)==null?"":UserUtil.getCurrentUser(mContext).getUsername(),appVersion,deviceVersion,hardWare,ex.getMessage()));
            mContext.startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
