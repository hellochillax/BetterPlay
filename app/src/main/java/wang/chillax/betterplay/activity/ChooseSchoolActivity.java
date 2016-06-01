package wang.chillax.betterplay.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;
import wang.chillax.betterplay.R;
import wang.chillax.betterplay.bmob.User;
import wang.chillax.betterplay.config.Urls;
import wang.chillax.betterplay.cusview.ToolBar;
import wang.chillax.betterplay.model.Province;
import wang.chillax.betterplay.model.School;
import wang.chillax.betterplay.utils.LogUtils;

public class ChooseSchoolActivity extends BaseActivity implements ToolBar.ToolBarListener {

    @Bind(R.id.toolbar)
    ToolBar mToolbar;
    @Bind(R.id.lv_province)
    ListView mProvinceListView;
    @Bind(R.id.lv_school)
    ListView mSchoolListView;


    List<String> provinceNameList = new ArrayList<>();
    List<String> schoolNameList = new ArrayList<>();
    List<Province.ProvinceList> provinceList = new ArrayList<>();
    List<School.SchoolList> schoolList = new ArrayList<>();

    private RequestQueue mRequestQueue;
    @Override
    protected void initDatas() {
        mToolbar.setToolBarListener(this);


        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,provinceNameList);
        mProvinceListView.setAdapter(provinceAdapter);
        loadProvince(provinceAdapter);  //下载省份数据

        final ArrayAdapter<String> schoolAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,schoolNameList);
        mSchoolListView.setAdapter(schoolAdapter);

        mProvinceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadSchool(position+1,schoolAdapter);
            }
        });

        mSchoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chooseSchool(position);
            }
        });
    }

    @Override
    protected void initViews() {

    }

    private void loadProvince(final ArrayAdapter<String> aa){
        mRequestQueue = Volley.newRequestQueue(this);
        LogUtils.d(Urls.PROVINCE_URL);
        StringRequest mRequest = new StringRequest(Urls.PROVINCE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Province p = new Gson().fromJson(s,Province.class);
                provinceList = p.getData();
                if(provinceList==null){
                    showToast(getResources().getString(R.string.error_network));
                }else {
                    for (int i = 0; i < provinceList.size(); i++) {
                        provinceNameList.add(provinceList.get(i).getProvince_name());

                    }
                    aa.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        mRequestQueue.add(mRequest);
    }

    private void loadSchool(int schoolId,final ArrayAdapter<String> aa){
        mRequestQueue = Volley.newRequestQueue(ChooseSchoolActivity.this);
        StringRequest mRequest = new StringRequest(Urls.SCHOOL_URL + schoolId, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                School sc = new Gson().fromJson(s,School.class);
                schoolList = sc.getData();
                schoolNameList.clear();
                for(int i =0; i<schoolList.size();i++){
                    schoolNameList.add(schoolList.get(i).getSchool_name());
                }
                aa.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        mRequestQueue.add(mRequest);
    }

    public void chooseSchool(int position){
        final String schoolName = schoolNameList.get(position);
        final User user = BmobUser.getCurrentUser(this,User.class);
        AlertDialog.Builder builder =  new AlertDialog.Builder(this)
                .setMessage("你选择了"+schoolName)
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.setSchool(schoolName);
                        user.update(ChooseSchoolActivity.this, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                showToast("选择成功");
                                //startActivity(new Intent(ChooseSchoolActivity.this,UserInfo.class));
                                setResult(RESULT_OK);
                                onBackPressed();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                showToast("选择失败，请稍后再试");
                            }
                        });
                    }
                })
                .setNegativeButton("重新选择", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }
    /**
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************
     * **********************************************************************************************************************************

     */
    @Override
    protected int initLayoutRes() {
        return R.layout.activity_choose_school;
    }

    @Override
    protected void onExit() {

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

    }

    @Override
    public void onInit(ImageView back, TextView titleLeft, TextView titleCenter, TextView titleRight, ImageView more) {
        titleCenter.setText("选择学校");
    }


}
