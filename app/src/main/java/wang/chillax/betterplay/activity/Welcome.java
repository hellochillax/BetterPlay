package wang.chillax.betterplay.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telecom.PhoneAccountHandle;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;
import java.util.ArrayList;

import wang.chillax.betterplay.R;
import wang.chillax.betterplay.config.Paths;
import wang.chillax.betterplay.service.CrashService;
import wang.chillax.betterplay.utils.CrashHandler;
import wang.chillax.betterplay.utils.LogUtils;

/**
 * Created by MAC on 15/12/7.
 */
public class Welcome extends Activity {

    /**
     * 欢迎界面停留的时间间隔
     */
    private final int DURATION=2000;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            openMainActivity();
        }
    };

    private void installCrashHandler() {
        CrashHandler.getInstance().install(getApplicationContext());
        startService(new Intent(getApplicationContext(), CrashService.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        long pre=System.currentTimeMillis();
        installCrashHandler();
        initDirs();
        initImageLoader();
        mHandler.sendEmptyMessageDelayed(0,Math.max(1000,DURATION-System.currentTimeMillis()+pre));
    }


    /**
     * 初始化缓存文件夹
     */
    private void initDirs() {
        File file=new File(Paths.imagesPath);
        if(!file.exists()){
            file.mkdirs();
        }
        file=new File(Paths.dbPath);
        if(!file.exists()){
            file.mkdirs();
        }
        file=new File(Paths.downloadPath);
        if(!file.exists()){
            file.mkdirs();
        }
    }


    private void openMainActivity() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
    private void initImageLoader() {
        DisplayImageOptions mOptions = new DisplayImageOptions.Builder().showImageOnLoading(0) // 设置图片在下载期间显示的图片
                .showImageForEmptyUri(0)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(0) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(false)// 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
//                .considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
                // .delayBeforeLoading(int delayInMillis)//int
                // delayInMillis为你设置的下载前的延迟时间
                // 设置图片加入缓存前，对bitmap进行设置
                // .preProcessor(BitmapProcessor preProcessor)
                .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
//                .displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少
//                .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
                .build();// 构建完成

//        String cacheDir = Paths.imagesPath;
//        File cachePath = new File(cacheDir);// 获取到缓存的目录地址
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//                .discCache(new UnlimitedDiscCache(cachePath))
//                // 自定义缓存路径
                .defaultDisplayImageOptions(mOptions)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
//                .imageDownloader(
//                        new BaseImageDownloader(App.this, 5 * 1000, 30 * 1000))
//                        // readTimeout(30)// 超时时间
                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onBackPressed() {

    }
}
