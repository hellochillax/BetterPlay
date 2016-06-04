package wang.chillax.betterplay.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
//import com.google.zxing.*;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.bmob.Order;
import wang.chillax.betterplay.bmob.User;
import wang.chillax.betterplay.cusview.ToolBar;

public class ScannerActivity extends BaseActivity implements ToolBar.ToolBarListener{

    @Bind(R.id.finish_deal)
    Button mFinishDealBtn;
    @Bind(R.id.rescan)
    Button mRescanBtn;
    @Bind(R.id.deal_imformation)
    TextView mInfo;

    Order mOrder;
    User user;// = BmobUser.getCurrentUser(this,User.class);
    String orderId;// = getIntent().getStringExtra("scan_result");

    @Override
    protected void initDatas() {
        user = BmobUser.getCurrentUser(this,User.class);
        orderId = getIntent().getStringExtra("scan_result");
        mInfo.setText(orderId);

    }

    @Override
    protected void initViews() {

    }

    @OnClick(R.id.finish_deal)
    void onFinishDealBtnClick(View v) {
        final BmobQuery<Order> query = new BmobQuery<>();
        query.addWhereEqualTo("order_id", orderId);
        query.findObjects(this, new FindListener<Order>() {
            @Override
            public void onSuccess(List<Order> list) {
                if(list.size()==1) {
                    mOrder = list.get(0);
                    if(mOrder.getStatus()==0){
                        mOrder.setStatus(1);
                        Log.e(mOrder.getObjectId(),"fuckyou!!!!!!!!!!!!!!!!!!!!!");
                        mOrder.update(ScannerActivity.this, mOrder.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                textShowInfo();
                                showToast("使用成功");

                            }
                            @Override
                            public void onFailure(int i, String s) {
                                showToast("使用失败，结果无法返回服务器");
                                Log.e(""+i+"!!!!",s);
                            }
                        });

                    } else {
                        showToast("订单已使用");
                    }

                } else if(list.size() == 0) {
                    showToast("订单不存在");
                }
            }

            @Override
            public void onError(int i, String s) {
                showToast("出错，请重试");
            }
        });
    }
    @OnClick(R.id.rescan)
    void onRescanBtnClick(View v) {
        onBackPressed();
    }

    void textShowInfo(){
        mInfo.append("\n该订单已成功使用");
        mInfo.append("\n活动为 "+mOrder.getTitle());
        mInfo.append("\n数量为 "+mOrder.getCount());
        mInfo.append("\n价格为 "+mOrder.getPrice());
        mInfo.append("\n创建时间 "+mOrder.getCreatedAt());
    }
    void updateOrder() {

    }
    @Override
    protected int initLayoutRes() {
        return R.layout.activity_scanner;
    }

    @Override
    protected void onExit() {

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_clam,R.anim.slide_out_right);
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
        titleCenter.setText("确认订单");
    }
}
