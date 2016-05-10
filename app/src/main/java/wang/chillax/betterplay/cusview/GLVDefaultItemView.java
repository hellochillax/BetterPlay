package wang.chillax.betterplay.cusview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import wang.chillax.betterplay.R;

/**
 * Created by MAC on 15/12/22.
 */
public class GLVDefaultItemView {
    View view;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.detail)
    TextView mDetail;

    public GLVDefaultItemView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.glv_item_default_style, null);
        ButterKnife.bind(this,view);
    }

    public GLVDefaultItemView setTitle(String text) {
        mTitle.setText(text);
        return this;
    }

    public GLVDefaultItemView setDetail(String text) {
        mDetail.setText(text);
        return this;
    }

    public GLVDefaultItemView setDetailColor(int color) {
        try {
            mDetail.setTextColor(color);
        }catch (Exception e){

        }
        return this;
    }

    public View getContentView() {
        return view;
    }

}
