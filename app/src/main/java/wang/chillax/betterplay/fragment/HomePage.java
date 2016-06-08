package wang.chillax.betterplay.fragment;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.zxing.client.android.CaptureActivity;
import com.yalantis.taurus.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.activity.GroupDetailAty;
import wang.chillax.betterplay.activity.ScannerActivity;
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
import wang.chillax.betterplay.utils.UserUtil;


/**
 * Created by MAC on 15/12/1.
 */
public class HomePage extends BasePage {

    @Bind(R.id.pull_to_refresh)
    PullToRefreshView mPtrView;
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

    static final int CODE_CAPTURE = 0x12;
    private static final int CODE_SAVE_ON_STOP=0x01;
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CODE_SAVE_ON_STOP:
                    saveDataOnDestroy();
                    break;
            }
        }
    };


    @Override
    protected void initDatas() {
        loadFromCache();
    }

    @Override
    protected void initViews() {
        mTopDao=new TopImageDao(context);
        mHomeDao=new HomeListDao(context);
        initPtrView();
        initHeaderView();
        initContentGv();
    }

    private void initHeaderView() {
        mHeaderView = (RelativeLayout) LayoutInflater.from(context).inflate(
                R.layout.roll_viewpager, null);
        mContentGv.addHeaderView(mHeaderView);
        mRollVp= (RollViewPager) mHeaderView.findViewById(R.id.top_vp);
        mRollVpUrls=new ArrayList<>();
        mRollVp.setImageUrls(mRollVpUrls);
        mRollVp.startRoll();
        loadTopView();
    }

    private void openTopPage(int pos) {
        TopImage top=mTopImages.get(pos);
        Intent intent = new Intent(context, WebPage.class);
        intent.putExtra(WebPage.URL,top.getAddress());
        intent.putExtra(WebPage.TITLE,top.getTitle());
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
            GroupFriend.sortByPriority(contentList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadTopView() {
        doTopTask();
    }

    private void initContentGv() {
        mAdapter=new ContentAdapter();
        mContentGv.setAdapter(mAdapter);
        mContentGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupFriend item=contentList.get(position-3);
                if(item.getPrice()<=0){
                    showToast(getResources().getString(R.string.not_start));


                }else{
                    Intent i=new Intent(context, GroupDetailAty.class);
                    ArrayList<GroupFriend> list=new ArrayList<>();
                    list.add(item);
                    i.putParcelableArrayListExtra("action",list);
                    startActivity(i);
                    playOpenAnimation();
                }
            }
        });

    }

    private void initPtrView() {
        mPtrView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doTopTask();
                doCoententTask();
            }
        });
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.page_home;
    }

    @Override
    public void updateByLevel(UserUtil.Level level) {

    }


    public void doTopTask(){
        BmobQuery<TopImage> quary=new BmobQuery<>();
        quary.setLimit(100);
        quary.findObjects(context, new FindListener<TopImage>() {
            @Override
            public void onSuccess(List<TopImage> list) {
                mTopImages=list;
                if(mTopImages!=null){
                    refreshLocalTop();
                    LogUtils.d(list.toString());
                    mPtrView.setRefreshing(false);
                }
            }

            @Override
            public void onError(int i, String s) {
                showToast(getResources().getString(R.string.error_network));
                LogUtils.e(s);
                mPtrView.setRefreshing(false);
            }
        });
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

        mRollVp.notifyDataSetChanged();
    }

    public void doCoententTask(){
        BmobQuery<GroupFriend> quary=new BmobQuery<>();
        quary.setLimit(100);
        quary.findObjects(context, new FindListener<GroupFriend>() {
            @Override
            public void onSuccess(List<GroupFriend> list) {
                contentList=list;
                GroupFriend.sortByPriority(contentList);
                mAdapter.notifyDataSetChanged();
                LogUtils.d(list.toString());
                mPtrView.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                showToast(getResources().getString(R.string.error_network));
                LogUtils.e(s);
                mPtrView.setRefreshing(false);
            }
        });
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
        //onDestory中,当用户主动杀死后台进程时,系统留给onDestory保存数据的时间非常短,以至于
        //数据根本保存不完就被强制退出,并不是代码的问题
        super.onDestroy();
        saveDataOnDestroy();
    }

    private void saveDataOnDestroy() {
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

    @Override
    public void onStart() {
        super.onStart();
        mRollVp.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mRollVp.onStop();
        mHandler.obtainMessage(CODE_SAVE_ON_STOP).sendToTarget();
    }
}