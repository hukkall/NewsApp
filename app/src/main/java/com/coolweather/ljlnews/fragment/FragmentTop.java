package com.coolweather.ljlnews.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coolweather.ljlnews.DetMessageActivity;
import com.coolweather.ljlnews.R;
import com.coolweather.ljlnews.adapter.NewAdapter;
import com.coolweather.ljlnews.entity.News;
import com.coolweather.ljlnews.entity.NewsItem;
import com.coolweather.ljlnews.util.HttpUtils;
import com.coolweather.ljlnews.util.JsonUtility;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//用一个碎片来放一个频道，在每个碎片中分别处理各种ui和数据
public class FragmentTop extends Fragment {
    //声明一些必须要的变量
    private News news;
    private static final String address = "https://way.jd.com/jisuapi/get?channel=头条&num=20&start=0&appkey=93e14b1331ec2205eb37b50e290ab542";
    private RecyclerView recyclerView;
    private List<NewsItem> list  = new ArrayList<NewsItem>();
    private NewAdapter newAdapter;
    private ProgressBar progressBar;                    //等待栏（不知道为什么没有显示，就没有用）
    private SwipeRefreshLayout swipeRefreshLayout;      //刷新用的布局
    @Nullable
    @Override
    //本方法是创建这个碎片时调用，主要是将各个组件都初始化起来
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_layout,container,false);
        recyclerView = view.findViewById(R.id.toprecycleview);
        progressBar = view.findViewById(R.id.topprogressbar);
        newAdapter = new NewAdapter(list);
        queryFromServer(address);                                   //碎片初始化后就直接发送一次数据请求
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout = view.findViewById(R.id.topswiperefreshlayout);
        initListner();
        return view;
    }
    //初始化监听器
    public void initListner(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryFromServer(address);
                Toast.makeText(getContext(),"刷新数据",Toast.LENGTH_SHORT).show();
                Log.d("测试数据","刷新数据");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        newAdapter.setOnRecyclerViewItemClickListener(new NewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                newAdapter.setThisPosition(position);                                       //使用接口回调来将position设置和使用该position
                Intent intent = new Intent(getContext(), DetMessageActivity.class);         //用Intent传递数据给DetMessageActivity
                intent.putExtra("list",(Serializable)list.get(position));
                startActivity(intent);
            }
        });
    }
    //这个是碎片开始显示时调用的方法，即碎片开始展示在用户面前时使用
    @Override
    public void onStart() {
        super.onStart();
        recyclerView.setAdapter(newAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }
    //碎片被重新调用时使用
    @Override
    public void onResume() {
        super.onResume();
    }



    //从服务器中请求数据
    public void queryFromServer(String address){
        Log.d("测试数据","发送请求！");
        HttpUtils.sendOkHttpRequest(address, new Callback() {               //用okhttp框架发送请求给服务器
            Handler mainHandler = new Handler(getContext().getMainLooper());
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("测试请求！","请求失败");
            }
            @Override
            //接收到返回值后开始逻辑处理
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("测试请求！","请求成功");
                String responseText;
                responseText = response.body().string();
                news = JsonUtility.handNewListResponse(responseText);
                String imgStr = null;
                list.clear();
                for(int i = 0;i < 20;i ++){
                    try {
                        imgStr = bitmapToString(getImage(news.getResult().getResult().getList().get(i).getPic()));  //将网络照片进行处理，具体看方法
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //获得经过解析后的json数据
                    NewsItem newsItem = new NewsItem.Builder().newContent(news.getResult().getResult().getList().get(i).getContent())
                            .newSrc(news.getResult().getResult().getList().get(i).getSrc())
                            .newtime(news.getResult().getResult().getList().get(i).getTime())
                            .newTitle(news.getResult().getResult().getList().get(i).getTitle())
                            .img(imgStr).build();
                    list.add(newsItem);
                    Log.d("测试","list改变！" + list.size());
                }
                //数据装载完之后跳转到转移回主线程并发送一个notifyDataSetChanged信号和将刷新栏结束
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        newAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                Log.d("测试数据！","数据装载完成");
            }
        });
        Log.d("测试请求！","开始请求");
    }
    //把bitmap转为String
    public static String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();   //获取一个子节数组输出流
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte [] byteArr = baos.toByteArray();       //转为byte数组
        return Base64.encodeToString(byteArr,Base64.DEFAULT);
    }

    //将url的图片转为bitmap
    public static Bitmap getImage(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            //设置固定大小
            //需要的大小
            float newWidth = 200f;
            float newHeigth = 200f;

            //图片大小
            int width = myBitmap.getWidth();
            int height = myBitmap.getHeight();

            //缩放比例
            float scaleWidth = newWidth / width;
            float scaleHeigth = newHeigth / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeigth);

            Bitmap bitmap = Bitmap.createBitmap(myBitmap, 0, 0, width, height, matrix, true);
            return bitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
