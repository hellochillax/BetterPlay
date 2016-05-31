package wang.chillax.betterplay.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.ButterKnife;
import wang.chillax.betterplay.R;

/**
 * Created by MAC on 15/12/1.
 */
public abstract class BaseActivity extends Activity {

    private Dialog mExitDialog;
    private Dialog mLoadingView;
    protected Activity mBaseAty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseAty=this;
        initStatusBar();
        if (initLayoutRes() != 0) {
            setContentView(initLayoutRes());
        }else {
            throw new NullPointerException("you may have forgot to set layout resource id on function initLayoutRes()");
        }
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    /**
     * 在此函数中初始化数据
     */
    protected abstract void initDatas();

    /**
     * 在此函数中初始化视图
     */
    protected abstract void initViews();

    /**
     * 在此函数中给出本Activity所用的布局
     */
    protected abstract int initLayoutRes();

    /**
     * 用户确认退出界面时回调的方法
     */
    protected abstract void onExit();

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * 实现经典的Activity打开动画
     */
    protected void playOpenAnimation(){
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_clam);
    }
    /**
     * 实现经典的Activity退出动画
     */
    protected void playExitAnimation(){
        overridePendingTransition(R.anim.slide_clam,R.anim.slide_out_right);
    }

    protected void showExitDialog(String title,String msg){
        if(mExitDialog==null){
            mExitDialog=new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(msg).setCancelable(false)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onExit();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create();
        }
        mExitDialog.show();
    }
    public void showLoadingDialog(String msg){
        if(mLoadingView==null){
            mLoadingView=new Dialog(this,R.style.base_aty_loading_dialog_style);
            mLoadingView.setCanceledOnTouchOutside(false);
        }
        View view= LayoutInflater.from(this).inflate(R.layout.base_aty_loading_dialog,null);
        mLoadingView.setContentView(view);
        TextView msgView = (TextView) view.findViewById(R.id.msg);
        if(TextUtils.isEmpty(msg)){
            msgView.setVisibility(View.GONE);
        }else {
            msgView.setText(msg);
        }
        mLoadingView.show();
    }
    public void hideLoadingDialog(){
        if(mLoadingView!=null&&mLoadingView.isShowing()){
            mLoadingView.dismiss();
        }
    }
}
