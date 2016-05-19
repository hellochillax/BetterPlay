package wang.chillax.betterplay.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.login.LoginListener;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.utils.LogUtils;
import wang.chillax.betterplay.utils.RegularUtils;
import wang.chillax.betterplay.utils.UserUtil;

/**
 * Created by MAC on 15/12/1.
 */
public class LoginActivity extends BaseActivity implements ToolBar.ToolBarListener {
    @Bind(R.id.toolbar)
    ToolBar mToolbar;
    @Bind(R.id.login_account)
    EditText loginAccount;
    @Bind(R.id.login_pwd)
    EditText loginPwd;
    @Bind(R.id.forgot_pwd)
    TextView mForgotPwd;

    @Bind(R.id.login_ok)
    Button loginOk;
    @OnClick(R.id.login_ok)
    public void login_ok(){
        String phone=loginAccount.getText().toString();
        String pwd=loginPwd.getText().toString();
        if(checkUserLegal(phone,pwd)){
            doLoginTask(phone,pwd);
        }
    }

    private void doLoginTask(final String phone, final String pwd) {
        showLoadingDialog(getResources().getString(R.string.waiting_login));
        UserUtil.login(this, phone, pwd, new UserUtil.LoginListenerr() {
            @Override
            public void onSuccess() {
//                showToast("登陆成功");
//                hideLoadingDialog();
//                setResult(RESULT_OK);
//                onBackPressed();
                loginToServer(phone);
            }

            @Override
            public void onFailure(int code, String msg) {
                switch (code) {
                    case UserUtil.CODE_DATA_ERROR:
                        showToast(getResources().getString(R.string.error_account));
                        break;
                    case UserUtil.CODE_DATA_NET_EOORO:
                        showToast(getResources().getString(R.string.error_network));
                        break;
                    default:
                        showToast(getResources().getString(R.string.error_unknown));
                        break;
                }
                hideLoadingDialog();
            }
        });
    }
    private void loginToServer(String phone) {
        //创建CommUser前必须先初始化CommunitySDK
        CommunitySDK sdk = CommunityFactory.getCommSDK(mBaseAty);
        CommUser user = new CommUser();
        user.name = "play_" + BmobUser.getCurrentUser(mBaseAty).getObjectId();
        user.id = phone;
        sdk.loginToUmengServerBySelfAccount(mBaseAty,user.name ,user.id, new LoginListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int stCode, CommUser commUser) {
                LogUtils.d("umeng login result is " + stCode);          //获取登录   结果状态码
                if (ErrorCode.NO_ERROR == stCode) {
                    //在此处可以跳转到任何一个你想要的activity
                    loginSuccess();
                }else{
                    UserUtil.getCurrentUser(mBaseAty).logOut(mBaseAty);
                    showToast(getResources().getString(R.string.error_unknown));
                }

            }

            private void loginSuccess() {
                showToast(getResources().getString(R.string.success_login));
                hideLoadingDialog();
                setResult(RESULT_OK);
                onBackPressed();
            }

        });
    }
    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {
        mToolbar.setToolBarListener(this);
        mForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPwd = new Intent(LoginActivity.this, ResetPwdActivity.class);
                startActivity(forgotPwd);
            }
        });
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.activity_login;
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
        openSignInActivity();
    }

    private void openSignInActivity() {
//        startActivityForResult(new Intent(this, SignInActivity.class), 0);
        startActivity(new Intent(this, SignInActivity.class));
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_clam);
    }

    @Override
    public void onInit(ImageView back, TextView titleLeft, TextView titleCenter,TextView titleRight, ImageView more) {
        back.setVisibility(View.GONE);
        titleLeft.setText("退出");
        titleCenter.setText("登陆");
        titleRight.setText("注册");
    }
    private boolean checkUserLegal(String phone, String pwd) {
        if(TextUtils.isEmpty(phone)){
            showToast(getResources().getString(R.string.error_phone_empty));
        }else if(TextUtils.isEmpty(pwd)){
            showToast(getResources().getString(R.string.error_pwd_empty));
        }else if(!RegularUtils.checkPhoneNum(phone)){
            showToast(getResources().getString(R.string.error_phone_illegal));
        }else if(!RegularUtils.checkPwd(pwd)){
            showToast(getResources().getString(R.string.error_pwd_illegal));
        }else {
            return true;
        }
        return false;
    }

}
