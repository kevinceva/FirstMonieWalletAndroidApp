package com.ceva.ubmobile.models;

/**
 * Created by brian on 04/07/2017.
 */

import com.google.android.gms.maps.model.LatLng;

public class MapLocation {
    public String name;
    public LatLng center;
    public String datetime;
    public String service;

    @SuppressWarnings("unused")
    public MapLocation() {
    }

    public MapLocation(String name, LatLng center, String datetime, String service) {
        this.name = name;
        this.center = center;
        this.datetime = datetime;
        this.service = service;
    }

    public MapLocation(String name, double lat, double lng) {
        this.name = name;
        this.center = new LatLng(lat, lng);
    }

    public String getService() {
        return service;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getCenter() {
        return center;
    }

    public void setCenter(LatLng center) {
        this.center = center;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}