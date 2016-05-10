package wang.chillax.betterplay.activity;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.LayoutDirection;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.bmob.Order;
import wang.chillax.betterplay.config.Paths;
import wang.chillax.betterplay.cusview.GLVAdapter;
import wang.chillax.betterplay.cusview.GLVDefaultItemView;
import wang.chillax.betterplay.cusview.GroupListView;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.utils.QRCodeUtil;
import wang.chillax.betterplay.utils.ScreenUtil;

/**
 * Created by MAC on 15/12/31.
 * 订单详情,用来在订单中心显示.
 */
public class OrderDetail extends BaseActivity implements ToolBar.ToolBarListener{

    @Bind(R.id.toolbar)
    ToolBar mToolbar;
    @Bind(R.id.glv)
    GroupListView mGlv;
    Order mOrder;//订单
    String mTitle;//标题
    GLVAdapter mAdapter;
    ImageView mQRCodeView;
    int mQRWidth;
    @Override
    protected void initDatas() {
        mQRWidth=ScreenUtil.getScreenWidth(this);
        mTitle=getIntent().getStringExtra("title");
        mOrder= (Order) getIntent().getParcelableArrayExtra("order")[0];
        mAdapter=new MyGLVAdapter(mGlv);
        mGlv.setGLVAdapter(mAdapter);
        mGlv.addFooterView(createFooterView());
        loadQRView();
    }

    private void loadQRView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file=new File(Paths.imagesPath);
                if(!file.exists()){
                    System.out.println(file.mkdirs());
                }
                boolean ok=QRCodeUtil.createQRImage(mOrder.getOrder_id(),mQRWidth,mQRWidth, BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher),Paths.imagesPath+"QR.jpeg");
                if(ok){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mQRCodeView.setImageBitmap(BitmapFactory.decodeFile(Paths.imagesPath+"QR.jpeg"));
                        }
                    });
                }
            }
        }).start();
    }

    private View createFooterView() {
        RelativeLayout layout=new RelativeLayout(this);
        AbsListView.LayoutParams lp=new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(lp);
        mQRCodeView=new ImageView(this);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(mQRWidth,mQRWidth);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mQRCodeView.setLayoutParams(params);
        layout.addView(mQRCodeView);
        return layout;
    }

    @Override
    protected void initViews() {
        mToolbar.setToolBarListener(this);
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.activity_order_detail;
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
        titleCenter.setText("订单详情");
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
                    return new GLVDefaultItemView(mBaseAty).setTitle(mTitle).getContentView();
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
