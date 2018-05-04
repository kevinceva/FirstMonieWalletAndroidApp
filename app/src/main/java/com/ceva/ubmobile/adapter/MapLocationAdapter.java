package com.ceva.ubmobile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.models.MapLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by brian on 04/07/2017.
 */

public class MapLocationAdapter extends RecyclerView.Adapter<MapLocationAdapter.MapLocationViewHolder> {
    protected HashSet<MapView> mMapViews = new HashSet<>();
    protected ArrayList<MapLocation> mMapLocations;

    public void setMapLocations(ArrayList<MapLocation> mapLocations) {
        mMapLocations = mapLocations;
    }

    @Override
    public MapLocationViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.locator_history_row, viewGroup, false);
        MapLocationViewHolder viewHolder = new MapLocationViewHolder(viewGroup.getContext(), view);

        mMapViews.add(viewHolder.mapView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MapLocationViewHolder viewHolder, int position) {
        MapLocation mapLocation = mMapLocations.get(position);

        viewHolder.itemView.setTag(mapLocation);

        viewHolder.title.setText(mapLocation.name);
        viewHolder.description.setText(mapLocation.datetime);
        viewHolder.service.setText(mapLocation.getService());

        viewHolder.setMapLocation(mapLocation);
    }

    @Override
    public int getItemCount() {
        return mMapLocations == null ? 0 : mMapLocations.size();
    }

    public HashSet<MapView> getMapViews() {
        return mMapViews;
    }

    public class MapLocationViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        public TextView title;
        public TextView description;
        public TextView service;
        public MapView mapView;
        protected GoogleMap mGoogleMap;
        protected MapLocation mMapLocation;
        private Context mContext;

        public MapLocationViewHolder(Context context, View view) {
            super(view);

            mContext = context;

            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            service = (TextView) view.findViewById(R.id.service);
            mapView = (MapView) view.findViewById(R.id.map);

            mapView.onCreate(null);
            mapView.getMapAsync(this);
        }

        public void setMapLocation(MapLocation mapLocation) {
            mMapLocation = mapLocation;

            // If the map is ready, update its content.
            if (mGoogleMap != null) {
                updateMapContents();
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            MapsInitializer.initialize(mContext);
            googleMap.getUiSettings().setMapToolbarEnabled(false);

            // If we have map data, update the map content.
            if (mMapLocation != null) {
                updateMapContents();
            }
        }

        protected void updateMapContents() {
            // Since the mapView is re-used, need to remove pre-existing mapView features.
            mGoogleMap.clear();

            // Update the mapView feature data and camera position.
            mGoogleMap.addMarker(new MarkerOptions().position(mMapLocation.center).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_agent_marker))));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mMapLocation.center, 17f);
            mGoogleMap.moveCamera(cameraUpdate);
        }

        private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

            View customMarkerView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
            ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
            markerImageView.setImageResource(resId);
            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
            customMarkerView.buildDrawingCache();
            Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
            Drawable drawable = customMarkerView.getBackground();
            if (drawable != null)
                drawable.draw(canvas);
            customMarkerView.draw(canvas);
            return returnedBitmap;
        }
    }

}