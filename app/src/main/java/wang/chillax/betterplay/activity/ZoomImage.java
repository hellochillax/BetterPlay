package wang.chillax.betterplay.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Interpolator;
import android.graphics.Path;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import butterknife.Bind;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.config.Paths;
import wang.chillax.betterplay.cusview.ClipView;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.utils.CommUtils;
import wang.chillax.betterplay.utils.LogUtils;

/**
 * Created by Xiao on 16/4/30.
 */
public class ZoomImage extends BaseActivity implements ToolBar.ToolBarListener{

    @Bind(R.id.zoom_image)
    ClipView mZI;//zoom_image

    Bitmap bitmap;

    @Bind(R.id.toolbar)
    ToolBar mToolbar;

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {
        mToolbar.setToolBarListener(this);
        String path = getIntent().getStringExtra("path");
        LogUtils.d("ZoomImage:....." + path);
        bitmap = BitmapFactory.decodeFile(path);
        mZI.setImageBitmap(bitmap);
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_zoom_image;
    }

    @Override
    protected void onExit() {

    }

    @Override
    public void onBackClicked() {

    }

    @Override
    public void onMoreClicked() {

    }

    @Override
    public void onTitleLeftClicked() {
        onBackPressed();
    }

    @Override
    public void onTitleRightClicked() {
        Bitmap bitmap=mZI.clip();
        try {
            CommUtils.writeBitmap2File(bitmap, Paths.iconLocal2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setResult(RESULT_OK);
        onBackPressed();
    }

    @Override
    public void onInit(ImageView back, TextView titleLeft, TextView titleCenter, TextView titleRight, ImageView more) {
        titleLeft.setText("取消");
        titleCenter.setText("剪切");
        titleRight.setText("确认");
        back.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_clam,R.anim.slide_out_bottom);
    }
}
