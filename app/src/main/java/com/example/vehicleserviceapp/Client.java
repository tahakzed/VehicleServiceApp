package com.example.vehicleserviceapp;

import java.util.List;

public class Client extends User{

    private List<String> vehicleNameAndType;
    private List<String> Bookings;
    private String imageId;
    public Client(String name,String email,String phone,double lat, double lng,List<String> vehicleNameAndType,List<String> Bookings
            ,String imageId){
        super(name,email,phone,lat,lng);
        this.vehicleNameAndType=vehicleNameAndType;
        this.Bookings=Bookings;
        this.imageId=imageId;
    }

    public List<String> getVehicleNameAndType() {
        return vehicleNameAndType;
    }

    public List<String> getBookings() {
        return Bookings;
    }

    public String getImageId() {
        return imageId;
    }
}
