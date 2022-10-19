package com.Parking_Mobile_App_Group3.api;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.RecyclerView;

import com.Parking_Mobile_App_Group3.api.models.ParkingLot;
import com.Parking_Mobile_App_Group3.api.models.Reservation;

public class ReservationAdapter extends CustomAdapter<Reservation>{
    ReservationClickedListener listener;

    public ReservationAdapter(ObservableArrayList<Reservation> data, ReservationClickedListener listener) {
        super(data);
        this.listener = listener;
    }

    @Override
    protected int getLayout() {
        return R.layout.reservations_list_item;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation res = data.get(position);
        TextView resDisplay = holder.getItemView().findViewById(R.id.reservationDisplay);
        String checkedIn = "No";

        if(res.checkedIn){
            checkedIn = "Yes";
        }

        String displayString = "User ID: " + res.userId + "\nChecked In: " + checkedIn;
        resDisplay.setText(displayString);

        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(res);
            }
        });
    }




}
