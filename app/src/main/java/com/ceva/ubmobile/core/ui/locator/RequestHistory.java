package com.ceva.ubmobile.core.ui.locator;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.MapLocationAdapter;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.MapLocation;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.GeoAPIClient;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.UBNSession;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestHistory extends BaseActivity {
    public MapLocationAdapter mListAdapter;
    public RecyclerView mRecyclerView;
    UBNSession session;
    ArrayList<MapLocation> mapLocationsList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_history);
        setToolbarTitle("My Requests");
        session = new UBNSession(this);
        mRecyclerView = findViewById(R.id.card_list);

        // Determine the number of columns to display, based on screen width.
        int rows = 1;
        GridLayoutManager layoutManager = new GridLayoutManager(this, rows, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mListAdapter = new MapLocationAdapter();
        mListAdapter.setMapLocations(mapLocationsList);

        if (NetworkUtils.isConnected(this)) {
            getRequestHistory(session.getUserName());
        } else {
            noInternetDialog();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (mListAdapter != null) {
            for (MapView m : mListAdapter.getMapViews()) {
                m.onLowMemory();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mListAdapter != null) {
            for (MapView m : mListAdapter.getMapViews()) {
                m.onPause();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode == ConnectionResult.SUCCESS) {
            mRecyclerView.setAdapter(mListAdapter);
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1).show();
        }

        if (mListAdapter != null) {
            for (MapView m : mListAdapter.getMapViews()) {
                m.onResume();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mListAdapter != null) {
            for (MapView m : mListAdapter.getMapViews()) {
                m.onDestroy();
            }
        }

        super.onDestroy();
    }


    public MapLocationAdapter createMapListAdapter(ArrayList<MapLocation> mapLocations) {

        MapLocationAdapter adapter = new MapLocationAdapter();
        adapter.setMapLocations(mapLocations);

        return adapter;
    }

    private void getRequestHistory(final String username) {
        // showSmoothProgress();
        showLoadingProgress();
        String params = "getdsahistoryforcustomer/" + username.toUpperCase();
        //String url = SecurityLayer.genURLCBC("inbox",params,this);
        ApiInterface apiService = GeoAPIClient.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //hideSmoothProgress();
                String resp = response.body();
                Log.debug("raw", resp);
                /*resp = resp.replace("\"[\\\"","[");
                resp = resp.replace("\\\"]\"","]");
                resp = resp.replace("\\","");
                resp = resp.replace("}\",\"{","},{");*/
                Log.debug(resp);
                try {
                    dismissProgress();

                    JSONObject obj = new JSONObject(resp);
                    //Log.debug(obj.toString());
                    String locstr = obj.optString("CUST_HISTORY");

                    JSONArray locationArray = new JSONArray(locstr);

                    int m = locationArray.length();
                    for (int i = 0; i < m; i++) {
                        JSONObject locObj = locationArray.getJSONObject(i);
                        LatLng latlng = new LatLng(locObj.optDouble("LATITUDE"), locObj.optDouble("LONGITUDE"));

                        mapLocationsList.add(new MapLocation(locObj.optString("DSA_FULL_NAME"), latlng, locObj.optString("TNX_DATE"), locObj.optString("SERVICE_TYPE")));
                        //mMap.addMarker(marker);
                    }
                    mListAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Log.Error(e);

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                //FirebaseCrash.report(t);
                //hideSmoothProgress();

            }
        });

    }

}
