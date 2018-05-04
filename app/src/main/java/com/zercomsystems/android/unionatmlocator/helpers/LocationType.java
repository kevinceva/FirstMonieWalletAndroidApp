package com.zercomsystems.android.unionatmlocator.helpers;

/**
 * Created by oreofe on 5/9/2016.
 */
public enum LocationType {
    ATM("ATM"),
    BRANCH("Branch"),
    SMART_BRANCH("Smart Branch");

    private final String name;


    LocationType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
