package com.example.michael.ezaytask.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Point {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("long")
    @Expose
    private Double longtiude ;

    @SerializedName("lat")
    @Expose
    private Double latitutde ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongtiude() {
        return longtiude;
    }

    public void setLongtiude(Double longtiude) {
        this.longtiude = longtiude;
    }

    public Double getLatitutde() {
        return latitutde;
    }

    public void setLatitutde(Double latitutde) {
        this.latitutde = latitutde;
    }
}
