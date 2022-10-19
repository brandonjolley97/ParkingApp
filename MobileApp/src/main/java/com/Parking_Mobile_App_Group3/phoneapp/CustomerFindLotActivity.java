package com.Parking_Mobile_App_Group3.phoneapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.Parking_Mobile_App_Group3.api.LotAdapter;

public class CustomerFindLotActivity extends ActivityWithUser {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_find_lot);
        RecyclerView lotsList = findViewById(R.id.lotList);

        LotAdapter lotAdapter = new LotAdapter(
                parkingLotViewModel.getLots(), (parkingLot -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("lotId", parkingLot.id);

                    Intent intent = new Intent(this, ConfirmLotActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
        ));

        lotsList.setAdapter(lotAdapter);
        lotsList.setLayoutManager(new LinearLayoutManager(this));


        findViewById(R.id.returnToMain).setOnClickListener((view) -> {
            Intent intent = new Intent(this, CustomerMainActivity.class);
            startActivity(intent);
        });


    }
}