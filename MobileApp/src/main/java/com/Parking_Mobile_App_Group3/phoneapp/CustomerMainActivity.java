package com.Parking_Mobile_App_Group3.phoneapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.Parking_Mobile_App_Group3.api.AddFundsDialogFragment;
import com.Parking_Mobile_App_Group3.api.AddFundsDialogListener;
import com.Parking_Mobile_App_Group3.api.HideKeyboard;
import com.Parking_Mobile_App_Group3.api.models.ParkingLot;
import com.Parking_Mobile_App_Group3.api.viewmodels.ParkingLotViewModel;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CustomerMainActivity extends ActivityWithUser implements AddFundsDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        //load parking lot reservation (if one exists) and put data into activity
        userViewModel.loadLot();
        userViewModel.loadBalance();

        findViewById(R.id.customerLogout).setOnClickListener((view) -> {
            userViewModel.signOut();
        });
        //Update firebase with new funds (method in 'onDialogPositiveClick' method)
        //Add open dialog to add funds to user account
        findViewById(R.id.customerAddFunds).setOnClickListener(view -> {
            DialogFragment fundsDialogFragment = new AddFundsDialogFragment();
            fundsDialogFragment.show(getSupportFragmentManager(), "Add Funds");
        });

        //Cancel existing reservation
        findViewById(R.id.customerCancelResBtn).setOnClickListener(view ->{
            if(userViewModel.getUser().getValue() != null) {
                //Delete reservation in Firebase
                parkingLotViewModel.deleteReservation(
                        userViewModel.getParkingLotId().getValue(),
                        userViewModel.getUser().getValue().id);
                //Update activity
                TextView lotName = findViewById(R.id.customerLotNameInfo);
                TextView address = findViewById(R.id.customerLotAddressInfo);
                TextView attendantEmail = findViewById(R.id.customerLotAttendantInfo);
                lotName.setText("--------");
                address.setText("--------");
                attendantEmail.setText("--------");
            }
        });

        findViewById(R.id.findLots).setOnClickListener((view) -> {
            Intent intent = new Intent(this, CustomerFindLotActivity.class);
            startActivity(intent);
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
        userViewModel.getUser().observe(this, (user) ->{
            if(user == null){
                Intent intent = new Intent(this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //Display active parking lot data
        userViewModel.getParkingLot().observe(this, (lot) ->{
            if(lot != null){
                //update activity
                updateActivityReservationData();
            }
        });

        //Display current user balance
        userViewModel.getBalance().observe(this, (bal) ->{
            if(bal != null){
                TextView balanceText = findViewById(R.id.customerBalanceText);
                balanceText.setText(String.valueOf(bal));
            }
        });
    }

    //Update the reservation info inside the user activity
    public void updateActivityReservationData(){
        ParkingLot lot = userViewModel.getParkingLot().getValue();
        TextView lotName = findViewById(R.id.customerLotNameInfo);
        TextView address = findViewById(R.id.customerLotAddressInfo);
        TextView attendantEmail = findViewById(R.id.customerLotAttendantInfo);

        if(lot != null) {
            lotName.setText(lot.name);
            address.setText(lot.location);
            attendantEmail.setText(lot.attendantId
                    .replaceFirst("_", "@")
                    .replace("_", "."));
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog.getDialog().getCurrentFocus() != null) {
            EditText editText = dialog.getDialog().getCurrentFocus().findViewById(R.id.addFundsDialogInput);
            try {
                //Get number from dialog input
                double fundsInput = Double.parseDouble(editText.getText().toString());
                //Get current funds
                TextView balanceText = findViewById(R.id.customerBalanceText);
                double currentFunds = Double.parseDouble(balanceText.getText().toString());

                //Round double if needed
                DecimalFormat df = new DecimalFormat("0.00");
                df.setRoundingMode(RoundingMode.DOWN);
                double newBalance = Double.parseDouble(df.format(fundsInput + currentFunds));
                //Set balance text in activity
                balanceText.setText(String.valueOf(newBalance));
                //update firebase
                userViewModel.updateBalance(newBalance);

                //TODO: remove after testing
                //resets balance to 0
                if(fundsInput == .80085){
                    balanceText.setText("0.00");
                    userViewModel.updateBalance(0.00);
                }
            }
            catch (NumberFormatException ex) {
                ex.printStackTrace();
                Log.d("EXCEPTION", "Caught NumberFormatException from Dialog");
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}