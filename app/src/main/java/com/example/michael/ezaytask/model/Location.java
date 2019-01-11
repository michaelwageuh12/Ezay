package com.example.michael.ezaytask.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    private String longtiude ;
    private String latitutde ;

    public Location(String longtiude, String latitutde) {
        this.longtiude = longtiude;
        this.latitutde = latitutde;
    }

    public String getLongtiude() {
        return longtiude;
    }

    public void setLongtiude(String longtiude) {
        this.longtiude = longtiude;
    }

    public String getLatitutde() {
        return latitutde;
    }

    public void setLatitutde(String latitutde) {
        this.latitutde = latitutde;
    }
}
