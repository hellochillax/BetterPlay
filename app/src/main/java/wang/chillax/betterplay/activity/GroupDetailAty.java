package wang.chillax.betterplay.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.bmob.GroupDetail;
import wang.chillax.betterplay.bmob.GroupFriend;
import wang.chillax.betterplay.cusview.GLVAdapter;
import wang.chillax.betterplay.cusview.GLVDefaultItemView;
import wang.chillax.betterplay.cusview.GroupListView;
import wang.chillax.betterplay.cusview.PullToZoomGLV;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.utils.ImageLoader;
import wang.chillax.betterplay.utils.LogUtils;
import wang.chillax.betterplay.utils.ScreenUtil;

/**
 * Created by MAC on 16/3/22.
 */
public class GroupDetailAty extends BaseActivity implements ToolBar.ToolBarListener {


    @Bind(R.id.action_bar)
    ToolBar mToolBar;
    @Bind(R.id.glv)
    PullToZoomGLV mGlv;
    GLVAdapter mAdapter;
    ImageView mHeaderView;
    GroupDetail mDetailData;
    GroupFriend mGroupFriend;
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    refreshData();
                    break;
            }
        }
    };

    @Override
    protected void initDatas() {
        mGroupFriend= (GroupFriend) getIntent().getParcelableArrayListExtra("action").get(0);
        new MsgTask().execute();
//        mGlv.addHeaderView(mHeaderView);
        mAdapter = new MyAdapter(mGlv);
        mGlv.setGLVAdapter(mAdapter);
    }

    @Override
    protected void initViews() {
        mToolBar.setToolBarListener(this);
        mHeaderView = mGlv.getHeaderView();
    }

//    private View createHeaderView() {
//        ImageView header = new ImageView(this);
//        header.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(this, 40)));
//        return header;
//    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_group_detail;
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
        if(BmobUser.getCurrentUser(this)!=null){
            doBuyAction();
        }else {
            showToast("请先登陆");
            doLoginAction();
        }
    }

    private void doLoginAction() {
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_clam);
    }

    private void doBuyAction() {
        if (mDetailData==null){
            showToast("请刷新重试");
        }else {
            Intent intent=new Intent(this,BuyPage.class);
            ArrayList<GroupDetail> list=new ArrayList<>();
            list.add(mDetailData);
            intent.putExtra("detail",mDetailData);
            startActivity(intent);
            playOpenAnimation();
        }
    }

    @Override
    public void onInit(ImageView back, TextView titleLeft, TextView titleCenter, TextView titleRight, ImageView more) {
        titleCenter.setText("商家详情");
        titleRight.setText("点击购买");
    }

    private class MyAdapter extends GLVAdapter {

        public MyAdapter(GroupListView lv) {
            super(lv);
        }

        @Override
        public int groupCount() {
            return 4;
        }

        @Override
        public int dividerHeight() {
            return ScreenUtil.dp2px(mBaseAty,30);
        }

        @Override
        public int countInGroup(int group) {
            switch (group) {
                case 0:
                    return 2;
                case 1:
                    return 2;
                case 2:
                    return 1;
                case 3:
                    return 2;
            }
            return 0;
        }

        @Override
        public View getView(int group, int position) {
            switch (group) {
                case 0:
                    if (position == 0) {
                        return new GLVDefaultItemView(mBaseAty).setTitle(mDetailData == null ? "" : mDetailData.getTitle()).getContentView();
                    } else if(position==1){
                        return new GLVDefaultItemView(mBaseAty).setTitle("价格").setDetail(mDetailData == null ? "" :  mDetailData.getPrice()+"元").getContentView();
                    }
                case 1:
                    if (position == 0) {
                        return new GLVDefaultItemView(mBaseAty).setTitle("团购时间").setDetail(mDetailData == null ? "" : mDetailData.getGroup_time()).getContentView();
                    } else {
                        return new GLVDefaultItemView(mBaseAty).setTitle("已购买人数").setDetail(mDetailData == null ? "" : mDetailData.getBuys() + "").getContentView();
                    }
                case 2:
                    return new GLVDefaultItemView(mBaseAty).setTitle("商家信息").setDetail("点击查看图文详情").getContentView();
                case 3:
                    if (position == 0) {
                        return new GLVDefaultItemView(mBaseAty).setTitle("使用期限").setDetail(mDetailData == null ? "" : mDetailData.getLegal_time()).getContentView();

                    } else {
                        return new GLVDefaultItemView(mBaseAty).setTitle("注意事项").setDetail(mDetailData == null ? "" : mDetailData.getRule()).getContentView();
                    }
            }
            throw new NullPointerException("error in GlvAdapter!!");
        }
    }

    private class MsgTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            BmobQuery<GroupDetail> query = new BmobQuery<>();
            query.addWhereEqualTo("group_id", 1);
            query.findObjects(GroupDetailAty.this, new FindListener<GroupDetail>() {
                @Override
                public void onSuccess(List<GroupDetail> list) {
                    mDetailData = list.get(0);
                    LogUtils.d("GroupDetailAty:" + mDetailData);
                    mHandler.obtainMessage(0).sendToTarget();
                }

                @Override
                public void onError(int i, String s) {
                    LogUtils.d(i+":"+s);
                    showToast("网络错误,请重试");
                }
            });
            return null;
        }
    }

    private void refreshData() {
        if(mDetailData!=null){
            ImageLoader.getInstance().displayImage(mDetailData.getImage().getFileUrl(this),mHeaderView);
            LogUtils.d("Image:"+mDetailData.getImage().getFileUrl(this));
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playExitAnimation();
    }
}
