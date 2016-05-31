package wang.chillax.betterplay.fragment;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yalantis.taurus.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.adapter.BaseAdapter;
import wang.chillax.betterplay.adapter.ViewHolder;
import wang.chillax.betterplay.bmob.Order;
import wang.chillax.betterplay.bmob.User;


public class UnpaidPage extends BasePage {
    @Bind(R.id.lv_unpaid)
    ListView mListView;
    @Bind(R.id.pull_to_refresh)
    PullToRefreshView mPtrView;
    List<Order> mOrders = new ArrayList<Order>();
    OrderAdapter mOrderAdapter;
    User user;


    @Override
    public int initLayoutRes() {
        return R.layout.fragment_unpaid_page;
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
        mOrderAdapter.notifyDataSetChanged();
    }

    public void getNewData() {
        BmobQuery<Order> query = new BmobQuery<Order>();
        query.addWhereEqualTo("username", user.getPhone());
        query.addWhereEqualTo("status", 0);
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
            }

            @Override
            public void onError(int i, String s) {
                showToast("失败");
                mPtrView.setRefreshing(false);
            }
        });
        mOrderAdapter.notifyDataSetChanged();
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
            ViewHolder holder= ViewHolder.get(UnpaidPage.super.context,convertView,R.layout.order_list_item,position,parent);
            holder.setText(R.id.orderName,""+mList.get(position).getOrder_id());
            holder.setText(R.id.orderCount,"*"+mList.get(position).getCount());
            holder.setText(R.id.orderPrice,""+mList.get(position).getPrice());
            //这个中间对东西赋值
            return holder.getConvertView();
            //return null;
        }
    }

}

