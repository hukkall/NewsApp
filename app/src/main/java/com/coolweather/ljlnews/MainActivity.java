package com.coolweather.ljlnews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.coolweather.ljlnews.adapter.ItemNewsAdapter;
import com.coolweather.ljlnews.adapter.pagerAdaper;
import com.coolweather.ljlnews.entity.News;
import com.coolweather.ljlnews.fragment.FragmentFina;
import com.coolweather.ljlnews.fragment.FragmentPhy;
import com.coolweather.ljlnews.fragment.FragmentTeco;
import com.coolweather.ljlnews.fragment.FragmentTop;
import com.coolweather.ljlnews.entity.NewsItem;
import com.coolweather.ljlnews.util.CenterLayoutManager;
import com.coolweather.ljlnews.util.HttpUtils;
import com.coolweather.ljlnews.util.JsonUtility;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.coolweather.ljlnews.util.JsonUtility.handNewListResponse;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView navImage;
    private RecyclerView recyclerView;
    private List<String> list;          //频道数据模拟
    private ItemNewsAdapter itemNewsAdapter;
    private ViewPager viewPager;
    private List<Fragment> fragments;
    private CenterLayoutManager centerLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NewsItem newsItem =new NewsItem.Builder().newSrc("ssss").build();
        initData();
        initView();
        setStatusBar();
        initListner();
    }

    //从服务器中请求数据
    public void queryFromServer(String address){

        HttpUtils.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("测试请求！","请求失败");
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("测试请求！","请求成功");
                String responseText;
                responseText = response.body().string();
                System.out.println("测试请求！" + responseText);
                News news = JsonUtility.handNewListResponse(responseText);
            }
        });
        Log.d("测试请求！","开始请求");
    }
    //初始化控件
    public void initView(){
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        navImage = findViewById(R.id.navimage);
        recyclerView = findViewById(R.id.recyclerview);
        viewPager = findViewById(R.id.viewpager);
        centerLayoutManager = new CenterLayoutManager(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.HORIZONTAL);
        centerLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(centerLayoutManager);
        itemNewsAdapter = new ItemNewsAdapter(list);
        recyclerView.setAdapter(itemNewsAdapter);
        viewPager.setAdapter(new pagerAdaper(getSupportFragmentManager(),fragments));
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);
    }
    //初始化点击事件
    public void initListner(){
        navImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);

            }
        });
        //使用刚刚写好的暴露的接口，这里用到了接口回溯
        itemNewsAdapter.setOnRecyclerViewItemClickListener(new ItemNewsAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //拿适配器调用适配器内部自定义好的setThisPosition方法
                itemNewsAdapter.setThisPosition(position);
                //设置当前的碎片
                viewPager.setCurrentItem(position);
                //点击后滑动到中央
                centerLayoutManager.smoothScrollToPosition(recyclerView,new RecyclerView.State(),position);
                //刷新适配器
                itemNewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLongClick(int position) {

            }
        });
    }
    //初始化数据
    public void initData(){
        //频道数据
        list = new ArrayList<String>();
        list.add(new String("热点"));
        list.add(new String("科技"));
        list.add(new String("体育"));
        list.add(new String("财经"));
        fragments = new ArrayList<>();
        fragments.add(new FragmentTop());
        fragments.add(new FragmentTeco());
        fragments.add(new FragmentPhy());
        fragments.add(new FragmentFina());
    }
    //设置状态栏妖颜色，字体颜色，实现沉浸式布局
    protected void setStatusBar() {                 //设置主题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent,getTheme() ));//设置状态栏颜色
            getWindow().getDecorView().setSystemUiVisibility(View.SCREEN_STATE_ON | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //写页面切换后的处理逻辑
    @Override
    public void onPageSelected(int position) {
        switch(position){
            case 0:
                Toast.makeText(getBaseContext(),"0",Toast.LENGTH_SHORT).show();
                itemNewsAdapter.setThisPosition(0);
                centerLayoutManager.smoothScrollToPosition(recyclerView,new RecyclerView.State(),0);
                itemNewsAdapter.notifyDataSetChanged();
                break;
            case 1:
                Toast.makeText(getBaseContext(),"1",Toast.LENGTH_SHORT).show();
                itemNewsAdapter.setThisPosition(1);
                centerLayoutManager.smoothScrollToPosition(recyclerView,new RecyclerView.State(),1);
                itemNewsAdapter.notifyDataSetChanged();
                break;
            case 2:
                Toast.makeText(getBaseContext(),"2",Toast.LENGTH_SHORT).show();
                itemNewsAdapter.setThisPosition(2);
                centerLayoutManager.smoothScrollToPosition(recyclerView,new RecyclerView.State(),2);
                itemNewsAdapter.notifyDataSetChanged();
                break;
            case 3:
                Toast.makeText(getBaseContext(),"3",Toast.LENGTH_SHORT).show();
                itemNewsAdapter.setThisPosition(3);
                centerLayoutManager.smoothScrollToPosition(recyclerView,new RecyclerView.State(),3);
                itemNewsAdapter.notifyDataSetChanged();
                break;
             default:
                 break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
