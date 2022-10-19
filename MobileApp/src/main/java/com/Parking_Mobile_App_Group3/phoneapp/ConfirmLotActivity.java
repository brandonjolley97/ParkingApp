package com.Parking_Mobile_App_Group3.phoneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.Parking_Mobile_App_Group3.api.models.ParkingLot;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class ConfirmLotActivity extends ActivityWithUser {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_lot);
        TextView lotInfo = findViewById(R.id.lotInfo);

        Bundle bundle = getIntent().getExtras();
        String lotId = bundle.getString("lotId");
        userViewModel.loadBalance();
        

        parkingLotViewModel.getLot(lotId).observe(this, lot ->{
            int freeSpaces = lot.numberParkingSpaces - lot.numberReservedSpaces;
            String lotString = "Name: " + lot.name + "\nLocation: " + lot.location +
                    "\nSpaces Available: " + freeSpaces + "/" + lot.numberParkingSpaces +"\nPrice: $" + lot.price;

            lotInfo.setText(lotString);

//            findViewById(R.id.confirmPurchase).setOnClickListener((view) -> {
//                lot.setNumberReservedSpaces(1);
//                //TODO: add observer on 'reservation' mutableLiveData from UserViewModel
//                Intent intent = new Intent(this, CustomerFindLotActivity.class);
//                startActivity(intent);
//            });
        });

        findViewById(R.id.confirmPurchase).setOnClickListener((view) -> {
            //Send error message if lot has no available spaces
            ParkingLot lot = parkingLotViewModel.getLot(lotId).getValue();
            int freeSpaces = lot.numberParkingSpaces - lot.numberReservedSpaces;
            if(freeSpaces <= 0){
                Snackbar snack = Snackbar.make(
                        view,
                        "Sorry, this parking lot has no available spaces",
                        BaseTransientBottomBar.LENGTH_LONG);

                View snackbarView = snack.getView();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                params.gravity = Gravity.TOP;
                snackbarView.setLayoutParams(params);
                snack.show();
                return;
            }
            //check if user has sufficient funds
            double usrBalance = userViewModel.getBalance().getValue();
            double lotCost = parkingLotViewModel.getLot(lotId).getValue().price;
            if(usrBalance >= lotCost) {
                //Only allow user to reserve a new parking space if they do not currently have one
                if (userViewModel.getUser().getValue().parkingLotId == null) {
                    parkingLotViewModel.getLot(lotId).getValue().setNumberReservedSpaces(1);

                    //create Reservation for customer
                    parkingLotViewModel.createReservation(lotId, userViewModel.getUser().getValue().id);

                    //Add lot id to user
                    userViewModel.addReservationLotId(lotId);

                    //Pay owner
                    parkingLotViewModel.payOwner(
                            parkingLotViewModel.getLot(lotId).getValue().price,
                            userViewModel.getUser().getValue().id,
                            parkingLotViewModel.getLot(lotId).getValue().owner
                    );
                    userViewModel.loadBalance();
                }

                //Wait for user balance to be updated before returning to customer main
                userViewModel.getBalance().observe(this, bal -> {
                    Intent intent = new Intent(this, CustomerMainActivity.class);
                    startActivity(intent);
                });
            }
            //Display message to user telling them they have insufficient funds
            else{
                Snackbar snack = Snackbar.make(
                        view,
                        "Insufficient funds",
                        BaseTransientBottomBar.LENGTH_LONG);

                View snackbarView = snack.getView();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                params.gravity = Gravity.TOP;
                snackbarView.setLayoutParams(params);
                snack.show();
            }

        });


        findViewById(R.id.returnToLotSelect).setOnClickListener((view) -> {
            Intent intent = new Intent(this, CustomerFindLotActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Display current user balance
        userViewModel.getBalance().observe(this, (bal) ->{
            if(bal != null){
                TextView balanceText = findViewById(R.id.currBalanceText);
                balanceText.setText(String.valueOf(bal));
            }
        });
    }
}