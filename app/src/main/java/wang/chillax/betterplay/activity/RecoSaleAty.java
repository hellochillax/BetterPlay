package wang.chillax.betterplay.activity;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.bmob.User;
import wang.chillax.betterplay.cusview.GLVAdapter;
import wang.chillax.betterplay.cusview.GLVDefaultItemView;
import wang.chillax.betterplay.cusview.GLVDefaultItemView2;
import wang.chillax.betterplay.cusview.GroupListView;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.utils.ScreenUtil;
import wang.chillax.betterplay.utils.UserUtil;

/**
 * Created by MAC on 16/4/6.
 * 我的-推荐优惠
 */
public class RecoSaleAty extends BaseActivity implements ToolBar.ToolBarListener ,GroupListView.OnGLVItemClickedListener{

    @Bind(R.id.toolbar)
    ToolBar mToolbar;
    @Bind(R.id.glv)
    GroupListView mGlv;

    String[] items=new String[]{"推荐码","返现值"};

    GLVAdapter mAdapter=new GLVAdapter(mGlv) {
        @Override
        public int groupCount() {
            return 1;
        }

        @Override
        public int dividerHeight() {
            return ScreenUtil.dp2px(mBaseAty,10);
        }

        @Override
        public int countInGroup(int group) {
            switch (group){
                case 0:
                    return 2;
            }
            return 0;
        }

        @Override
        public View getView(int group, int position) {
            switch (position){
                case 0:
                    return new GLVDefaultItemView(mBaseAty)
                            .setTitle(items[position])
                            .setDetail(UserUtil.getCurrentUser(mBaseAty).getCode())
                            .getContentView();
                case 1:
                    return new GLVDefaultItemView(mBaseAty)
                            .setTitle(items[position])
                            .setDetail("¥ "+UserUtil.getCurrentUser(mBaseAty).getScore())
                            .setDetailColor(Color.RED)
                            .getContentView();
            }
            throw new NullPointerException("RecoSaleAty getView null....");
        }
    };

    @Override
    protected void initDatas() {
        mToolbar.setToolBarListener(this);
    }

    @Override
    protected void initViews() {
        mGlv.setGLVAdapter(mAdapter);
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_reco_sale;
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
        titleCenter.setText("推荐优惠");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playExitAnimation();
    }


    @Override
    public void onItemClicked(int group, int position) {

    }
}
