package com.zercomsystems.android.unionatmlocator.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.UBNApplication;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;
import com.zercomsystems.android.unionatmlocator.adapters.TxATMDetailsAdapter;
import com.zercomsystems.android.unionatmlocator.helpers.LocationType;
import com.zercomsystems.android.unionatmlocator.helpers.RError;
import com.zercomsystems.android.unionatmlocator.helpers.RResponse;
import com.zercomsystems.android.unionatmlocator.models.ATM;
import com.zercomsystems.android.unionatmlocator.models.ATMLocation;
import com.zercomsystems.android.unionatmlocator.models.Location;
import com.zercomsystems.android.unionatmlocator.transports.LocationTransport;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by android on 21/07/2017.
 */

public class TxATMDetailsActivityATM extends ATMBaseActivity {
    private Unbinder unbinder;
    private MaterialDialog progressDialog;
    @BindView(R.id.atm_state)
    TextView mState;
    @BindView(R.id.atm_status)
    TextView mStatus;
    @BindView(R.id.address_to_location)
    TextView mAddress;
    @BindView(R.id.top_part)
    View view;
    @BindView(R.id.status_state_img)
    ImageView mStateImg;
    @BindView(R.id.refresh_this_atm)
    ImageView refreshImg;
    @BindView(R.id.list_of_atms)
    RecyclerView recyclerView;
    private ATM atm;
    private ArrayList<ATM> atms;
    private LocationType type;
    // private LatLng mCurrentLatLng;
    private double distance;
    private Double latitude;
    private Double longitude;

    @Override
    public int getLayoutId() {
        return R.layout.tx_details_atm_activity;
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        UBNApplication.getBus().register(this);
    }

    @OnClick(R.id.refresh_this_atm)
    protected void refreshATM() {
        Log.d(TAG, "refreshATM");
        queryTerminal();
    }

    private TxATMDetailsAdapter.TxATMPicker listener = new TxATMDetailsAdapter.TxATMPicker() {
        @Override
        public void onATMClicked(ATM atm) {
            // what to do
            Log.d(TAG, "listener: atm");
            TxATMDetailsActivityATM.this.atm = atm;
            queryTerminal();
        }
    };


    private void prepareRecycler() {
        Log.d(TAG, "prepareRecycler");
        TxATMDetailsAdapter adapter = new TxATMDetailsAdapter(atms, this, type);
        adapter.setListener(listener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    private void buildDisp() {
        Log.d(TAG, "buildDisp");
        progressDialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .cancelable(false)
                .typeface(Typeface.createFromAsset(getAssets(), getString(R.string.font_medium)), Typeface.createFromAsset(getAssets(), getString(R.string.font_medium)))
                .content("Please wait..").build();
    }

    public void showDialog() {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        unbinder = ButterKnife.bind(this);

        type = (LocationType) getIntent().getSerializableExtra(TxATMDetailsActivityATM.class.getSimpleName());

        buildDisp();

        if (type == null) {
            Toast.makeText(this, "an error occurred, unable to get intent type", Toast.LENGTH_LONG).show();
            finish();
        }

        atms = (ArrayList<ATM>) getIntent().getSerializableExtra(ATM.class.getSimpleName());
        // mCurrentLatLng = getIntent().getParcelableExtra("currentLatLng");
        latitude = getIntent().getDoubleExtra("latitude", 9991);
        longitude = getIntent().getDoubleExtra("longitude", 9991);
        distance = getIntent().getDoubleExtra("distance", 0);
        atm = atms.get(0);
        //mAddress.setText(distance+"km - "+atm.getAddress());

        updateHeader();

        switch (type) {

            case ATM: {
                // perform network operations

                if (atms.size() == 1) {
                    // increase the size
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    height = (int) ((90.0 / 100.0) * height);
                    params.height = height; //ViewGroup.LayoutParams.MATCH_PARENT;
                    view.setLayoutParams(params);

                }

                // make request to check status
                queryTerminal();

                break;
            }

            case BRANCH:
            case SMART_BRANCH: {
                //
                ViewGroup.LayoutParams params = view.getLayoutParams();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                height = (int) ((90.0 / 100.0) * height);
                params.height = height; //ViewGroup.LayoutParams.MATCH_PARENT;
                view.setLayoutParams(params);
                UBNApplication.getInstance().getPicasso().load(R.drawable.tx_branch).into(mStateImg);//branch_tx


                mStatus.setText(" ");
                mState.setText(" ");

                refreshImg.setVisibility(View.GONE);

                break;
            }

            default: {
                Toast.makeText(this, "Unsupported Location Type", Toast.LENGTH_LONG).show();
            }

        }

        prepareRecycler();

    }

    @Subscribe
    public void updateUI(ATM atm) {
        Log.d(TAG, "updateUI");
        hideDialog();
        String state;
        String status;

        if (atm == null) {
            state = "OUT OF SERVICE";
            UBNApplication.getInstance().getPicasso().load(R.drawable.atm_tx_off).into(mStateImg); //offline
            status = " ";
            mState.setTextColor(ContextCompat.getColor(this, R.color.map_red));
        } else if (!atm.isON()) {

            state = "ATM OFFLINE";
            UBNApplication.getInstance().getPicasso().load(R.drawable.atm_tx_off).into(mStateImg); //offline
            status = " ";
            mState.setTextColor(ContextCompat.getColor(this, R.color.map_red));
        } else if (atm.isDispensing()) {

            state = "ATM ONLINE";
            status = "DISPENSING";
            UBNApplication.getInstance().getPicasso().load(R.drawable.atm_tx_on).into(mStateImg); // online
            mState.setTextColor(ContextCompat.getColor(this, R.color.map_green));
            mStatus.setTextColor(ContextCompat.getColor(this, R.color.map_green));
        } else {
            state = "ATM ONLINE";
            status = "NOT DISPENSING";
            UBNApplication.getInstance().getPicasso().load(R.drawable.atm_tx_on).into(mStateImg); //not_dispensing
            mState.setTextColor(ContextCompat.getColor(this, R.color.map_green));
            mStatus.setTextColor(ContextCompat.getColor(this, R.color.map_yellow));
        }

        mState.setText(state);
        mStatus.setText(status);

        if (atm != null)
            this.atm = atm;

        updateHeader();
//        if (atm != null){
//            this.atm = atm;
//            mAddress.setText((atm.getAddress() == null) ? distance+"km - address not defined" : distance+"km - "+atm.getAddress());
//    }else {
//            mAddress.setText((this.atm.getAddress() == null) ? distance+"km - address not defined": distance+"km - "+this.atm.getAddress());
//        }
    }


    private void updateHeader() {
        String header;
        header = (this.atm.getAddress() == null) ? distance + "km - address not defined" : distance + "km - " + this.atm.getAddress();
        mAddress.setText(header.toUpperCase());
    }


    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        UBNApplication.getBus().unregister(this);
    }

    @OnClick(R.id.take_me_there)
    public void goToLocation() {
        Log.d(TAG, "goToLocation");
        // to to the requested location
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                "http://maps.google.com/maps?saddr="
                        + latitude + ","
                        + longitude +
                        "&daddr=" + atm.getLat() + ","
                        + atm.getLng()));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        unbinder.unbind();
    }

    private void queryTerminal() {
        Log.d(TAG, "queryTerminal");
        showDialog();
        LocationTransport.getATMByTerminalID(atm, new LocationTransport.Callback() {

            @Override
            public void onSuccess(ArrayList<Location> locations) {
                Log.d(TAG, "getATMByTerminalID: onSuccess");

                if (locations.isEmpty()) {
                    UBNApplication.getBus().post("Unable to get this ATM Terminal status, check again later");
                    return;
                }

                ATMLocation atm = (ATMLocation) locations.get(0);
                ATM atm1 = new ATM(atm.getTerminalID()).setAddress(atm.getAddress()).setName(atm.getName()).setLat(atm.getPosition().latitude)
                        .setLng(atm.getPosition().longitude).setDispensing(atm.isDispensing())
                        .setON(atm.isOperating());
                atm1.isSelected = true;
                UBNApplication.getBus().post(atm1);

                System.out.println("Terminal id :" + atm1);
                // send it

            }

            @Override
            public void onFailure(RError error) {
                Log.d(TAG, "getATMByTerminalID: onFailure");

                UBNApplication.getBus().post(error);

                System.out.println("RError id :" + error.message);

            }

        });

    }//RResponse

    @Subscribe
    public void showError(RError msg) {
        Log.d(TAG, "showError: RError");
        hideDialog();
        try {
            TxATMDetailsActivityATM.this.updateUI(null);
            if (msg.code == RResponse.RESOURCE_NOT_FOUND) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String textMsg = (msg.code == RResponse.RESOURCE_NOT_FOUND) ? "This ATM is Out-of-service, check back later" : msg.message;
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .cancelable(false)
                .contentColorRes(R.color.carbon_text)
                .negativeText("Dismiss").dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        dialog.dismiss();
                    }
                })
                .typeface(Typeface.createFromAsset(getAssets(), getString(R.string.font_medium)), Typeface.createFromAsset(getAssets(), getString(R.string.font_medium)))
                .content(textMsg).build();

        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Subscribe
    public void showError(String msg) {
        Log.d(TAG, "showError: String");
        hideDialog();
        try {
            TxATMDetailsActivityATM.this.updateUI(null);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .cancelable(false)
                .contentColorRes(R.color.carbon_text)
                .negativeText("Dismiss").dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                })
                .typeface(Typeface.createFromAsset(getAssets(), getString(R.string.font_medium)), Typeface.createFromAsset(getAssets(), getString(R.string.font_medium)))
                .content(msg).build();

        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.goback)
    public void goBack() {
        this.onBackPressed();
    }


}
