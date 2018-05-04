package com.ceva.ubmobile.core.ui.locator;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.azure.RegistrationIntentService;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.LandingPage;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.GeoAPIClient;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.ScalingUtilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestAgent extends BaseActivity implements
        GoogleMap.OnMarkerClickListener, NavigationView.OnNavigationItemSelectedListener, OnLocationUpdatedListener,
        OnActivityUpdatedListener, OnMapReadyCallback, OnMapClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";
    public static final String KEY_NAME = "customer";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_DSA_REQUEST = "dsarequest";
    public static final String KEY_COMMON_ID = "common_id";
    private static final int PLACE_PICKER_REQUEST = 2002;
    private static final int LOCATION_PERMISSION_ID = 1001;
    SmartLocation smartLocation;
    boolean isLocationSet = false;
    boolean isRequestSent = false;
    int countSend = 0;
    List<Marker> locationList = new ArrayList<>();
    LatLng custLatlng = null;
    String customer = null;
    String phoneNumber = null;
    @BindView(R.id.name)
    TextView customerName;
    @BindView(R.id.phoneNumber)
    TextView phoneTxt;
    @BindView(R.id.agentDetails)
    LinearLayout agentDetails;
    @BindView(R.id.requestBtn)
    TextView confirmRequest;
    @BindView(R.id.introduction)
    LinearLayout introduction;
    @BindView(R.id.agentRequestBtn)
    Button agentRequest;
    @BindView(R.id.agentDets)
    LinearLayout theWhitePart;
    boolean isRequestMade = false;
    boolean isRequestConfirmed = false;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    boolean isFirstTime = true;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.pulsator)
    PulsatorLayout pulsator;
    @BindView(R.id.profile_image)
    CircularImageView profile_image;
    @BindView(R.id.btnContact)
    Button btnContact;
    UBNSession session;
    @BindView(R.id.choices)
    RadioGroup services;
    String selected_service = "cashwithdrawal";
    String addressString = "";
    String common_id;
    String message;
    String previous_common = "null";
    private GoogleMap mMap;
    private LocationGooglePlayServicesProvider provider;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation = null;

    @Override
    public void onStart() {
        super.onStart();
        session = new UBNSession(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            mGoogleApiClient.connect();
            smartLocation.location(provider).stop();
            smartLocation.activity().stop();
        } catch (Exception e) {
            Log.Error(e);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            mGoogleApiClient.disconnect();
            smartLocation.location(provider).stop();
            smartLocation.activity().stop();
        } catch (Exception e) {
            Log.Error(e);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_agent);
        setToolbarTitle("Request Agent");
        ButterKnife.bind(this);
        session = new UBNSession(this);
        registerWithNotificationHubs();

        boolean isRequestInitiated = session.getBoolean(KEY_DSA_REQUEST);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        /*if(isRequestInitiated){
            common_id = session.getString(KEY_COMMON_ID);
        }else{
            common_id = UUID.randomUUID().toString();
        }*/
        //session.setString(KEY_COMMON_ID,null);
        if (session.getString(KEY_LAT) == null) {
            if (!TextUtils.isEmpty(session.getString(KEY_COMMON_ID))) {
                previous_common = session.getString(KEY_COMMON_ID);
            }
            common_id = UUID.randomUUID().toString();
            session.setString(KEY_COMMON_ID, common_id);

        } else {
            common_id = session.getString(KEY_COMMON_ID);
        }

        //session = new UBNSession(this);
        //session.setUserName("COLLINS");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        services.check(R.id.withdrawal);
        services.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.withdrawal) {
                    selected_service = "cashwithdrawal";
                } else if (i == R.id.deposit) {
                    selected_service = "cashdeposit";
                } else {
                    selected_service = "openaccount";
                }
            }
        });

        if (getIntent().getStringExtra(KEY_LAT) != null) {

            pulsator.stop();
            pulsator.setVisibility(View.GONE);
            custLatlng = new LatLng(Double.parseDouble(getIntent().getStringExtra(KEY_LAT)), Double.parseDouble(getIntent().getStringExtra(KEY_LON)));
            customer = getIntent().getStringExtra(KEY_NAME);
            phoneNumber = "+" + getIntent().getStringExtra(KEY_PHONE);
            message = getIntent().getStringExtra("message");

            session.setString(KEY_LAT, getIntent().getStringExtra(KEY_LAT));
            session.setString(KEY_LON, getIntent().getStringExtra(KEY_LON));
            session.setString(KEY_NAME, getIntent().getStringExtra(KEY_NAME));
            session.setString(KEY_PHONE, getIntent().getStringExtra(KEY_PHONE));
            session.setString("message", getIntent().getStringExtra("message"));

            customerName.setText(customer);
            phoneTxt.setText(phoneNumber);
            showAgentDetails();
            if (NetworkUtils.isConnected(RequestAgent.this)) {
                session.setBoolean(KEY_DSA_REQUEST, false);
                //sendStatus(session.getUserName(), mLocation, "CR", "USER", "Customer Accepted", session.getAccountName() + " accepted", session.getPhoneNumber(), common_id, session.getAccountName().toUpperCase(), "NA");
            } else {
                noInternetDialog();
            }
            if (message.toLowerCase().contains("cancel")) {
                session.setString(KEY_LAT, null);
            }
            //requestFields.setVisibility(View.GONE);

        } else {
            if (session.getString(KEY_LAT) != null) {
                pulsator.stop();
                pulsator.setVisibility(View.GONE);
                agentRequest.setVisibility(View.GONE);
                custLatlng = new LatLng(Double.parseDouble(session.getString(KEY_LAT)), Double.parseDouble(session.getString(KEY_LON)));
                customer = session.getString(KEY_NAME);
                phoneNumber = session.getString(KEY_PHONE);
                message = session.getString("message");

                customerName.setText(customer);
                phoneTxt.setText(phoneNumber);
                showAgentDetails();
            } else {
                showIntro();
            }
        }

        /*confirmRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLocationSet && !isRequestSent) {
                    if (NetworkUtils.isConnected(RequestAgent.this)) {
                        sendStatus(session.getUserName(), mLocation, "AU", "USER", "Customer Request", session.getAccountName() + " is requesting your services", session.getPhoneNumber(), UUID.randomUUID().toString(), session.getUserName().toUpperCase());
                    } else {
                        noInternetDialog();
                    }
                }
            }
        });*/
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_warning_white_48, getResources());
                new LovelyStandardDialog(RequestAgent.this)
                        .setTopColorRes(R.color.union_red)
                        .setButtonsColorRes(R.color.midnight_blue)
                        .setIcon(icon)
                        .setTitle("Confirmation")
                        .setMessage("Please confirm that you would you like to cancel this request.")
                        .setPositiveButton("Don't cancel", null)
                        .setNegativeButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                session.setString(KEY_COMMON_ID, null);
                                sendStatus(session.getUserName(), mLocation, "CU", "USER", "Cancelled Customer Request", session.getAccountName() + " has cancelled", session.getPhoneNumber(), previous_common, session.getAccountName().toUpperCase(), "NA");
                            }
                        })
                        .show();
            }
        });
        agentRequest.setVisibility(View.GONE);
        agentRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setBoolean(KEY_DSA_REQUEST, true);
                if (isRequestMade) {
                    //if (isRequestSent) {
                    if (isRequestSent) {

                        agentRequest.setVisibility(View.GONE);

                        if (NetworkUtils.isConnected(RequestAgent.this)) {
                            session.setBoolean(KEY_DSA_REQUEST, false);
                            session.setString(KEY_COMMON_ID, null);
                            session.setString(KEY_LAT, null);
                            sendStatus(session.getUserName(), mLocation, "CU", "USER", "Cancelled Customer Request", session.getAccountName() + " has cancelled", session.getPhoneNumber(), common_id, session.getAccountName().toUpperCase(), "NA");
                        } else {
                            noInternetDialog();
                        }

                    } else {
                        agentRequest.setVisibility(View.GONE);
                        String title = "";

                        if (NetworkUtils.isConnected(RequestAgent.this)) {
                            if (selected_service.equals("openaccount")) {
                                title = "Account Opening Request";
                            } else if (selected_service.equals("cashdeposit")) {
                                title = "Cash Deposit Request";
                            } else if (selected_service.equals("cashwithdrawal")) {
                                title = "Cash Withdrawal Request";
                            }

                            sendStatus(session.getUserName(), mLocation, "AU", "USER", title, session.getAccountName() + " is requesting your services | " + addressString, session.getPhoneNumber(), common_id, session.getAccountName().toUpperCase(), selected_service);
                        } else {
                            noInternetDialog();
                        }
                    }
                    // }
                } else {
                    if (isLocationSet) {
                        LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        try {
                            Log.debug("starting place picker");
                            startActivityForResult(builder.build(RequestAgent.this), PLACE_PICKER_REQUEST);
                        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                        if (latLng != null) {
                            //String textg = ;
                            //addressString = getCompleteAddressString(latLng);
                            //address.setText(address.getText().toString().replace("{}", addressString));
                            // LocationAddress locationAddress = new LocationAddress();
                            //LocationAddress.getAddressFromLocation(latLng.latitude, latLng.longitude,
                            //      getApplicationContext(), new GeocoderHandler());
                        }

                    }
                }

            }
        });
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(phoneNumber);
            }
        });

    }

    private void showIntro() {
        introduction.setVisibility(View.VISIBLE);
        hideAddress();
    }

    private void hideIntro() {
        introduction.setVisibility(View.GONE);
        showAddress();
    }

    private void showAddress() {
        address.setVisibility(View.VISIBLE);
    }

    private void hideAddress() {
        address.setVisibility(View.GONE);
    }


    private void hideTheWhitePart() {
        theWhitePart.setVisibility(View.GONE);
        profile_image.setVisibility(View.GONE);
        agentRequest.setVisibility(View.GONE);
    }

    private void showTheWhitePart() {
        theWhitePart.setVisibility(View.VISIBLE);
        agentDetails.setVisibility(View.VISIBLE);
        profile_image.setVisibility(View.VISIBLE);
        agentRequest.setVisibility(View.GONE);
        introduction.setVisibility(View.GONE);
    }

    private void showAgentDetails() {

        if (message.toLowerCase().contains("cancel")) {
            session.setString(KEY_LAT, null);
            session.setString(KEY_COMMON_ID, null);

            pulsator.setVisibility(View.VISIBLE);
            confirmRequest.setText("Request Cancelled");
            //pulsator.setColor(getResources().getColor(R.color.union_red));
            confirmRequest.setBackground(getResources().getDrawable(R.drawable.red_circle_white_border));
            agentRequest.setVisibility(View.GONE);
            pulsator.stop();
            confirmRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(RequestAgent.this, LandingPage.class));
                }
            });
            hideTheWhitePart();
        } else if (message.toLowerCase().contains("service")) {
            session.setString(KEY_LAT, null);
            session.setString(KEY_COMMON_ID, null);
            hideTheWhitePart();
            pulsator.setVisibility(View.VISIBLE);
            confirmRequest.setText("Thanks for using this service");
            //pulsator.setColor(getResources().getColor(R.color.union_red));
            //confirmRequest.setBackground(getResources().getDrawable(R.drawable.blue_circle_border));
            agentRequest.setVisibility(View.GONE);
            pulsator.stop();
            confirmRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(RequestAgent.this, LandingPage.class));
                }
            });
        } else {
            showTheWhitePart();
            introduction.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_locator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            Log.debug("home pressed");
            onBackPressed();
            return true;
        }
        if (id == R.id.action_history) {
            startActivity(new Intent(this, RequestHistory.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        session.setBoolean(UBNSession.KEY_LOGIN_STATUS, false);
        if (isFirstTime) {
            isFirstTime = false;
        } else {
            startLocation();

        }
        if (mMap != null) {
            mMap.clear();
        }
    }

    public void startLocation() {

        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        smartLocation = new SmartLocation.Builder(this).logging(true).build();

        smartLocation.location(provider).start(this);
        smartLocation.activity().start(this);

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
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.debug("Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.debug("Can't find style. Error: ", e.toString());
        }
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setPadding(50, 150, 50, 50);
        mMap.setMyLocationEnabled(true);
        if (getIntent().getStringExtra(KEY_LAT) != null) {
            mMap.clear();
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(custLatlng)
                    .title(customer)
                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_agent_marker))));
            marker.setTag(0);
        } else if (session.getString(KEY_LAT) != null) {
            mMap.clear();
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(custLatlng)
                    .title(customer)
                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_agent_marker))));
            marker.setTag(0);
        }
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
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void sendLocationData(final String username, final Location location) {
        // showSmoothProgress();
        pulsator.start();
        if (location == null) {

        }
        confirmRequest.setText("Finding agents...");
        String params = "gpscustlocation/" + username + "/" + location.getLatitude() + "/" + location.getLongitude() + "/" + getDate(location.getTime());
        //String url = SecurityLayer.genURLCBC("inbox",params,this);
        ApiInterface apiService = GeoAPIClient.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //hideSmoothProgress();

                try {
                    pulsator.stop();
                    pulsator.setVisibility(View.GONE);
                    confirmRequest.setText("Request Agent");
                    confirmRequest.startAnimation(AnimationUtils.loadAnimation(RequestAgent.this, R.anim.shake_animation));
                    String resp = URLDecoder.decode(response.body(), "UTF-8");
                    JSONObject obj = new JSONObject(resp);
                    if (obj.optString(Constants.KEY_CODE).equals("00")) {

                        String locstr = obj.optString("DSA_lIST");

                        JSONArray locationArray = new JSONArray(locstr);

                        int m = locationArray.length();
                        for (int i = 0; i < m; i++) {
                            JSONObject locObj = locationArray.getJSONObject(i);
                            LatLng latlng = new LatLng(locObj.optDouble("Latitude"), locObj.optDouble("Longitude"));
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(latlng)
                                    .title(locObj.optString("DsaUser"))
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_agent_marker))));
                            marker.setTag(i);
                            locationList.add(marker);
                            //mMap.addMarker(marker);
                        }
                        // if (m > 0) {
                        agentRequest.setVisibility(View.VISIBLE);
                        //} else {
                        //  ResponseDialogs.infoToActivity("Agents Not Found", "There are no agents around you at the moment. Please try again later.",RequestAgent.this,  LandingPage.class, new Bundle());
                        // }
                    } else {
                        ResponseDialogs.infoToActivity("Info", obj.optString("respdesc"), RequestAgent.this, LandingPage.class, new Bundle());

                    }

                } catch (Exception e) {
                    Log.Error(e);

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                //FirebaseCrash.log(t.toString());
                //hideSmoothProgress();

            }
        });

    }

    public void updateStatus(String lat, String lon, String name, String phone) {
        Log.debug("ubnreceiver name " + name);
        custLatlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        customer = name;
        phoneNumber = phone;

        customerName.setText(name);
        phoneTxt.setText(phone);
        showAgentDetails();
        // requestFields.setVisibility(View.GONE);
    }

    private void sendStatus(final String username, final Location location, final String status, final String type, final String title, final String message, final String mobile, final String common_id, String customer_id, String service) {
//@Path("/userrecognize/{username}/{latitude}/{longitude}/{dateandtime}/{status}/{type}/{message}/{tittle}/{mobile}/{common_id}/{customer_id}/{ostype}/{servicetype}”)
        //showSmoothProgress();
        pulsator.setVisibility(View.VISIBLE);
        pulsator.start();
        if (!status.equalsIgnoreCase("CU")) {
            confirmRequest.setText("Sending request...");
        } else {
            confirmRequest.setText("Cancelling...");
        }

        //agentRequest.setVisibility(View.GONE);
        hideTheWhitePart();
        String params = "userrecognize/" + username + "/" + location.getLatitude() + "/" + location.getLongitude() + "/" + getDate(location.getTime()) + "/" + status + "/" + type + "/" + message + "/" + title + "/" + mobile + "/" + common_id + "/" + customer_id + "/android/" + service;
        //String url = SecurityLayer.genURLCBC("inbox",params,this);
        ApiInterface apiService = GeoAPIClient.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                try {
                    String resp = URLDecoder.decode(response.body(), "UTF-8");
                    JSONObject obj = new JSONObject(resp);
                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        //showSmoothProgress();

                        if (status.equalsIgnoreCase("CU")) {
                            confirmRequest.setText("Request Cancelled");
                            //pulsator.setColor(getResources().getColor(R.color.union_red));
                            confirmRequest.setBackground(getResources().getDrawable(R.drawable.red_circle_white_border));
                            agentRequest.setVisibility(View.GONE);
                            pulsator.stop();
                            session.setString(KEY_LAT, null);
                            session.setString(KEY_COMMON_ID, null);
                            confirmRequest.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(RequestAgent.this, LandingPage.class));
                                }
                            });
                        } else if (status.equalsIgnoreCase("CR")) {
                            session.setString(KEY_LAT, null);
                            session.setString(KEY_COMMON_ID, null);
                        } else {
                            isRequestSent = true;

                            confirmRequest.setText("Waiting...");
                            agentRequest.setText("Cancel Request");
                            agentRequest.setBackgroundColor(getResources().getColor(R.color.union_red));
                            agentRequest.setVisibility(View.VISIBLE);
                            new CountDownTimer(45000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    long num = millisUntilFinished / 1000;
                                    String tim = String.valueOf(num);
                                    if (tim.length() == 1) {
                                        tim = "0" + tim;
                                    }
                                    tim = "waiting...\n00:" + tim + "s";
                                    confirmRequest.setText(tim);
                                }

                                public void onFinish() {
                                    confirmRequest.setText("waiting...");
                                    String text = "There are no agents available at the moment. Would you like to wait or cancel the request?";
                                    new LovelyStandardDialog(RequestAgent.this)
                                            .setTopColorRes(R.color.colorPrimary)
                                            .setButtonsColorRes(R.color.midnight_blue)
                                            .setIcon(R.drawable.ic_bulb_white)
                                            .setTitle("INFO")
                                            .setMessage(text)
                                            .setPositiveButton("WAIT", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                }
                                            })
                                            .setNegativeButton("CANCEL", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    sendStatus(session.getUserName(), mLocation, "CU", "USER", "Cancelled Customer Request", session.getAccountName() + " has cancelled", session.getPhoneNumber(), common_id, session.getAccountName().toUpperCase(), "NA");
                                                }
                                            })
                                            .show();

                                }
                            }.start();
                        }
                    } else if (obj.optString(Constants.KEY_CODE).equals("02")) {
                        session.setString(KEY_COMMON_ID, null);
                        session.setString(KEY_LAT, null);
                        new LovelyStandardDialog(RequestAgent.this)
                                .setTopColorRes(R.color.colorPrimary)
                                .setButtonsColorRes(R.color.midnight_blue)
                                .setIcon(R.drawable.ic_bulb_white)
                                .setTitle("INFO")
                                .setMessage(obj.optString(Constants.KEY_MSG))
                                .setPositiveButton("Close", null)
                                .setOnButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        onBackPressed();
                                    }
                                })
                                .show();
                    } else {
                        session.setString(KEY_COMMON_ID, null);
                        session.setString(KEY_LAT, null);
                        warningDialog(obj.optString(Constants.KEY_MSG));
                    }

                } catch (Exception e) {
                    session.setString(KEY_COMMON_ID, null);
                    session.setString(KEY_LAT, null);
                    Log.Error(e);
                    ResponseDialogs.warningDialogLovelyToActivity(RequestAgent.this, "Error", getString(R.string.error_server), LandingPage.class);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                session.setString(KEY_COMMON_ID, null);
                session.setString(KEY_LAT, null);
                //FirebaseCrash.report(t);
                ResponseDialogs.warningDialogLovelyToActivity(RequestAgent.this, "Error", getString(R.string.error_server), LandingPage.class);
                // hideSmoothProgress();

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {

    }

    @Override
    public void onLocationUpdated(Location location) {
        if (location != null) {

            isLocationSet = true;
            mLocation = location;

            dismissProgress();

            CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
            if (getIntent().getStringExtra(KEY_LAT) != null) {
                mMap.clear();
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(custLatlng)
                        .title(customer)
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_agent_marker))));
                marker.setTag(0);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(custLatlng));
                mMap.animateCamera(zoom);

            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

                mMap.animateCamera(zoom);
                if (session.getString(KEY_LAT) == null) {
                    if (countSend == 0) {
                        sendLocationData(session.getUserName(), mLocation);
                    }
                }
                countSend++;
            }

        } else {
            showLoadingProgress();
        }
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.profile_image);
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

    private String getCompleteAddressString(final LatLng latLng) {
        pulsator.setVisibility(View.VISIBLE);
        pulsator.start();
        confirmRequest.setText("Obtaining address...");
        agentRequest.setVisibility(View.GONE);
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null) {

                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(",");
                }
                pulsator.stop();
                agentRequest.setVisibility(View.VISIBLE);
                agentRequest.setText("Confirm Request");
                isRequestMade = true;
                pulsator.setVisibility(View.GONE);
                hideIntro();
                strAdd = "" + strReturnedAddress.toString();
                Log.debug("My Current location address", "" + strReturnedAddress.toString());
                return strAdd;
            } else {
                Log.debug("My Current location address", "No Address returned!");
                //getCompleteAddressString(latLng);
                pulsator.stop();
                agentRequest.setVisibility(View.VISIBLE);
                agentRequest.setText("Confirm Request");
                isRequestMade = true;
                pulsator.setVisibility(View.GONE);
                hideIntro();
                return " address on map";
            }
        } catch (Exception e) {
            e.printStackTrace();
            pulsator.stop();
            agentRequest.setVisibility(View.VISIBLE);
            agentRequest.setText("Confirm Request");
            isRequestMade = true;
            pulsator.setVisibility(View.GONE);
            hideIntro();
            Log.debug("My Current location address", "Cannot get Address!");
            //getCompleteAddressString(latLng);
            return " address on map";
        }

    }

    private String getDate(long timeStamp) {

        try {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date netDate = (new Date(timeStamp));
            return URLEncoder.encode(sdf.format(netDate), "UTF-8");
        } catch (Exception ex) {
            return "xx";
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 40000)
                        .show();
            } else {
                //  Log.i(TAG, "This device is not supported by Google Play Services.");
                //ToastNotify("This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void registerWithNotificationHubs() {
        Log.debug("azure", " Registering with Notification Hubs");

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.

            if (session.getUserName() != null) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                intent.putExtra("username", session.getUserName().toUpperCase());
                //intent.putExtra("username",session.getUserName().toUpperCase());
                Log.debug("AZURE USER:", session.getUserName().toUpperCase());
                startService(intent);
            }

        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.debug("result places: " + requestCode + " resultcode: " + resultCode + " data: " + data.toString());
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOCATION_PERMISSION_ID:
                    startLocation();
                    break;
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(data, this);
                    StringBuilder stBuilder = new StringBuilder();
                    String placename = String.format("%s", place.getName());
                    String latitude = String.valueOf(place.getLatLng().latitude);
                    String longitude = String.valueOf(place.getLatLng().longitude);
                    String addresst = String.format("%s", place.getAddress());
                    stBuilder.append(placename);
                    stBuilder.append("\n");
                /*stBuilder.append("Latitude: ");
                stBuilder.append(latitude);
                stBuilder.append("\n");
                stBuilder.append("Logitude: ");
                stBuilder.append(longitude);
                stBuilder.append("\n");*/
                    stBuilder.append(" ");
                    stBuilder.append(addresst);
                    pulsator.stop();
                    agentRequest.setVisibility(View.VISIBLE);
                    agentRequest.setText("Confirm Request");
                    isRequestMade = true;
                    pulsator.setVisibility(View.GONE);

                    addressString = stBuilder.toString();
                    String addressStringV = address.getText().toString() + "\n\n" + addressString;
                    address.setText(addressStringV);
                    hideIntro();
                    Log.debug("address obtained", addressStringV);
                    break;
            }
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            pulsator.setVisibility(View.VISIBLE);
            pulsator.start();
            confirmRequest.setText("Obtaining address...");
            agentRequest.setVisibility(View.GONE);
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            // tvAddress.setText(locationAddress);
            pulsator.stop();
            agentRequest.setVisibility(View.VISIBLE);
            agentRequest.setText("Confirm Request");
            isRequestMade = true;
            pulsator.setVisibility(View.GONE);
            hideIntro();
            addressString = locationAddress;
            address.setText(address.getText().toString().replace("{}", addressString));
        }
    }

}
