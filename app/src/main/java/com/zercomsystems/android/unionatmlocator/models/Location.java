package com.zercomsystems.android.unionatmlocator.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.ceva.ubmobile.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.zercomsystems.android.unionatmlocator.helpers.LocationType;

import java.util.Comparator;

/**
 * Created by oreofe on 5/9/2016.
 */
public class Location implements Parcelable {

    protected String name;
    protected String address;
    protected String state;
    protected String terminalID;
    protected LatLng position;
    protected boolean operating;
    protected String custodianName;
    protected String custodianEmail;
    protected String custodianNumber;
    protected double distance;
    protected boolean dispensing;
    protected LocationType locationType;
    private boolean isMultiple;

    public static Comparator<Location> SORT_DISTANCE
            = new Comparator<Location>() {
        public int compare(Location a, Location b) {
            //  String fn = ""+a.distance;
            //  String sn = ""+b.distance;

            if (a.distance < b.distance)
                return -1;
            else if (a.distance > b.distance) {
                return 1;
            } else
                return 0;
            //ascending order
            //return fn.compareTo(sn);
        }
    };


    public String getCustodianEmail() {
        return custodianEmail;
    }

    public String getCustodianName() {
        return custodianName;
    }

    public String getCustodianNumber() {
        return custodianNumber;
    }

    public Location setCustodianEmail(String custodianEmail) {
        this.custodianEmail = custodianEmail;
        return this;
    }


    public Location setMultiple(boolean multiple) {
        isMultiple = multiple;
        return this;
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    public Location setCustodianNumber(String custodianNumber) {
        this.custodianNumber = custodianNumber;
        return this;
    }

    public Location setCustodianName(String custodianName) {
        this.custodianName = custodianName;
        return this;
    }

    public Location(String name, String state, String address, String terminalID, LocationType type) {
        this(name, state, address, terminalID, null, type, true, null);
    }

    public Location(String name, String state, String address, String terminalID, LatLng position, LocationType type) {
        this(name, state, address, terminalID, position, type, true, null);
    }

    public Location(String name, String state, String address, String terminalID, LatLng position, LocationType type, boolean operating) {
        this(name, state, address, terminalID, position, type, operating, null);
    }

    public Location(String name, String state, String address, String terminalID, LatLng position, LocationType type, boolean operating, Double distance) {
        this.name = name;
        this.address = address.toUpperCase();
        this.state = state;
        this.position = position;
        this.locationType = type;
        this.operating = operating;

        if (distance != null)
            this.distance = distance;

        if (terminalID != null)
            this.terminalID = terminalID;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getState() {
        return state;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public LocationType getLocationType() {
        return this.locationType;
    }

    public void setLocationType(LocationType type) {
        this.locationType = type;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public LatLng getPosition() {
        return position;
    }


    public BitmapDescriptor getIcon() {
        int resID;

        Log.v("0TAG", locationType.name() + " - " + locationType.getName());

        switch (locationType) {
            case BRANCH:
                resID = R.drawable.ic_branch;
                break;
            case SMART_BRANCH:
                resID = R.drawable.ic_smart_branch_main;
                break;
            default:
                resID = R.drawable.ic_atm;
                break;
        }

        return BitmapDescriptorFactory.fromResource(resID);
    }

    public BitmapDescriptor getIcon(LocationType type) {
        int resID;

        switch (type) {
            case BRANCH:
                resID = R.drawable.ic_branch;
                break;
            case SMART_BRANCH:
                resID = R.drawable.ic_smart_branch_main;
                break;
            default:
                resID = R.drawable.ic_atm;
                break;
        }

        return BitmapDescriptorFactory.fromResource(resID);
    }

    public boolean isOperating() {
        return operating;
    }

    public void setOperating(boolean operating) {
        this.operating = operating;
    }

    public boolean isDispensing() {
        return dispensing;
    }

    public void setDispensing(boolean dispensing) {
        this.dispensing = dispensing;
    }


    public double getDistance() {
        Log.v("models.Location", String.valueOf(distance));
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }


    public void getDistanceFromGoogle() {
        //  GeoApiContext context = new GeoApiContext().setApiKey("......0 ");
    }


    //parcel part
    public Location(Parcel in) {

        this.name = in.readString();
        this.address = in.readString();
        this.terminalID = in.readString();
        this.custodianName = in.readString();
        this.custodianEmail = in.readString();
        this.custodianNumber = in.readString();
        this.distance = in.readDouble();
        this.position = in.readParcelable(LatLng.class.getClassLoader());
        this.locationType = LocationType.valueOf(in.readString());
        this.operating = in.readByte() != 0x00;
        this.operating = in.readByte() != 0x00;
        this.isMultiple = in.readByte() != 0x00;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address.toUpperCase());
        dest.writeString(terminalID);
        dest.writeString(custodianName);
        dest.writeString(custodianEmail);
        dest.writeString(custodianNumber);
        dest.writeDouble(distance);
        dest.writeParcelable(position, flags);
        dest.writeString(locationType.name());
        dest.writeByte((byte) (operating ? 0x01 : 0x00));
        dest.writeByte((byte) (dispensing ? 0x01 : 0x00));
        dest.writeByte((byte) (isMultiple ? 0x01 : 0x00));

    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

}
