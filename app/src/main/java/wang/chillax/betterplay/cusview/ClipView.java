package wang.chillax.betterplay.cusview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import wang.chillax.betterplay.R;
import wang.chillax.betterplay.utils.ScreenUtil;

/**
 * Created by Xiao on 16/5/1.
 *
 * 图片剪切的view
 */
public class ClipView extends ImageView {

    Matrix matrix = new Matrix();
//    Matrix savedMatrix = new Matrix();
    float minScaleR;// 最小缩放比例
    static final float MAX_SCALE = 4f;// 最大缩放比例

    static final int NONE = 0;// 初始状态
    static final int DRAG = 1;// 拖动
    static final int ZOOM = 2;// 缩放
    int mode = NONE;

    PointF prev = new PointF();
    PointF mid = new PointF();
    float dist = 1f;

    protected Context context;
    protected int SCREEN_WIDTH;
    protected int SCREEN_HEIGHT;
    /**
     * 配置选项
     */
    protected class Options{
        public int stroke_color,DEFAULT_STROKE_COLOR;
        public int clip_size,DEFAULT_CLIP_SIZE;
        public int stroke_width,DEFAULT__STROKE_WIDTH;
        public Options(){
            DEFAULT_STROKE_COLOR= Color.GREEN;
            DEFAULT_CLIP_SIZE=ScreenUtil.getScreenWidth(context)/2;
            DEFAULT__STROKE_WIDTH=ScreenUtil.dp2px(context,2);
        }
    }

    Options options;

    /**
     * 用于布局中的构造函数
     */
    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        SCREEN_WIDTH=ScreenUtil.getScreenWidth(context);
        SCREEN_HEIGHT=ScreenUtil.getScreenHeight(context);
        options=new Options();
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.ClipView);
        options.clip_size=ta.getDimensionPixelSize(R.styleable.ClipView_clip_size,options.DEFAULT_CLIP_SIZE);
        options.stroke_width=ta.getDimensionPixelSize(R.styleable.ClipView_stroke_width,options.DEFAULT__STROKE_WIDTH);
        options.stroke_color=ta.getColor(R.styleable.ClipView_stroke_color,options.DEFAULT_STROKE_COLOR);
        ta.recycle();
        init();
    }

    /**
     * 初始化函数
     */
    private void init() {
        setScaleType(ScaleType.MATRIX);
    }

    /**
     * 重写onDraw函数,添加选取框
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(options.stroke_color);
        paint.setStrokeWidth(options.stroke_width);
        RectF rect=new RectF((SCREEN_WIDTH-options.clip_size)/2,
                (SCREEN_HEIGHT-options.clip_size)/2,
                (SCREEN_WIDTH+options.clip_size)/2,
                (SCREEN_HEIGHT+options.clip_size)/2);
        canvas.drawRect(rect,paint);
    }

    /**
     * 监听触控事件,处理图片伸缩效果
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                mode=DRAG;
                prev.set(event.getX(),event.getY());
//                savedMatrix.set(matrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode=ZOOM;
//                savedMatrix.set(matrix);
                mid.set((event.getX(0) + event.getX(1))/2,(event.getY(0) + event.getY(1))/2);
                dist = (float) spacing(event);
                break;
            case MotionEvent.ACTION_MOVE:
//                matrix.set(savedMatrix);
                if(mode==DRAG){
                    matrix.postTranslate(event.getX() - prev.x, event.getY()
                            - prev.y);
                }else if(mode==ZOOM){
                    double newDist = spacing(event);
                    if (newDist > 10f) {
//                        matrix.set(savedMatrix);
                        float tScale = (float) (newDist / dist);
                        dist = (float) spacing(event);
                        matrix.postScale(tScale, tScale, mid.x, mid.y);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode=NONE;
                break;
        }
        prev.set(event.getX(),event.getY());
        setImageMatrix(matrix);
        return true;
    }
    /**
     * 两点的距离
     */
    private double spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Math.sqrt(x * x + y * y);
    }

    /**
     * 剪切图片
     */
    public Bitmap clip(){
        setDrawingCacheEnabled(true);
        Bitmap bitmap=Bitmap.createBitmap(getDrawingCache(),
                (SCREEN_WIDTH-options.clip_size)/2+options.stroke_width,
                (SCREEN_HEIGHT-options.clip_size)/2+options.stroke_width,
                options.clip_size-2*options.stroke_width,options.clip_size-2*options.stroke_width);
        setDrawingCacheEnabled(false);
        return bitmap;
    }
}
