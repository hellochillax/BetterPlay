package wang.chillax.betterplay.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.adapter.BaseAdapter;
import wang.chillax.betterplay.adapter.ViewHolder;
import wang.chillax.betterplay.bmob.User;
import wang.chillax.betterplay.cusview.ToolBar;

/**
 * Created by MAC on 15/12/1.
 */
public class UserInfo extends BaseActivity implements ToolBar.ToolBarListener{

    static final int TYPE_1 = 0;
    static final int TYPE_2 = 1;
    @Bind(R.id.toolbar)
    ToolBar mToolbar;
    @Bind(R.id.list)
    ListView mListView;
    MyAdapter mAdapter;
    String [] titles=new String[]{"昵称","性别","电话","学校","学号"};
    String [] details=new String[titles.length];
    ViewHolder [] holders = new ViewHolder[titles.length];
    boolean isMessageChanged = false;

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {
        initOthers();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        initSexItem();
                        break;
                    case 3:
                        initSchoolItem();
                }
            }
        });
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.activity_userinfo;
    }

    @Override
    protected void onExit() {

    }

    private void initOthers() {
        mToolbar.setToolBarListener(this);
        User user = BmobUser.getCurrentUser(this,User.class);
        if(user!=null){
            details[0]=user.getNickname();
            details[1]=user.getSex();
            details[2]=user.getPhone();
            details[3]=user.getSchool();
            details[4]=user.getStunum();
        }
        mAdapter=new MyAdapter();
        addFooterView();
        mListView.setAdapter(mAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                showToast(position+"");
//            }
//        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CODE_CHOOSE_SCHOOL){
            User user = BmobUser.getCurrentUser(this,User.class);
//            if(user!=null){
//                details[1]=user.getUsername();
//                details[2]=user.getNickname();
//                details[3]=user.getSchool();
//            }
            details[3]=user.getSchool();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_clam,R.anim.slide_out_right);
    }

    @Override
    public void onBackClicked() {
        onBackPressed();
    }

    @Override
    public void onMoreClicked() {

    }

    @Override
    public void onTitleLeftClicked() {

    }

    @Override
    public void onTitleRightClicked() {
        /**
         * 在此处实现
         * 1 将EditText里的文本转换到user里
         * 2 上传更新*/
        User user = BmobUser.getCurrentUser(this,User.class);
        if(user!=null){
            user.setNickname(((EditText)holders[0].getConvertView().findViewById(R.id.detail)).getText().toString());
            user.setSex(((TextView)holders[1].getConvertView().findViewById(R.id.detail)).getText().toString());
            user.setPhone(((EditText)holders[2].getConvertView().findViewById(R.id.detail)).getText().toString());
            user.setSchool(((TextView)holders[3].getConvertView().findViewById(R.id.detail)).getText().toString());
            user.setStunum(((EditText)holders[4].getConvertView().findViewById(R.id.detail)).getText().toString());
        }
        user.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                showToast("修改成功");
            }

            @Override
            public void onFailure(int i, String s) {
                showToast("修改失败，请稍后重试");
            }
        });
    }

    @Override
    public void onInit(ImageView back, TextView titleLeft, TextView titleCenter, TextView titleRight, ImageView more) {
        titleCenter.setText("个人中心");
        titleRight.setText("保存");
    }

    public void initSexItem(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserInfo.this)
                .setMessage("请选择性别")
                .setPositiveButton("男", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        holders[1].setText(R.id.detail,"男");
                    }
                })
                .setNegativeButton("女", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        holders[1].setText(R.id.detail,"女");
                    }
                });
        mBuilder.create().show();
    }
    public void initSchoolItem(){
        startActivityForResult(new Intent(this,ChooseSchoolActivity.class),CODE_CHOOSE_SCHOOL);
        playOpenAnimation();
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if(position==1||position==3)
                return 1;
            return 0;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(position==1||position==3){
                holder=ViewHolder.get(UserInfo.this,convertView,R.layout.userinfo_list_item2,position,parent);
            } else {
                holder=ViewHolder.get(UserInfo.this,convertView,R.layout.userinfo_list_item,position,parent);
            }
            holders[position] = holder;
            holder.setText(R.id.title,titles[position]).setText(R.id.detail, TextUtils.isEmpty(details[position])?"点击设置":details[position]);
            return holder.getConvertView();
        }

//        @Override
//        public void notifyDataSetChanged() {
//            details[0]
//            super.notifyDataSetChanged();
//        }
    }

    void addFooterView(){
        View footerView=LayoutInflater.from(this).inflate(R.layout.userinfo_list_footer,null);
        mListView.addFooterView(footerView);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }
    private void logout() {
        BmobUser.logOut(this);
        showToast("注销成功");
        onBackPressed();
    }
    public static int CODE_CHOOSE_SCHOOL=0x11;
}
