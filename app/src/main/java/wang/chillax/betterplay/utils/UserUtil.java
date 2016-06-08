package wang.chillax.betterplay.utils;

import android.content.Context;
import android.util.Log;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Source;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.login.LoginListener;

import java.util.Random;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;
import wang.chillax.betterplay.bmob.User;
import wang.chillax.betterplay.config.Keys;

/**
 * Created by MAC on 15/12/2.
 */
public class UserUtil {
    /**
     * 用户等级划分
     * PLAIN:普通用户
     * AGENT:代理用户
     * ADMIN:管理员用户
     * @see wang.chillax.betterplay.utils.UserUtil
     */
    public enum  Level{
        PLAIN,AGENT,ADMIN
    }

    /**
     * 根据用户的level级别来判断用户当前的等级状态
     * 0~99:普通用户
     * 100~199:代理用户
     * 888:管理员
     */
    public static Level getUserLevel(int level){
        if(level>=0&&level<100){
            return Level.PLAIN;
        }
        if(level>=100&&level<200){
            return Level.AGENT;
        }
        if(level==888){
            return Level.ADMIN;
        }
        throw new IllegalArgumentException("用户级别值有误");
    }
    /**
     * 判断当前是否已经有用户登陆
     * @param context
     * @return
     */
    public static User getCurrentUser(Context context){
        return BmobUser.getCurrentUser(context,User.class);
    }
    /**
     *
     *
     * 登陆
     *
     *
     * 错误类型:
     * 1.未注册或者账号密码有误
     *   code: 101
     *   msg: username or password incorrect
     * 2.网络错误
     *   code: 9016
     *   msg: The network is not available,please check your network!
     */
    public static void login(final Context context, final String phone, final String pwd, final LoginListenerr listener){
        BmobUser user=new BmobUser();
        user.setUsername(phone);
        user.setPassword(pwd);
        user.login(context, new SaveListener() {
            @Override
            public void onSuccess() {
                loginToUmeng();
            }
            private  void loginToUmeng() {
                //创建CommUser前必须先初始化CommunitySDK
                CommunitySDK sdk = CommunityFactory.getCommSDK(context);
//                CommUser user = new CommUser();
                String name = "bp_"+phone.substring(7);
                sdk.loginToUmengServerBySelfAccount(context, name,phone, new LoginListener() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(int stCode, CommUser commUser) {
                        Log.d("tag", "login result is "+stCode);          //获取登录结果状态码
                        if (ErrorCode.NO_ERROR==stCode) {
                            listener.onSuccess();
                        }

                    }
                });
            }
            @Override
            public void onFailure(int i, String s) {
                listener.onFailure(i,s);
            }
        });
    }



    /**
     *  注册
     *
     *  错误类型:
     *  202:username '18840833200' already taken.
     */
    public static void signin(Context context,String phone,String pwd, final SignInListener listener){
        String code=phone.substring(7);
        Random random=new Random(System.currentTimeMillis());
        code+=random.nextInt(99);
        User user = new User();
        user.setUsername(phone);
        user.setPassword(pwd);
        user.setCode(code);
        user.signUp(context, new SaveListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int code, String msg) {
                if(code==202){
                    listener.onFailure(CODE_ALREADY_TAKEN, msg);
                }else {
                    listener.onFailure(code, msg);
                }
            }
        });
    }

    /**
     *  发送手机号验证码
     */
    public static void verify(Context context,String phone){
        BmobSMS.requestSMSCode(context, phone, Keys.BMOB_SMS_NAME, new RequestSMSCodeListener() {

            @Override
            public void done(Integer integer, BmobException e) {

            }
        });
    }
    /**
     *  验证验证码是否正确
     */
    public static void verifyCode(Context context,String phone,String code, final VerifyCodeListener listener){
        BmobSMS.verifySmsCode(context, phone, code, new VerifySMSCodeListener() {

            @Override
            public void done(BmobException ex) {
                if (ex == null) {//短信验证码已验证成功
                    listener.onSuccess();
                } else {
                    listener.onFailure(ex.getErrorCode(),ex.getLocalizedMessage());
                }
            }
        });
    }

    public static void logout(Context context) {
        BmobUser.logOut(context);
    }


    public interface LoginListenerr{
        void onSuccess();
        void onFailure(int code,String msg);
    }
    public interface SignInListener{
        void onSuccess();
        void onFailure(int code,String msg);
    }
    public interface VerifyCodeListener{
        void onSuccess();
        void onFailure(int code,String msg);
    }
    public interface UpdateeListener{
        void onSuccess();
        void onFailure(int code,String msg);
    }
    public static final int CODE_DATA_ERROR=101;
    public static final int CODE_DATA_NET_EOORO=9016;
    public static final int CODE_ALREADY_TAKEN=202;//已被注册
}
