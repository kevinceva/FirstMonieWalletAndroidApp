package com.zercomsystems.android.unionatmlocator.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.UBNApplication;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;
import com.zercomsystems.android.unionatmlocator.adapters.LocationAdapter;
import com.zercomsystems.android.unionatmlocator.helpers.Constants;
import com.zercomsystems.android.unionatmlocator.helpers.RError;
import com.zercomsystems.android.unionatmlocator.helpers.RResponse;
import com.zercomsystems.android.unionatmlocator.models.ATM;
import com.zercomsystems.android.unionatmlocator.transports.LocationTransport;
import com.zercomsystems.android.unionatmlocator.models.Location;
import com.zercomsystems.android.unionatmlocator.helpers.LocationType;
import com.zercomsystems.android.unionatmlocator.helpers.PermissionUtils;
import com.zercomsystems.android.unionatmlocator.views.CustomDialog;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivityATM extends ATMBaseActivity
        implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private MaterialDialog acquireDialog;

    private MaterialDialog progressDialog;

    private ArrayList<Location> old_data = new ArrayList<>();

    private Location mDestinationLocation;

    private ArrayList<Location> mLocationList = new ArrayList<>();

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    // Google Map
    private GoogleMap googleMap;

    private LocationType mLocationType;

    private LatLngBounds.Builder builder = new LatLngBounds.Builder();

    private BottomSheetBehavior mBottomSheetBehavior;

    private HashMap<Marker, Location> mLocationHashMap = new HashMap<Marker, Location>();

    private boolean mRequestingLocationUpdates = true;
    private android.location.Location mCurrentLocation;
    private boolean hasCurrentLocation = false;

    private volatile boolean lock;


    //views
    @BindView(R.id.bottom_sheet_top_bar)
    public LinearLayout mBottomSheetTopBarView;

    @BindView(R.id.bottom_sheet_recycler_view)
    public RecyclerView mRecyclerView;

    @BindView(R.id.bottom_sheet)
    public View mBottomSheet;

    @BindView(R.id.fab)
    public FloatingActionButton mFab;

    @BindView(R.id.locationLabel)
    public TextView mLocationLabel;
    private boolean mNotFoundAlert = false;

    private boolean permissiongranted = false;


    private CustomDialog screenMsg;

    private Marker mCurrentMarker;
    private boolean user_seen;

    @Override
    public int getLayoutId() {
        return R.layout.activity_map_locator;
    }

    private AssentCallback callback = new AssentCallback() {
        @Override
        public void onPermissionResult(PermissionResultSet result) {
            Log.d(TAG, "AssentCallback: result");
            // Permission granted or denied
            boolean status = result.allPermissionsGranted();
            if (status) {

                initiateMap();
                mPermissionDenied = true;
            } else {
                Toast.makeText(MapActivityATM.this, "GPS required to view services around you", Toast.LENGTH_LONG).show();
                MapActivityATM.this.finish();
            }
        }
    };


    private BottomSheetBehavior.BottomSheetCallback mSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            Log.d(TAG, "mBottomSheetBehavior: BottomSheetCallback");
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                mFab.show();
                mBottomSheet.scrollTo(0, 0);

            } else {
                if (mCurrentLocation == null)
                    ensureLocationPermission();

                mFab.hide();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        mGoogleApiClient.connect();
        super.onStart();
        UBNApplication.getBus().register(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        //Bind all views
        ButterKnife.bind(this);

        Assent.setActivity(this, this);
//        if (!Assent.isPermissionGranted(Assent.ACCESS_FINE_LOCATION)) {
//            Assent.requestPermissions(callback, 124, Assent.ACCESS_FINE_LOCATION);
//        }

        progressDialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .cancelable(false)
                .typeface(Typeface.createFromAsset(getAssets(), getString(R.string.font_medium)), Typeface.createFromAsset(getAssets(), getString(R.string.font_medium)))
                .content("Retrieving List..").build();

        acquireDialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .cancelable(false)
                .typeface(Typeface.createFromAsset(getAssets(), getString(R.string.font_medium)), Typeface.createFromAsset(getAssets(), getString(R.string.font_medium)))
                .content("Acquiring your Location..").build();

        mLocationType = (LocationType) getIntent().getSerializableExtra("location_type");
        String pointer = getIntent().getAction();

        if (!TextUtils.isEmpty(pointer)) {

            switch (pointer) {

                case "com.zercomsystems.android.unionatmlocator.VIEW_ATMS": {
                    mLocationType = LocationType.ATM;
                    break;
                }

                case "com.zercomsystems.android.unionatmlocator.VIEW_BRANCHES": {
                    mLocationType = LocationType.BRANCH;
                    break;
                }

            }

        }

        switch (mLocationType) {

            case BRANCH:
                mLocationLabel.setText("LIST BRANCHES");
                break;

            case SMART_BRANCH:
                mLocationLabel.setText("LIST SMART BRANCHES");
                break;

            default:
                mLocationLabel.setText("LIST ATMs");
                break;
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mFab: OnClickListener");
                animateToLocation(mCurrentLocation, 16);
            }
        });

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setPeekHeight((int) UBNApplication.convertDpToPixel(50));

        mBottomSheetBehavior.setBottomSheetCallback(mSheetCallback);


        mBottomSheetTopBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        //Initialize map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initiateMap();
    }


    private void showACQ() {
        Log.d(TAG, "showACQ");
        try {
            acquireDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void hideACQ() {
        Log.d(TAG, "hideACQ");
        try {
            acquireDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
        UBNApplication.getBus().unregister(this);
    }


    public void showDialog() {
        Log.d(TAG, "showDialog");
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideDialog() {
        Log.d(TAG, "hideDialog");
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.w(TAG, "Permission disabled");
            if (!Assent.isPermissionGranted(Assent.ACCESS_FINE_LOCATION)) {
                Assent.requestPermissions(callback, 124, Assent.ACCESS_FINE_LOCATION);
            }

            return;
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        createLocationRequest();

        //addLocationsToMarkers
        initLocation(mLocationType);

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }


    protected void startLocationUpdates() throws SecurityException {
        Log.d(TAG, "startLocationUpdates");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    private void ensureLocationPermission() {
        Log.d(TAG, "ensureLocationPermission");
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

    }


    private void buildAlertMessageNoGps() {
        Log.d(TAG, "buildAlertMessageNoGps");
        final CustomDialog alert = new CustomDialog(this);
        alert.setMessage("Your GPS is disabled.\nThis application requires the service to work effectively, Do you want to enable it?");
        alert.setAlertType(CustomDialog.ENQUIRY);
        alert.setActionButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        alert.setNeutralButton("No", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                finish();

            }
        });

        try {
            alert.showDialog();
            setOnScreenFocus(alert);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOnScreenFocus(CustomDialog alert) {
        screenMsg = alert;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        if (LOCATION_PERMISSION_REQUEST_CODE == requestCode) {
            if (resultCode != RESULT_OK) {
                final CustomDialog alert = new CustomDialog(MapActivityATM.this);
                alert.setMessage("Enable GPS to use this feature");
                alert.setAlertType(CustomDialog.ERROR);
                alert.setActionButton("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        MapActivityATM.this.finish();
                    }
                });

                try {
                    alert.showDialog();
                    setOnScreenFocus(alert);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void initiateMap() {
        Log.d(TAG, "initiateMap");
        showACQ();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();


    }

    protected void createLocationRequest() {
        Log.d(TAG, "createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                Log.d(TAG, "setResultCallback: Result");
                final Status status = result.getStatus();
                final LocationSettingsStates settings = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MapActivityATM.this,
                                    LOCATION_PERMISSION_REQUEST_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        break;
                }
            }
        });


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(android.location.Location location) {
        //hideACQ();
        Log.d(TAG, "onLocationChanged: Location");

        mCurrentLocation = location;

        if (!hasCurrentLocation) {
            moveToCurrentPosition();
        }

        if (mLocationList != null && mLocationList.isEmpty()) {
            initLocation(mLocationType);
            hasCurrentLocation = true;
        }

    }



    @Override

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: googleMap");
        this.googleMap = googleMap;
        googleMap.setTrafficEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult");

        Assent.handleResult(permissions, grantResults);

//        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
//            return;
//        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            initiateMap();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }


    @Override
    protected void onResumeFragments() {
        Log.d(TAG, "onResumeFragments");
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        Assent.setActivity(this, this);

        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }


    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        Log.d(TAG, "showMissingPermissionError");
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void animateToLocation(android.location.Location location, float zoom) {
        Log.d(TAG, "animateToLocation: location, zoom");
        if (location == null)
            ensureLocationPermission();

        else {
            final LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            builder.include(currentLatLng);
            final LatLngBounds bounds = builder.build();
            final int padding = 25;
            CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            googleMap.animateCamera(update);

        }

    }

    public void moveToCurrentPosition() {
        Log.d(TAG, "moveToCurrentPosition");
        //Add current position to marker
        if (mCurrentMarker != null) {
            mCurrentMarker.remove();

        }
        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        MarkerOptions options = new MarkerOptions()
                .position(currentLatLng)
                .title("You are Here");
        mCurrentMarker = googleMap.addMarker(options);
        // Move the camera instantly to the current position with zoom of 15.
        animateToLocation(mCurrentLocation, 16);

    }

    private void addLocationToMarker(ArrayList<Location> locationArrayList) {
        Log.d(TAG, "addLocationToMarker: locationList");
        old_data.clear(); // new code
        googleMap.clear(); // new code
        for (Location location : locationArrayList) {

            // create marker
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location.getPosition())
                    .title(location.getName())
                    .icon(location.getIcon(mLocationType))
                    .snippet(location.getAddress());

            // adding marker
            Marker marker = googleMap.addMarker(markerOptions);

            old_data.add(location); // new code
            //add it to hashMap too
            mLocationHashMap.put(marker, location);


        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        if (isFinishing())
            Assent.setActivity(this, null);
    }

    private void initLocation(final LocationType locationType) {
        Log.d(TAG, "initLocation: locationType");

//        final AppCompatActivity activity = this;

        if (mCurrentLocation == null) {

            Log.w(TAG, "Location is currently null");
            return;
        } else {
            Log.w(TAG, "Location is has a value");
        }


        hideACQ();

        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        if (screenMsg == null || !screenMsg.isOnScreen()) {
            if (Constants.DEBUG)
                System.err.println("called this block of code on showDialgog");
            showDialog();
        }

        LocationTransport.getLocationList(locationType, currentLatLng);


    }


    @Subscribe
    public void update(ArrayList<Location> locations) {
        Log.d(TAG, "getLocationList: onSuccess");

        if (screenMsg != null && screenMsg.isOnScreen()) {
            if (Constants.DEBUG)
                System.err.println("called this block of code on onSuccess");
            showDialog(); // show the loading screen if not there
            screenMsg.dismiss(); // remove Error dialog from display
            screenMsg = null; // remove Error dialog instance
        }

        if (locations.size() == 0) {
            return;
        }

        mLocationList = locations;

        builder = new LatLngBounds.Builder();
        builder.include(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        bigg:
        for (int i = 0; i < locations.size(); i++) {
            builder.include(locations.get(i).getPosition());

            if (mLocationType == LocationType.ATM) {
                if (i > (locations.size() * 0.265) && locations.size() > 10) {
                    break bigg;
                }
            } else if (i > (locations.size() * 0.28) || i > 5) {
                //for other services
                break bigg;
            }
        }

        try {

            if (!locations.get(0).getAddress().equalsIgnoreCase(old_data.get(0).getAddress())) {
                user_seen = false;
                addLocationToMarker(locations);

                if (Constants.DEBUG)
                    Log.w("LocationTransport", ".getLocationList() update true");

            } else {
                if (Constants.DEBUG)
                    Log.w("LocationTransport", ".getLocationList() update false");
            }

        } catch (Exception e) {
            e.printStackTrace();
            addLocationToMarker(locations);
        }


        //setEvents
        setEvents();
        hideDialog();

        if (!user_seen) {
            moveToCurrentPosition(); //* original
            user_seen = true;
        }

        setRecyclerView(locations);
    }

    @Subscribe
    public void update(RError error) {
        Log.d(TAG, "getLocationList: onFailure");

        hideDialog();
        if (screenMsg != null && screenMsg.isOnScreen()) {
            if (Constants.DEBUG)
                System.err.println("called this block of code on onFailure");
            return; // there is something on screen please return
        }

        if (error.code == RResponse.RESOURCE_NOT_FOUND) {

            //lets track this alert
            if (mNotFoundAlert)
                return;

            mNotFoundAlert = true;

            final CustomDialog alert = new CustomDialog(this);
            alert.setMessage("Oops, Service unavailable, please check back later");
            alert.setAlertType(CustomDialog.ERROR);
            alert.setActionButton("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    finish();
                }
            });

            try {
                alert.showDialog();
                setOnScreenFocus(alert);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (error.code == RResponse.INTERNET_UNAVAILABLE) {

            final CustomDialog alert = new CustomDialog(this);
            alert.setMessage("Oops, There Is Currently No Internet Connection Available.");
            alert.setAlertType(CustomDialog.ERROR);
            alert.setActionButton("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    finish();
                }
            });

            try {
                alert.showDialog();
                setOnScreenFocus(alert);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            final CustomDialog alert = new CustomDialog(this);
            alert.setMessage("An Unexpected Error occurred, Confirm you have a working Internet connection");
            alert.setAlertType(CustomDialog.ERROR);
            alert.setActionButton("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    finish();
                }
            });

            try {
                alert.showDialog();
                setOnScreenFocus(alert);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    // new code
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");

        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            //mBottomSheet BottomSheetBehavior
            // we want to scroll down
            mBottomSheet.scrollTo(0, 0);
        } else
            super.onBackPressed();

    }


    private void setEvents() {
        Log.d(TAG, "setEvents");

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                // Getting view from the layout file
                Log.d(TAG, "setInfoWindowAdapter: marker");

                View v = getLayoutInflater().inflate(R.layout.map_info_window, null);

                TextView title = (TextView) v.findViewById(R.id.map_title);
                TextView address = (TextView) v.findViewById(R.id.map_snippet);
                TextView distance = (TextView) v.findViewById(R.id.map_distance);
                LinearLayout distanceView = (LinearLayout) v.findViewById(R.id.map_distance_view);
                LinearLayout rightImage = (LinearLayout) v.findViewById(R.id.map_arrow_view);

                if (marker.getSnippet() != null) {

                    Location location = mLocationHashMap.get(marker);


                    String name = location.getName();

                    String finalName;

                    switch (location.getLocationType()) {

                        case ATM: {
                            //finalName = location.isMultiple() ? "Multiple ATMs" : "Single ATM";
                            finalName = "Union Bank ATM";
                        }
                        break;

                        case BRANCH: {
                            // finalName = location.isMultiple() ? "Multiple Branches" : "Union Bank Branch";
                            finalName = name;//"Union Bank Branch";
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

                    title.setText(finalName);
                    address.setText(location.getAddress());
                    distance.setText(String.valueOf(location.getDistance()));
                } else {

                    title.setText(marker.getTitle());
                    distanceView.setVisibility(View.GONE);
                    rightImage.setVisibility(View.GONE);
                    address.setVisibility(View.GONE);
                }

                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                mDestinationLocation = mLocationHashMap.get(marker);

                if (mDestinationLocation != null)
                    goToLocation(mDestinationLocation);
            }
        });

    }

    protected void setRecyclerView(final ArrayList<Location> locations) {
        Log.d(TAG, "setRecyclerView");

        LocationAdapter locationAdapter = new LocationAdapter(locations);
        mRecyclerView.setAdapter(locationAdapter);

        locationAdapter.setListnerer(new LocationAdapter.ATMLocationAdapterListnerer() {
            @Override
            public void onATMClicked(View v, int position, Location location) {
                goToLocation(location);
            }
        });

    }

    public void goToLocation(Location destinationLocation) {
        Log.d(TAG, "goToLocation");

        ArrayList<ATM> atms = new ArrayList<>();

        for (Location location : old_data) {
            if (destinationLocation.getPosition().latitude == location.getPosition().latitude &&
                    destinationLocation.getPosition().longitude == location.getPosition().longitude) {
                ATM atm = new ATM(location.getTerminalID());
                atm.setAddress(location.getAddress());
                atm.setName(location.getName());
                atm.setState(location.getState());

                atm.setLat(destinationLocation.getPosition().latitude)
                        .setLng(destinationLocation.getPosition().longitude);
                atms.add(atm);
            }
        }

        Intent intent = new Intent(MapActivityATM.this, TxATMDetailsActivityATM.class);
        //LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        //intent.putExtra("currentLatLng", currentLatLng);
        intent.putExtra("latitude", mCurrentLocation.getLatitude());
        intent.putExtra("longitude", mCurrentLocation.getLongitude());
        intent.putExtra("distance", destinationLocation.getDistance());
        intent.putExtra(ATM.class.getSimpleName(), atms);
        intent.putExtra(TxATMDetailsActivityATM.class.getSimpleName(), mLocationType);

        startActivity(intent);
    }

}





