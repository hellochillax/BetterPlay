package wang.chillax.betterplay.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by MAC on 15/12/1.
 */
public class ImageLoader {
    private static ImageLoader loader=new ImageLoader();
    private ImageLoader(){

    }
    public static ImageLoader getInstance(){
        return loader;
    }
    public void loadImage(String url, final ImageLoadListener listener){
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                listener.onLoadingStarted(s,view);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                listener.onLoadingFailed(s,view,failReason);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                listener.onLoadingComplete(s,view,bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                listener.onLoadingCancelled(s,view);
            }
        });
    }
    public void displayImage(String url, ImageView iv){
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(url,iv);
    }
    public interface ImageLoadListener{
        void onLoadingStarted(String s, View view);
        void onLoadingFailed(String s, View view, FailReason failReason);
        void onLoadingComplete(String s, View view, Bitmap bitmap);
        void onLoadingCancelled(String s, View view);
    }
}
