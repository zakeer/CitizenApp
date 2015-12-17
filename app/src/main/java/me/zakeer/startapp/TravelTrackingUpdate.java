package me.zakeer.startapp;


import android.location.Location;

public interface TravelTrackingUpdate {
    void updateLocation(Location location ,String from);
}
