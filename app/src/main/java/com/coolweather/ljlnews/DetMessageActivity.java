package com.coolweather.ljlnews;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolweather.ljlnews.entity.News;
import com.coolweather.ljlnews.entity.NewsItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
//新闻详细活动，用于显示新闻的详细
public class DetMessageActivity extends AppCompatActivity {
    //声明几个组件
    private TextView newsTitle,newContent,newsSrc,newsTime;
    private ImageView newsImage,backImage;
    private NewsItem newsItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.det_message);
        initView();
        newsItem = (NewsItem) getIntent().getSerializableExtra("list");
        initData();
    }
    //初始化组件
    public void initView(){
        newContent = findViewById(R.id.detnewscontent);
        newsImage = findViewById(R.id.detnewsimage);
        newsSrc = findViewById(R.id.newssrctext);
        newsTitle = findViewById(R.id.newstitle);
        newsTime = findViewById(R.id.newstimetext);
        backImage = findViewById(R.id.backiamge);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //装载数据
    public void initData(){
        String content = "";
        newsTime.setText(newsItem.getNewtime());
        newsSrc.setText(newsItem.getNewSrc());
        newsTitle.setText(newsItem.getNewTitle());
        //把图片转为bitmap后装入imageview
        newsImage.setImageBitmap(toBitmap(newsItem.getImg()));
        //用JSON开源库解析数据，
        Document doc = Jsoup.parse(newsItem.getNewContent());
        Elements body = doc.getElementsByTag("p");
        //数据细节处理
        for(int i = 0 ;i < body.size();i++){
            content +="         "  + body.get(i).text() + "\n";
        }
        newContent.setText(content);
    }
    //字符串转为bitmap
    public Bitmap toBitmap(String str){
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(str, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,bitmapArray.length);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
}
