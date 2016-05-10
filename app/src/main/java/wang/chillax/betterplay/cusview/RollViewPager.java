package wang.chillax.betterplay.cusview;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import wang.chillax.betterplay.R;
import wang.chillax.betterplay.utils.LogUtils;
import wang.chillax.betterplay.utils.ScreenUtil;

public class RollViewPager extends ViewPager {
    private String TAG = "RollViewPager";
    private int MAX_PAGE_NUM = Integer.MAX_VALUE;//ViewPager最大页数，用于设置伪无限循环效果
    private int PAGE_CHANGE_DURATION = 1000;//ViewPager更换页面的间隔
    private int PAGE_CHANGE_SPEED = 2000;//ViewPager切换页面的速度
    private Context context;
    private int currentItem;
    private ArrayList<String> uriList;
    private TextView title;
    private ArrayList<String> titles;
    private int[] resImageIds;
    private OnPagerClickCallback onPagerClickCallback;
    private boolean isShowResImage = true;
//    MyOnTouchListener myOnTouchListener;
    ViewPagerTask viewPagerTask;
    private PagerAdapter adapter;

//    /**
//     * 触摸时按下的点 *
//     */
//    PointF downP = new PointF();
//    /**
//     * 触摸时当前的点 *
//     */
//    PointF curP = new PointF();
    private int abc = 1;
    private float mLastMotionX;
    private float mLastMotionY;


//    private long start = 0;

    int dot_normal,dot_focus;
    ViewGroup layout;
    List<View> dotList;

    public void setDots(ViewGroup layout, int dot_normal, int dot_focus) {
        this.layout=layout;
        this.dot_normal=dot_normal;
        this.dot_focus=dot_focus;
    }


//    public class MyOnTouchListener implements OnTouchListener {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            curP.x = event.getX();
//            curP.y = event.getY();
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//
//                    start = System.currentTimeMillis();
//                    // 记录按下时候的坐标
//                    // 切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
//                    downP.x = event.getX();
//                    downP.y = event.getY();
//                    // 此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
//                    // getParent().requestDisallowInterceptTouchEvent(true);
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    Log.i("d", (curP.x - downP.x) + "----" + (curP.y - downP.y));
//                    // if (Math.abs(curP.x - downP.x) > Math.abs(curP.y - downP.y)
//                    // && (getCurrentItem() == 0 || getCurrentItem() == getAdapter()
//                    // .getCount() - 1)) {
//                    // getParent().requestDisallowInterceptTouchEvent(false);
//                    // } else {
//                    // getParent().requestDisallowInterceptTouchEvent(false);
//                    // }
//                    // 此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
//                    break;
//                case MotionEvent.ACTION_CANCEL:
//                    // getParent().requestDisallowInterceptTouchEvent(false);
//                    break;
//                case MotionEvent.ACTION_UP:
//                    downP.x = event.getX();
//                    downP.y = event.getY();
//                    long duration = System.currentTimeMillis() - start;
//                    break;
//            }
//            return true;
//        }
//    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                abc = 1;
                mLastMotionX = x;
                mLastMotionY = y;
                handler.removeCallbacksAndMessages(null);
                break;
            case MotionEvent.ACTION_MOVE:

                handler.removeCallbacksAndMessages(null);
                if (abc == 1) {
                    if (Math.abs(x - mLastMotionX) < Math.abs(y - mLastMotionY)) {
                        abc = 0;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                handler.postDelayed(viewPagerTask, PAGE_CHANGE_DURATION);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

//    public void setChangeDuration(int duration) {
//        if (duration > 0) {
//            PAGE_CHANGE_DURATION = duration;
//        }
//    }
//
//    public void setChangeSpeed(int speed, Interpolator interpolator) {
//
//        if (speed > 0) {
//            PAGE_CHANGE_SPEED=speed;
//            new ViewPagerScroller(context, interpolator).initViewPagerScroll(this);
//        }
//    }

    public class ViewPagerTask implements Runnable {
        @Override
        public void run() {
            currentItem = currentItem + 1;
            handler.obtainMessage().sendToTarget();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            RollViewPager.this.setCurrentItem(currentItem);
            postDelayed(viewPagerTask, PAGE_CHANGE_DURATION);
        }
    };

    public RollViewPager(Context context,
                         OnPagerClickCallback onPagerClickCallback) {
        super(context);
        this.context = context;
        this.onPagerClickCallback = onPagerClickCallback;
        viewPagerTask = new ViewPagerTask();
//        myOnTouchListener = new MyOnTouchListener();
        new ViewPagerScroller(context,new AccelerateDecelerateInterpolator()).setScrollDuration(PAGE_CHANGE_SPEED).initViewPagerScroll(this);
    }

    public void setUriList(ArrayList<String> uriList) {
        isShowResImage = false;
        this.uriList = uriList;

    }

    public void notifyDataChange() {
        if(uriList!=null&&uriList.size()>0&&layout!=null&&dot_normal!=0&&dot_focus!=0){
            layout.removeAllViews();
            if(dotList!=null)dotList.clear();else dotList=new ArrayList<>();
            final int size=uriList.size();
            final int radius=ScreenUtil.dp2px(context,10);
            final LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(radius,radius);
            lp.setMargins(radius/3,radius/3,radius/3,radius/3);
            for (int i=0;i<size;i++){
                View view=new View(context);
                view.setLayoutParams(lp);
                view.setBackgroundResource(dot_normal);
                layout.addView(view);
                dotList.add(view);
            }
        }
        adapter.notifyDataSetChanged();
    }


    public void setResImageIds(int[] resImageIds) {
        isShowResImage = true;
        this.resImageIds = resImageIds;
    }

    public void setTitle(TextView title, ArrayList<String> titles) {
        this.title = title;
        this.titles = titles;
        if (title != null && titles != null && titles.size() > 0)
            title.setText(titles.get(0));
    }

    private boolean hasSetAdapter = false;


    public void startRoll() {
        if (!hasSetAdapter) {
            hasSetAdapter = true;
//            addOnPageChangeListener(new MyOnPageChangeListener());
            adapter = new ViewPagerAdapter();
            this.setAdapter(adapter);
            if (isShowResImage) {
                currentItem = MAX_PAGE_NUM / 2 / resImageIds.length * resImageIds.length;
            } else {
                currentItem = MAX_PAGE_NUM / 2 / uriList.size() * uriList.size();
            }
        }
        //用反射直接改变CurrentItem为中间值，防止卡顿
        try {
            Field field = ViewPager.class.getDeclaredField("mCurItem");
            field.setAccessible(true);
            field.setInt(this, currentItem);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.postDelayed(viewPagerTask, PAGE_CHANGE_DURATION);
    }

    public void onStart() {
        stopRoll();
        handler.postDelayed(viewPagerTask, PAGE_CHANGE_DURATION);
    }
    public void onStop(){
        stopRoll();
    }

    public void stopRoll() {
        handler.removeCallbacksAndMessages(null);
    }

    class MyOnPageChangeListener implements OnPageChangeListener {
        int oldPosition = 0;

        @Override
        public void onPageSelected(int position) {
            currentItem = position;
            if (title != null)
                title.setText(titles.get(position % titles.size()));
            oldPosition = position;
            if(dotList!=null&&dotList.size()>0){
                for (View view:dotList)view.setBackgroundResource(dot_normal);
                dotList.get(position % titles.size()).setBackgroundResource(dot_focus);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return MAX_PAGE_NUM;
        }

        @Override
        public Object instantiateItem(View container, final int position) {
            View view = View.inflate(context, R.layout.viewpager_item, null);
            ((ViewPager) container).addView(view);
//            view.setOnTouchListener(myOnTouchListener);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPagerClickCallback.onPagerClick(position%uriList.size());
                }
            });
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            if (isShowResImage) {
                imageView.setImageResource(resImageIds[position % resImageIds.length]);
            } else {
                ImageLoader.getInstance().displayImage(uriList.get(position % uriList.size()),
                        imageView);
            }
            return view;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    public interface OnPagerClickCallback {
        void onPagerClick(int position);
    }
    /**
     * ViewPager 滚动速度设置
     */
    public class ViewPagerScroller extends Scroller {
        private int mScrollDuration = 2000;             // 滑动速度

        /**
         * 设置速度速度
         *
         * @param duration
         */
        public ViewPagerScroller setScrollDuration(int duration) {
            this.mScrollDuration = duration;
            return this;
        }

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        public void initViewPagerScroll(ViewPager viewPager) {
            try {
                Field mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                mScroller.set(viewPager, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}