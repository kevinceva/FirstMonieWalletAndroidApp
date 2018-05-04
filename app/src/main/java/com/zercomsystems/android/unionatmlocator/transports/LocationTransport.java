package com.zercomsystems.android.unionatmlocator.transports;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ceva.ubmobile.core.UBNApplication;
import com.google.android.gms.maps.model.LatLng;
import com.zercomsystems.android.unionatmlocator.helpers.Constants;
import com.zercomsystems.android.unionatmlocator.helpers.LocationType;
import com.zercomsystems.android.unionatmlocator.helpers.RError;
import com.zercomsystems.android.unionatmlocator.helpers.RResponse;
import com.zercomsystems.android.unionatmlocator.models.ATM;
import com.zercomsystems.android.unionatmlocator.models.ATMLocation;
import com.zercomsystems.android.unionatmlocator.models.BranchLocation;
import com.zercomsystems.android.unionatmlocator.models.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by EdgeTech on 5/5/2016.
 */
public class LocationTransport {
    private static String LOG_TAG = LocationTransport.class.getSimpleName();

    public interface Callback {
        void onSuccess(ArrayList<Location> locations);

        void onFailure(RError error);
    }

    /**
     * Query result for LocationType based on a current coordinate
     * @param type LocationType (ATM, BRANCH, SMART_BRANCH)
     * @param currentLatLng Coordinate to use in filtering the result
     * @param callback interface to call upon result success or failure
     */
    public static void getLocationList(LocationType type, LatLng currentLatLng, LocationTransport.Callback callback) {
        Log.d(LOG_TAG, "getLocationList");
        switch (type) {
            case ATM:
                LocationTransport.getATMLocationList(currentLatLng, callback);
                break;
            case SMART_BRANCH:
                LocationTransport.getBranchLocationList(currentLatLng, true, callback);
                break;

            default:
                LocationTransport.getBranchLocationList(currentLatLng, false, callback);
                break;
        }

    }


    /**
     * Query result for LocationType based on a current coordinate
     * @param type LocationType (ATM, BRANCH, SMART_BRANCH)
     * @param currentLatLng Coordinate to use in filtering the result
     */
    public static void getLocationList(LocationType type, LatLng currentLatLng) {
        Log.d(LOG_TAG, "getLocationList");
        switch (type) {
            case ATM:
                LocationTransport.getATMLocationList(currentLatLng);
                break;
            case SMART_BRANCH:
                LocationTransport.getBranchLocationList(currentLatLng, true);
                break;

            default:
                LocationTransport.getBranchLocationList(currentLatLng, false);
                break;
        }

    }


    private static void getATMLocationList(LatLng currentLatLng) {
        //TODO get list from API
        Log.d(LOG_TAG, "getATMLocationList");

        String url = Constants.ATM_SCAN + "?lat=" + currentLatLng.latitude + "&lng=" + currentLatLng.longitude;
        String requestTag = "GET_ATM_LIST";

        Log.v(LOG_TAG, url);

        Response.Listener listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.v("0TAG", response.toString());

                ArrayList<Location> locationList = new ArrayList<>();
                try {
                    JSONArray bodyArray = response.getJSONArray("body");

                    for (int i = 0; i < bodyArray.length(); i++) {

                        JSONObject object = bodyArray.getJSONObject(i);

                        String name = object.getString("name");
                        String address = object.getString("address").toLowerCase();
                        String state = object.getString("state");
                        String terminalID = object.getString("terminal_id");
                        Double lat = object.getDouble("lat");
                        Double lng = object.getDouble("lng");
                        double distance = object.getDouble("distance");
                        LatLng position = new LatLng(lat, lng);


                        Log.v("0TAG", "distance" + distance);
                        locationList.add(new ATMLocation(name, state, address, terminalID, position, distance));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.sort(locationList, Location.SORT_DISTANCE);

                // use Otto Bus
                UBNApplication.getBus().post(locationList);
                Log.d(LOG_TAG, "Otto bus success");
            }

        };

        //Set up error listener for Volley response
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                RError response;

                if (error instanceof ServerError) {
                    if (error.networkResponse.statusCode == RResponse.RESOURCE_NOT_FOUND)
                        response = new RError(RResponse.RESOURCE_NOT_FOUND, "Not found");

                    else
                        response = new RError(RResponse.SERVER_ERROR, "An unexpected error has occurred");
                } else if (error instanceof NoConnectionError)
                    response = RError.INTERNET_UNAVAILABLE();

                else
                    response = new RError(RResponse.SERVER_ERROR, error.toString());


                // use Otto Bus
                UBNApplication.getBus().post(response);

            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, listener, errorListener);

        UBNApplication.getInstance().addToRequestQueue(jsonObjectRequest, requestTag);

    }

    private static void getATMLocationList(LatLng currentLatLng, final LocationTransport.Callback callback) {
        //TODO get list from API
        Log.d(LOG_TAG, "getATMLocationList");

        String url = Constants.ATM_SCAN + "?lat=" + currentLatLng.latitude + "&lng=" + currentLatLng.longitude;
        String requestTag = "GET_ATM_LIST";

        Log.v(LOG_TAG, url);

        Response.Listener listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.v("0TAG", response.toString());

                ArrayList<Location> locationList = new ArrayList<>();
                try {
                    JSONArray bodyArray = response.getJSONArray("body");

                    for (int i = 0; i < bodyArray.length(); i++) {

                        JSONObject object = bodyArray.getJSONObject(i);

                        String name = object.getString("name");
                        String address = object.getString("address").toLowerCase();
                        String state = object.getString("state");
                        String terminalID = object.getString("terminal_id");
                        Double lat = object.getDouble("lat");
                        Double lng = object.getDouble("lng");
                        double distance = object.getDouble("distance");
                        LatLng position = new LatLng(lat, lng);


                        Log.v("0TAG", "distance" + distance);
                        locationList.add(new ATMLocation(name, state, address, terminalID, position, distance));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.sort(locationList, Location.SORT_DISTANCE);

                callback.onSuccess(locationList);
            }

        };

        //Set up error listener for Volley response
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                RError response;

                if (error instanceof ServerError) {
                    if (error.networkResponse.statusCode == RResponse.RESOURCE_NOT_FOUND)
                        response = new RError(RResponse.RESOURCE_NOT_FOUND, "Not found");

                    else
                        response = new RError(RResponse.SERVER_ERROR, "An unexpected error has occurred");
                } else if (error instanceof NoConnectionError)
                    response = RError.INTERNET_UNAVAILABLE();

                else
                    response = new RError(RResponse.SERVER_ERROR, error.toString());


                callback.onFailure(response);

            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, listener, errorListener);

        UBNApplication.getInstance().addToRequestQueue(jsonObjectRequest, requestTag);

    }


    /**
     * Get the operating status of an ATM
     * @param terminalID ATM Terminal ID
     */
    public static void getATMByTerminalID(final ATM terminalID) {
        //TODO get list from API
        //And return type Location

        String url = Constants.ATM_DETAILS + terminalID.getId();

        String requestTag = "GET_ATM_BY_TERMINAL_ID " + terminalID.getId(); //

        Log.v(LOG_TAG, "Attempting " + requestTag);

        Response.Listener listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.v("0TAG", response.toString());

                ArrayList<Location> locationList = new ArrayList<>();

                try {

                    int code = response.getInt("code");

                    if (code == 200) {

                        JSONObject object = response.getJSONObject("body");

                        boolean operating = Boolean.valueOf(object.getString("operating"));
                        boolean dispensing = Boolean.valueOf(object.getString("dispensing"));
                        LatLng position = new LatLng(0, 0);

                        ATMLocation tmp = new ATMLocation(terminalID.getName(), "", terminalID.getAddress(), terminalID.getId(), position, operating, dispensing);

                        locationList.add(tmp.setCustodianEmail("")
                                .setCustodianName("")
                                .setCustodianNumber(""));

                        UBNApplication.getBus().post(locationList);

                    } else if (code == 404) {

                        String msg = "This ATM is Out-of-service";
                        msg = TextUtils.isEmpty(msg) ? "Error occurred in communication" : msg;
                        RError error = new RError(404, msg);
                        UBNApplication.getBus().post(error);

                    } else {

                        String msg = response.getString("message");
                        msg = TextUtils.isEmpty(msg) ? "Error occurred in communication" : msg;
                        RError error = new RError(510, msg);
                        UBNApplication.getBus().post(error);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    RError error = new RError(500, "Unable to connect. Make sure you're online, or try again later.");
                    UBNApplication.getBus().post(error);
                }
            }

        };

        //Set up error listener for Volley response
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                RError response;

                if (error instanceof ServerError) {
                    if (error.networkResponse.statusCode == RResponse.RESOURCE_NOT_FOUND)
                        response = new RError(RResponse.RESOURCE_NOT_FOUND, "Not found");

                    else
                        response = new RError(RResponse.SERVER_ERROR, "Unable to connect. Make sure you're online, or try again later.");
                } else if (error instanceof NoConnectionError)
                    response = RError.INTERNET_UNAVAILABLE();

                else
                    response = new RError(RResponse.SERVER_ERROR, error.toString());


                UBNApplication.getBus().post(response);

            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, listener, errorListener);

        UBNApplication.getInstance().addToRequestQueue(jsonObjectRequest, requestTag);

    }

    /**
     * Get the operating status of an ATM
     * @param terminalID ATM Terminal ID
     * @param callback interface to call on result or failure
     */
    public static void getATMByTerminalID(final ATM terminalID, final LocationTransport.Callback callback) {
        //TODO get list from API
        //And return type Location

        String url = Constants.ATM_DETAILS + terminalID.getId();
        com.ceva.ubmobile.core.ui.Log.debug("getATMByTerminalID:" + url);

        String requestTag = "GET_ATM_BY_TERMINAL_ID " + terminalID.getId(); //

        Log.v(LOG_TAG, "Attempting " + requestTag);

        Response.Listener listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.v("0TAG", response.toString());

                ArrayList<Location> locationList = new ArrayList<>();

                try {

                    int code = response.getInt("code");

                    if (code == 200) {

                        JSONObject object = response.getJSONObject("body");

                        boolean operating = Boolean.valueOf(object.getString("operating"));
                        boolean dispensing = Boolean.valueOf(object.getString("dispensing"));
                        LatLng position = new LatLng(terminalID.getLat(), terminalID.getLng());

                        ATMLocation tmp = new ATMLocation(terminalID.getName(), "", terminalID.getAddress(), terminalID.getId(), position, operating, dispensing);

                        locationList.add(tmp.setCustodianEmail("")
                                .setCustodianName("")
                                .setCustodianNumber(""));

                        callback.onSuccess(locationList);

                    } else if (code == 404) {

                        String msg = "This ATM is Out-of-service";
                        msg = TextUtils.isEmpty(msg) ? "Error occurred in communication" : msg;
                        RError error = new RError(404, msg);
                        callback.onFailure(error);

                    } else {

                        String msg = response.getString("message");
                        msg = TextUtils.isEmpty(msg) ? "Error occurred in communication" : msg;
                        RError error = new RError(510, msg);
                        callback.onFailure(error);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    RError error = new RError(500, "Unable to connect. Make sure you're online, or try again later.");
                    callback.onFailure(error);
                }
            }

        };

        //Set up error listener for Volley response
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                RError response;

                if (error instanceof ServerError) {
                    if (error.networkResponse.statusCode == RResponse.RESOURCE_NOT_FOUND)
                        response = new RError(RResponse.RESOURCE_NOT_FOUND, "Not found");

                    else
                        response = new RError(RResponse.SERVER_ERROR, "Unable to connect. Make sure you're online, or try again later.");
                } else if (error instanceof NoConnectionError)
                    response = RError.INTERNET_UNAVAILABLE();

                else
                    response = new RError(RResponse.SERVER_ERROR, error.toString());


                callback.onFailure(response);

            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, listener, errorListener);

        UBNApplication.getInstance().addToRequestQueue(jsonObjectRequest, requestTag);

    }


    private static void getBranchLocationList(LatLng currentLatLng, final boolean isSmart) {

        Log.v(LOG_TAG, "Attempting get list of branch locations ");

        String url = Constants.BRANCH_SCAN;
        LocationType type;
        //if its smart
        if (isSmart) {
            url = Constants.SMART_BRANCH_SCAN;
        }

        //lets add query
        url += "?lat=" + currentLatLng.latitude + "&lng=" + currentLatLng.longitude;

        String requestTag = "GET_BRANCH_LIST";

        Log.v(LOG_TAG, url);

        Response.Listener listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                ArrayList<Location> locationList = new ArrayList<>();

                try {
                    int code = response.getInt("code");

                    if (code == 200)
                        locationList = responseToBranches(response, (isSmart) ? LocationType.SMART_BRANCH : LocationType.BRANCH);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.sort(locationList, Location.SORT_DISTANCE);

                UBNApplication.getBus().post(locationList);

            }

        };


        //Set up error listener for Volley response
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RError response;

                if (error instanceof TimeoutError)
                    response = new RError(RResponse.REQUEST_TIMED_OUT, "Timed out");
                else if (error instanceof ServerError) {

                    if (error.networkResponse.statusCode == RResponse.RESOURCE_NOT_FOUND)
                        response = new RError(RResponse.RESOURCE_NOT_FOUND, "Not found");

                    else
                        response = new RError(RResponse.SERVER_ERROR, "An unexpected error has occurred");
                } else if (error instanceof NoConnectionError)
                    response = RError.INTERNET_UNAVAILABLE();

                else
                    response = new RError(RResponse.SERVER_ERROR, error.toString());

                UBNApplication.getBus().post(response);
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, listener, errorListener);

        UBNApplication.getInstance().addToRequestQueue(jsonObjectRequest, requestTag);
    }


    private static void getBranchLocationList(LatLng currentLatLng, final boolean isSmart, final LocationTransport.Callback callback) {

        Log.v(LOG_TAG, "Attempting get list of branch locations ");

        String url = Constants.BRANCH_SCAN;
        //if its smart
        if (isSmart) {
            url = Constants.SMART_BRANCH_SCAN;
        }

        //lets add query
        url += "?lat=" + currentLatLng.latitude + "&lng=" + currentLatLng.longitude;

        String requestTag = "GET_BRANCH_LIST";

        Log.v(LOG_TAG, url);

        Response.Listener listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                ArrayList<Location> locationList = new ArrayList<>();

                try {
                    int code = response.getInt("code");

                    if (code == 200)
                        locationList = responseToBranches(response, (isSmart) ? LocationType.SMART_BRANCH : LocationType.BRANCH);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.sort(locationList, Location.SORT_DISTANCE);

                callback.onSuccess(locationList);

            }

        };


        //Set up error listener for Volley response
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RError response;

                if (error instanceof TimeoutError)
                    response = new RError(RResponse.REQUEST_TIMED_OUT, "Timed out");
                else if (error instanceof ServerError) {

                    if (error.networkResponse.statusCode == RResponse.RESOURCE_NOT_FOUND)
                        response = new RError(RResponse.RESOURCE_NOT_FOUND, "Not found");

                    else
                        response = new RError(RResponse.SERVER_ERROR, "An unexpected error has occurred");
                } else if (error instanceof NoConnectionError)
                    response = RError.INTERNET_UNAVAILABLE();

                else
                    response = new RError(RResponse.SERVER_ERROR, error.toString());

                callback.onFailure(response);
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, listener, errorListener);

        UBNApplication.getInstance().addToRequestQueue(jsonObjectRequest, requestTag);
    }


    private static ArrayList<Location> responseToBranches(JSONObject response, LocationType type) throws JSONException {

        ArrayList<Location> locationList = new ArrayList<>();

        JSONArray bodyArray = response.getJSONArray("body");

        for (int i = 0; i < bodyArray.length(); i++) {
            JSONObject object = bodyArray.getJSONObject(i);
            String name = object.getString("name");
            String address = object.getString("address");
            String state = object.getString("state");
            String terminalID = object.getString("id");
            Double lat = object.getDouble("lat");
            Double lng = object.getDouble("lng");
            Double distance = object.getDouble("distance");
            LatLng position = new LatLng(lat, lng);

            locationList.add(new BranchLocation(name, state, address, terminalID, position, true, distance, type));
            System.err.println(address + "@" + distance);
        }

        return locationList;

    }
}