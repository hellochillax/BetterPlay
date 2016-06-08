package wang.chillax.betterplay.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import org.json.JSONObject;
import org.xml.sax.ContentHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import c.b.BP;
import c.b.PListener;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CloudCodeListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.bmob.GroupDetail;
import wang.chillax.betterplay.bmob.Order;

/**
 * Created by MAC on 15/12/16.
 * 处理用户订单的工具类
 */
public class OrderUtils {

    private static String mOrderId=null;

    public static void pay(final Activity context, final GroupDetail detail, final  int count, final String code, final PayListenerr listenerr){
        /**
         * 第4个参数为true时调用支付宝支付，为false时调用微信支付
         */
        final double total=count*detail.getPrice();
        BP.pay(detail.getTitle(), detail.getTitle(),total , true, new PListener() {
            @Override
            public void orderId(String s) {
                mOrderId=s;
                LogUtils.d(s);
            }

            @Override
            public void succeed() {
                quary();
            }

            @Override
            public void fail(int i, String s) {
                LogUtils.d(String.format("支付错误:%d..%s", i, s));
                if(i==-3){
                    if(listenerr!=null){
                        listenerr.onFail("请先安装支付插件");
                        installPayPlugin(context);
                    }
                }else if (i == 10777) {
                    BP.ForceFree();
                    pay(context,detail,count,code,listenerr);
                } else if (i == 9010) {
                    if(listenerr!=null){
                        listenerr.onFail("网络错误,请重试");
                    }
                } else {
                    if(listenerr!=null){
                        listenerr.onFail("支付失败,请重试");
                    }
                }
            }

            @Override
            public void unknow() {
                if(listenerr!=null){
                    listenerr.onFail("支付失败,请重试");
                }
            }
            public void quary(){
//                BP.query(context, "efb9f6386e60b77b3918da9663689c73", new QListener() {
//                    @Override
//                    public void succeed(String s) {
//                        LogUtils.d("query result is : ");
//                        if (s.equals("SUCCESS")){
//                            updateOrder();
//                        }
//                    }
//
//                    @Override
//                    public void fail(int i, String s) {
//                        LogUtils.d("查询订单出错:"+i+":"+s);
//                    }
//                });
                updateOrder();
            }
            public void updateOrder(){
                LogUtils.d("支付消息:" + mOrderId + ":" + BmobUser.getCurrentUser(context).getUsername() + ":" + detail.getTitle() + ":" + detail.getObjectId());
                JSONObject params=new JSONObject();
                try {
                    params.put("id",mOrderId)
                            .put("user",BmobUser.getCurrentUser(context).getUsername())
                            .put("token",BmobUser.getCurrentUser(context).getSessionToken())
                            .put("group",detail.getGroup_id())
                            .put("title",detail.getTitle())
                            .put("back",detail.getBack())
                            .put("count",count)
                            .put("price",total);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                //第一个参数是上下文对象，第二个参数是云端逻辑的方法名称，第三个参数是上传到云端逻辑的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
                ace.callEndpoint(context, "createOrder", params, new CloudCodeListener() {
                    @Override
                    public void onSuccess(Object object) {
                        listenerr.onSuccess(new Order(mOrderId,BmobUser.getCurrentUser(context).getUsername(),detail.getGroup_id(),code,count,total,detail.getTitle(),0));
                        LogUtils.d(object.toString());
                    }
                    @Override
                    public void onFailure(int code, String msg) {
                        LogUtils.d("订单生成失败,请联系客服人员");
                        LogUtils.d(code+":"+msg);
                    }
                });
            }
        });

    }


    public static void installPayPlugin(final Context context){
        if(copyApkFromAssets(context, "pay_plugin.apk", Environment.getExternalStorageDirectory().getAbsolutePath()+"/pay_plugin.apk")){
            AlertDialog.Builder m = new AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_launcher).setMessage("是否安装？")
                    .setIcon(R.drawable.ic_launcher)
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath()+"/pay_plugin.apk"),
                                    "application/vnd.android.package-archive");
                            context.startActivity(intent);
                        }
                    });
            m.show();
        }
    }
    public static boolean copyApkFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyIsFinish;
    }

    public interface PayListenerr{
        void onSuccess(Order order);
        void onFail(String msg);
    }
}
