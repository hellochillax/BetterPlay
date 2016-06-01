package wang.chillax.betterplay.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.bmob.GroupDetail;
import wang.chillax.betterplay.bmob.Order;
import wang.chillax.betterplay.bmob.User;
import wang.chillax.betterplay.config.Keys;
import wang.chillax.betterplay.cusview.GLVAdapter;
import wang.chillax.betterplay.cusview.GLVDefaultItemView;
import wang.chillax.betterplay.cusview.GroupListView;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.utils.LogUtils;
import wang.chillax.betterplay.utils.OrderUtils;
import wang.chillax.betterplay.utils.ScreenUtil;

/**
 * Created by MAC on 15/12/16.
 *
 * 订单支付界面
 */
public class BuyPage extends BaseActivity implements ToolBar.ToolBarListener {


    GroupDetail detail;
    boolean pay=false;  //用户是否付款成功
    int count=1;  //用户选择的票的数量
    Order order=null;//订单
//    EditText mRecommendEt;
//    ViewGroup mRecommendVG;

    @Bind(R.id.toolbar)
    ToolBar mToolbar;
    @Bind(R.id.glv)
    GroupListView mGlv;

    GLVAdapter mAdapter;
    void to_reduce(){
        if(count==1){
            showToast("至少买一张");
        }else{
            count--;
            mAdapter.notifyDataSetChanged();
        }
    }
    void to_plus(){
        if(count==5){
            showToast("最多买五张");
        }else{
            count++;
            mAdapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void initDatas() {
        detail= (GroupDetail) getIntent().getSerializableExtra("detail");
        mAdapter=new GLVAdapter(mGlv){

            @Override
            public int groupCount() {
                return 3;
            }

            @Override
            public int dividerHeight() {
                return ScreenUtil.dp2px(BuyPage.this,20);
            }

            @Override
            public int countInGroup(int group) {
                final int curr=group;
                switch (curr){
                    case 0:
                        return 1;
                    case 1:
                        return 4;
                    case 2:
                        return 2;
                    default:
                        return 0;
                }
            }

            @Override
            public View getView(int group, int position) {
                final int curr=group;
                switch (curr){
                    case 0:
                        return new GLVDefaultItemView(BuyPage.this).setTitle(detail.getTitle()).getContentView();
                    case 1:
                        if(position==0){
                            return new GLVDefaultItemView(BuyPage.this).setTitle("单价").setDetail(detail.getPrice()+"").getContentView();
                        }else if(position==2){
                            return new GLVDefaultItemView(BuyPage.this).setTitle("小计").setDetail("￥"+detail.getPrice()*count).setDetailColor(Color.RED).getContentView();
                        }else if(position==3){
                            return new GLVDefaultItemView(mBaseAty).setTitle("返现").setDetail("￥"+detail.getBack()*count).getContentView();
                        }else{
                            return createPriceItemView();
                        }
                    case 2:
                        if(position==0) {
                            return new GLVDefaultItemView(BuyPage.this).setTitle("手机号").setDetail(BmobUser.getCurrentUser(BuyPage.this).getUsername()).getContentView();
                        }else if(position==1){
                            return new GLVDefaultItemView(BuyPage.this).setTitle("推荐码").setDetail(BmobUser.getCurrentUser(BuyPage.this,User.class).getCode()).getContentView();
                        }
                }
                throw new NullPointerException("null pointer in getView of BuyPage!!");
            }

        };
        mGlv.addFooterView(createFooterView());
        mGlv.setGLVAdapter(mAdapter);
    }

//    /**
//     * 推荐码的View
//     */
//    private View createRecommendView(){
//        if(mRecommendVG==null){
//            mRecommendVG= (ViewGroup) LayoutInflater.from(this).inflate(R.layout.buypage_recommend,null);
//            mRecommendEt= (EditText) mRecommendVG.findViewById(R.id.detail);
//        }
//        return mRecommendVG;
//    }

    private View createPriceItemView() {
        View view=LayoutInflater.from(this).inflate(R.layout.buypage_price_item,null);
        view.findViewById(R.id.reduce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to_reduce();
            }
        });
        view.findViewById(R.id.plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to_plus();
            }
        });
        ((TextView)view.findViewById(R.id.count)).setText(""+count);
        ((TextView)view.findViewById(R.id.reduce)).setTextColor(count==1?Color.GRAY:Color.GREEN);
        ((TextView)view.findViewById(R.id.plus)).setTextColor(count==5?Color.GRAY:Color.GREEN);
        return view;
    }

    private View createFooterView() {
        View view=LayoutInflater.from(this).inflate(R.layout.buypage_footer_buy,null);
        view.findViewById(R.id.buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyAction();
            }
        });
        return view;
    }

    @Override
    protected void initViews() {
        mToolbar.setToolBarListener(this);
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.activity_buypage;
    }

    @Override
    protected void onExit() {
        pay=true;
        finish();
    }

    private void buyAction() {
        //验证代理购买权限
        User user=BmobUser.getCurrentUser(this,User.class);
        int level=user.getLevel();
        if(level>=100&&level<200){
            payAction(user.getCode());
        }else if(level==888){
            payAction(null);
        }else{
            showToast("只有代理才能下单");
        }
//        String code=mRecommendEt.getText().toString();
//        if(TextUtils.isEmpty(code)){
//            payAction(null);
//        }else if(code.trim().equals(BmobUser.getCurrentUser(this,User.class).getCode())){
//            showToast("不能填写自己的推荐码");
//            mRecommendEt.setText("");
//        }else {
//            if(code.length()==6&&code.matches("\\d*")){
//                new CheckCodeTask().execute(code);
//            }else{
//                showToast("推荐码不合法");
//                mRecommendEt.setText("");
//            }
//        }
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    showToast("推荐码错误");
                    break;
                case -1:
                    showToast(Keys.NET_ERROR);
                    break;
            }
        }
    };

    private class CheckCodeTask extends AsyncTask{

        @Override
        protected Object doInBackground(final Object[] params) {
            BmobQuery<User> query=new BmobQuery<>();
            query.addWhereEqualTo("code",params[0]);
            query.findObjects(BuyPage.this, new FindListener<User>() {
                @Override
                public void onSuccess(List<User> list) {
                    if(list!=null&&list.size()>0){
                        LogUtils.d(list.get(0).toString());
                        payAction(params[0].toString());
                    }else {
                        mHandler.obtainMessage(1).sendToTarget();
                    }
                }

                @Override
                public void onError(int i, String s) {
                    mHandler.obtainMessage(-1).sendToTarget();
                }
            });
            return null;
        }
    }

    private void payAction(String code){

        OrderUtils.pay(this, detail, count,code, new OrderUtils.PayListenerr() {

            @Override
            public void onSuccess(Order o) {
                pay=true;
                order=o;
                showToast("支付成功");
                setResult(RESULT_OK);
                onBackPressed();
            }

            @Override
            public void onFail(String msg) {
                showToast(msg);
            }
        });
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
        buyAction();
    }

    @Override
    public void onInit(ImageView back, TextView titleLeft, TextView titleCenter, TextView titleRight, ImageView more) {
        titleCenter.setText("付款");
    }

    @Override
    public void onBackPressed() {
        if(pay){
            openOrderDetail();
            finish();
        }else {
            showExitDialog("提醒","付款还未完成,确定要退出付款界面吗?");
        }
    }

    private void openOrderDetail() {
        Intent intent=new Intent(this,OrderDetail.class);
        intent.putExtra("title",detail.getTitle());
        intent.putExtra("order",new Parcelable[]{order});
        startActivity(intent);
        playOpenAnimation();
    }
}
