package com.axdav.messageapp.Model;

/*Own LatLng class because google's implementation missed a
no argument constructor so didnt work against the realtime database*/

public class MyLatLng {
    private double latitude;
    private double longitude;
    private String username;
    public MyLatLng(){}

    public MyLatLng(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public MyLatLng(double latitude, double longitude, String username){
        this.latitude = latitude;
        this.longitude = longitude;
        this.username = username;
    }

    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public String getUsername() {
        return username;
    }
}
