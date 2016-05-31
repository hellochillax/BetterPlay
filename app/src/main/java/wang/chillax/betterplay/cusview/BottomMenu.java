package wang.chillax.betterplay.cusview;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wang.chillax.betterplay.R;

/**
 * Created by MAC on 15/12/1.
 */
public class BottomMenu extends LinearLayout {
    @Bind(R.id.page_1)
    LinearLayout page1;
    @Bind(R.id.page_2)
    LinearLayout page2;
    @Bind(R.id.page_3)
    LinearLayout page3;
    @Bind(R.id.find_image)
    ImageView findImage;
    @Bind(R.id.forum_image)
    ImageView forumImage;
    @Bind(R.id.self_image)
    ImageView selfImage;
    @Bind(R.id.find_text)
    TextView findText;
    @Bind(R.id.forum_text)
    TextView forumText;
    @Bind(R.id.self_text)
    TextView selfText;
    private OnBottomMenuSelectedListener listener;
    private Context context;
    private ActionBar actionBar;

    public BottomMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        addSubViews();
    }

    private void addSubViews() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_menu, null);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(view, 0, lp);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.page_1)
    public void to_page1(){
        if (listener != null) listener.onMenuSelected(0);
        actionBar.setTitle("发现");
        updateBottomMenu(0);
    }
    @OnClick(R.id.page_2)
    public void to_page2(){
        if (listener != null) listener.onMenuSelected(1);
        actionBar.setTitle("论坛");
        updateBottomMenu(1);
    }
    @OnClick(R.id.page_3)
    public void to_page3(){
        if (listener != null) listener.onMenuSelected(2);
        actionBar.setTitle("我的");
        updateBottomMenu(2);
    }
    public void setSelection(int index){
        switch (index){
            case 0:
                to_page1();
                break;
            case 1:
                to_page2();
                break;
            case 2:
                to_page3();
                break;
        }
    }
    private void updateBottomMenu(int index){
        findImage.setImageResource(index==0?R.mipmap.find_lighlighted:R.mipmap.find_normal);
        forumImage.setImageResource(index == 1 ? R.mipmap.forum_lighlighted : R.mipmap.forum_normal);
        selfImage.setImageResource(index == 2 ? R.mipmap.self_lighlighted : R.mipmap.self_normal);
        int color_normal=getResources().getColor(R.color.bottom_menu_text_color);
        int color_highlighted=getResources().getColor(R.color.bottom_menu_text_highlighted_color);
        findText.setTextColor(index==0? color_highlighted:color_normal);
        forumText.setTextColor(index==1? color_highlighted:color_normal);
        selfText.setTextColor(index==2? color_highlighted:color_normal);
    }

    public interface OnBottomMenuSelectedListener {
        void onMenuSelected(int index);
    }

    public void setOnSelectedListener(OnBottomMenuSelectedListener listener) {
        this.listener = listener;
    }

    public void setActionBar(ActionBar actionBar) {
        this.actionBar = actionBar;
    }
}
