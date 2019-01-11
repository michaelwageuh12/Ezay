package com.example.michael.ezaytask.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Trip {
    @SerializedName("id")
    @Expose
    private int id;

    //@SerializedName("datetime")
    //@Expose
    //private Date dateTime;

    @SerializedName("source")
    @Expose
    private Point source;

    @SerializedName("destination")
    @Expose
    private Point destination;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }*/

    public Point getSource() {
        return source;
    }

    public void setSource(Point source) {
        this.source = source;
    }

    public Point getDestination() {
        return destination;
    }

    public void setDestination(Point destination) {
        this.destination = destination;
    }
}
