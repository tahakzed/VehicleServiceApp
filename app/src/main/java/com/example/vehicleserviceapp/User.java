package com.example.vehicleserviceapp;

public class User {
    private String name;
    private String email;
    private String phone;
    private double lat;
    private double lng;
    public User(String name,String email,String phone,double lat, double lng){
        this.name=name;
        this.email=email;
        this.phone=phone;
        this.lat=lat;
        this.lng=lng;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getPhone() {
        return phone;
    }
}
