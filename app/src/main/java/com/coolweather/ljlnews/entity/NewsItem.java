package com.coolweather.ljlnews.entity;


import java.io.Serializable;

//使用建造者模式构建实体类
public class NewsItem implements Serializable {
    private String newTitle;
    private String newSrc;
    private String img;
    private String newtime;
    private String newContent;

    public NewsItem(Builder builder) {
        this.img = builder.img;
        this.newSrc = builder.newSrc;
        this.newtime = builder.newtime;
        this.newTitle = builder.newTitle;
        this.newContent = builder.newContent;
    }
    //此处建立一个构造着对象，在对象中持有所有要进行构建的参数
    public static class Builder{
        private String newTitle;
        private String newSrc;
        private String img;
        private String newtime;
        private String newContent;
        //每次用对应参数名来设置，并把此builder修改后返回
        public Builder newTitle(String newTitle){
            this.newTitle = newTitle;
            return this;
        }
        public Builder newSrc(String newSrc){
            this.newSrc = newSrc;
            return this;
        }
        public Builder img(String img){
            this.img = img;
            return this;
        }
        public Builder newtime(String newtime){
            this.newtime = newtime;
            return this;
        }
        public Builder newContent(String newContent){
            this.newContent = newContent;
            return  this;
        }


        //构建完成后用实体对象的构造函数将builder传出后完成构建
        public NewsItem build(){
            return new NewsItem(this);
        }
    }

    public String getNewTitle() {
        return newTitle;
    }

    public String getNewSrc() {
        return newSrc;
    }

    public String getImg() {
        return img;
    }

    public String getNewtime() {
        return newtime;
    }

    public String getNewContent() {
        return newContent;
    }
}
