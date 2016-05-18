package wang.chillax.betterplay.fragment;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.adapter.BaseAdapter;
import wang.chillax.betterplay.adapter.ViewHolder;
import wang.chillax.betterplay.bmob.Order;

/**
 * 已支付界面, 你还要继承一下那个下拉刷新的接口 我暂时先不写
 */
public class PaidPage extends BasePage implements PtrHandler{
        @Bind(R.id.lv_paid)
        ListView mListView;
        List<Order> mOrders = new ArrayList<Order>();
        OrderAdapter mOrderAdapter;

        @Override
        public int initLayoutRes() {
            return R.layout.fragment_paid_page;
        }

        @Override
        public void initViews() {
        }

        @Override
        public void initDatas() {
                mOrders.add(new Order("","",1,"",1,1.0));
                mOrders.add(new Order("","",1,"",1,1.0));
                mOrders.add(new Order("","",1,"",1,1.0));
                mOrderAdapter = new OrderAdapter(mOrders);
                mListView.setAdapter(mOrderAdapter);

        }

        @Override
        public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return false;
        }

        @Override
        public void onRefreshBegin(PtrFrameLayout frame) {

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
                        ViewHolder holder= ViewHolder.get(PaidPage.super.context,convertView,R.layout.order_list_item,position,parent);
                        //这个中间对东西赋值
                        return holder.getConvertView();
                        //return null;
                }
        }

    }
