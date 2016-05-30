package wang.chillax.betterplay.cusview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.util.List;

import wang.chillax.betterplay.R;
import wang.chillax.betterplay.utils.ImageLoader;

public class RollViewPager extends ViewPager{

    private Context context;
    private int dot_normal_res = 0;
    private int dot_focus_res = 0;
    private int dot_radius = 10;

    private RollAdapter mAdapter;
    private int[] mImageRes;
    private List<String> mImageUrls;

    private int ITEM_DURATION = 2000;
    private int ITEM_INTERVAL = 2000;
    private final int MAX_VALUE=Integer.MAX_VALUE;
    private int currItem;//current item of viewpager

    private boolean mScroll = false;
    private float mLastMotionX;
    private float mLastMotionY;

    /**
     * update the item of viewpager every  ITEM_INTERVAL
     */
    private Handler mHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            setCurrentItem(++currItem);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(),ITEM_INTERVAL);
        }
    };

    /**
     * construct method for xml usage
     */
    public RollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initSomeAttrs(attrs);
        initOthers();
    }

    private void initOthers() {
        mAdapter=new RollAdapter();
        setAdapter(mAdapter);
        addOnPageChangeListener(new RollChangeListener());
        new RollScroll(context,new AccelerateDecelerateInterpolator()).install();
    }

    /**
     * init the params from xml tag,eg:android:layout_width=wrap_content
     */
    private void initSomeAttrs(AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RollViewPager);
        ITEM_DURATION = (int) ta.getDimension(R.styleable.RollViewPager_item_duration, ITEM_DURATION);
        ITEM_INTERVAL = (int) ta.getDimension(R.styleable.RollViewPager_item_interval, ITEM_INTERVAL);
        dot_focus_res = ta.getResourceId(R.styleable.RollViewPager_dot_focus_res, dot_focus_res);
        dot_normal_res = ta.getResourceId(R.styleable.RollViewPager_dot_normal_res, dot_normal_res);
        dot_radius = ta.getDimensionPixelSize(R.styleable.RollViewPager_dot_radius, dot_radius);
    }

    public void setImageRes(int[] res){
        mImageRes=res;
    }

    public void setImageUrls(List<String> urls){
        mImageUrls=urls;
        mAdapter.notifyDataSetChanged();
    }

    /**
     * start to roll
     */
    public void startRoll(){
        int len=0;
        if(mImageUrls!=null&&mImageUrls.size()>0){
            len=mImageUrls.size();
        }else if(mImageRes!=null&&mImageRes.length>0){
            len=mImageRes.length;
        }
        if(len<=0){
            currItem=0;
        }else{
            currItem=MAX_VALUE/2/len*len;
            try {
                Field field=ViewPager.class.getDeclaredField("mCurItem");
                field.setAccessible(true);
                field.setInt(this,currItem);
                field.setAccessible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(),ITEM_INTERVAL);
    }

    /**
     * stop to roll
     */
    public void stopRoll(){
        mHandler.removeCallbacksAndMessages(null);
    }

    public void onStart(){
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(),ITEM_INTERVAL);
    }
    public void onStop(){
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                requestDisallowInterceptTouchEvent(true);
                mScroll = true;
                mLastMotionX = x;
                mLastMotionY = y;
                onStop();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mScroll == true) {
                    if (Math.abs(x - mLastMotionX) < Math.abs(y - mLastMotionY)) {
                        mScroll = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                requestDisallowInterceptTouchEvent(false);
                onStart();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    /**
     * the adapter of RollViewPager
     */
    private class RollAdapter extends PagerAdapter {


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if(mImageUrls!=null&&mImageUrls.size()>0){
                return MAX_VALUE;
            }
            if(mImageRes!=null&&mImageRes.length>0){
                return MAX_VALUE;
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView itemView=createItemView();
            if(mImageUrls!=null&&mImageUrls.size()>0){
                ImageLoader.getInstance().displayImage(mImageUrls.get(calRealPos(position)),itemView);
            }else if(mImageRes!=null&&mImageRes.length>0){
                itemView.setImageResource(mImageRes[calRealPos(position)]);
            }
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

    /**
     * create a item of viewpager
     * @return
     */
    private ImageView createItemView(){
        ImageView item=new ImageView(context);
        item.setScaleType(ImageView.ScaleType.FIT_XY);
        LayoutParams lp=new LayoutParams();
        item.setLayoutParams(lp);
        return item;
    }

    /**
     * calculate and return the real position of the res array
     * @param pos
     * @return
     */
    private int calRealPos(int pos){
        if(mImageUrls!=null&&mImageUrls.size()>0){
            return pos%mImageUrls.size();
        }
        if(mImageRes!=null&&mImageRes.length>0){
            return pos%mImageRes.length;
        }
        return 0;
    }
    private class RollChangeListener implements OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currItem=position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
    private class RollScroll extends Scroller{

        public RollScroll(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy,ITEM_DURATION);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, ITEM_DURATION);
        }

        public void install(){
            try {
                Field field=ViewPager.class.getDeclaredField("mScroller");
                field.setAccessible(true);
                field.set(RollViewPager.this,this);
                field.setAccessible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}