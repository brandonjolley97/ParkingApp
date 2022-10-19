package com.Parking_Mobile_App_Group3.phoneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DecimalFormat;

public class CheckInCheckOutActivity extends ActivityWithUser {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_check_out);
        TextView checkInfo = findViewById(R.id.checkInfo);

        Bundle bundle = getIntent().getExtras();
        String lotId = bundle.getString("lotId");
        boolean checkedIn = bundle.getBoolean("checkedIn");
        String customerId = bundle.getString("customer");
        String status;

        if(checkedIn){
            status = "Customer is checked-in.";
        }
        else{
            status = "Customer needs to be checked in.";
        }

        findViewById(R.id.returnToAttenMain).setOnClickListener((view) -> {
            Intent intent = new Intent(this, AttendantMainActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.checkIn).setOnClickListener((view) -> {
            if(checkedIn){
                parkingLotViewModel.deleteReservation(lotId, customerId);
                Intent intent = new Intent(this, AttendantMainActivity.class);
                startActivity(intent);
            }
            else {
                parkingLotViewModel.checkInReservation(lotId, customerId);
                //Get parking lot rate
                //TODO: could be source of bugs here
                parkingLotViewModel.getLot(lotId).observe(this, lot ->{
                    if(lot != null) {
                        DecimalFormat df = new DecimalFormat("0.00");
                        double rate = .10;
                        double attendantCut = Double.parseDouble(df.format(lot.price * rate));
                        parkingLotViewModel.payAttendant(attendantCut, userViewModel.getUser().getValue().id);
                        userViewModel.loadBalance();

                        userViewModel.getBalance().observe(this, bal -> {
                            Intent intent = new Intent(this, AttendantMainActivity.class);
                            startActivity(intent);
                        });
                    }
                });
            }
        });

        parkingLotViewModel.getLot(lotId).observe(this, lot ->{
            int freeSpaces = lot.numberParkingSpaces - lot.numberReservedSpaces;
            String lotString = "Lot Name: " + lot.name + "\nLocation: " + lot.location +
                    "\nCustomer ID: " + customerId + "\nStatus: " + status;

            checkInfo.setText(lotString);

//            findViewById(R.id.confirmPurchase).setOnClickListener((view) -> {
//                lot.setNumberReservedSpaces(1);
//                //TODO: add observer on 'reservation' mutableLiveData from UserViewModel
//                Intent intent = new Intent(this, CustomerFindLotActivity.class);
//                startActivity(intent);
//            });
        });
    }
}