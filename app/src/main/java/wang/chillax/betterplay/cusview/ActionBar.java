package wang.chillax.betterplay.cusview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.utils.ScreenUtil;

/**
 * Created by MAC on 15/12/1.
 */
public class ActionBar extends RelativeLayout {

    @Bind(R.id.logo)
    ImageView logoView;
    @Bind(R.id.title)
    TextView titleView;

    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        int margin = ScreenUtil.dp2px(context, 10);
//        lp.setMargins(margin, margin, margin, margin);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_actionbar, null);
        addView(view, 0, lp);
        ButterKnife.bind(this);
    }

    public void setLogoVisible(int v) {
        logoView.setVisibility(v);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }
}
