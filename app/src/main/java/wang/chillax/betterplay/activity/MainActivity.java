package wang.chillax.betterplay.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.comm.ui.fragments.CommunityMainFragment;

import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import c.b.BP;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.bmob.User;
import wang.chillax.betterplay.config.Keys;
import wang.chillax.betterplay.cusview.BottomMenu;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.fragment.BasePage;
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

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            User user = UserUtil.getCurrentUser(MainActivity.this);
            if (user.getLevel() != msg.arg1) {
                BmobUser.logOut(MainActivity.this);
                Toast.makeText(MainActivity.this, getResources().getString(R.string.error_config_changed), Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), LoginActivity.CODE_START_FOR_RESULT);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LoginActivity.CODE_START_FOR_RESULT:
                if(resultCode==RESULT_OK){
                    updateByLevel(UserUtil.getUserLevel(UserUtil.getCurrentUser(MainActivity.this).getLevel()));
                }
                break;
            default:
                super.onActivityResult(requestCode,resultCode,data);
                break;
        }
    }

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
        mActionBar.setToolBarListener(new ToolBar.ToolBarListener() {

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
                User user = UserUtil.getCurrentUser(MainActivity.this);
                if (user != null && UserUtil.Level.ADMIN == UserUtil.getUserLevel(user.getLevel())) {
                    updateByLevel(UserUtil.Level.ADMIN);
                }
            }
        });
        findUserLevelOnCreate();
        beginFollowUserLevel();
    }

    BmobRealTimeData rtd;

    private void beginFollowUserLevel() {
        User user = UserUtil.getCurrentUser(this);
        if (user == null) return;
        rtd = new BmobRealTimeData();
        rtd.start(this, new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject data) {
                LogUtils.d(data.toString());
                Pattern pattern = Pattern.compile("level\":(\\d*)");
                Matcher matcher = pattern.matcher(data.toString());
                if (matcher.find()) {
                    int level = Integer.valueOf(matcher.group(1));
                    mHandler.obtainMessage(0, level, level).sendToTarget();
                }
            }

            @Override
            public void onConnectCompleted() {
                rtd.subRowUpdate("_User", UserUtil.getCurrentUser(MainActivity.this).getObjectId());
            }
        });
//        if(rtd.isConnected()){
//            rtd.subRowUpdate("_User", user.getObjectId());
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rtd != null) {
            rtd.unsubTableUpdate("_User");
        }
    }

    public void findUserLevelOnCreate() {
        User user = UserUtil.getCurrentUser(this);
        if (user == null) return;
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", user.getUsername());
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> object) {
//                updateByLevel(UserUtil.getUserLevel(object.get(0).getLevel()));
                mHandler.obtainMessage(0,object.get(0).getLevel(),0).sendToTarget();
            }

            @Override
            public void onError(int code, String msg) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void updateByLevel(UserUtil.Level level) {
        switch (level) {
            case PLAIN:
                mActionBar.getMoreView().setVisibility(View.GONE);
                break;
            case AGENT:
                mActionBar.getMoreView().setVisibility(View.GONE);
                break;
            case ADMIN:
                mActionBar.getMoreView().setVisibility(View.VISIBLE);
                mActionBar.getMoreView().setImageResource(R.drawable.qr_scanner_icon);
                break;
        }
        if (fms[0] != null) {
            ((BasePage) fms[0]).updateByLevel(level);
        }
        if (fms[2] != null) {
            ((BasePage) fms[2]).updateByLevel(level);
        }
    }

    private void initBmobService() {
        // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(getApplicationContext(), Keys.BMOB_APP_ID);
        BP.init(getApplicationContext(), Keys.BMOB_APP_ID);
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
            fms[0] = fm.findFragmentByTag("home");
            fms[1] = fm.findFragmentByTag("find");
            fms[2] = fm.findFragmentByTag("self");
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
                    fms[1] = mFeedsFragment;
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
        if (mFeedsFragment != null) {
            ft.hide(mFeedsFragment);
        }
    }

    @Override
    public void onMenuSelected(int index) {
        setCurrPage(index);
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
