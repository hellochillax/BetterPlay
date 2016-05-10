package wang.chillax.betterplay.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.activity.GroupDetailAty;
import wang.chillax.betterplay.activity.WebPage;
import wang.chillax.betterplay.adapter.BaseAdapter;
import wang.chillax.betterplay.bmob.GroupFriend;
import wang.chillax.betterplay.bmob.TopImage;
import wang.chillax.betterplay.cusview.HeaderGridView;
import wang.chillax.betterplay.cusview.RectImageView;
import wang.chillax.betterplay.cusview.RollViewPager;
import wang.chillax.betterplay.dao.HomeListDao;
import wang.chillax.betterplay.dao.TopImageDao;
import wang.chillax.betterplay.utils.ImageLoader;
import wang.chillax.betterplay.utils.LogUtils;
import wang.chillax.betterplay.utils.ScreenUtil;


/**
 * Created by MAC on 15/12/1.
 */
public class HomePage extends BasePage implements PtrHandler {


    @Bind(R.id.store_house_ptr_frame)
    PtrFrameLayout mPtrFrame;
    @Bind(R.id.content_gv)
    HeaderGridView mContentGv;
    ContentAdapter mAdapter;
    List<GroupFriend> contentList=new ArrayList<>();

    ViewGroup mHeaderView;
    RollViewPager mRollVp;


    ArrayList<String> mRollVpUrls;
    List<TopImage> mTopImages;
    ArrayList<String> mTitles;

    TopImageDao mTopDao;//顶部数据缓存
    HomeListDao mHomeDao;//ListView的数据缓存


    @Override
    protected void initDatas() {
        loadFromCache();
        mPtrFrame.autoRefresh();
    }

    @Override
    protected void initViews() {
        mTopDao=new TopImageDao(context);
        mHomeDao=new HomeListDao(context);
        initPtrFrameLayout();
        initHeaderView();
        initContentGv();
    }

    private void initHeaderView() {
        mHeaderView = (RelativeLayout) LayoutInflater.from(context).inflate(
                R.layout.roll_viewpager, null);
        mContentGv.addHeaderView(mHeaderView);
        mRollVp = new RollViewPager(context,new RollViewPager.OnPagerClickCallback() {
            @Override
            public void onPagerClick(int position) {
                openTopPage(position);
            }
        });
        mRollVpUrls=new ArrayList<>();
        mRollVpUrls.add("");//先添加一个无用的URL.
        mTitles=new ArrayList<>();
        mTitles.add("");
        mRollVp.setUriList(mRollVpUrls);
        mRollVp.setTitle((TextView) mHeaderView.findViewById(R.id.title),mTitles);
        mRollVp.setDots((ViewGroup) mHeaderView.findViewById(R.id.dots_ll),R.mipmap.dot_normal,R.mipmap.dot_focus);
        mRollVp.startRoll();
        LinearLayout layout = (LinearLayout) mHeaderView
                .findViewById(R.id.top_news_viewpager);
        layout.addView(mRollVp);
        loadTopView();
    }

    private void openTopPage(int pos) {
        TopImage top=mTopImages.get(pos);
        Intent intent = new Intent(context, WebPage.class);
        intent.putExtra("url",top.getAddress());
        intent.putExtra("title",top.getTitle());
        startActivity(intent);
        playOpenAnimation();
    }

    private void loadFromCache() {
        List<TopImage> lists=mTopDao.getTopImages();
        if (lists!=null){
            mTopImages=lists;
            refreshLocalTop();
        }
        List<GroupFriend> glists=mHomeDao.getHomeLists();
        if (glists!=null){
            contentList=glists;
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadTopView() {
        new TopTask().execute();
    }

    private void initContentGv() {
        mAdapter=new ContentAdapter();
        mContentGv.setAdapter(mAdapter);
        mContentGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               final int pos=position;
                Intent i=new Intent(context, GroupDetailAty.class);
                ArrayList<GroupFriend> list=new ArrayList<GroupFriend>();
                list.add(contentList.get(position-3));
                i.putParcelableArrayListExtra("action",list);
                startActivity(i);
                playOpenAnimation();
            }
        });

    }

    private void initPtrFrameLayout() {
        mPtrFrame.setPullToRefresh(true);
        mPtrFrame.setPtrHandler(this);
        // header
        final StoreHouseHeader header = new StoreHouseHeader(context);
        header.setPadding(0, ScreenUtil.dp2px(context, 15), 0, 0);
        header.initWithString("Better Play");
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.page_home;
    }


    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return mContentGv.getCount()>0&&mContentGv.getChildAt(0).getTop()>=0;
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        new TopTask().execute();
        new ContentTask().execute();
    }


    private class TopTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            BmobQuery<TopImage> quary=new BmobQuery<>();
            quary.setLimit(100);
            quary.findObjects(context, new FindListener<TopImage>() {
                @Override
                public void onSuccess(List<TopImage> list) {
                    mTopImages=list;
                    if(mTopImages!=null){
                        refreshLocalTop();
                        LogUtils.d(list.toString());
                    }
                }

                @Override
                public void onError(int i, String s) {
                    showToast("网络错误,请重试");
                    LogUtils.e(s);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mPtrFrame.refreshComplete();
        }
    }

    /**
     * 顶部数据获取成功后,在这里更新UI
     */
    private void refreshLocalTop() {
        mRollVpUrls.clear();
        if(mTitles!=null){
            mTitles.clear();
        }else {
            mTitles=new ArrayList<>();
        }
        for (TopImage image:mTopImages){
            mRollVpUrls.add(image.getImageUrl()!=null?image.getImageUrl():image.getImage().getFileUrl(context));
            mTitles.add(image.getTitle());
        }
        mRollVp.notifyDataChange();
    }

    private class ContentTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            BmobQuery<GroupFriend> quary=new BmobQuery<>();
            quary.setLimit(100);
            quary.findObjects(context, new FindListener<GroupFriend>() {
                @Override
                public void onSuccess(List<GroupFriend> list) {
                    contentList=list;
                    mAdapter.notifyDataSetChanged();
                    LogUtils.d(list.toString());
                }

                @Override
                public void onError(int i, String s) {
                    showToast("网络错误,请重试");
                    LogUtils.e(s);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mPtrFrame.refreshComplete();
        }
    }

    private class ContentAdapter extends BaseAdapter{

        private int ITEM_PADDING=ScreenUtil.dp2px(context,5);

        @Override
        public int getCount() {
            return contentList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GroupFriend gr=contentList.get(position);
            RectImageView logo;
            if(convertView==null){
                logo=new RectImageView(context,null);
                switch (position%mContentGv.getNumColumns()){
                    case 0:
                        logo.setPadding(ITEM_PADDING*2,ITEM_PADDING,ITEM_PADDING,ITEM_PADDING);
                        break;
                    case 1:
                        logo.setPadding(ITEM_PADDING,ITEM_PADDING,ITEM_PADDING,ITEM_PADDING);
                        break;
                    case 2:
                        logo.setPadding(ITEM_PADDING,ITEM_PADDING,ITEM_PADDING*2,ITEM_PADDING);
                        break;
                }
                logo.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
                logo.setScaleType(ImageView.ScaleType.FIT_XY);
            }else {
                logo=(RectImageView) convertView;
            }
            ImageLoader.getInstance().displayImage(gr.getLogoUrl()!=null?gr.getLogoUrl():gr.getLogo().getFileUrl(context),logo);
            return logo;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mTopImages.size()>0){
            mTopDao.clear();
            for (TopImage image:mTopImages){
                mTopDao.insert(image);
            }
        }
        if (contentList.size()>0){
            mHomeDao.clear();
            for (GroupFriend friend:contentList){
                mHomeDao.insert(friend);
            }
        }
    }
}