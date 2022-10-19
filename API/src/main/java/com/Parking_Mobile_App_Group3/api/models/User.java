package com.Parking_Mobile_App_Group3.api.models;
import com.google.firebase.auth.FirebaseUser;

public class User {
    public String id;
    public String email;
    public String userType;
    public double balance;
    public String parkingLotId;

//    public User(FirebaseUser user) {
//        this.uid = user.getUid();
//        this.email = user.getEmail();
//    }
    public User(FirebaseUser user, String userType) {
        this.id = user.getEmail().replace("@", "_").replace(".", "_");
        this.email = user.getEmail();
        this.userType = userType;
        this.balance = 0;
        this.parkingLotId = null;
    }
}
