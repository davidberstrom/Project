package com.axdav.messageapp.Model;

/*Own LatLng class because google's implementation missed a
no argument constructor so didnt work against the realtime database*/

public class MyLatLng {
    private double latitude;
    private double longitude;
    private String username;

    /*Required empty constructor*/
    public MyLatLng(){}

    /*constructor which takes to parameters*/
    public MyLatLng(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /*Returns the latitude*/
    public double getLatitude(){
        return latitude;
    }
    /*returns the longitude*/
    public double getLongitude(){
        return longitude;
    }
}
