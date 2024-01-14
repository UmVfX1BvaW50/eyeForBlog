package com.re.eyeforblog;

public class data {
    public String url;
    public String content;
    public data(String url, String content){
        this.url = url;
        this.content = content;
    }
    public String getUrl(){
        return this.url;
    }
    public String getContent(){
        return this.content;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void setContent(String content){
        this.content = content;
    }
}
