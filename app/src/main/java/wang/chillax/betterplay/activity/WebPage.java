package wang.chillax.betterplay.activity;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.bmob.TopImage;
import wang.chillax.betterplay.cusview.ToolBar;

/**
 * Created by MAC on 15/12/8.
 * 活动详情界面
 */
public class WebPage extends BaseActivity implements ToolBar.ToolBarListener{


    public static final String URL="url";
    public static final String TITLE="title";

    @Bind(R.id.webview) WebView mWebView;
    @Bind(R.id.pb) ProgressBar mPb;
    @Bind(R.id.toolbar) ToolBar mToolBar;


    String url;
    String title;


    @Override
    protected void initDatas() {
        loadWebView();
    }

    private void loadWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mPb.setProgress(newProgress);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mPb.setVisibility(View.GONE);
            }
        });
        mWebView.loadUrl(url);
    }

    @Override
    protected void initViews() {
        url=getIntent().getStringExtra(URL);
        title=getIntent().getStringExtra(TITLE);
        mToolBar.setToolBarListener(this);
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.activity_webpage;
    }

    @Override
    protected void onExit() {

    }

    @Override
    public void onBackPressed() {
        mWebView.destroy();
        super.onBackPressed();
        playExitAnimation();
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
        titleCenter.setText(title==null?"":title);
    }

}