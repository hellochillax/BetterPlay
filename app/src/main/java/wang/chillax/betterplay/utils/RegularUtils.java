package wang.chillax.betterplay.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wang.chillax.betterplay.config.Regular;

/**
 * Created by MAC on 15/12/2.
 */
public class RegularUtils {
    /**
     * 验证手机号合法性
     * @param phone
     * @return
     */
    public static boolean checkPhoneNum(String phone){
        return Pattern.matches(Regular.REGULAR_PHONE,phone);
    }

    /**
     * 验证密码合法性
     * @param pwd
     * @return
     */
    public static boolean checkPwd(String pwd){
        return Pattern.matches(Regular.REGULAR_PWD,pwd);
    }
    /**
     * 验证注册验证码是否合法
     */
    public static boolean checkVerifyCode(String code){
        return Pattern.matches(Regular.REGULAR_VERIFY_CODE,code);
    }
}
