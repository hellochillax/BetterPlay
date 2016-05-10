package com.mac.scanner.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.mac.scanner.R;
import com.mac.scanner.bmob.Order;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by MAC on 16/4/5.
 */
public class ResultAty extends Activity {

    @Bind(R.id.result)
    TextView mResult;
    private String order_id;
    private Order order;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mResult.setText(order.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_aty);
        ButterKnife.bind(this);
        order_id = getIntent().getStringExtra("order_id");
        new MyTask().execute();
    }

    protected class MyTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            BmobQuery<Order> query = new BmobQuery<>();
            query.addWhereEqualTo("order_id", order_id);
            query.findObjects(ResultAty.this, new FindListener<Order>() {
                @Override
                public void onSuccess(List<Order> list) {
                    order = list.get(0);
                    mHandler.obtainMessage().sendToTarget();
                }

                @Override
                public void onError(int i, String s) {

                }
            });
            return null;
        }
    }
}
