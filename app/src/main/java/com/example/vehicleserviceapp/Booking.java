package com.example.vehicleserviceapp;

public class Booking {
    String clientEmail;
    double clientLat;
    double clientLng;
    String clientName;
    String clientPhone;
    String date;
    String time;
    String adminEmail;
    String paymentStatus;
    String paymentDate;
    String paymentTime;
    long paymentCharges;
    long paymentTip;
    String vehicleName;
    String vehicleType;
    String bookingID;

    String status;
    String serviceStationName;
    public Booking(String bookingID, String status, String serviceStationName,String clientName, String clientPhone,String clientEmail, double clientLat,double clientLng,
                    String vehicleName,String vehicleType,String date,String time,String adminEmail,String paymentStatus,String paymentDate,
                   String paymentTime,long paymentCharges,long paymentTip){
        this.bookingID=bookingID;
        this.clientName=clientName;
        this.clientPhone=clientPhone;
        this.clientEmail=clientEmail;
        this.clientLat=clientLat;
        this.clientLng=clientLng;
        this.vehicleName=vehicleName;
        this.vehicleType=vehicleType;
        this.date=date;
        this.time=time;

        this.status=status;
        this.serviceStationName=serviceStationName;
        this.paymentCharges=paymentCharges;
        this.adminEmail=adminEmail;
        this.paymentStatus=paymentStatus;
        this.paymentDate=paymentDate;
        this.paymentTime=paymentTime;
        this.paymentTip=paymentTip;
    }

    public long getPaymentCharges() {
        return paymentCharges;
    }

    public long getPaymentTip() {
        return paymentTip;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public String getServiceStationName() {
        return serviceStationName;
    }

    public String getStatus() {
        return status;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public double getClientLat() {
        return clientLat;
    }

    public double getClientLng() {
        return clientLng;
    }

    public String getBookingID() {
        return bookingID;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

}
