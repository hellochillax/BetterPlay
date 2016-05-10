package wang.chillax.betterplay.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.config.Keys;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.utils.RegularUtils;
import wang.chillax.betterplay.utils.SmsObserver;

/**
 * Created by qingliu on 12/16/15.
 */
public class ResetPwdActivity extends BaseActivity implements ToolBar.ToolBarListener,View.OnClickListener{
    public Context mContext;
    private Handler mHandler;
    private Handler mTimeHandler = new Handler();
    private SmsObserver mSmsObserver;
    public static final int MSG_RECIVER_CODE = 1;

    @Bind(R.id.find_pass_phone)
    EditText mPhoneNUm;
    @Bind(R.id.phone_confirm_code)
    EditText mPhoneCode;
    @Bind(R.id.send_code_btn)
    Button mSendCode;
    @Bind(R.id.first_pwd)
    EditText mFirstPwd;
    @Bind(R.id.verify_pwd)
    EditText mVerifyPwd;
    @Bind(R.id.reset_pwd_btn)
    Button mResetPwd;
    @Bind(R.id.rest_toolbar)
    ToolBar mResetBar;

    @Override
    protected void initDatas() {
        mContext = getApplicationContext();
        //未获得验证时将控件设置为disable
        mFirstPwd.setEnabled(false);
        mVerifyPwd.setEnabled(false);
        mResetPwd.setEnabled(false);

        //监听事件
        mSendCode.setOnClickListener(this);
        mResetPwd.setOnClickListener(this);
    }

    @Override
    protected void initViews() {
        autoCode();
        smsOberverInit();
        mResetBar.setToolBarListener(this);
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.activity_resetpwd;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.send_code_btn:
                if (isPhoneNum(String.valueOf(mPhoneNUm.getText()))){
                    sendPhoneCode();
                }else {
                    showToast("请输入正确的手机号！");
                }
            break;
            case R.id.reset_pwd_btn:
                resetPwd();
            break;
        }
    }

    public void sendPhoneCode() {
        mSendCode.setEnabled(false);
        mSendCode.setText(R.string.sending_code);
        mPhoneCode.requestFocus();
        BmobSMS.requestSMSCode(mContext, String.valueOf(mPhoneNUm.getText()), Keys.BMOB_SMS_NAME, new RequestSMSCodeListener() {

            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null){
                    showToast("验证码发送成功将为您自动填写");
                    mFirstPwd.setEnabled(true);
                    mVerifyPwd.setEnabled(true);
                    mResetPwd.setEnabled(true);
                    mTimeHandler.postDelayed(new Runnable() {
                        private int second = 60;
                        @Override
                        public void run() {
                            if (second > 0){
                                second--;
                                mSendCode.setText("重新发送(" + second + "秒)");
                                mTimeHandler.postDelayed(this, 1000);
                            }else {
                                mSendCode.setEnabled(true);
                                mSendCode.setText("重新发送");
                            }
                        }
                    },1000);

                }else{
                    showToast("Sorry,验证码发送失败！" + e);
                }
            }
        });
    }

    public void resetPwd(){
        if (checkField()){
            BmobUser.resetPasswordBySMSCode(this, String.valueOf(mPhoneCode.getText()),mFirstPwd.getText().toString(), new ResetPasswordByCodeListener() {

                @Override
                public void done(BmobException ex) {
                    // TODO Auto-generated method stub
                    if(ex==null){
                        showToast("密码重置成功！");
                        onBackPressed();
                    }else{
                        Log.i("smile", "重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                        showToast("呜呜。。。出错了！");
                    }
                }
            });
        }else {
            //showToast("请检查密码和验证码是否输入正确！");
        }

    }

    public boolean checkField(){
        boolean valid = true;
        //判断验证码是否输入正确
        if (!(mPhoneCode.getText().toString().length() == 6)) {
            showToast("验证码输入错误！");
            valid = false;
        }
        return checkDoublePwd(mFirstPwd.getText().toString(),mVerifyPwd.getText().toString());
    }

    private boolean checkDoublePwd(String pwd1, String pwd2) {
        return RegularUtils.checkPwd(pwd1) && pwd1.equals(pwd2);
    }
    @Override
    public void onBackPressed() {
        mTimeHandler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }

    private void autoCode(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_RECIVER_CODE){
                    String code = (String)msg.obj;
                    mPhoneCode.setText(code);
                }
            }
        };
    }

    private void smsOberverInit(){
        mSmsObserver = new SmsObserver(mHandler, this);
        Uri mUri = Uri.parse("content://sms");
        this.getContentResolver().registerContentObserver(mUri, true, mSmsObserver);
    }

    @Override
    protected void onExit() {

    }

    private boolean isPhoneNum(String phoneNum){
        String phoneReg = "[0-9]{11}";
        Pattern p = Pattern.compile(phoneReg);
        Matcher phoneMatcher = p.matcher(phoneNum);
        return phoneMatcher.find();
    }
    @Override
    public void onBackClicked() {
        onBackPressed();
    }

    @Override
    public void onMoreClicked() {

    }

    @Override
    public void onTitleLeftClicked() {

    }

    @Override
    public void onTitleRightClicked() {

    }

    @Override
    public void onInit(ImageView back, TextView titleLeft, TextView titleCenter, TextView titleRight, ImageView more) {

    }


}
