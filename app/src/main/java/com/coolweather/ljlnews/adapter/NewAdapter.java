package com.coolweather.ljlnews.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coolweather.ljlnews.DetMessageActivity;
import com.coolweather.ljlnews.R;
import com.coolweather.ljlnews.entity.NewsItem;

import java.util.List;

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.ViewHolder> {
    private List<NewsItem> newsItems;
    //初始化并装载view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsitem,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public NewAdapter(List<NewsItem> newsItems) {
        this.newsItems = newsItems;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int fPosition = position;     //暂存此时的position
        //图片处理
        NewsItem newsItem = newsItems.get(position);
        String img = newsItem.getImg();
        byte []decode = Base64.decode(img,1);
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(decode,0,decode.length);
        holder.img.setImageBitmap(bitmap);
        //设置各个信息
        holder.newTime.setText(newsItems.get(position).getNewtime());
        holder.newSrc.setText(newsItems.get(position).getNewSrc());
        holder.newTitle.setText(newsItems.get(position).getNewTitle());
        //设置自定义的点击事件，这里是给每个view都设置一次，每次点击就会返回对应的item
        if(onRecyclerViewItemClickListener != null){
            holder.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewItemClickListener.onClick(fPosition);         //这里是反馈回来的position
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    //用一个viewholder持有并返回控件
    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView newTitle;
        private TextView newTime;
        private TextView newSrc;
        private ImageView img;
        private View view;              //返回整个的view实例，可以实现对整个view的点击
        public View getView() {
            return view;
        }
        public void setView(View view) {
            this.view = view;
        }
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            newTitle = view.findViewById(R.id.newsdesc);
            newSrc = view.findViewById(R.id.newsource);
            newTime = view.findViewById(R.id.newstime);
            img = view.findViewById(R.id.newimage);
        }
    }

    private NewAdapter.OnItemClickListener onRecyclerViewItemClickListener;    //接口回调的实体类

    //使用接口回调技术来做点击item跳转
    //首先定义一个时间点击接口
    //定义一个点击事件接口
    public interface OnItemClickListener {
        void onClick(int position);
    }

    //先声明一个int成员变量,用来记录点击了那个position
    private int thisPosition;
    //再定义一个int类型的返回值方法
    public int getthisPosition() {
        return thisPosition;
    }
    //其次定义一个方法用来绑定当前参数值的方法
    //此方法是在调用此适配器的地方调用的，此适配器内不会被调用到
    public void setThisPosition(int thisPosition) {
        this.thisPosition = thisPosition;
    }
    //设置接口的实例
    public void setOnRecyclerViewItemClickListener(NewAdapter.OnItemClickListener onItemClickListener) {
        this.onRecyclerViewItemClickListener = onItemClickListener;
    }

}
