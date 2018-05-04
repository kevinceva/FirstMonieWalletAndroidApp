package com.zercomsystems.android.unionatmlocator.models;


import java.io.Serializable;
import java.util.Locale;

/**
 * Created by oreofe on 5/17/2016.
 */
public class ATM implements Serializable {
    private String id;
    private String name;
    private String state;
    private String city;
    private String location;
    private String address;
    private double lat;
    private double lng;
    private String problem;
    private boolean isDispensing;
    private boolean isON;
    public boolean isSelected;


    public boolean isDispensing() {
        return isDispensing;
    }

    public ATM setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public ATM setLng(double lng) {
        this.lng = lng;
        return this;
    }


    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public boolean isON() {
        return isON;
    }

    public ATM setDispensing(boolean dispensing) {
        isDispensing = dispensing;
        return this;
    }

    public ATM setON(boolean ON) {
        isON = ON;
        return this;
    }

    public ATM(String id) {
        setId(id);
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }


    public String getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    public String getProblem() {
        return problem;
    }

    public ATM setId(String id) {
        this.id = id;
        return this;
    }

    public ATM setName(String name) {
        this.name = name;
        return this;
    }

    public ATM setState(String state) {
        this.state = state;
        return this;
    }

    public ATM setCity(String city) {
        this.city = city;
        return this;
    }

    public ATM setLocation(String location) {
        this.location = location;
        return this;
    }

    public ATM setAddress(String address) {
        this.address = address;
        return this;
    }


    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "terminal: %s,  Name: %s, Address: %s", id, name, address);
    }


    public ATM setProblem(String problem) {
        this.problem = problem;
        return this;
    }

}
