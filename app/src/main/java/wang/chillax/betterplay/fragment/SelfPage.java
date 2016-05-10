package wang.chillax.betterplay.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.activity.HeartAty;
import wang.chillax.betterplay.activity.LoginActivity;
import wang.chillax.betterplay.activity.MsgCenterAty;
import wang.chillax.betterplay.activity.PayOrders;
import wang.chillax.betterplay.activity.RecoSaleAty;
import wang.chillax.betterplay.activity.SaleCardAty;
import wang.chillax.betterplay.activity.SettingActivity;
import wang.chillax.betterplay.activity.UserInfo;
import wang.chillax.betterplay.activity.ZoomImage;
import wang.chillax.betterplay.bmob.User;
import wang.chillax.betterplay.config.Paths;
import wang.chillax.betterplay.cusview.GLVAdapter;
import wang.chillax.betterplay.cusview.GLVDefaultItemView2;
import wang.chillax.betterplay.cusview.GroupListView;
import wang.chillax.betterplay.utils.CommUtils;
import wang.chillax.betterplay.utils.ImageLoader;
import wang.chillax.betterplay.utils.ScreenUtil;
import wang.chillax.betterplay.utils.UserUtil;

/**
 * Created by MAC on 15/12/1.
 */
public class SelfPage extends BasePage implements GroupListView.OnGLVItemClickedListener {
    @Bind(R.id.glv)
    GroupListView mListView;

    View headerView;
    ImageView imageView;
    LinearLayout info;
    TextView nameView;
    TextView levelView;

    Dialog mDialog;//图片选择

    String[][] items = new String[][]{
            {"我的订单", "心愿单", "消息中心"}, {"优惠券", "推荐优惠"}, {"设置"}
    };
    int[][] icons = new int[][]{
            {R.mipmap.self_my_order, R.mipmap.self_my_heart, R.mipmap.self_msg_center},
            {R.mipmap.self_sale_card, R.mipmap.self_reco_sale},
            {R.mipmap.self_setting}
    };

    @Override
    public int initLayoutRes() {
        return R.layout.page_self;
    }

    @Override
    public void initViews() {
        addHeaderView(LayoutInflater.from(context));
        mListView.setGLVItemClickedListener(this);
    }

    @Override
    public void initDatas() {
        initOthers();
    }

    private void initOthers() {
        refreshUserInfoByBmob();
    }

    private void addHeaderView(LayoutInflater inflater) {
        headerView = inflater.inflate(R.layout.self_list_header, null);
        mListView.addHeaderView(headerView);
        mListView.setGLVAdapter(new MyAdapter(mListView));
        info = (LinearLayout) headerView.findViewById(R.id.user_info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtil.getCurrentUser(context) != null) {
                    openUserInfoActivity();
                } else {
                    openLoginActivity();
                }
            }
        });
        imageView = (ImageView) headerView.findViewById(R.id.user_image);
        nameView = (TextView) headerView.findViewById(R.id.user_name);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtil.getCurrentUser(context) != null) {
                    getPhotoDialog().show();
                } else {
                    openLoginActivity();
                }
            }
        });
        levelView = (TextView) headerView.findViewById(R.id.user_level);
    }

    public Dialog getPhotoDialog() {
        if (mDialog == null) {
            mDialog = create(context, new onItemChoosedListener() {
                @Override
                public void onItemChoosed(int position) {
                    switch (position) {
                        case 0:
                            Intent intent = new Intent();
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            //根据版本号不同使用不同的Action
                            if (Build.VERSION.SDK_INT < 19) {
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                            } else {
                                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                            }
                            startActivityForResult(intent, IMAGE_REQUEST_CODE);
                            break;
                        case 1:
                            Intent intentFromCapture = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            File file = createImageFile();
                            outputImageUri = Uri.fromFile(file);
                            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, outputImageUri);
                            startActivityForResult(intentFromCapture,
                                    CAMERA_REQUEST_CODE);
                            break;

                    }
                    mDialog.dismiss();
                }
            }, new String[]{"图库", "拍照", "取消"});
        }
        return mDialog;
    }

    private Uri outputImageUri;

    /**
     * 更新用户昵称和级别
     */
    protected void refreshUserInfoByBmob() {
        User user = UserUtil.getCurrentUser(context);
        if (user != null) {
            nameView.setText("昵称:" + (user.getNickname() == null ? user.getUsername() : user.getNickname()));
            int level = user.getLevel();
            if (level < 100) {
                levelView.setText("级别: " + user.getLevel() + "(小卒)");
            } else if (level < 200) {
                levelView.setText("等级: " + user.getLevel() + "(代理)");
            } else if (level == 888) {
                levelView.setText("等级: " + user.getLevel() + "(管理员)");
            }
            ImageLoader.getInstance().loadImage(user.getHead() == null ? "" : user.getHead().getFileUrl(context), new ImageLoader.ImageLoadListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    imageView.setImageResource(R.mipmap.default_user_head);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap == null) {
                        imageView.setImageResource(R.mipmap.default_user_head);
                    } else {
                        imageView.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                    imageView.setImageResource(R.mipmap.default_user_head);
                }
            });
        } else {
            nameView.setText("点我登录");
            levelView.setText("等级: 请先登录");
            imageView.setImageResource(R.mipmap.logo);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //结果码不等于取消时候
//        if (resultCode != 0) {
            switch (requestCode) {
                case SELF_PAGE_REQ_CODE:
                    //登陆或者注册成功
                    refreshUserInfoByBmob();
                    break;
                case SELF_PAGE_REQ_CODE_INFO:
                    //用户信息修改
                    refreshUserInfoByBmob();
                    break;
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(CommUtils.getPath(context, data.getData()));
                    break;
                case CAMERA_REQUEST_CODE:
                    startPhotoZoom(outputImageUri.getPath());
                    break;
                case RESULT_REQUEST_CODE:
                    handleIconTask();
                    break;
            }
//        }
    }

    /**
     * 截取成功之后,显示并上传
     */
    private void handleIconTask() {
        final BmobFile bmobFile = new BmobFile(new File(Paths.iconLocal2));
        bmobFile.uploadblock(context, new UploadFileListener() {

            @Override
            public void onSuccess() {
                //bmobFile.getFileUrl(context)--返回的上传文件的完整地址
                User newUser = new User();
                newUser.setHead(bmobFile);
                BmobUser bmobUser = BmobUser.getCurrentUser(context);
                newUser.update(context, bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        showToast("更新成功");
                        refreshUserInfoByBmob();
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                    }
                });
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }

            @Override
            public void onFailure(int code, String msg) {
                showToast("更新失败");
            }
        });
    }

    public File createImageFile() {
        try {
            File file = new File(Paths.iconLocal);
            return file;
        } catch (Exception e) {
            //do noting
            return null;
        }
    }

    protected void startPhotoZoom(String path) {
        if (path == null) return;
        Intent intent = new Intent();
        intent.setClass(context, ZoomImage.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_clam);
    }

    private void openPayOrdersAty() {
        startActivity(new Intent(context, PayOrders.class));
        playOpenAnimation();
    }

    private void openMyHeartAty() {
        startActivity(new Intent(context, HeartAty.class));
        playOpenAnimation();
    }

    private void openMsgCenterAty() {
        startActivity(new Intent(context, MsgCenterAty.class));
        playOpenAnimation();
    }

    private void openSaleCardAty() {
        startActivity(new Intent(context, SaleCardAty.class));
        playOpenAnimation();
    }

    private void openRecoSaleAty() {
        if(UserUtil.getCurrentUser(context)!=null){
            startActivity(new Intent(context, RecoSaleAty.class));
            playOpenAnimation();
        }else{
            showToast("请先登录");
        }
    }

    private void openSettingAty() {
        startActivity(new Intent(context, SettingActivity.class));
        playOpenAnimation();
    }

    private void openUserInfoActivity() {
        Intent intent = new Intent(context, UserInfo.class);
//        startActivity(intent);
        startActivityForResult(intent, SELF_PAGE_REQ_CODE_INFO);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
    }

    private void openLoginActivity() {
        Intent intent = new Intent(context, LoginActivity.class);
//        startActivity(intent);
        startActivityForResult(intent, SELF_PAGE_REQ_CODE);
        getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_clam);
    }


    @Override
    public void onItemClicked(int group, int position) {
        switch (group) {
            case 0:
                switch (position) {
                    case 0:
                        openPayOrdersAty();
                        break;
                    case 1:
                        openMyHeartAty();
                        break;
                    case 2:
                        openMsgCenterAty();
                        break;
                }
                break;
            case 1:
                switch (position) {
                    case 0:
                        openSaleCardAty();
                        break;
                    case 1:
                        openRecoSaleAty();
                        break;
                }
                break;
            case 2:
                switch (position) {
                    case 0:
                        openSettingAty();
                        break;
                }
                break;
        }
    }

    class MyAdapter extends GLVAdapter {

        public MyAdapter(GroupListView lv) {
            super(lv);
        }

        @Override
        public int groupCount() {
            return items.length;
        }

        @Override
        public int dividerHeight() {
            return ScreenUtil.dp2px(context, 10);
        }

        @Override
        public int countInGroup(int group) {
            return items[group].length;
        }

        @Override
        public View getView(int group, int position) {
            return new GLVDefaultItemView2(context)
                    .setIcon(icons[group][position])
                    .setTitle(items[group][position])
                    .getContentView();
        }
    }

    /**
     * 请求码
     */
    private static final int SELF_PAGE_REQ_CODE_INFO = 0x01;
    private static final int SELF_PAGE_REQ_CODE = 0x02;
    private static final int IMAGE_REQUEST_CODE = 0x03;//选择本地照片
    private static final int CAMERA_REQUEST_CODE = 0x04;//拍照
    private static final int RESULT_REQUEST_CODE = 0x05;//剪切图片


    /**
     * ChooseDialog
     * 当用户头像被点击之后出现
     */
    public Dialog create(Context context, final onItemChoosedListener listener, String... params) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_choose_dialog, null);
        final Button b1 = (Button) view.findViewById(R.id.item1);
        final Button b2 = (Button) view.findViewById(R.id.item2);
        final Button b3 = (Button) view.findViewById(R.id.item3);
        if (params.length == 3) {
            b1.setText(params[0]);
            b2.setText(params[1]);
            b3.setText(params[2]);
        }
        View.OnClickListener lis = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (v.getId() == b1.getId()) {
                        listener.onItemChoosed(0);
                    } else if (v.getId() == b2.getId()) {
                        listener.onItemChoosed(1);
                    } else {
                        listener.onItemChoosed(2);
                    }
                }
            }
        };
        b1.setOnClickListener(lis);
        b2.setOnClickListener(lis);
        b3.setOnClickListener(lis);
        Dialog dialog = new Dialog(context, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = context.getResources().getDisplayMetrics().heightPixels;
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return dialog;
    }

    public interface onItemChoosedListener {
        public void onItemChoosed(int position);
    }

}
