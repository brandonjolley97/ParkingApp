package com.Parking_Mobile_App_Group3.phoneapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Parking_Mobile_App_Group3.api.AddFundsDialogFragment;
import com.Parking_Mobile_App_Group3.api.AddFundsDialogListener;
import com.Parking_Mobile_App_Group3.api.ReservationAdapter;
import com.Parking_Mobile_App_Group3.api.models.ParkingLot;
import com.Parking_Mobile_App_Group3.api.viewmodels.ParkingLotViewModel;
import com.Parking_Mobile_App_Group3.api.viewmodels.UserViewModel;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class AttendantMainActivity extends ActivityWithUser implements AddFundsDialogListener {
    ReservationAdapter resAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_attendant_main);
        RecyclerView customerList = findViewById(R.id.customerList);

        userViewModel.loadBalance();

        userViewModel.getParkingLotId().observe(this, lotId ->{
            userViewModel.loadReservations();
            //Recycler View
           resAdapter = new ReservationAdapter(
                    userViewModel.getLotReservations(),
                    (reservation) -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("lotId", lotId);
                        bundle.putBoolean("checkedIn", reservation.checkedIn);
                        bundle.putString("customer", reservation.userId);

                        Intent intent = new Intent(this, CheckInCheckOutActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    });
            customerList.setAdapter(resAdapter);
            //TODO: testing
            System.out.println(userViewModel.getLotReservations());
        });
        customerList.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.attendantLogout).setOnClickListener((view) -> {
            userViewModel.signOut();
        });

        //Update firebase with new funds (method in 'onDialogPositiveClick' method)
        //Add open dialog to add funds to user account
        findViewById(R.id.attendantAddFunds).setOnClickListener(view -> {
            DialogFragment fundsDialogFragment = new AddFundsDialogFragment();
            fundsDialogFragment.show(getSupportFragmentManager(), "Add Funds");
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

        //Display current user balance
        userViewModel.getBalance().observe(this, (bal) ->{
            if(bal != null){
                TextView balanceText = findViewById(R.id.attendantBalanceText);
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
                TextView balanceText = findViewById(R.id.attendantBalanceText);
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