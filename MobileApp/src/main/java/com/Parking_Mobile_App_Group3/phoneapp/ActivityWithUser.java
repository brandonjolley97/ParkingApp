package com.Parking_Mobile_App_Group3.phoneapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.Parking_Mobile_App_Group3.api.models.ParkingLot;
import com.Parking_Mobile_App_Group3.api.viewmodels.ParkingLotViewModel;
import com.Parking_Mobile_App_Group3.api.viewmodels.UserViewModel;

public abstract class ActivityWithUser extends AppCompatActivity{
    protected UserViewModel userViewModel;
    protected ParkingLotViewModel parkingLotViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        parkingLotViewModel = new ViewModelProvider(this).get(ParkingLotViewModel.class);
        //Load user balance
        if(userViewModel.getUser().getValue() != null){
            userViewModel.loadBalance();
        }

    }
}