package wang.chillax.betterplay.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.cusview.GLVAdapter;
import wang.chillax.betterplay.cusview.GLVDefaultItemView;
import wang.chillax.betterplay.cusview.GLVDefaultItemView2;
import wang.chillax.betterplay.cusview.GroupListView;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.utils.ScreenUtil;

/**
 * Created by qingliu on 12/5/15.
 */
public class SettingActivity extends BaseActivity implements ToolBar.ToolBarListener ,GroupListView.OnGLVItemClickedListener{

    @Bind(R.id.set_toolbar)
    ToolBar mToolbar;
    @Bind(R.id.setting_list)
    GroupListView mSettingList;
    SettingAdapter mAdapter;

    String[][] items=new String[][]{
            {"关于我们","意见反馈","常见问题"}
    };

    @Override
    protected void initDatas() {
        mAdapter=new SettingAdapter(mSettingList);
        mSettingList.setGLVAdapter(mAdapter);
    }

    @Override
    protected void initViews() {
        mToolbar.setToolBarListener(this);
        mSettingList.setGLVItemClickedListener(this);
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.activity_setting;
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
        titleCenter.setText("设置");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playExitAnimation();
    }

    @Override
    public void onItemClicked(int group, int position) {
        switch (position){
            case 0:
                openAboutUsAty();
                break;
            case 1:
                openFeedAty();
                break;
            case 2:
                openHelpAty();
                break;
        }
    }

    private void openAboutUsAty() {
        startActivity(new Intent(this,AboutActivity.class));
        playOpenAnimation();
    }
    private void openFeedAty(){
        startActivity(new Intent(this,FeedActivity.class));
        playOpenAnimation();
    }
    private void openHelpAty(){
//        Intent intent=new Intent(this,WebPage.class);
//        startActivity(intent);
//        playOpenAnimation();
    }

    private class SettingAdapter extends GLVAdapter{

        public SettingAdapter(GroupListView lv) {
            super(lv);
        }

        @Override
        public int groupCount() {
            return items.length;
        }

        @Override
        public int dividerHeight() {
            return ScreenUtil.dp2px(SettingActivity.this,10);
        }

        @Override
        public int countInGroup(int group) {
            return items[group].length;
        }

        @Override
        public View getView(int group, int position) {
            return new GLVDefaultItemView2(SettingActivity.this)
                    .setIconVisible(View.GONE)
                    .setTitle(items[group][position])
                    .getContentView();
        }
    }
}
