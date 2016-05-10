package wang.chillax.betterplay.cusview;

import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by MAC on 15/12/22.
 */
public abstract class GLVAdapter{

    WeakReference<GroupListView> mListView;

    public GLVAdapter(GroupListView lv){mListView=new WeakReference<>(lv);}

    public abstract int groupCount();
    public abstract int dividerHeight();
    public abstract int countInGroup(int group);

    public abstract View getView(int group, int position);
    public void notifyDataSetChanged() {
        mListView.get().getInnerAdapter().notifyDataSetChanged();
    }
}
