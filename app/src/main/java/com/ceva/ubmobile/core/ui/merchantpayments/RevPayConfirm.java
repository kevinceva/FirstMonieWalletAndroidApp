package com.ceva.ubmobile.core.ui.merchantpayments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.ceva.ubmobile.BuildConfig;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.ConfirmAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;
import com.ceva.ubmobile.utils.ImageUtils;
import com.ceva.ubmobile.utils.NumberUtilities;
import com.ceva.ubmobile.utils.ScalingUtilities;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RevPayConfirm extends BaseActivity {
    @BindView(R.id.confirm_list1)
    RecyclerView confirmationRecyler;

    ConfirmAdapter confirmAdapter1;
    List<ConfirmModel> confirmItems1 = new ArrayList<>();
    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.btnPay)
    Button btnPay;
    @BindView(R.id.pin)
    EditText pin;
    UBNSession session;
    @BindView(R.id.account)
    Spinner accountFrom;
    List<String> accountList = new ArrayList<>();
    boolean isReady = false;
    @BindView(R.id.token_auth)
    LinearLayout token_auth;
    @BindView(R.id.auth_mode)
    RadioGroup auth_mode;
    @BindView(R.id.auth_code)
    EditText auth_code;
    boolean isTokenRequired = false;
    @BindView(R.id.amount)
    EditText amountEdit;
    String AUTH_MODE_TYPE = Constants.KEY_AUTH_TOKEN;
    String webref = "";
    String agencycode, revenuecode, creditaccount, fullresp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rev_pay_confirm);
        ButterKnife.bind(this);
        setToolbarTitle("Confirm Details");
        initializeconfirm();
        session = new UBNSession(this);
        token_auth.setVisibility(View.GONE);
        auth_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.rb_sms) {
                    AUTH_MODE_TYPE = Constants.KEY_AUTH_SMS;
                    if (NetworkUtils.isConnected(RevPayConfirm.this)) {
                        generateSMSToken(session.getUserName(), session.getPhoneNumber(), Constants.KEY_FUNDTRANS_WITHIN);
                    } else {
                        noInternetDialog();
                    }
                } else {
                    AUTH_MODE_TYPE = Constants.KEY_AUTH_TOKEN;
                }
            }
        });
        accountList.addAll(session.getAccountNumbersNoDOM());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_row, accountList);
        accountFrom.setAdapter(adapter);

        confirmAdapter1 = new ConfirmAdapter(confirmItems1, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        confirmationRecyler.setLayoutManager(mLayoutManager);
        confirmationRecyler.setItemAnimator(new DefaultItemAnimator());
        confirmationRecyler.setAdapter(confirmAdapter1);

        String payload = getIntent().getStringExtra("data").trim();

        try {
            JSONObject jsonObject = new JSONObject(payload);

            webref = getIntent().getStringExtra("webref");

            ConfirmModel con = new ConfirmModel("Payer Name", jsonObject.optString("PayerName"));
            confirmItems1.add(con);

            con = new ConfirmModel("Amount Due", jsonObject.optString("AmountDue"));
            confirmItems1.add(con);

            con = new ConfirmModel("Reference Status", jsonObject.optString("StatusMessage"));
            confirmItems1.add(con);

            con = new ConfirmModel("Agency Code", jsonObject.optString("AgencyCode"));
            confirmItems1.add(con);

            con = new ConfirmModel("Revenue Code", jsonObject.optString("RevenueCode"));
            confirmItems1.add(con);

            confirmAdapter1.notifyDataSetChanged();

            agencycode = jsonObject.optString("AgencyCode");
            revenuecode = jsonObject.optString("RevenueCode");
            creditaccount = jsonObject.optString("CreditAccount");
            fullresp = payload;
            if (BuildConfig.DEBUG && SecurityLayer.isDemo) {
                fullresp = "NA";
            }

            //amountEdit.setText(jsonObject.optString("AmountDue"));
            //amountEdit.setInputType(InputType.TYPE_NULL);
        } catch (Exception e) {
            Log.Error(e.toString());
        }
        btnPay.setText("Continue");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick();
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isConnected(RevPayConfirm.this)) {
                    onPaylick();
                } else {
                    noInternetDialog();
                }
            }
        });

    }

    //@OnClick(R.id.btnBack)
    public void onBackClick() {
        if (isReady) {
            hideFancyConfirm();
            isReady = false;
            btnPay.setText("Continue");
        } else {
            startActivity(new Intent(this, RevPayRef.class));
        }
    }

    //@OnClick(R.id.btnPay)
    public void onPaylick() {
        String accountDebit = accountFrom.getSelectedItem().toString();
        String[] accountSplit = accountDebit.split("-");
        String accountNo = accountSplit[(accountSplit.length - 1)].trim();
        String amount = amountEdit.getText().toString();
        String pins = pin.getText().toString();
        if (isReady) {
            if (isTokenRequired) {
                String auth = auth_code.getText().toString();
                if (TextUtils.isEmpty(auth)) {
                    auth_code.setError("Please enter the authentication code");
                } else {
                    doPayment(session.getUserName(), webref, amount, accountNo, pins, AUTH_MODE_TYPE, auth, agencycode, revenuecode, creditaccount, fullresp);
                }
            } else {
                doPayment(session.getUserName(), webref, amount, accountNo, pins, Constants.KEY_AUTH_NO_AUTH, Constants.KEY_AUTH_NO_AUTH, agencycode, revenuecode, creditaccount, fullresp);
            }

        } else {
            if (TextUtils.isEmpty(amount)) {
                amountEdit.setError("Please enter amount you would like to pay");
            } else if (TextUtils.isEmpty(pins)) {
                pin.setError("Please enter your transaction PIN");
            } else {
                confirmItems.clear();

                confirmItems.addAll(confirmItems1);

                ConfirmModel confirmModel = new ConfirmModel("Account Debited", accountDebit);
                confirmItems.add(confirmModel);

                confirmModel = new ConfirmModel("Amount Paid", Constants.KEY_NAIRA + amount);
                confirmItems.add(confirmModel);

                showFancyConfirm();
                Utility.hideKeyboard(RevPayConfirm.this);
                if (NetworkUtils.isConnected(RevPayConfirm.this)) {
                    isReady = true;
                    checkIfTokenRequired(session.getUserName(), amount, "REV_PAY", pins);
                    btnPay.setText("Pay");
                } else {
                    noInternetDialog();
                }
            }
        }
    }


    private void checkIfTokenRequired(String username, String amount, String service, String pin) {
        //@Path("/isTokenAuthRequired/{userid}/{amount}/{servicetype}/{pin}")
        showLoadingProgress();
        String params = username + "/" + amount + "/" + service + "/" + pin;
        String session_id = UUID.randomUUID().toString();
        String url = SecurityLayer.genURLCBC("isTokenAuthRequired", params, this);

        // String urlparam = "accountnovallidatin/" + SecurityLayer.generalEncrypt(accountNumber);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, RevPayConfirm.this);
                    //isTokenRequired = true;
                    //token_auth.setVisibility(View.VISIBLE);

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {

                        if (obj.optString("isTokenAuthRequired").equals("Y")) {
                            isTokenRequired = true;
                            token_auth.setVisibility(View.VISIBLE);
                        } else if (obj.optString("isTokenAuthRequired").equals("N")) {
                            isTokenRequired = false;
                            token_auth.setVisibility(View.GONE);
                        } else {
                            ResponseDialogs.warningDialogLovely(RevPayConfirm.this, "Error", obj.optString(Constants.KEY_MSG));
                        }

                    } else {
                        ResponseDialogs.warningDialogLovely(RevPayConfirm.this, "Error", obj.optString(Constants.KEY_MSG));
                        hideFancyConfirm();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hideFancyConfirm();
                    ResponseDialogs.warningDialogLovely(RevPayConfirm.this, "Error", getString(R.string.error_500));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                hideFancyConfirm();
                ResponseDialogs.warningDialogLovely(RevPayConfirm.this, "Error", getString(R.string.error_500));
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    private void doPayment(String username, final String merchantcode, final String amount, String account, String pin, String auth, String auth_val, String agencycode, String revenuecode, String creditaccount, String totalrespofini) {
        //@Path("/revpayprocesspayment/{userid}/{revpaywebguid}/{amountpaid}/{account}/{pin}/{authtype: .}/{authvalue: .}/{agencycode}/{revenuecode}/{creditaccount}/{totalrespofini}")
        btnPay.setEnabled(false);
        showLoadingProgress();
        Log.debug("The response " + totalrespofini);

        String params = username + "/" + merchantcode + "/" + amount + "/" + account + "/" + pin + "/" + auth + "/" + auth_val + "/" + agencycode + "/" + revenuecode + "/" + creditaccount + "/" + totalrespofini;
        String url = SecurityLayer.genURLCBC("revpayprocesspayment", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("getMerchantsList", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {

                        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_check_circle_white, getResources());
                        final JSONObject finalObj = obj;
                        new LovelyStandardDialog(RevPayConfirm.this)
                                .setTopColor(ImageUtils.getColorByThemeAttr(getApplicationContext(),R.attr._ubnColorPrimaryDark, Color.BLUE))
                                .setButtonsColorRes(R.color.midnight_blue)
                                .setIcon(icon)
                                .setTitle("Success")
                                .setMessage(responsemessage)
                                .setPositiveButton("Download Receipt", null)
                                .setOnButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String uur = finalObj.optJSONObject("processpaymentresp").optString("ReceiptNumber");
                                        Uri uri = Uri.parse(uur);
                                        downLoadFile(uri, "RevPayReceipt.pdf");
                                        showToast("Download is in progress. Please check the notification bar.");
                                        startActivity(new Intent(RevPayConfirm.this, DashBoard.class));
                                    }
                                })
                                .show();

                        //ResponseDialogs.infoToActivity(getString(R.string.success), responsemessage, RevPayConfirm.this, DashBoard.class, new Bundle());
                    } else {
                        btnPay.setEnabled(true);
                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, RevPayConfirm.this);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                btnPay.setEnabled(true);
                com.ceva.ubmobile.core.ui.Log.debug("billerfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

}
