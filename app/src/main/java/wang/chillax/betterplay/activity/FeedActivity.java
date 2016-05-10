package wang.chillax.betterplay.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.cusview.ToolBar;

/**
 * Created by qingliu on 12/6/15.
 */
public class FeedActivity extends BaseActivity implements ToolBar.ToolBarListener{

    @Bind(R.id.feed_toolbar)
    ToolBar mFeedToolBar;

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {
        mFeedToolBar.setToolBarListener(this);
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.activity_feed;
    }

    @Override
    protected void onExit() {

    }

    @Override
    public void onBackClicked() {
        playExitAnimation();
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
        titleCenter.setText("意见反馈");
    }
}
