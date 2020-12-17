package com.coolweather.ljlnews.util;

import com.coolweather.ljlnews.entity.News;
import com.google.gson.Gson;

import org.json.JSONObject;

public class JsonUtility {
    public static News handNewListResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            return new Gson().fromJson(jsonObject.toString(), News.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
