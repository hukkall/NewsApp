package com.coolweather.ljlnews.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class FragmentPhy extends Fragment {
    private News news;
    private static final String address = "https://way.jd.com/jisuapi/get?channel=体育&num=20&start=0&appkey=93e14b1331ec2205eb37b50e290ab542";
    private RecyclerView recyclerView;
    private List<NewsItem> list  = new ArrayList<NewsItem>();
    private NewAdapter newAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phy_layout,container,false);
        recyclerView = view.findViewById(R.id.phyrecycleview);
        progressBar = view.findViewById(R.id.phyprogressbar);
        newAdapter = new NewAdapter(list);
        queryFromServer(address);
        swipeRefreshLayout = view.findViewById(R.id.physwiperefreshlayout);
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
                newAdapter.setThisPosition(position);
                Intent intent = new Intent(getContext(), DetMessageActivity.class);
                intent.putExtra("list",(Serializable)list.get(position));
                startActivity(intent);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

        recyclerView.setAdapter(newAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        progressBar.setVisibility(View.VISIBLE);
        Log.d("测试生命","发出数据改变信号！" + list.size());
        progressBar.setVisibility(View.INVISIBLE);
        Log.d("测试生命", "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //从服务器中请求数据
    public void queryFromServer(String address){
        Log.d("测试数据","发送请求！");
        HttpUtils.sendOkHttpRequest(address, new Callback() {
            Handler mainHandler = new Handler(getContext().getMainLooper());
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("测试请求！","请求失败");
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                boolean flag = false;
                Log.d("测试请求！","请求成功");
                String responseText;
                responseText = response.body().string();
                System.out.println("测试请求！" + responseText);
                news = JsonUtility.handNewListResponse(responseText);
                String imgStr = null;
                list.clear();
                for(int i = 0;i < 20;i ++){
                    try {
                        imgStr = bitmapToString(getImage(news.getResult().getResult().getList().get(i).getPic()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    NewsItem newsItem = new NewsItem.Builder().newContent(news.getResult().getResult().getList().get(i).getContent())
                            .newSrc(news.getResult().getResult().getList().get(i).getSrc())
                            .newtime(news.getResult().getResult().getList().get(i).getTime())
                            .newTitle(news.getResult().getResult().getList().get(i).getTitle())
                            .img(imgStr).build();
                    list.add(newsItem);
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        newAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
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
