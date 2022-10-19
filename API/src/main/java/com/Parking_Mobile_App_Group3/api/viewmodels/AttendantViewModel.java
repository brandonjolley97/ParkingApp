package com.Parking_Mobile_App_Group3.api.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Parking_Mobile_App_Group3.api.models.Attendant;
import com.Parking_Mobile_App_Group3.api.models.ParkingLot;
import com.Parking_Mobile_App_Group3.api.models.Reservation;
import com.Parking_Mobile_App_Group3.api.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.database.ValueEventListener;

//public class AttendantViewModel extends UserViewModel{
//    ObservableArrayList<Reservation> lotReservations = new ObservableArrayList<>();
//    MutableLiveData<ParkingLot> parkingLot = new MutableLiveData<>();
//    MutableLiveData<Attendant> attendant = new MutableLiveData<>();
//
//    AttendantViewModel(){
//        super();
//        attendant.setValue(new Attendant(auth.getCurrentUser()) );
//        loadReservations();
//    }
//
//    public ObservableArrayList<Reservation> getLotReservations() {
//        return lotReservations;
//    }
//
//    //Load all the reservations for a particular parking lot
//    public void loadReservations(){
//        database.child("lotData").child(parkingLot.getValue().id).child("reservations")
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        String resId = snapshot.getKey();
//                        boolean checkedIn = Boolean.valueOf(snapshot.child("checkedIn").getValue().toString());
//                        Reservation res = new Reservation(resId, checkedIn);
//                        lotReservations.add(res);
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        String resId = snapshot.getKey();
//                        boolean checkedIn = Boolean.valueOf(snapshot.child("checkedIn").getValue().toString());
//                        Reservation res = new Reservation(resId, checkedIn);
//
//                        for(Reservation r : lotReservations){
//                            if(r.userId.equals(snapshot.getKey())){
//                                lotReservations.add(lotReservations.indexOf(r), res);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                        String resId = snapshot.getKey();
//                        boolean checkedIn = Boolean.valueOf(snapshot.child("checkedIn").getValue().toString());
//                        Reservation res = new Reservation(resId, checkedIn);
//                        lotReservations.remove(res);
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
//
//}
