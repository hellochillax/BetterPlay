package wang.chillax.betterplay.cusview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by MAC on 16/3/17.
 * 由于主界面的每个item需要控制成正方形,所以自定义一下.
 */
public class RectImageView extends ImageView {

    private Context mContext;

    public RectImageView(Context context) {
        this(context,null);
    }

    public RectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        initView();
    }

    private void initView() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
