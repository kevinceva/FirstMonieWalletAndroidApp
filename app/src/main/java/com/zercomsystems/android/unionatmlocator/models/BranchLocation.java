package com.zercomsystems.android.unionatmlocator.models;

import android.annotation.SuppressLint;

import com.google.android.gms.maps.model.LatLng;
import com.zercomsystems.android.unionatmlocator.helpers.LocationType;

/**
 * Created by oreofe on 5/13/2016.
 */
@SuppressLint("ParcelCreator")
public class BranchLocation extends Location {


    public BranchLocation(String name, String state, String address, String terminalID, LatLng position, boolean operating) {
        super(name, state, address, terminalID, position, LocationType.BRANCH, operating);

    }

    public BranchLocation(String name, String state, String address, String terminalID, LatLng position, boolean operating, double distance) {
        super(name, state, address, terminalID, position, LocationType.BRANCH, operating, distance);

    }

    public BranchLocation(String name, String state, String address, String terminalID, LatLng position, boolean operating, double distance, LocationType type) {
        super(name, state, address, terminalID, position, type, operating, distance);
    }


}