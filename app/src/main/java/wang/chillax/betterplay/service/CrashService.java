package wang.chillax.betterplay.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Process;

import cn.bmob.v3.listener.SaveListener;
import wang.chillax.betterplay.bmob.CrashLog;
import wang.chillax.betterplay.utils.LogUtils;

/**
 * Created by MAC on 15/12/30.
 * 该Service为单独的进程，用于在程序崩溃退出时采取异步方式向服务器提交Crash信息。
 */
public class CrashService extends IntentService{

    public CrashService(String name) {
        super(name);
    }
    public CrashService() {
        super(null);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        //***这里Copy一下的原因：CrashLog经过序列化和反序列化后，会丢失部分父类BmobObject的属性值，导致空指针异常。
        // 这里重新申请申请一个CrashLog对象，就是为了解决这个问题，没其他的特殊含义。
        CrashLog log= ((CrashLog) intent.getSerializableExtra("Crash"));
        if(log==null)return;
        log=log.copy();
        LogUtils.d(log.toString());
        log.save(getApplicationContext(),new SaveListener(){
            @Override
            public void onSuccess() {
                Process.killProcess(Process.myPid());
            }

            @Override
            public void onFailure(int i, String s) {
                Process.killProcess(Process.myPid());
            }
        });
        try {
            Thread.sleep(6000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
