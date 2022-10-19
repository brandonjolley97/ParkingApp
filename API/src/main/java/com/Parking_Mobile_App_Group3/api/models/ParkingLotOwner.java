package com.Parking_Mobile_App_Group3.api.models;

import com.google.firebase.auth.FirebaseUser;

public class ParkingLotOwner extends User{
    String ownedParkingLotId;

    public ParkingLotOwner(FirebaseUser user){
        super(user, "owner");
    }
}
