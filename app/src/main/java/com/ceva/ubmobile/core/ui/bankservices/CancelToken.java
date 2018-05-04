package com.ceva.ubmobile.core.ui.bankservices;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.ImageUtils;
import com.ceva.ubmobile.utils.NumberUtilities;
import com.ceva.ubmobile.utils.ScalingUtilities;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelToken extends BaseActivity {
    @BindView(R.id.transRef)
    EditText transRef;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.rd_cancel)
    RadioButton rd_cancel;
    @BindView(R.id.rd_status)
    RadioButton rd_status;
    @BindView(R.id.btnToken)
    Button btnToken;
    @BindView(R.id.txtRef)
    TextView txtRef;
    @BindView(R.id.phoneNumber)
    EditText phoneNumber;
    @BindView(R.id.checkBox)
    CheckBox isSelf;
    @BindView(R.id.phone_self)
    LinearLayout phone_self;
    int operation = 0;
    boolean isCancel = false;
    boolean isReady;
    UBNSession session;
    String reftitle = "";
    @BindView(R.id.banktype)
    RadioGroup accountType;
    boolean isWallet = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_token);
        ButterKnife.bind(this);
        session = new UBNSession(this);
        initializeconfirm();
        hideTransactionPin();
        accountType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                isWallet = i != R.id.bank;
            }
        });
        isSelf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                phoneNumber.setText(session.getPhoneNumber());
            }
        });
        if (getIntent().getStringExtra("extra") != null) {
            String extra = getIntent().getStringExtra("extra");
            if (extra.equals("1")) {

                rd_status.setChecked(true);
                statusFields();

            } else {
                rd_cancel.setChecked(true);
                cancelFields();

            }

        } else {
            onBackPressed();
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.rd_status) {
                    statusFields();
                } else {
                    cancelFields();
                }
            }
        });

        btnToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isReady) {
                    btnToken.setVisibility(View.GONE);
                } else {
                    if (transRef.getText().toString().isEmpty()) {
                        warningDialog("Please enter " + reftitle);
                    } else {
                        if (isCancel) {
                            if (transaction_pin_base.getText().toString().isEmpty()) {
                                warningDialog("Please enter your transaction pin");
                            } else {
                                Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_bulb_white, getResources());
                                new LovelyStandardDialog(CancelToken.this)
                                        .setTopColor(ImageUtils.getColorByThemeAttr(CancelToken.this,R.attr._ubnColorPrimaryDark, Color.BLUE))
                                        .setButtonsColorRes(R.color.midnight_blue)
                                        .setIcon(icon)
                                        .setTitle("Confirm")
                                        .setMessage("Would you like to cancel token of transaction reference number " + transRef.getText().toString() + "?")
                                        .setNegativeButton("Cancel", null)
                                        .setPositiveButton("Proceed", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (NetworkUtils.isConnected(CancelToken.this)) {

                                                    cancelToken(session.getUserName(), transRef.getText().toString(), transaction_pin_base.getText().toString());
                                                } else {
                                                    noInternetDialog();
                                                }
                                            }
                                        })
                                        .show();

                            }
                        } else {
                            if (phoneNumber.getText().toString().isEmpty()) {
                                warningDialog("Please enter phone number");
                            } else {
                                if (NetworkUtils.isConnected(CancelToken.this)) {
                                    //to do
                                    tokenStatus(session.getUserName(), transRef.getText().toString(), phoneNumber.getText().toString());
                                } else {
                                    noInternetDialog();
                                }
                            }
                        }
                    }
                }
            }
        });
        radioGroup.setVisibility(View.GONE);
    }

    private void statusFields() {
        isCancel = false;
        phone_self.setVisibility(View.VISIBLE);
        operation = 1;
        setToolbarTitle("Token Status");
        btnToken.setText("Get Status");
        reftitle = "Payment Code";
        txtRef.setText(reftitle);

        hideTransactionPin();
    }

    private void cancelFields() {
        isCancel = true;
        phone_self.setVisibility(View.GONE);
        operation = 0;
        setToolbarTitle("Cancel Token");
        btnToken.setText("Cancel Token");
        reftitle = "Transaction Reference Number";
        txtRef.setText(reftitle);
        showTransactionPin();
    }

    private void cancelToken(final String username, String transactionRef, String pin) {
        //@Path("/canceltoken/{username}/{transactionRef}/{pin}")
        //showConfirmProgress();
        showLoadingProgress();

        String params = username + "/" + transactionRef + "/" + pin;
        String endpoint = "canceltoken";

        if (isWallet) {
            endpoint = "cancelwallettoken";
            //@Path("/cancelwallettoken/{username}/{subscriberID}/{token}/{pin}")
            params = username + "/" + session.getPhoneNumber() + "/" + transactionRef + "/" + pin;
        }
        Log.debug("params: ", params);

        String url = SecurityLayer.genURLCBC(endpoint, params, getApplicationContext());

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("cancel", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {
                        JSONObject obj2 = obj.getJSONObject("canceltockenresp");
                        String tokenresp = obj2.optString("description");
                        //updateBalances(username);
                        //showTransactionComplete(0, responsemessage);
                        ResponseDialogs.successToActivity(getString(R.string.success), tokenresp, CancelToken.this, DashBoard.class, new Bundle());

                    } else {
                        //showTransactionComplete(1, responsemessage);
                        ResponseDialogs.failStatic(getString(R.string.error), responsemessage, CancelToken.this);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    //showTransactionComplete(1, getString(R.string.error_server));
                    ResponseDialogs.failStatic(getString(R.string.error), getString(R.string.error_server), CancelToken.this);
                }
                //buttonClose_base.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                //dismissProgress();
                //showTransactionComplete(1, getString(R.string.error_server));
                ResponseDialogs.failStatic(getString(R.string.error), getString(R.string.error_server), CancelToken.this);
                // buttonClose_base.setVisibility(View.VISIBLE);
                com.ceva.ubmobile.core.ui.Log.debug("billerfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }

        });

    }

    private void tokenStatus(final String username, String transactionRef, String mobile) {
        //@Path("/gettokenstatus/{username}/{paycode}/{subscriberID}")
        //   showConfirmProgress();
        showLoadingProgress();
        mobile = NumberUtilities.getNumbersOnlyNoDecimal(mobile);

        String params = username + "/" + transactionRef + "/" + mobile;
        Log.debug("params: ", params);
        String endpoint = "gettokenstatus";
        if (isWallet) {
            //@Path("/getwallettokenstatus/{username}/{subscriberID}/{token}")
            params = username + "/" + session.getPhoneNumber() + "/" + transactionRef;
            endpoint = "getwallettokenstatus";
        }

        String url = SecurityLayer.genURLCBC(endpoint, params, getApplicationContext());

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("cancel", response.body());
                dismissProgress();

                try {
                    //TO DO
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, CancelToken.this);

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    JSONObject obj2 = obj.getJSONObject("gettockenresp");

                    if (responsecode.equals("00")) {
                        String stat = "";

                        switch (Integer.parseInt(obj2.optString("status"))) {
                            case 0:
                                stat = "Open";
                                break;
                            case 1:
                                stat = "In-Use";
                                break;
                            case 2:
                                stat = "Cancelled";
                                break;
                            case 3:
                                stat = "Reversed";
                                break;
                            case 4:
                                stat = "Closed";
                                break;
                            case 5:
                                stat = "Locked";
                                break;
                            case 6:
                                stat = "Expired";
                                break;
                        }

                        ResponseDialogs.successToActivity("STATUS", "Status: " + stat, CancelToken.this, CashMenu.class, new Bundle());
                    } else {
                        //showFancyConfirm();
                        //showTransactionComplete(1, responsemessage);
                        ResponseDialogs.failStatic(getString(R.string.error), responsemessage, CancelToken.this);
                    }

                } catch (Exception e) {
                    Log.Error(e);
                }

                /*try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();
                    showFancyConfirm();
                    showTransactionComplete(1, response.body());
                    if (responsecode.equals("00")) {


                        showTransactionComplete(0, responsemessage);
                        //updateBalances(username);
                        JSONObject obj2 = obj.getJSONObject("gettockenresp");


                        *//*0  "Open"
                        1  "In-Use"
                        2  "Cancelled"
                        3  "Reversed"
                        4  "Closed"
                        5  "Locked"
                        6  "Expired"*//*
                        String stat = "";

                        switch (Integer.parseInt(obj2.optString("status"))) {
                            case 0:
                                stat = "Open";
                                break;
                            case 1:
                                stat = "In-Use";
                                break;
                            case 2:
                                stat = "Cancelled";
                                break;
                            case 3:
                                stat = "Reversed";
                                break;
                            case 4:
                                stat = "Closed";
                                break;
                            case 5:
                                stat = "Locked";
                                break;
                            case 6:
                                stat = "Expired";
                                break;
                        }

                        ConfirmModel model = new ConfirmModel("Status", stat);
                        confirmItems.add(model);

                        model = new ConfirmModel("Description", obj2.optString("description"));
                        confirmItems.add(model);

                        model = new ConfirmModel("Amount", NumberUtilities.getWithDecimalPlusCurrency(Double.parseDouble(obj2.optString("amount"))));
                        confirmItems.add(model);

                        *//*model = new ConfirmModel("Surcharge",NumberUtilities.getWithDecimalPlusCurrency(Double.parseDouble(obj2.optString("surcharge"))));
                        confirmItems.add(model);*//*

                        model = new ConfirmModel("Subscriber ID", obj2.optString("subscriberId"));
                        confirmItems.add(model);

                        model = new ConfirmModel("Token Life", (Double.parseDouble(obj2.optString("tokenLifeTimeInMinutes"))/60)+" Hrs");
                        confirmItems.add(model);

                       // model = new ConfirmModel("Token", obj2.optString("token"));
                        //confirmItems.add(model);

                        *//*model = new ConfirmModel("Settlement Code",obj2.optString("settlementCode"));
                        confirmItems.add(model);*//*

                        model = new ConfirmModel("Channel", obj2.optString("channel"));
                        confirmItems.add(model);

                        buttonClose_base.setVisibility(View.VISIBLE);

                        //ResponseDialogs.successToActivity(getString(R.string.success), responsemessage, PayDueBills.this, DashBoard.class,new Bundle());

                    } else {
                         showTransactionComplete(1, responsemessage);
                       // ResponseDialogs.failStatic(getString(R.string.error), responsemessage, CancelToken.this);
                        //ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, PayDueBills.this);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    showTransactionComplete(1, getString(R.string.error_server));
                   // ResponseDialogs.failStatic(getString(R.string.error), getString(R.string.error_server), CancelToken.this);
                    Log.debug("statusfail", e.toString());
                }
                */

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                //showTransactionComplete(1, getString(R.string.error_server));
                //ResponseDialogs.failStatic(getString(R.string.error), getString(R.string.error_server), CancelToken.this);
                //buttonClose_base.setVisibility(View.VISIBLE);
                Log.debug("statusfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }

        });

    }

}
