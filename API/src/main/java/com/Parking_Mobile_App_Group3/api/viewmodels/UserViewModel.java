package com.Parking_Mobile_App_Group3.api.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Parking_Mobile_App_Group3.api.models.ParkingLot;
import com.Parking_Mobile_App_Group3.api.models.Reservation;
import com.Parking_Mobile_App_Group3.api.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class UserViewModel extends ViewModel {
    FirebaseAuth auth;
    DatabaseReference database;
    MutableLiveData<User> user = new MutableLiveData<>();
    MutableLiveData<String> authErrorMessage = new MutableLiveData<>();
    MutableLiveData<ParkingLot> parkingLot = new MutableLiveData<>();
    MutableLiveData<Double> balance = new MutableLiveData<>();
    ObservableArrayList<Reservation> lotReservations = new ObservableArrayList<>();
    MutableLiveData<String> parkingLotId = new MutableLiveData<>();
    MutableLiveData<String> genericErrorMsg = new MutableLiveData<>();

    public UserViewModel() {
        this.auth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance().getReference();
        authErrorMessage.setValue("");
        loadUser();

        this.auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser fbUser = auth.getCurrentUser();

                if (fbUser == null) {
                    Log.d("AUTH", "Auth state changed | user: NULL");
                    user.setValue(null);
                } else {
                    Log.d("AUTH", "Auth state changed | user: EXISTS");
                    user.setValue(new User(fbUser, null));
                    loadUser();
                    loadReservations();
                }
            }
        });
    }
    public ObservableArrayList<Reservation> getLotReservations() {
        return lotReservations;
    }

    public MutableLiveData<String> getParkingLotId() {
        return parkingLotId;
    }

    //Load all the reservations for the user's parking lot
    public void loadReservations(){
        if(parkingLotId.getValue() == null) return;

        database.child("lotData").child(parkingLotId.getValue()).child("reservations")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String resId = snapshot.getKey();
                        boolean checkedIn = Boolean.valueOf(snapshot.child("checkedIn").getValue().toString());
                        Reservation res = new Reservation(resId, checkedIn);

                        for(Reservation r : lotReservations){
                            if(r.userId.equals(snapshot.getKey())){
                                return;
                            }
                        }
                        lotReservations.add(res);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String resId = snapshot.getKey();
                        boolean checkedIn = Boolean.valueOf(snapshot.child("checkedIn").getValue().toString());
                        Reservation res = new Reservation(resId, checkedIn);

                        for(Reservation r : lotReservations){
                            if(r.userId.equals(snapshot.getKey())){
                                lotReservations.remove(r);
                                lotReservations.add(res);
//                                lotReservations.add(lotReservations.indexOf(r), res);
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
    }


    //Load user from current Firebase authenticated user
    private void loadUser(){
        if(user.getValue() == null) {
            Log.d("LOAD_USER", "Load user FAILURE, getting Firebase auth user");

            FirebaseUser fbUser = auth.getCurrentUser();
            if(fbUser == null){
                Log.d("LOAD_USER", "FAILED to get Firebase auth user");
                return;
            }
            user.setValue(new User(fbUser, null));

        }

        user.getValue().id = auth.getCurrentUser().getEmail()
                .replace("@", "_")
                .replace(".", "_");

        database.child("accounts").child(user.getValue().id).child("parkingLotId")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.getValue() == null) return;

                            user.getValue().parkingLotId = snapshot.getValue().toString();
                            parkingLotId.setValue(snapshot.getValue().toString());
                            Log.d("PARKING LOT ID LOADED", snapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //Load owner's parking lot
    public void loadLot(){
        if(user.getValue() == null) return;

        database.child("accounts").child(user.getValue().id).child("parkingLotId")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //cancel if snapshot doesn't exist
                        if(!snapshot.exists()) return;

                        String parkingLotId = snapshot.getValue().toString();
                        database.child("lotData").child(parkingLotId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!snapshot.exists()) return;

                                        String owner = snapshot.child("owner").getValue().toString();
                                        String location = snapshot.child("location").getValue().toString();
                                        String sPrice = snapshot.child("price").getValue().toString();
                                        String sNumSpaces = snapshot.child("numSpaces").getValue().toString();
                                        String lotName = snapshot.child("name").getValue().toString();
                                        String attendantId = snapshot.child("attendantId").getValue().toString();
                                        String sNumReserved = snapshot.child("spacesReserved").getValue().toString();

                                        int numSpaces = Integer.parseInt(sNumSpaces);
                                        double price = Double.parseDouble(sPrice);
                                        int intReserved = Integer.parseInt(sNumReserved);

                                        ParkingLot newLot = new ParkingLot(lotName, numSpaces, owner, location, price, attendantId, intReserved);
                                        newLot.id = snapshot.getKey();
                                        parkingLot.setValue(newLot);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //remove lot from listing
    //Called from Owner's ManageLotsActivity
    public void removeLot(){
        if(parkingLot.getValue() == null) return;
        if(parkingLotId.getValue() == null) return;

        database.child("lotData").child(parkingLotId.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;
                //TODO: Check if parking lot has reservations
                int activeReservations = Integer.parseInt(snapshot.child("spacesReserved").getValue().toString());
                if(activeReservations > 0){
                    genericErrorMsg.setValue("Cannot remove parking lot with active reservations");
                    return;
                }
                //parking lot doesn't have active reservations
                //TODO: remove parking lot from 'lotData'
                String ownerId = snapshot.child("owner").getValue().toString();
                String attendantId = snapshot.child("attendantId").getValue().toString();
                database.child("lotData").child(parkingLotId.getValue()).removeValue();

                //TODO: remove id from Owner
                database.child("accounts").child(ownerId).child("parkingLotId").removeValue();
                parkingLotId.setValue(null);
                parkingLot.setValue(null);

                //TODO: remove id from attendant
                database.child("accounts").child(attendantId).child("parkingLotId").removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Store lot data
    public void storeLotData(String lotName, String location, int numSpaces, double price, String attendantEmail, int numReserved) {
        String attendantId = attendantEmail.replace("@", "_").replace(".","_");

        //TODO: send error message to user
        //Stops user from adding an invalid attendant to their parking lot
        database.child("accounts").child(attendantId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {genericErrorMsg.setValue("Invalid attendant"); return;}
                if(!snapshot.child("userType").exists()){genericErrorMsg.setValue("Invalid attendant"); return;}
                if(!snapshot.child("userType").getValue().equals("attendant")) {
                    Log.d("ADD_ATTENDANT", "User is not attendant");
                    genericErrorMsg.setValue("Invalid attendant");
                    return;
                }
                //Attendant is valid
                Log.d("ADD_ATTENDANT", "The user is valid attendant");
                //Prevent parking lot owners from overwritting attendant's current assigned parking Lot
                database.child("accounts").child(attendantId).child("parkingLotId").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists() || snapshot.getValue() == null){
                            //Store Parking Lot data
                            database.child("accounts").child(user.getValue().id).child("parkingLotId").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    HashMap<String, String> map = new HashMap<>();

                                    map.put("owner", user.getValue().id);
                                    map.put("location", location);
                                    map.put("price", String.valueOf(price));
                                    map.put("numSpaces", String.valueOf(numSpaces));
                                    map.put("name", lotName);
                                    map.put("attendantId", attendantId);
                                    map.put("spacesReserved", String.valueOf(numReserved));
                                    ParkingLot newLot = new ParkingLot(lotName, numSpaces, user.getValue().id, location, price, attendantId, numReserved);

                                    //save lot to local
                                    parkingLot.setValue(newLot);

                                    //create new parking Lot
                                    if(!snapshot.exists()){
                                        //save key from firebase location
                                        String key = database.child("lotData").push().getKey();
                                        newLot.id = key;
                                        //Add parking lot Id to attendant
                                        database.child("accounts").child(attendantId).child("parkingLotId").setValue(key);

                                        //save lot to firebase in 'lotData'
                                        database.child("lotData").child(key).setValue(map);

                                        //save lot id to owner in 'accounts' child
                                        database.child("accounts").child(user.getValue().id).child("parkingLotId")
                                                .setValue(key);
                                    }

                                    //Update existing parking Lot
                                    else{
                                        //update parking lot data in 'lotData' child
                                        database.child("lotData").child(snapshot.getValue().toString()).setValue(map);
                                        //Add parking lot Id to attendant
                                        database.child("accounts").child(attendantId).child("parkingLotId").setValue(snapshot.getValue());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            genericErrorMsg.setValue("");
                        }
                        else {
                            Log.d("ADD_ATTENDANT", "Attendant already has a parking lot");
                            genericErrorMsg.setValue("Attendant already has a parking lot\nOr invalid attendant");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public MutableLiveData<String> getGenericErrorMsg() {
        return genericErrorMsg;
    }

    public MutableLiveData<ParkingLot> getParkingLot(){
        return parkingLot;
    }

    public MutableLiveData<Double> getBalance(){ return balance; }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public MutableLiveData<String> getAuthError(){
        //Reset message
        return authErrorMessage;
    }

    //Change current user balance in Firebase
    public void updateBalance(double newBalance){
        //TODO: remove after testing
        if(user.getValue() == null){
            Log.d("NULL USER-UPDATE-BALANCE", "User was NULL when trying to UPDATE funds. How did you do this?");
            System.exit(69);
        }
        database.child("accounts").child(user.getValue().id).child("balance")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            database.child("accounts").child(user.getValue().id).child("balance").setValue("0.00").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    database.child("accounts").child(user.getValue().id).child("balance").setValue(df.format(newBalance));
                                }
                            });
                        }

                        else {
                            database.child("accounts").child(user.getValue().id).child("balance").setValue(String.valueOf(newBalance));
                            //set user balance for view model
                            balance.setValue(newBalance);
                            user.getValue().balance = newBalance;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //Load current user balance
    public void loadBalance(){
        //TODO: remove after testing
        if(user.getValue() == null){
            Log.d("NULL USER-LOAD-BALANCE", "User was NULL when trying to LOAD funds. How did you do this?");
            System.exit(79);
        }
        database.child("accounts").child(user.getValue().id).child("balance")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            database.child("accounts").child(user.getValue().id).child("balance").setValue("0.00");
                            return;
                        }

                        double loadedBalance = 0.00;
                        //Create balance in Firebase
                        if(!snapshot.exists()){
                            database.child("accounts").child(user.getValue().id).child("balance").setValue("0.00");
                        }
                        else{
                            loadedBalance = Double.parseDouble(snapshot.getValue().toString());
                        }
                        //set user balance for view model
                        balance.setValue(loadedBalance);

                        if(user.getValue() != null) {
                            user.getValue().balance = loadedBalance;
                            Log.d("LOAD BALANCE", "User balance loaded");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void saveUserToFirebase(String type) {
        if (user.getValue() == null) return;

        //Only saves user to Firebase if the email has not been used before.
        database.child("accounts").child(user.getValue().id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    database.child("accounts").child(user.getValue().id).child("email")
                            .setValue(user.getValue().email);
                    //Add a onComplete listener and call the onAuth() method once all data has been
                    //  saved to firebase
                    database.child("accounts").child(user.getValue().id).child("userType")
                            .setValue(type).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            onAuth();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void signUp(String email, String password, String type) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.getException() instanceof FirebaseAuthUserCollisionException
                            || task.getException() instanceof FirebaseException
                            || task.getException() instanceof FirebaseAuthWeakPasswordException){

                            Log.d("FIREBASE ERROR", task.getException().getMessage());
                            authErrorMessage.setValue(task.getException().getMessage());
                            return;
                        }
                        Log.d("AUTH RESULT", task.getResult().getUser().getEmail());
                        //Save user to firebase
                        saveUserToFirebase(type);
                    }
                });
    }

    public void signIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.getException() instanceof FirebaseAuthInvalidCredentialsException
                            || task.getException() instanceof FirebaseAuthInvalidUserException
                            || task.getException() instanceof FirebaseException){

                            Log.d("FIREBASE ERROR", task.getException().getMessage());
                            authErrorMessage.setValue(task.getException().getMessage());
                            return;
                        }
                        Log.d("AUTH RESULT", task.getResult().getUser().getEmail());
                    }
                });
    }

    public void signOut() {
        auth.signOut();
    }

    //gets user info from Firebase
    private void onAuth(){
        database.child("accounts").child(user.getValue().id).child("userType")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String type = snapshot.getValue().toString();
                            user.setValue(new User(auth.getCurrentUser(), type));
//                            System.out.print("\nUser Type: " + type);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void addReservationLotId(String lotId){
        //TODO: remove after testing
        if(user.getValue() == null){
            Log.d("USER NULL", "User is NULL when trying to add reservation. How did you do this?");
            System.exit(666);
        }
        database.child("accounts").child(user.getValue().id).child("parkingLotId").setValue(lotId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadLot();
            }
        });
    }
}

