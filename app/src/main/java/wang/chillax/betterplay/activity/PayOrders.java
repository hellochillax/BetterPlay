package wang.chillax.betterplay.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.bmob.Order;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.fragment.OutOfDatePage;
import wang.chillax.betterplay.fragment.PaidPage;
import wang.chillax.betterplay.fragment.UnpaidPage;
import wang.chillax.betterplay.utils.LogUtils;
import wang.chillax.betterplay.utils.UserUtil;

/**
 * Created by MAC on 15/12/16.
 * 个人中心已付订单页面
 */
public class PayOrders extends FragmentActivity implements ToolBar.ToolBarListener {

    @Bind(R.id.toolbar)
    ToolBar mToolbar;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    FragmentPagerAdapter mAdapter;
    //PayOrdersAdapter mAdapter;
    List<Order> orderList;

    @Bind(R.id.tv_paid)
    TextView mPaidText;
    @Bind(R.id.tv_unpaid)
    TextView mUnpaidText;
    @Bind(R.id.tv_out_of_date)
    TextView mOutOfDateText;

    List<Fragment> mFragments = new ArrayList<Fragment>();
    /******************************************************************************************************************************
     * 两行注释之间的是BaseActivity代码 复制过来的*/

    private Dialog mExitDialog;
    private Dialog mLoadingView;
    protected Activity mBaseAty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseAty=this;
        initStatusBar();
        if (initLayoutRes() != 0) {
            setContentView(initLayoutRes());
        }else {
            throw new NullPointerException("you may have forgot to set layout resource id on function initLayoutRes()");
        }
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

/*    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }*/

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.action_bar_bg_color);//通知栏所需颜色
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * 实现经典的Activity打开动画
     */
    protected void playOpenAnimation(){
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_clam);
    }
    /**
     * 实现经典的Activity退出动画
     */
    protected void playExitAnimation(){
        overridePendingTransition(R.anim.slide_clam,R.anim.slide_out_right);
    }

    protected void showExitDialog(String title,String msg){
        if(mExitDialog==null){
            mExitDialog=new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(msg).setCancelable(false)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onExit();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
        }
        mExitDialog.show();
    }
    public void showLoadingDialog(String msg){
        if(mLoadingView==null){
            mLoadingView=new Dialog(this,R.style.base_aty_loading_dialog_style);
            mLoadingView.setCanceledOnTouchOutside(false);
        }
        View view= LayoutInflater.from(this).inflate(R.layout.base_aty_loading_dialog,null);
        mLoadingView.setContentView(view);
        TextView msgView = (TextView) view.findViewById(R.id.msg);
        if(TextUtils.isEmpty(msg)){
            msgView.setVisibility(View.GONE);
        }else {
            msgView.setText(msg);
        }
        mLoadingView.show();
    }
    public void hideLoadingDialog(){
        if(mLoadingView!=null&&mLoadingView.isShowing()){
            mLoadingView.dismiss();
        }
    }
    /*******************************************************************************************************************************/
    /**
     * 相当于一个伪继承，完善几个BaseActivity里的抽象函数吧
     */

    protected void initDatas() {
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //changeTab(position);
            }

            @Override
            public void onPageSelected(int position) {
                changeTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    protected void initViews() {
        mToolbar.setToolBarListener(this);
        //mPaidText.setTextColor(getResources().getColor(R.color.umeng_comm_feed_detail_blue));

        mFragments.add(new PaidPage());
        //mFragments.add(new PaidPage());
       // mFragments.add(new PaidPage());

        mFragments.add(new UnpaidPage());
        mFragments.add(new OutOfDatePage());

/*        PaidPage mPaidPage = new PaidPage();
        UnpaidPage mUnpaidPage = new UnpaidPage();
        OutOfDatePage mOutOfDatePage = new OutOfDatePage();*/

    }


    protected int initLayoutRes() {
        return R.layout.activity_payorders;
    }

    @OnClick(R.id.tv_paid)
    public void onPaidClick(View v){
        changeTab(0);
        mViewPager.setCurrentItem(0,true);
    }
    @OnClick(R.id.tv_unpaid)
    public void onUnpaidClick(View v){
        changeTab(1);
        mViewPager.setCurrentItem(1,true);
    }
    @OnClick(R.id.tv_out_of_date)
    public void onOutOfDateClick(View v){
        changeTab(2);
        mViewPager.setCurrentItem(2,true);
    }

    public void changeTab(int position){
        switch (position){
            case 0:
                mPaidText.setTextColor(getResources().getColor(R.color.umeng_comm_feed_detail_blue));
                mUnpaidText.setTextColor(getResources().getColor(R.color.umeng_comm_black_color));
                mOutOfDateText.setTextColor(getResources().getColor(R.color.umeng_comm_black_color));
                break;
            case 1:
                mUnpaidText.setTextColor(getResources().getColor(R.color.umeng_comm_feed_detail_blue));
                mPaidText.setTextColor(getResources().getColor(R.color.umeng_comm_black_color));
                mOutOfDateText.setTextColor(getResources().getColor(R.color.umeng_comm_black_color));
                break;
            case 2:
                mOutOfDateText.setTextColor(getResources().getColor(R.color.umeng_comm_feed_detail_blue));
                mUnpaidText.setTextColor(getResources().getColor(R.color.umeng_comm_black_color));
                mPaidText.setTextColor(getResources().getColor(R.color.umeng_comm_black_color));
                break;
        }
    }

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
        titleCenter.setText("我的订单");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playExitAnimation();
    }


    private void refrushOrderList() {
        BmobQuery<Order> query =new BmobQuery<>();
        query.addWhereEqualTo("user", UserUtil.getCurrentUser(this));
        query.order("-updatedAt");
        query.include("user.username,action");
        query.findObjects(this, new FindListener<Order>() {
            @Override
            public void onSuccess(List<Order> list) {
                orderList=list;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                showToast("网络错误");
                LogUtils.d(s);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

 /*   class PayOrdersAdapter extends RecyclerView.Adapter<OrderHolder>{


        @Override
        public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new OrderHolder(LayoutInflater.from(PayOrders.this).inflate(R.layout.order_list_item,null));
        }

        @Override
        public void onBindViewHolder(OrderHolder holder, final int position) {
            holder.setOrder(orderList.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOrderDetailPage(orderList.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList==null?0:orderList.size();
        }
    }
*/
    private void openOrderDetailPage(Order order) {
        Intent intent=new Intent(this,OrderDetail.class);
        intent.putExtra("order",new Parcelable[]{order});
        startActivity(intent);
    }

/*    class OrderHolder extends RecyclerView.ViewHolder{
        TextView title;
        public OrderHolder(View itemView) {
            super(itemView);
            title= (TextView)itemView.findViewById(R.id.title);
        }
        public void setOrder(Order order){
//            title.setText(order.getGroup().getName());
        }
    }*/

}
