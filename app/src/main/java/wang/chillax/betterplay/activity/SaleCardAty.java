package wang.chillax.betterplay.activity;

import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.cusview.ToolBar;

/**
 * Created by MAC on 16/4/6.
 * 我的-优惠券
 */
public class SaleCardAty extends BaseActivity implements ToolBar.ToolBarListener{

    @Bind(R.id.toolbar)
    ToolBar mToolbar;


    @Override
    protected void initDatas() {
        mToolbar.setToolBarListener(this);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_sale_card;
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
        titleCenter.setText("优惠券");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playExitAnimation();
    }
}
