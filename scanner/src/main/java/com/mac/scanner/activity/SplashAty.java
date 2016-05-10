package com.mac.scanner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.mac.scanner.config.Config;

import c.b.BP;
import cn.bmob.v3.Bmob;

/**
 * Created by MAC on 16/4/5.
 */
public class SplashAty extends Activity {

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            startActivity(new Intent(SplashAty.this,MainAty.class));
            onBackPressed();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler.sendEmptyMessageDelayed(0,2000);
    }
    private void initBmobService() {
        // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, Config.BMOB_APP_ID);
        BP.init(this,Config.BMOB_APP_ID);
    }
}
