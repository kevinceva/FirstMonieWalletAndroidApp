package com.ceva.ubmobile.core.ui.maps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.ATMLocation;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.NumberUtilities;
import com.ceva.ubmobile.utils.TinyDB;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ATMLocator extends BaseActivity implements
        GoogleMap.OnMarkerClickListener, NavigationView.OnNavigationItemSelectedListener, OnLocationUpdatedListener,
        OnActivityUpdatedListener, OnMapReadyCallback {

    public static final String KEY_ATM = "atmlocations";
    private static final int LOCATION_PERMISSION_ID = 1001;
    UBNSession session;
    SmartLocation smartLocation;
    boolean isLocationSet = false;
    int countSend = 0;
    List<Marker> locationList = new ArrayList<>();
    ArrayList<ATMLocation> atmLocationList = new ArrayList<ATMLocation>();
    ArrayList<ATMLocation> nearbyATMs = new ArrayList<ATMLocation>();
    TinyDB tinyDB;
    boolean isFirstTime = true;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    private GoogleMap mMap;
    private LocationGooglePlayServicesProvider provider;
    private Location mLocation = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atmlocator);
        setToolbarTitle("ATM + Branch Locator");
        session = new UBNSession(this);
        tinyDB = new TinyDB(this);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void startLocation() {

        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        smartLocation = new SmartLocation.Builder(this).logging(true).build();

        smartLocation.location(provider).start(this);
        smartLocation.activity().start(this);
    }

    public void onResume() {
        super.onResume();
        if (isFirstTime) {
            isFirstTime = false;
        } else {
            startLocation();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                final LatLng latLng = marker.getPosition();
                Log.debug("marker click called");
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", latLng.latitude, latLng.longitude, marker.getTitle());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoContents(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoWindow(final Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.atm_info_window, null);

                // Getting the position from the marker
                final LatLng latLng = arg0.getPosition();

                // Getting reference to the TextView to set latitude
                TextView address = v.findViewById(R.id.address);

                // Getting reference to the TextView to set longitude
                TextView distance = v.findViewById(R.id.distance);

                // Setting the latitude
                address.setText(arg0.getTitle());

                // Setting the longitude
                distance.setText(arg0.getSnippet());

                // Returning the view containing InfoWindow contents
                return v;
            }
        });
        if (isLocationServicesAvailable(this)) {

            startLocation();
        } else {
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.orange)
                    .setButtonsColorRes(R.color.midnight_blue)
                    .setIcon(R.drawable.ic_warning_white_48)
                    .setTitle("Location disabled")
                    .setMessage("Please enable location in settings to enable us provide you with agents around you.")
                    .setPositiveButton("Go To Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_PERMISSION_ID);
                        }
                    })
                    .setNegativeButton("Cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onBackPressed();
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case LOCATION_PERMISSION_ID:
                    startLocation();
                    break;
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {
        if (!isLocationSet) {
            startLocation();
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        if (location != null) {

            if (!isLocationSet) {
                mLocation = location;
            } else {

                Log.debug("location count " + countSend);
            }
            isLocationSet = true;
            dismissProgress();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);
            mMap.animateCamera(zoom);

            if (countSend == 0) {
                //sendLocationData(session.getUserName(), mLocation);
            }
            countSend++;
            if (tinyDB.getListObject(KEY_ATM, ATMLocation.class).size() == 0) {
                fetchLocations(location);
            } else {
                //fetchLocations(location);
                atmLocationList = tinyDB.getListATM(KEY_ATM);
                setATMLocations(150000, "atm");
            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if (i == R.id.rd_atm) {
                        setATMLocations(150000, "atm");
                    } else {
                        setATMLocations(150000, "branch");
                    }
                }
            });
        } else {
            showLoadingProgress();
        }
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custome_marker_circle, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.profile_image);
        //markerImageView.setImageResource(resId);
        Picasso.with(this).load(resId).into(markerImageView);
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

    private void fetchLocations(final Location mLocation) {
        showLoadingProgress();
        String urlparam = "atmlocationlist";
        String url = urlparam;
        try {
            if (SecurityLayer.isSecure) {
                url = SecurityLayer.beforeLogin("dummy", UUID.randomUUID().toString(), urlparam, this);
            } else {
                url = urlparam;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("atmlocationlist", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    if (SecurityLayer.isSecure) {
                        obj = SecurityLayer.decryptBeforeLogin(obj, getApplicationContext());
                    }

                    JSONArray array = new JSONArray(obj.optString("atmlocdata"));

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject un = array.getJSONObject(i);

                            ATMLocation location = new ATMLocation(un.optString(ATMLocation.KEY_LOCATION), un.optString(ATMLocation.KEY_BANKADDR),
                                    un.optString(ATMLocation.KEY_LATITUDE), un.optString(ATMLocation.KEY_LONGITUDE));

                            atmLocationList.add(location);

                        }
                        if (Log.DEBUG_MODE) {
                            ATMLocation location = new ATMLocation("Nairobi", "Ring Rd Parklands",
                                    "-1.2627149", "36.8001435");
                            atmLocationList.add(location);
                        }

                        tinyDB.putListATM(KEY_ATM, atmLocationList);

                        if (setATMLocations(10000, "atm").size() == 0) {
                            if (setATMLocations(20000, "atm").size() == 0) {
                                setATMLocations(300000, "atm");
                            }
                        }

                    } else {
                        ResponseDialogs.warningDialogLovely(ATMLocator.this, "Error", obj.optString(Constants.KEY_MSG));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //SecurityLayer.generateToken(getApplicationContext());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                //SecurityLayer.generateToken(getApplicationContext());
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    private List<ATMLocation> setATMLocations(int radius, String type) {
        nearbyATMs.clear();
        int jx = 0;
        for (int k = 0; k < atmLocationList.size(); k++) {
            ATMLocation location1 = atmLocationList.get(k);
            if (!location1.getLatitude().contains(",")) {
                float distance = NumberUtilities.distance((float) mLocation.getLatitude(),
                        (float) mLocation.getLongitude(),
                        Float.valueOf(location1.getLatitude()), Float.valueOf(location1.getLongitude()));

                if (distance <= radius) {
                    Log.debug("distance from atm" + distance);

                    if (jx == 50) {
                        break;
                    }
                    LatLng latlng = new LatLng(Float.valueOf(location1.getLatitude()), Float.valueOf(location1.getLongitude()));
                    if (type.equalsIgnoreCase("atm")) {
                        mMap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .title(location1.getBankAddress())
                                .snippet(String.valueOf(NumberUtilities.getWithDecimal((double) (distance / 1000))))
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_full_atm))));
                    } else {
                        mMap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .title(location1.getBankAddress())
                                .snippet(String.valueOf(NumberUtilities.getWithDecimal((double) (distance / 1000))))
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_open_account))));
                    }
                    nearbyATMs.add(location1);
                    jx++;
                }
            }
        }
        return nearbyATMs;
    }
}