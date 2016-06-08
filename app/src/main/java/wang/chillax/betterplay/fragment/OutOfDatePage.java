package wang.chillax.betterplay.fragment;


import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yalantis.taurus.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.activity.OrderDetail;
import wang.chillax.betterplay.adapter.BaseAdapter;
import wang.chillax.betterplay.adapter.ViewHolder;
import wang.chillax.betterplay.bmob.Order;
import wang.chillax.betterplay.bmob.User;
import wang.chillax.betterplay.utils.UserUtil;


public class OutOfDatePage extends BasePage {
    @Bind(R.id.lv_out_of_date)
    ListView mListView;
    @Bind(R.id.pull_to_refresh)
    PullToRefreshView mPtrView;
    List<Order> mOrders = new ArrayList<Order>();
    OrderAdapter mOrderAdapter;
    User user;


    @Override
    public int initLayoutRes() {
        return R.layout.fragment_out_of_date_page;
    }

    @Override
    public void updateByLevel(UserUtil.Level level) {

    }

    @Override
    public void initViews() {
        mPtrView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewData();
            }
        });
    }

    @Override
    public void initDatas() {
        user = BmobUser.getCurrentUser(super.context,User.class);
        mOrderAdapter = new OrderAdapter(mOrders);
        mListView.setAdapter(mOrderAdapter);
        getNewData();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openOrderDetail(position);
            }
        });
    }

    private void openOrderDetail(int position){
        Intent intent=new Intent(super.context,OrderDetail.class);
        intent.putExtra(OrderDetail.ORDER,new Parcelable[]{mOrders.get(position)});
        startActivity(intent);
        playOpenAnimation();
    }

    public void getNewData() {
        BmobQuery<Order> query = new BmobQuery<Order>();
        query.addWhereEqualTo("username", user.getPhone());
        query.findObjects(super.context, new FindListener<Order>() {
            @Override
            public void onSuccess(List<Order> list) {
                mOrders.clear();
                for(Order o: list) {
                    mOrders.add(o);
                }
                //mOrders = list;
                //showToast("成功");
                Log.e("fffff*****",""+mOrders.size());
                mPtrView.setRefreshing(false);
                mOrderAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(int i, String s) {
                showToast("失败");
                mPtrView.setRefreshing(false);
            }
        });
    }
    class OrderAdapter extends BaseAdapter{


        List<Order> mList;
        OrderAdapter(List<Order> list){
            this.mList = list;
        }
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder= ViewHolder.get(OutOfDatePage.super.context,convertView,R.layout.order_list_item,position,parent);
            holder.setText(R.id.orderName,""+mList.get(position).getTitle());
            holder.setText(R.id.orderCount,"*"+mList.get(position).getCount());
            holder.setText(R.id.orderPrice,""+mList.get(position).getPrice());
            holder.setText(R.id.orderDate,mList.get(position).getCreatedAt());

            //这个中间对东西赋值
            return holder.getConvertView();
            //return null;
        }
    }

}

