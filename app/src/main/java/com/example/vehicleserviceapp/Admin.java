package com.example.vehicleserviceapp;

import java.util.List;

public class Admin extends User{
    private String serviceStationName;
    private List<String> reviews,bookings;
    private int charges;
    Admin(String name, String email, String phone, double lat, double lng, String serviceStationName, List<String> reviews,int charges,List<String> bookings ){
        super(name,email,phone,lat,lng);
        this.serviceStationName=serviceStationName;
        this.reviews=reviews;
        this.charges=charges;
        this.bookings=bookings;
    }

    public String getServiceStationName() {
        return serviceStationName;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public int getCharges() {
        return charges;
    }

    public List<String> getBookings() {
        return bookings;
    }
}
