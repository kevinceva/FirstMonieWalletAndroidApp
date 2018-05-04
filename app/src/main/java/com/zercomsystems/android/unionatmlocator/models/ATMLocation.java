package com.zercomsystems.android.unionatmlocator.models;

import android.annotation.SuppressLint;

import com.google.android.gms.maps.model.LatLng;
import com.zercomsystems.android.unionatmlocator.helpers.LocationType;

/**
 * Created by oreofe on 5/13/2016.
 */
@SuppressLint("ParcelCreator")
public class ATMLocation extends Location {

    public ATMLocation(String name, String state, String address, String terminalID, LatLng position, double distance) {
        super(name, state, address, terminalID, position, LocationType.ATM, false, distance);

        this.dispensing = true;

    }

    public ATMLocation(String name, String state, String address, String terminalID, LatLng position, boolean operating) {
        super(name, state, address, terminalID, position, LocationType.ATM, operating, null);

        this.dispensing = true;
    }

    public ATMLocation(String name, String state, String address, String terminalID, LatLng position, boolean operating, Double distance) {
        super(name, state, address, terminalID, position, LocationType.ATM, operating, distance);

        this.dispensing = true;
    }


    public ATMLocation(String name, String state, String address, String terminalID, LatLng position, boolean operating, boolean dispensing) {

        super(name, state, address, terminalID, position, LocationType.ATM, operating, null);

        this.dispensing = dispensing;
    }


    public ATMLocation(String name, String state, String address, String terminalID, LatLng position, boolean operating, boolean dispensing, Double distance) {

        super(name, state, address, terminalID, position, LocationType.ATM, operating, distance);

        this.dispensing = dispensing;
    }


    public String getProblem() {
        String problem;

        if (!isOperating())
            problem = "ATM Out of Service";
        else if (!isDispensing())
            problem = "Cash Not Dispensing";
        else
            problem = "ATM In Service";

        return problem;
    }

}
