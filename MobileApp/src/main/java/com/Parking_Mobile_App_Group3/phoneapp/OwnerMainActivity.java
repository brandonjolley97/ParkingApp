package com.Parking_Mobile_App_Group3.phoneapp;

import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.Parking_Mobile_App_Group3.api.AddFundsDialogFragment;
import com.Parking_Mobile_App_Group3.api.AddFundsDialogListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class OwnerMainActivity extends ActivityWithUser implements AddFundsDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_main);
        Button manageLots = findViewById(R.id.ownerManageLotBtn);

        //load parking lot (if one exists) and put data into activity
        userViewModel.loadLot();

        findViewById(R.id.ownerLogout).setOnClickListener((view) -> {
            userViewModel.signOut();
        });

        //Update firebase with new funds (method in 'onDialogPositiveClick' method)
        //Add open dialog to add funds to user account
        findViewById(R.id.ownerAddFunds).setOnClickListener(view -> {
            DialogFragment fundsDialogFragment = new AddFundsDialogFragment();
            fundsDialogFragment.show(getSupportFragmentManager(), "Add Funds");
        });

        manageLots.setOnClickListener((view) -> {
            Intent intent = new Intent(this, ManageLotsActivity.class);
            startActivity(intent);
        });
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

                TextView lotName = findViewById(R.id.ownerLotNameInfo);
                TextView address = findViewById(R.id.ownerLotAddressInfo);
                TextView price = findViewById(R.id.ownerLotPriceInfo);
                TextView attendantEmail = findViewById(R.id.ownerLotAttendantInfo);

                lotName.setText(lot.name);
                address.setText(lot.location);
                price.setText(String.valueOf(lot.price));
                attendantEmail.setText(lot.attendantId
                        .replaceFirst("_", "@")
                        .replace("_", "."));
            }
        });
        //Display current user balance
        userViewModel.getBalance().observe(this, (bal) ->{
            if(bal != null){
                TextView balanceText = findViewById(R.id.ownerBalanceText);
                balanceText.setText(String.valueOf(bal));
            }
        });
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog.getDialog().getCurrentFocus() != null) {
            EditText editText = dialog.getDialog().getCurrentFocus().findViewById(R.id.addFundsDialogInput);
            try {
                //Get number from dialog input
                double fundsInput = Double.parseDouble(editText.getText().toString());
                //Get current funds
                TextView balanceText = findViewById(R.id.ownerBalanceText);
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