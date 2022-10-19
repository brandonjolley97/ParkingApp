package com.Parking_Mobile_App_Group3.api.viewmodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Parking_Mobile_App_Group3.api.models.ParkingLot;
import com.Parking_Mobile_App_Group3.api.models.Reservation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class ParkingLotViewModel extends ViewModel {
    DatabaseReference database;
    ObservableArrayList<ParkingLot> lots = new ObservableArrayList<>();
    MutableLiveData<ParkingLot> tempLot = new MutableLiveData<>();
    ObservableArrayList<Reservation> allReservations = new ObservableArrayList<>();
    ObservableArrayList<Reservation> lotReservations = new ObservableArrayList<>();



    public ParkingLotViewModel(){
        this.database = FirebaseDatabase.getInstance().getReference();
        loadData();
    }

    public void setParkingLot(String lotId){
        lotReservations = getReservations(lotId);
    }

    public ObservableArrayList<Reservation> getLotReservations(){
        return lotReservations;
    }

    private void loadData(){
        database.child("lotData").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(!snapshot.exists()) return;

                String owner = snapshot.child("owner").getValue().toString();
                String location = snapshot.child("location").getValue().toString();
                String price = snapshot.child("price").getValue().toString();
                String numSpaces = snapshot.child("numSpaces").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                String attenId = snapshot.child("attendantId").getValue().toString();
                String sReserved = snapshot.child("spacesReserved").getValue().toString();

                double dPrice = Double.parseDouble(price);
                int iNumSpaces = Integer.parseInt(numSpaces);
                int iReserved = Integer.parseInt(sReserved);

                ParkingLot newLot = new ParkingLot(name, iNumSpaces, owner, location, dPrice, attenId, iReserved);
                newLot.id = snapshot.getKey();
                lots.add(newLot);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String owner = snapshot.child("owner").getValue().toString();
                String location = snapshot.child("location").getValue().toString();
                String price = snapshot.child("price").getValue().toString();
                String numSpaces = snapshot.child("numSpaces").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                String attenId = snapshot.child("attendantId").getValue().toString();
                String sReserved = snapshot.child("spacesReserved").getValue().toString();

                double dPrice = Double.parseDouble(price);
                int iNumSpaces = Integer.parseInt(numSpaces);
                int iReserved = Integer.parseInt(sReserved);

                ParkingLot newLot = new ParkingLot(name, iNumSpaces, owner, location, dPrice, attenId, iReserved);
                newLot.id = snapshot.getKey();
                //TODO: testing
                //Get index in 'lots' of existing parking lot that is changed
                int index;
                for(ParkingLot l: lots){
                    if(l.id.equals(snapshot.getKey())){
                        index = lots.indexOf(l);
                        lots.set(index, newLot);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String owner = snapshot.child("owner").getValue().toString();
                String location = snapshot.child("location").getValue().toString();
                String price = snapshot.child("price").getValue().toString();
                String numSpaces = snapshot.child("numSpaces").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                String attenId = snapshot.child("attendantId").getValue().toString();
                String sReserved = snapshot.child("spacesReserved").getValue().toString();

                double dPrice = Double.parseDouble(price);
                int iNumSpaces = Integer.parseInt(numSpaces);
                int iReserved = Integer.parseInt(sReserved);

                ParkingLot newLot = new ParkingLot(name, iNumSpaces, owner, location, dPrice, attenId, iReserved);
                newLot.id = snapshot.getKey();
                lots.remove(newLot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public ObservableArrayList<ParkingLot> getLots(){
        return lots;
    }

    public MutableLiveData<ParkingLot> getLot(String lotId){
        database.child("lotData").child(lotId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String owner = snapshot.child("owner").getValue().toString();
                String location = snapshot.child("location").getValue().toString();
                String price = snapshot.child("price").getValue().toString();
                String numSpaces = snapshot.child("numSpaces").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                String attenId = snapshot.child("attendantId").getValue().toString();
                String sReserved = snapshot.child("spacesReserved").getValue().toString();

                double dPrice = Double.parseDouble(price);
                int iNumSpaces = Integer.parseInt(numSpaces);
                int iReserved = Integer.parseInt(sReserved);

                ParkingLot newLot = new ParkingLot(name, iNumSpaces, owner, location, dPrice, attenId, iReserved);
                tempLot.setValue(newLot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return tempLot;
    }

    // Create reservation in Firebase, Probably update 'reservation' MutableLiveData
    //  Used in CustomerMainActivity
    public void createReservation(String parkingLotId, String userId){
        //Add reservation to ParkingLot
        Reservation newRes = new Reservation(userId, false);
        database.child("lotData").child(parkingLotId).child("reservations").child(userId)
                .child("checkedIn").setValue(false);

        allReservations.add(newRes);

        //Update availability of parkingSpaces in Firebase
        database.child("lotData").child(parkingLotId).child("spacesReserved").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int currentRes = Integer.parseInt(snapshot.getValue().toString());
                database.child("lotData").child(parkingLotId).child("spacesReserved").setValue(String.valueOf(currentRes + 1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Check-in reservation in Firebase, Probably update 'reservation' MutableLiveData
    //  Used in AttendantMainActivity
    public void checkInReservation(String parkingLotId, String userId){
        database.child("lotData").child(parkingLotId).child("reservations").child(userId).child("checkedIn").setValue(true);
        Reservation newRes = new Reservation(userId, true);
        //Update reservations MutableLiveData
        for(Reservation r : allReservations){
            if(r.userId.equals(userId)){
                allReservations.add(allReservations.indexOf(r), newRes);
            }
        }
    }


    // Delete reservation in Firebase, Probably update 'reservation' MutableLiveData
    //  Used in AttendantMainActivity
    public void deleteReservation(String parkingLotId, String userId){
        if(parkingLotId == null || userId == null) return;

        //Remove parkingLotId from customer
        database.child("accounts").child(userId).child("parkingLotId").removeValue();
        //Remove child from ParkingLot
        database.child("lotData").child(parkingLotId).child("reservations").child(userId).removeValue();
        //Update number reserved spaces of ParkingLot
        database.child("lotData").child(parkingLotId).child("spacesReserved").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;

                int currentRes = Integer.parseInt(snapshot.getValue().toString());
                database.child("lotData").child(parkingLotId).child("spacesReserved").setValue(String.valueOf(currentRes - 1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Update reservations MutableLiveData
        for(Reservation r : allReservations){
            if(r.userId.equals(userId)){
                allReservations.remove(r);
            }
        }

    }

    private ObservableArrayList<Reservation> getReservations(String lotId){
        database.child("lotData").child(lotId).child("reservations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String resId = snapshot.getKey();
                boolean checkedIn = Boolean.valueOf(snapshot.child("checkedIn").getValue().toString());
                Reservation res = new Reservation(resId, checkedIn);
                lotReservations.add(res);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String resId = snapshot.getKey();
                boolean checkedIn = Boolean.valueOf(snapshot.child("checkedIn").getValue().toString());
                Reservation res = new Reservation(resId, checkedIn);

                for(Reservation r : lotReservations){
                    if(r.userId.equals(snapshot.getKey())){
                        lotReservations.add(lotReservations.indexOf(r), res);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String resId = snapshot.getKey();
                boolean checkedIn = Boolean.valueOf(snapshot.child("checkedIn").getValue().toString());
                Reservation res = new Reservation(resId, checkedIn);
                lotReservations.remove(res);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return allReservations;
    }

    //Sends money to owner and dev from customer
    public void payOwner(double amount, String customerId, String ownerId){
        DecimalFormat df = new DecimalFormat("0.00");

        double devRate = .35;
        double devCut = Double.parseDouble(df.format(amount * devRate));
        double ownerCut = amount - devCut;

        //subtract from customer
        updateUserBalance(amount, false, customerId);
        //add to owner
        updateUserBalance(ownerCut, true, ownerId);
        //add to dev
        updateDevBalance(devCut, true);
    }

    //Send money to attendants from dev
    public void payAttendant(double amount, String attendantId){
        //remove money from dev
        updateDevBalance(amount, false);
        //Pay attendant
        updateUserBalance(amount, true, attendantId);
    }

    //Sends money to app (the money the developers get)
    private void updateDevBalance(double amount, boolean add){
        if(!add){
            amount = amount * -1;
        }
        double finalAmount = amount;
        database.child("app").child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    database.child("app").child("balance").setValue("0.00");
                }
                DecimalFormat df = new DecimalFormat("0.00");
                double current = Double.parseDouble((String) snapshot.getValue());
                database.child("app").child("balance").setValue(df.format(current + finalAmount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUserBalance(double amount, boolean add, String userId){
        if(!add){
            amount = amount * -1;
        }
        double finalAmount = amount;
        database.child("accounts").child(userId).child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    database.child("accounts").child(userId).child("balance").setValue("0.00").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            DecimalFormat df = new DecimalFormat("0.00");
                            database.child("accounts").child(userId).child("balance").setValue(df.format(finalAmount));
                        }
                    });
                }
                else {
                    double current = Double.parseDouble(snapshot.getValue().toString());
                    DecimalFormat df = new DecimalFormat("0.00");
                    database.child("accounts").child(userId).child("balance").setValue(df.format(current + finalAmount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
