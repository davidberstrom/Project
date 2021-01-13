package com.axdav.messageapp.Model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String imageURL;
    private String userId;

    public User(){

    }

    public User(String username,String userId,String imageURL){
        this.username = username;
        this.userId = userId;
        this.imageURL = imageURL;
    }

    public User(String username, String userId){
        this.username = username;
        this.userId = userId;
        imageURL = "Default";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }
    public String getImageURL(){
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String toString(){
        return getUsername() +" "+getUserId();
    }
}
