package com.Parking_Mobile_App_Group3.api.models;

public class Reservation {
    public String userId;
    public boolean checkedIn;

    public Reservation(String userId, boolean checkedIn){
        this.userId = userId;
        this.checkedIn = checkedIn;
    }
}
