package wang.chillax.betterplay.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.adapter.BaseAdapter;
import wang.chillax.betterplay.adapter.ViewHolder;
import wang.chillax.betterplay.cusview.GLVDefaultItemView;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.utils.CommUtils;
import wang.chillax.betterplay.utils.LogUtils;

/**
 * Created by qingliu on 12/6/15.
 */
public class AboutActivity extends BaseActivity implements ToolBar.ToolBarListener{
    @Bind(R.id.about_list)
    ListView mAboutList;
    @Bind(R.id.about_toolbar)
    ToolBar mToolbar;
    @Bind(R.id.version)
    TextView mVersion;

    @Override
    protected void initDatas() {
        mVersion.setText("V"+getVersionName());
    }

    @Override
    protected void initViews() {
        mToolbar.setToolBarListener(this);
        aboutList();
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.activity_about;
    }

    @Override
    protected void onExit() {

    }

    private void aboutList() {
        mAboutList.setAdapter(new MyAdapter());
        mAboutList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        // TODO: 12/6/15 接入QQ
                        openQQ();
                        break;
                    case 1:
                        // TODO: 12/6/15 接入微信
                        openWeChat();
                        break;
                    case 2:
                        // TODO: 12/6/15 调用电话
                        openPhone();
                        break;
                }
            }
        });
    }

    private void openPhone() {
        Uri uri = Uri.parse("tel:"+aboutText[2][1]);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);
    }

    private void openWeChat() {

    }

    private void openQQ() {
        String url="mqqwpa://im/chat?chat_type=wpa&uin="+aboutText[0][1];
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public void startActivity(Intent intent) {
        if (intent.getData().toString().contains(aboutText[0][1])&&!CommUtils.isQQClientAvailable(this))return;
        super.startActivity(intent);
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

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return aboutText.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return new GLVDefaultItemView(AboutActivity.this)
                    .setTitle(aboutText[position][0])
                    .setDetail(aboutText[position][1])
                    .setDetailColor(Color.GRAY)
                    .getContentView();
        }
    }

    String[][] aboutText = {
            {"QQ群","488737782"}, {"微信公众号","Better-Play"}, {"合作洽谈","18840833200"}
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playExitAnimation();
    }
    private String getVersionName()
    {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }
}
