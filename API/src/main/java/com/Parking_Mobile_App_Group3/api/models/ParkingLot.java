package com.Parking_Mobile_App_Group3.api.models;

public class ParkingLot {
    public String id;
    public String name;
    public String owner;
    public String location;
    public double price;
    public String attendantId;
    public int numberParkingSpaces;
    public int numberReservedSpaces;

    public ParkingLot(String name, int numberParkingSpaces, String owner, String location,
                      double price, String attendantId, int numReserved){
        this.name = name;
        this.price = price;
        this.owner = owner;
        this.location = location;
        this.numberParkingSpaces = numberParkingSpaces;
        this.numberReservedSpaces = numReserved;
        this.attendantId = attendantId;

    }

    public void setNumberReservedSpaces(int val){
        this.numberReservedSpaces += val;
    }
}
