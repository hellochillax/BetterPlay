package wang.chillax.betterplay.utils;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wang.chillax.betterplay.activity.SignInActivity;


/**
 * Created by qingliu on 12/4/15.
 */
public class SmsObserver extends ContentObserver {

    private Context mContext;
    private Handler mHandler;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SmsObserver(Handler handler, Context context) {
        super(handler);
        mContext = context;
        mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        String smsCode;
        Log.e("DEBUG","SMS reciver");
        Log.e("DEBUG", uri.toString());
        //判断是否是第一次接收到Uri
        if (uri.toString().equals("content://sms/raw")){
            return;
        }
        //获取收件箱的Uri
        Uri inboxUrl = Uri.parse("content://sms/inbox");
        Cursor cursor = mContext.getContentResolver().query(inboxUrl, null, null, null, "date desc");
        if (cursor != null){
            if (cursor.moveToFirst()){
                String fromName = cursor.getString(cursor.getColumnIndex("address"));
                String smsContent = cursor.getString(cursor.getColumnIndex("body"));

                Toast.makeText(mContext,"发件人：" + fromName + " " + "内容为:" + smsContent,
                        Toast.LENGTH_SHORT).show();

                Pattern code = Pattern.compile("\\d{6}");
                Matcher matcher = code.matcher(smsContent);

                if (matcher.find()){
                    smsCode = matcher.group(0);
                    Log.e("DEBUG", "验证码为:" + smsCode);
                    mHandler.obtainMessage(SignInActivity.MSG_RECIVER_CODE, smsCode).sendToTarget();
                }

            }
            cursor.close();
        }
    }
}
