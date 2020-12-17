package com.coolweather.ljlnews.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;
//碎片适配器
public class pagerAdaper extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    //初始化时将碎片列表传入进来
    public pagerAdaper(@NonNull FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    //根据position返回碎片
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    //返回碎片的数量，也就是展示在界面中的碎片数
    public int getCount() {
        return fragments.size();
    }
}
