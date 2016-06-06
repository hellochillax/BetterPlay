package wang.chillax.betterplay.activity;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.bmob.Order;
import wang.chillax.betterplay.config.Paths;
import wang.chillax.betterplay.cusview.GLVAdapter;
import wang.chillax.betterplay.cusview.GLVDefaultItemView;
import wang.chillax.betterplay.cusview.GroupListView;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.utils.QRCodeUtil;
import wang.chillax.betterplay.utils.ScreenUtil;

public class OrderUseActivity extends BaseActivity implements ToolBar.ToolBarListener {

    @Bind(R.id.toolbar)
    ToolBar mToolbar;
    @Bind(R.id.glv)
    GroupListView mGlv;
    Order mOrder;//订单
    GLVAdapter mAdapter;
    String orderId;

    @Override
    protected void initDatas() {
        orderId = getIntent().getStringExtra("scan_result");
        /***
         * 获取订单数据数据
         */
        final BmobQuery<Order> query = new BmobQuery<>();
        query.addWhereEqualTo("order_id", orderId);
        query.findObjects(this, new FindListener<Order>() {
            @Override
            public void onSuccess(List<Order> list) {
                if(list.size()==1) {
                    mOrder = list.get(0);
                    if(mOrder.getStatus()==0){
                        initListView();
                        showUseView();
                    } else {
                        initListView();
                        showAlreadyUsedView();
                    }

                } else if(list.size() == 0) {
                    showNoOrderView();
                }
            }

            @Override
            public void onError(int i, String s) {
                showToast(getResources().getString(R.string.error_network));
            }
        });




    }

    private void initListView(){
        mAdapter=new MyGLVAdapter(mGlv);
        mGlv.setGLVAdapter(mAdapter);

    }
    private void showUseView() {
        final Button btn = (Button) findViewById(R.id.info_btn);
        btn.setClickable(true);
        btn.setText(getResources().getString(R.string.use_order));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrder.setStatus(1);
                mOrder.update(OrderUseActivity.this, mOrder.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        showToast(getResources().getString(R.string.use_order_successfully));
                        btn.setText(getResources().getString(R.string.use_order_successfully));
                        //btn.setVisibility(View.INVISIBLE);
                        btn.setClickable(false);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        mOrder.setStatus(0);
                        showToast(getResources().getString(R.string.use_order_unsuccessfully));
                    }
                });
            }
        });


    }
    private void showAlreadyUsedView() {
        final Button btn = (Button) findViewById(R.id.info_btn);
        btn.setClickable(false);
        btn.setText(getResources().getString(R.string.order_already_used));
    }
    private void showNoOrderView() {
        final Button btn = (Button) findViewById(R.id.info_btn);
        btn.setClickable(false);
        btn.setText(getResources().getString(R.string.order_not_found));
    }
    @Override
    protected void initViews() {
        mToolbar.setToolBarListener(this);
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.activity_order_use;
    }

    @Override
    protected void onExit() {

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
        titleCenter.setText("使用订单");
    }

    class MyGLVAdapter extends GLVAdapter{

        public MyGLVAdapter(GroupListView lv) {
            super(lv);
        }

        @Override
        public int groupCount() {
            return 4;
        }

        @Override
        public int dividerHeight() {
            return ScreenUtil.dp2px(mBaseAty,15);
        }

        @Override
        public int countInGroup(int group) {
            switch (group){
                case 0:
                case 3:
                    return 1;
                case 1:
                    return 2;
                case 2:
                    return 3;
            }
            return 0;
        }

        @Override
        public View getView(int group, int position) {
            switch (group){
                case 0:
                    return new GLVDefaultItemView(mBaseAty).setTitle(mOrder.getTitle()).getContentView();
                case 1:
                    if(position==0){
                        return new GLVDefaultItemView(mBaseAty).setTitle("张数").setDetail(mOrder.getCount()+"").getContentView();
                    }else {
                        return new GLVDefaultItemView(mBaseAty).setTitle("价格").setDetail(mOrder.getPrice()+"").getContentView();
                    }
                case 2:
                    if(position==0) {
                        return new GLVDefaultItemView(mBaseAty).setTitle("手机号").setDetail(mOrder.getUsername()).getContentView();
                    }else if(position==1){
                        return new GLVDefaultItemView(mBaseAty).setTitle("订单号").setDetail(mOrder.getOrder_id()).getContentView();
                    }else {
                        return new GLVDefaultItemView(mBaseAty).setTitle("推荐码").setDetail(mOrder.getCode()).getContentView();
                    }
                case 3:
                    if(position==0){
                        return new GLVDefaultItemView(mBaseAty).setTitle("购买时间").setDetail(mOrder.getCreatedAt()).getContentView();
                    }
            }
            throw new NullPointerException("OrderDetail...MyGLVAdapter...getView() occur error");
        }
    }
}
