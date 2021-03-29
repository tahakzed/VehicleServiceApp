package com.example.vehicleserviceapp;

import java.util.List;

public class Admin extends User{
    private String serviceStationName;
    private List<String> reviews,bookings;
    private long chargesCar,chargesBike;
    Admin(String name, String email, String phone, double lat, double lng, String serviceStationName, List<String> reviews,long chargesCar,long chargesBike,List<String> bookings ){
        super(name,email,phone,lat,lng);
        this.serviceStationName=serviceStationName;
        this.reviews=reviews;
        this.chargesCar=chargesCar;
        this.chargesBike=chargesBike;
        this.bookings=bookings;
    }

    public String getServiceStationName() {
        return serviceStationName;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public long getChargesBike() {
        return chargesBike;
    }

    public long getChargesCar() {
        return chargesCar;
    }

    public List<String> getBookings() {
        return bookings;
    }
}
