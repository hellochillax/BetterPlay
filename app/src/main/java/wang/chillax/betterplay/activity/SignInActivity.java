package wang.chillax.betterplay.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.config.Urls;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.utils.RegularUtils;
import wang.chillax.betterplay.utils.SmsObserver;
import wang.chillax.betterplay.utils.UserUtil;

/**
 * Created by MAC on 15/12/1.
 * <p/>
 * 注册
 */
public class SignInActivity extends BaseActivity implements ToolBar.ToolBarListener {

    public static final int MSG_RECIVER_CODE = 1;
    public static final int READ_SMS_REQUEST_CODE = 111;
    @Bind(R.id.cb)
    CheckBox mCheckBox;
    @Bind(R.id.legal)
    TextView mLegalText;

    @OnClick(R.id.legal)
    void toLegalPage(){
        Intent intent=new Intent(this, WebPage.class);
        intent.putExtra("url", Urls.DECLARATION);
        intent.putExtra("title","软件使用协议");
        startActivity(intent);
    }

    private boolean isPermission = false;
    //SMS的Observer
    private SmsObserver mObserver;
    private Handler mHandler;

    @Bind(R.id.toolbar)
    ToolBar mToolbar;
    @Bind(R.id.title1)
    TextView title1;
    @Bind(R.id.title2)
    TextView title2;
    @Bind(R.id.title3)
    TextView title3;
    @Bind(R.id.edit1)
    EditText edit1;
    @Bind(R.id.edit2)
    EditText edit2;
    @Bind(R.id.edit2_layout)
    LinearLayout edit2Layout;
    /**
     * 记录当前显示的是第几个页面
     */
    int currPage = 0;
    /**
     * 记录用户使用的手机号
     */
    String phone;
    /**
     * 记录用户使用的密码
     */
    String pwd;

    /**
     * 注册第一步:输入手机号,获取验证码
     */
    private void doStepOne() {
        phone = edit1.getText().toString();
        if (!checkPhoneLegal(phone)) {
            return;
        }
        if (!mCheckBox.isChecked()){
            showToast("请阅读并同意协议");
            return;
        }
        mCheckBox.setVisibility(View.GONE);
        mLegalText.setVisibility(View.GONE);
        UserUtil.verify(this, phone);
        showToast("验证码已发送,请注意查收");
        currPage++;
        resetEditText();
        resetTitle();
    }

    /**
     * 注册第二步:输入验证码
     */
    private void doStepTwo() {
        String verify = edit1.getText().toString();
        if (!checkVerifyCodeLegal(verify)) {
            showToast("请正确输入6位数字验证码");
            return;
        }
        UserUtil.verifyCode(this, phone, verify, new UserUtil.VerifyCodeListener() {
            @Override
            public void onSuccess() {
                showToast("验证码输入正确");
                currPage++;
                resetEditText();
                resetTitle();
            }

            @Override
            public void onFailure(int code, String msg) {
                showToast("验证码输入错误");
                resetEditText();
            }
        });
    }

    /**
     * 注册第三步:输入密码
     */
    private void doStepThree() {
        pwd = edit1.getText().toString();
        String pwd2 = edit2.getText().toString();
        if (checkDoublePwd(pwd, pwd2)&&RegularUtils.checkPwd(pwd)) {
            currPage++;
            doNextStep();
        }
    }

    /**
     * 注册第四步:注册成功
     */
    private void doStepFour() {
        UserUtil.signin(this, phone, pwd, new UserUtil.SignInListener() {
            @Override
            public void onSuccess() {
//                loginToServer();
                UserUtil.getCurrentUser(mBaseAty).logOut(mBaseAty);
                loginSuccess();
            }

//            private void loginToServer() {
//                //创建CommUser前必须先初始化CommunitySDK
//                CommunitySDK sdk = CommunityFactory.getCommSDK(mBaseAty);
//                CommUser user = new CommUser();
//                user.name = "play_" + BmobUser.getCurrentUser(mBaseAty).getObjectId();
//                user.id = phone;
//                sdk.loginToUmengServerBySelfAccount(mBaseAty,user.name ,user.id, new LoginListener() {
//                    @Override
//                    public void onStart() {
//
//                    }
//
//                    @Override
//                    public void onComplete(int stCode, CommUser commUser) {
//                        LogUtils.d("umeng login result is " + stCode);          //获取登录   结果状态码
//                        if (ErrorCode.NO_ERROR == stCode) {
//                            //在此处可以跳转到任何一个你想要的activity
//                            loginSuccess();
//                        }
//
//                    }
//
//                });
//            }

            @Override
            public void onFailure(int code, String msg) {
                if (code == UserUtil.CODE_ALREADY_TAKEN) {
//                    showToast("已经被注册,请直接登陆");
                    loginSuccess();
                } else {
                    showToast("注册失败,请重试");
                }
            }
        });
    }

    private void loginSuccess() {
        showToast("注册成功");
        onBackPressed();
        setResult(RESULT_OK);
    }

    private boolean checkDoublePwd(String pwd1, String pwd2) {
        return RegularUtils.checkPwd(pwd1) && pwd1.equals(pwd2);
    }

    private boolean checkVerifyCodeLegal(String code) {
        return RegularUtils.checkVerifyCode(code);
    }


    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {
        resetTitle();
        mToolbar.setToolBarListener(this);
        autoCode();
        smsOberverInit();
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.activity_signin;
    }

    @Override
    protected void onExit() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_clam, R.anim.slide_out_bottom);
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
        onBackPressed();
    }

    @Override
    public void onTitleRightClicked() {
        doNextStep();
    }

    private void doNextStep() {
        switch (currPage) {
            case 0:
                doStepOne();
                break;
            case 1:
                doStepTwo();
                break;
            case 2:
                doStepThree();
                break;
            case 3:
                doStepFour();
                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mObserver);
    }

    private void resetEditText() {
        edit1.setText("");
        switch (currPage) {
            case 1:
                edit1.setHint("验证码");
                break;
            case 2:
                edit1.setHint("密码");
                edit1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                edit1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
        }
        edit2Layout.setVisibility(currPage == 2 ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onInit(ImageView back, TextView titleLeft, TextView titleCenter, TextView titleRight, ImageView more) {
        back.setVisibility(View.GONE);
        titleLeft.setText("退出");
        titleCenter.setText("注册");
        titleRight.setText("下一步");
    }

    private boolean checkPhoneLegal(String phone) {
        if (TextUtils.isEmpty(phone)) {
            showToast("手机号不能为空");
        } else if (!RegularUtils.checkPhoneNum(phone)) {
            showToast("请输入正确的手机号码");
        } else {
            return true;
        }
        return false;
    }

    public void resetTitle() {
        int color = Color.parseColor("#6dd876");
        title1.setTextColor(currPage == 0 ? color : Color.BLACK);
        title2.setTextColor(currPage == 1 ? color : Color.BLACK);
        title3.setTextColor(currPage == 2 ? color : Color.BLACK);
    }

    private void autoCode() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_RECIVER_CODE) {
                    String code = (String) msg.obj;
                    edit1.setText(code);
                }
            }
        };
    }

    private void smsOberverInit() {
        mObserver = new SmsObserver(mHandler, this);
        Uri mUri = Uri.parse("content://sms");
        this.getContentResolver().registerContentObserver(mUri, true, mObserver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case READ_SMS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    showToast("您拒绝了权限申请，\n将不能完成自动短信验证码填写");
                }
                break;
            case MainActivity.READ_PHONE_STATE_CODE:

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
