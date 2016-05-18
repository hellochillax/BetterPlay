package wang.chillax.betterplay.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import wang.chillax.betterplay.R;

/**
 * Created by MAC on 15/12/1.
 */
public abstract class BasePage extends Fragment{
    protected Context context;
    protected View contentView;


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.context=context;
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context=activity;
    }

    public void showToast(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView=inflater.inflate(initLayoutRes(),null,false);
        ButterKnife.bind(this,contentView);
        initViews();
        initDatas();
        return contentView;
    }
    /**
     * 在此函数中初始化数据
     */
    protected abstract void initDatas();

    /**
     * 在此函数中初始化视图
     */
    protected abstract void initViews();

    /**
     * 在此函数中给出本Activity所用的布局
     */
    protected abstract int initLayoutRes();
    /**
     * 实现经典的Activity打开动画
     */
    protected void playOpenAnimation(){
        ((Activity)context).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_clam);
    }
    /**
     * 实现经典的Activity退出动画
     */
    protected void playExitAnimation(){
        ((Activity)context).overridePendingTransition(R.anim.slide_clam,R.anim.slide_out_right);
    }
}
