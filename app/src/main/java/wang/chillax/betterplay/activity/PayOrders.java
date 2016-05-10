package wang.chillax.betterplay.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.bmob.Order;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.utils.LogUtils;
import wang.chillax.betterplay.utils.UserUtil;

/**
 * Created by MAC on 15/12/16.
 * 个人中心已付订单页面
 */
public class PayOrders extends BaseActivity implements ToolBar.ToolBarListener {

    @Bind(R.id.toolbar)
    ToolBar mToolbar;
    PayOrdersAdapter mAdapter;
    List<Order> orderList;

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {
        mToolbar.setToolBarListener(this);
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.activity_payorders;
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
        titleCenter.setText("已付订单");
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

    class PayOrdersAdapter extends RecyclerView.Adapter<OrderHolder>{


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

    private void openOrderDetailPage(Order order) {
        Intent intent=new Intent(this,OrderDetail.class);
        intent.putExtra("order",new Parcelable[]{order});
        startActivity(intent);
    }

    class OrderHolder extends RecyclerView.ViewHolder{
        TextView title;
        public OrderHolder(View itemView) {
            super(itemView);
            title= (TextView)itemView.findViewById(R.id.title);
        }
        public void setOrder(Order order){
//            title.setText(order.getGroup().getName());
        }
    }
}
