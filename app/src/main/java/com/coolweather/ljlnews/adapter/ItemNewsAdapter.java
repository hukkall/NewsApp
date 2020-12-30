package com.coolweather.ljlnews.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coolweather.ljlnews.R;

import java.util.List;

//创建新闻适配器
public class ItemNewsAdapter extends RecyclerView.Adapter<ItemNewsAdapter.ViewHolder> {
    private List<String> strings;                                   //装载频道信息的list
    private OnItemClickListener onRecyclerViewItemClickListener;    //接口回调的实体类
    public static final int UPDATE_STATE = 101;
    public static final int UPDATE_NAME = 102;

    //用一个viewholder持有并返回控件
    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView itemName;
        private View view;
        public View getView() {
            return view;
        }
        public void setView(View view) {
            this.view = view;
        }
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //对控件进行实例
            itemName = itemView.findViewById(R.id.channel_text);
            view = itemView;
        }
    }

    //先声明一个int成员变量
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

    public void setOnRecyclerViewItemClickListener(OnItemClickListener onItemClickListener) {
        this.onRecyclerViewItemClickListener = onItemClickListener;
    }

    public ItemNewsAdapter(List<String> strings) {
        this.strings = strings;
    }

    //在ViewHolder创建时进行contenxt的装载，viewholder的创建初始化
    @NonNull
    @Override
    public ItemNewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //装入组件时对组件进行逻辑处理
    @Override
    public void onBindViewHolder(@NonNull ItemNewsAdapter.ViewHolder holder, int position) {
        String str  = strings.get(position);
        final int fPosition = position;
        holder.itemName.setText(str);
        if(position == getthisPosition()){                 //再将返回回来的进行比较
            holder.itemName.setTextColor(Color.parseColor("#ffffff"));
            holder.getView().setBackgroundColor(Color.parseColor("#FC8915"));
        }else{
            holder.itemName.setTextColor(Color.BLACK);
            holder.getView().setBackgroundColor(Color.parseColor("#ffffff"));
        }
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


    //返回列表的大小
    @Override
    public int getItemCount() {
        return strings.size();
    }


    //定义一个点击事件接口
    public interface OnItemClickListener {
        void onClick(int position);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }
}
