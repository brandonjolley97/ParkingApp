package com.Parking_Mobile_App_Group3.phoneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Parking_Mobile_App_Group3.api.HideKeyboard;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class ManageLotsActivity extends ActivityWithUser {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_lots);
        //load parking lot (if one exists) and put data into activity
        userViewModel.loadLot();

        Button saveChanges = findViewById(R.id.saveChanges);
        Button removeLot = findViewById(R.id.removeLotBtn);
        Button back = findViewById(R.id.back);
        EditText editPrice = findViewById(R.id.editPrice);
        EditText editAddress = findViewById(R.id.editAddress);
        EditText editAttendant = findViewById(R.id.editAttendant);
        EditText editSpaces = findViewById(R.id.editSpaces);
        EditText editName = findViewById(R.id.editName);

        back.setOnClickListener((view) -> {
            Intent intent = new Intent(this, OwnerMainActivity.class);
            startActivity(intent);
        });

        saveChanges.setOnClickListener((view) -> {
            String spaces = editSpaces.getText().toString();
            String price = editPrice.getText().toString();
            //Initialize number of reserved spaces to 0
            int reserved = 0;
            try {
                int iSpaces = Integer.parseInt(spaces);
                double dPrice = Double.parseDouble(price);
                userViewModel.storeLotData(editName.getText().toString(), editAddress.getText().toString(),
                        iSpaces, dPrice, editAttendant.getText().toString(), reserved );
                //Wait until the parkingLot is updated to change activity
                userViewModel.getParkingLot().observe(this, (lot)->{
                    if(lot != null){
                        Intent intent = new Intent(this, OwnerMainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            catch(NumberFormatException ex){
                Log.d("EXCEPTION", ex.getMessage());

                Snackbar snack = Snackbar.make(
                        view,
                        "Please enter valid info to form.\nDid you add the correct attendant email?",
                        BaseTransientBottomBar.LENGTH_LONG);

                View snackbarView = snack.getView();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                params.gravity = Gravity.TOP;
                snackbarView.setLayoutParams(params);
                snack.show();
            }
            userViewModel.getGenericErrorMsg().observe(this, msg ->{
                if(msg != null){
                    if(msg.equals("")) return;

                    Snackbar snack = Snackbar.make(
                            view,
                            msg,
                            BaseTransientBottomBar.LENGTH_LONG);

                    View snackbarView = snack.getView();
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    snackbarView.setLayoutParams(params);
                    snack.show();
                }
            });

        });

        removeLot.setOnClickListener(view ->{
            userViewModel.removeLot();
            //observe updates to parkingLotId, if changed to null, finish the activity
            userViewModel.getParkingLot().observe(this, (lot)->{
                if(lot == null){
                    Intent intent = new Intent(this, OwnerMainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            //Observe updates to error messages
            userViewModel.getGenericErrorMsg().observe(this, msg ->{
                if(msg != null){
                    if(msg.equals("")) return;

                    Snackbar snack = Snackbar.make(
                            view,
                            msg,
                            BaseTransientBottomBar.LENGTH_LONG);

                    View snackbarView = snack.getView();
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    snackbarView.setLayoutParams(params);
                    snack.show();
                }
            });
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        HideKeyboard.run(this);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Display active parking lot data if present
        userViewModel.getParkingLot().observe(this, (lot) ->{
            if(lot != null){
                EditText editPrice = findViewById(R.id.editPrice);
                EditText editAddress = findViewById(R.id.editAddress);
                EditText editAttendant = findViewById(R.id.editAttendant);
                EditText editSpaces = findViewById(R.id.editSpaces);
                EditText editName = findViewById(R.id.editName);

                editName.setText(lot.name);
                editAddress.setText(lot.location);
                editPrice.setText(String.valueOf(lot.price));
                editAttendant.setText(lot.attendantId
                        .replaceFirst("_", "@")
                        .replace("_", "."));
                editSpaces.setText(String.valueOf(lot.numberParkingSpaces));
            }
        });
    }
}