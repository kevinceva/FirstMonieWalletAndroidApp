package com.zercomsystems.android.unionatmlocator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.zercomsystems.android.unionatmlocator.models.Location;

import java.util.ArrayList;

/**
 * Created by oreofe on 5/9/2016.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder> {

    private ArrayList<Location> locationList;

    private ArrayList<Location> locationList_flt;

    private ATMLocationAdapterListnerer listnerer;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, address;
        Location location;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            address = (TextView) view.findViewById(R.id.address);
            view.setOnClickListener(this);
            //  locationType = (TextView) view.findViewById(R.id.locationType);
        }

        @Override
        public void onClick(View v) {
            listnerer.onATMClicked(v, getPosition(), location);
        }
    }

    public void add(int position, Location item) {
        locationList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Location item) {
        int position = locationList.indexOf(item);
        locationList.remove(position);
        notifyItemRemoved(position);
    }

    public void setListnerer(ATMLocationAdapterListnerer listnerer) {
        this.listnerer = listnerer;
    }

    public LocationAdapter(ArrayList<Location> locationList) {
        this.locationList = locationList;
        this.locationList_flt = new ArrayList<>();

        for (Location location : locationList) {

            // if flt already contains data dont add
            if (!containsRaw(location)) {
                locationList_flt.add(location);
            }
        }
    }

    private boolean containsRaw(Location location) {
        for (Location l : locationList_flt) {
            if (l.getPosition().longitude == location.getPosition().longitude &&
                    l.getPosition().latitude == location.getPosition().latitude) {
                l.setMultiple(true);
                location.setMultiple(true);
                return true;
            }
        }
        return false;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_location, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Location location = locationList_flt.get(position);
        String name = location.getName();
        if (name.toLowerCase().contains("atm")) {
            int j = name.toLowerCase().indexOf("atm");
            name = name.substring(0, j);
        }
        String address = location.getAddress();
        if (address.toLowerCase().contains("atm")) {
            int j = address.toLowerCase().indexOf("atm");
            address = address.substring(0, j);
        }

        String finalName;

        switch (location.getLocationType()) {

            case ATM: {
                //   finalName = location.isMultiple() ? "Multiple ATMs" : "Single ATM";
                finalName = "Union Bank ATM";
            }
            break;

            case BRANCH: {
                //    finalName = location.isMultiple() ? "Multiple Branches" : "Union Bank Branch";
                finalName = location.getName();// //"Union Bank Branch";
            }
            break;

            case SMART_BRANCH: {
                //  finalName =  location.isMultiple() ? "Multiple Smart Branches" : "Union Smart Branch";
                finalName = "Union Bank Smart Branch";
            }
            break;

            default: {
                finalName = name;
            }
            break;

        }

        holder.name.setText(location.getDistance() + "km" + " - " + finalName);
        holder.address.setText(address.toUpperCase());
        holder.location = location;
    }

    @Override
    public int getItemCount() {
        // return locationList.size();
        return locationList_flt.size();
    }

    public interface ATMLocationAdapterListnerer {
        void onATMClicked(View v, int position, Location location);
    }
}

