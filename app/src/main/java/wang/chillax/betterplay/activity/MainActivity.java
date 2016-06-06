package wang.chillax.betterplay.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.ui.fragments.CommunityMainFragment;
import com.umeng.common.ui.widgets.ScaleImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import c.b.BP;
import cn.bmob.v3.Bmob;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.bmob.User;
import wang.chillax.betterplay.config.Keys;
import wang.chillax.betterplay.cusview.BottomMenu;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.fragment.FindPage;
import wang.chillax.betterplay.fragment.HomePage;
import wang.chillax.betterplay.fragment.SelfPage;
import wang.chillax.betterplay.utils.LogUtils;
import wang.chillax.betterplay.utils.UserUtil;

public class MainActivity extends AppCompatActivity implements BottomMenu.OnBottomMenuSelectedListener {

    public static final int READ_SMS_REQUEST_CODE = 111;
    public static final int READ_PHONE_STATE_CODE = 112;

    @Bind(R.id.toolbar)
    ToolBar mActionBar;
    Fragment[] fms;
    FragmentManager fm;
    @Bind(R.id.bottom_menu)
    BottomMenu mBottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        try{
//            CommunityFactory.getCommSDK(this);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initBmobService();
//        initUmengSdk();
        initPagers();
        initStatusBar();
//        getPermission();
        mActionBar.setToolBarListener(new ToolBar.ToolBarListener(){

            @Override
            public void onBackClicked() {

            }

            @Override
            public void onMoreClicked() {
                startActivity(new Intent(MainActivity.this, CaptureActivity.class));
            }

            @Override
            public void onTitleLeftClicked() {

            }

            @Override
            public void onTitleRightClicked() {

            }

            @Override
            public void onInit(ImageView back, TextView titleLeft, TextView titleCenter, TextView titleRight, ImageView more) {
                User user= UserUtil.getCurrentUser(MainActivity.this);
                if(user!=null&&user.getLevel()== Keys.Level.ADMIN){
                    more.setImageResource(R.drawable.qr_scanner_icon);
                    more.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initBmobService() {
        // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(getApplicationContext(), Keys.BMOB_APP_ID);
        BP.init(getApplicationContext(),Keys.BMOB_APP_ID);
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.action_bar_bg_color);//通知栏所需颜色
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initPagers() {
        fm = getSupportFragmentManager();
        //http://my.oschina.net/wangxnn/blog/417581
        //activity后台被杀死之后,三个界面会被保存下来,这里进行恢复
        fms = new Fragment[3];
        if (fm.getFragments() != null) {
            fms[0] =  fm.findFragmentByTag("home");
            fms[1] = fm.findFragmentByTag("find");
            fms[2] =  fm.findFragmentByTag("self");
        }
        mBottomMenu.setOnSelectedListener(this);
        mBottomMenu.setActionBar(mActionBar);
        mBottomMenu.setSelection(0);
    }
    CommunityMainFragment mFeedsFragment;

    private void setCurrPage(int index) {
        FragmentTransaction ft = fm.beginTransaction();
        hidePages(ft);
        switch (index) {
            case 0:
                if (fms[0] == null) {
                    fms[0] = new HomePage();
                    ft.add(R.id.content, fms[0], "home");
                } else {
                    ft.show(fms[0]);
                }
                break;
            case 1:
                if (fms[1] == null) {
                    mFeedsFragment = new CommunityMainFragment();
          //设置Feed流页面的返回按钮不可见
                    mFeedsFragment.setBackButtonVisibility(View.INVISIBLE);
                    fms[1]=mFeedsFragment;
                    ft.add(R.id.content, fms[1], "find");
         //添加并显示Fragment
                } else {
                    ft.show(fms[1]);
                }
                break;
            case 2:
                if (fms[2] == null) {
                    fms[2] = new SelfPage();
                    ft.add(R.id.content, fms[2], "self");
                } else {
                    ft.show(fms[2]);
                }
                break;
        }
        ft.commit();
    }

    private void hidePages(FragmentTransaction ft) {
        for (Fragment page : fms) {
            if (page != null) {
                ft.hide(page);
            }
        }
        if(mFeedsFragment!=null){
            ft.hide(mFeedsFragment);
        }
    }

    @Override
    public void onMenuSelected(int index) {
        setCurrPage(index);
    }

//    @TargetApi(23)
//    private void getPermission() {
//        //如果是6.0以上系统就运行动态权限检测
//        if (Build.VERSION.SDK_INT >= 23) {
//            final int[] requestCode = {checkSelfPermission(Manifest.permission.READ_SMS),
//                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE)};
//            final String[] permissionStr = {Manifest.permission.READ_SMS,
//                    Manifest.permission.READ_PHONE_STATE};
//            final String[] dialogStr = {"利未校园想要获取您的短信信息来完成自动验证码填写\n " +
//                    "如果您拒绝了权限获取可能会引起应用程序的不稳定",
//                    "利未校园想要获取您的手机状态"};
//            new Object(){
//               public void checkPermission(){
//                    /*判断是权限请求是否被用户拒绝
//            * 如果没有被拒绝在自己弹出dialog来提示用户
//            * 如果第一次执行则调用系统默认的dialog来获取权限*/
//                   for (int i = 1; i < dialogStr.length; i++) {
//                       if (shouldShowRequestPermissionRationale(permissionStr[i])) {
//                           showMessageOKCancel(dialogStr[i], new DialogInterface.OnClickListener() {
//                               @Override
//                               public void onClick(DialogInterface dialog, int which) {
//                                   int j = 111;
//                                   requestPermissions(permissionStr, j);
//                                   j++;
//                               }
//                           });
//                       } else if (requestCode[i] != PackageManager.PERMISSION_GRANTED) {
//                           ActivityCompat.requestPermissions(MainActivity.this, permissionStr, 111 + i);
//                       }
//                   }
//               }
//            }.checkPermission();
//        }
//    }

//    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(this)
//                .setMessage(message)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
//    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
