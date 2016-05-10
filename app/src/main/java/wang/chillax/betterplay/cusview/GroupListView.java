package wang.chillax.betterplay.cusview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import wang.chillax.betterplay.adapter.BaseAdapter;
import wang.chillax.betterplay.utils.LogUtils;

/**
 * Created by MAC on 15/12/21.
 * 可以定义分组的ListView
 */
public class GroupListView extends ListView implements AdapterView.OnItemClickListener{

    private Context mContext;
    private BaseAdapter mInnerAdapter;
    private GLVAdapter mAdapter;
    private OnGLVItemClickedListener mListener;

    public GroupListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        setOnItemClickListener(this);
    }

    public void setGLVAdapter(GLVAdapter adapter) {
        if (adapter == null) return;
        mAdapter = adapter;
        refreshList();
    }

    private void refreshList() {
        if (mInnerAdapter == null) {
            mInnerAdapter = new InnerAdapter();
            setAdapter(mInnerAdapter);
        } else {
            mInnerAdapter.notifyDataSetChanged();
        }
    }

    private View createDefaultDivider() {
        View divider = new View(mContext);
        divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        divider.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mAdapter.dividerHeight()));
        return divider;
    }

    @SuppressWarnings("unused")
    public GLVAdapter getGLVAdapter() {
        return mAdapter;
    }

    public BaseAdapter getInnerAdapter(){
        return mInnerAdapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mListener==null)return;
        int group=mAdapter.groupCount();
        int pos=position-getHeaderViewsCount();//这里不知什么鬼,貌似系统没有减掉header??
        for (int i=0;i<group;i++){
            if(pos-mAdapter.countInGroup(i)-1<0){
                mListener.onItemClicked(i,pos);
                return;
            }else {
                pos-=mAdapter.countInGroup(i);
                pos--;
            }
        }
    }

    private class InnerAdapter extends BaseAdapter{

        List<Integer> mRange=null;

        public InnerAdapter(){
            refreshRange();
        }

        @Override
        public int getCount() {
            int total = 0;
            for (int i = 0; i < mAdapter.groupCount(); i++) {
                total += mAdapter.countInGroup(i);
            }
            total+=mAdapter.groupCount()-1;
            return total > 0 ? total : 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mRange.contains(position)) {
                return createDefaultDivider();
            }
            int divider=0;
            int begin=0;
            for (Integer pos:mRange){
                if(position<pos){
                    return mAdapter.getView(divider,position-begin);
                }
                begin=pos+1;
                divider++;
            }
            return createDefaultDivider();
        }
        public void refreshRange(){
            if(mRange==null){
                mRange=new ArrayList<>();
            }else{
                mRange.clear();
            }
            int total=0;
            for (int i=0;i<mAdapter.groupCount();i++){
                total+=mAdapter.countInGroup(i)+(i==0?0:1);
                mRange.add(total);
            }
        }

        @Override
        public void notifyDataSetChanged() {
            refreshRange();
            super.notifyDataSetChanged();
        }
    }
    public void setGLVItemClickedListener(OnGLVItemClickedListener listener){
        mListener=listener;
    }
    public interface OnGLVItemClickedListener{
        void onItemClicked(int group,int position);
    }

}
