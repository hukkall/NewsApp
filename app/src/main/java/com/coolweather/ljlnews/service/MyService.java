package com.coolweather.ljlnews.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.coolweather.ljlnews.MainActivity;
import com.coolweather.ljlnews.R;
import com.coolweather.ljlnews.entity.NewsItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyService extends Service {
    /**
     * 唯一前台通知ID
     */
    private static final int NOTIFICATION_ID = 1000;
    public MyService() {
    }
    //服务创建时调用
    @Override
    public void onCreate() {
        super.onCreate();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //开始服务时调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NewsItem newsItem = (NewsItem) intent.getSerializableExtra("list");
        Notification notification = createForegroundNotification(newsItem);
        startForeground(NOTIFICATION_ID,notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
    //创建notification
    private Notification createForegroundNotification(NewsItem item) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        // 唯一的通知通道的id.
        String notificationChannelId = "notification_channel_id_01";
        //Android8.0以上的系统，需要新建消息通道
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "前台通道服务";     //设置通道名字
            //设置通道的优先级，1000最高，依次递减
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, channelName, importance);
            notificationChannel.setDescription("一个前台通道");
            //创建通道
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,notificationChannelId);
        //设置通知的小图标
        builder.setSmallIcon(R.drawable.newicon);
        //设置通知的大图标
        builder.setLargeIcon(getImage(item.getImg()));
        //设置通知的标题
        builder.setContentTitle(item.getNewTitle());
        //设置通知的正文
        String content = "";
        //用JSON开源库解析数据，
        Document doc = Jsoup.parse(item.getNewContent());
        Elements body = doc.getElementsByTag("p");
        //数据细节处理
        for(int i = 0 ;i < body.size();i++){
            content +=  body.get(i).text() + "\n";
        }
        builder.setContentText(content);
        //设置通知显示的时间
        builder.setWhen(System.currentTimeMillis());
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        //设置风格
        builder.setStyle(new NotificationCompat.BigTextStyle());
        return builder.build();
    }
}
