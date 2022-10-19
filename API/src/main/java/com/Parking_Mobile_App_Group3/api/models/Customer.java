package com.Parking_Mobile_App_Group3.api.models;

import com.google.firebase.auth.FirebaseUser;

public class Customer extends User{
    String reservedParkingLot;
    String reservedParkingSpace;

    public Customer(FirebaseUser user){
        super(user, "customer");
    }
}
