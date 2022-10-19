package com.Parking_Mobile_App_Group3.api;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.RecyclerView;

import com.Parking_Mobile_App_Group3.api.models.ParkingLot;

public class LotAdapter extends CustomAdapter{

    LotClickedListener listener;

    public LotAdapter(ObservableArrayList<ParkingLot> data, LotClickedListener listener) {
        super(data);
        this.listener = listener;
    }

    @Override
    protected int getLayout() {
        return R.layout.lots_list_item;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParkingLot parkingLot = (ParkingLot) data.get(position);
        TextView lotDisplay = holder.getItemView().findViewById(R.id.lotDisplay);

        int freeSpaces = parkingLot.numberParkingSpaces - parkingLot.numberReservedSpaces;

        String displayString = "Name: " + parkingLot.name + "\nLocation: " + parkingLot.location +
                "\nSpaces Available: " + freeSpaces  + "\nPrice: $" + parkingLot.price;
        lotDisplay.setText(displayString);

        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(parkingLot);
            }
        });
    }


}
