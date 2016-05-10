package wang.chillax.betterplay.cusview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import wang.chillax.betterplay.R;

/**
 * Created by MAC on 15/12/22.
 */
public class GLVDefaultItemView2 {

    View view;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.detail)
    TextView mDetail;
    @Bind(R.id.icon)
    ImageView mIcon;
    @Bind(R.id.arrow)
    ImageView mArrow;

    public GLVDefaultItemView2(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.glv_item_default_2_style, null);
        ButterKnife.bind(this, view);
    }

    public GLVDefaultItemView2 setTitle(String text) {
        mTitle.setText(text);
        return this;
    }

    public GLVDefaultItemView2 setDetail(String text) {
        mDetail.setText(text);
        return this;
    }

    public GLVDefaultItemView2 setIcon(int resId) {
        mIcon.setImageResource(resId);
        return this;
    }

    public GLVDefaultItemView2 setDetailColor(int color) {
        try {
            mDetail.setTextColor(color);
        } catch (Exception e) {

        }
        return this;
    }

    public GLVDefaultItemView2 setIconVisible(int v){
        mIcon.setVisibility(v);
        return this;
    }

    public View getContentView() {
        return view;
    }

}
