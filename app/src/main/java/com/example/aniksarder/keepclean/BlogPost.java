package com.example.aniksarder.keepclean;


import java.util.Date;

public class BlogPost extends BlogPostId {

    public String user_id;
    public String image_url;
    public String image_thumb;
    public String desc;
    public String place_name;
    public String address ;
    public Date timestamp ;

    public BlogPost(){

    }

    public BlogPost(String user_id, String image_url, String desc , String place_name, String address, String image_thumb, Date timestamp){
        this.user_id = user_id;
        this.image_url = image_url;
        this.image_thumb = image_thumb;
        this.desc = desc;
        this.place_name = place_name;
        this.address = address;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;

    }
    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
