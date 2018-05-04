package com.ceva.ubmobile.core.signon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ceva.ubmobile.BuildConfig;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.SecurityQuestionsModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepTwo extends BaseActivity {
    public static final String KEY_REG_TYPE = "regType";
    LinearLayout password_fields, account_fields;
    EditText accountNumber, OTP;
    boolean isOTPEntered;
    Button button;
    ArrayList<String> usernames = new ArrayList<>();
    String customermobile = "";
    UBNSession session;
    @BindView(R.id.acc_type)
    Spinner accountTypeSpinner;
    @BindView(R.id.accountLabel)
    TextView accountLabel;
    int selected = 0;
    String endpoint = "accountnovallidatin";
    String otpendpoint = "otpvalidation";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steptwo);
        ButterKnife.bind(this);
        setToolbarTitle("Account Validation");
        session = new UBNSession(this);
        password_fields = findViewById(R.id.password_fields);
        account_fields = findViewById(R.id.account_fields);
        accountNumber = findViewById(R.id.accountNumber);
        OTP = findViewById(R.id.OTP);
        button = findViewById(R.id.button);

        accountTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    accountLabel.setText("Account Number");
                    endpoint = "accountnovallidatin";
                    otpendpoint = "otpvalidation";
                } else {
                    accountLabel.setText("Phone Number");
                    endpoint = "iswalletmobilno";
                    otpendpoint = "walletotpvalidation";
                }
                selected = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.isNotNull(accountNumber.getText().toString())) {
                    if (isOTPEntered) {
                        if (Utility.isNotNull(OTP.getText().toString())) {
                            if (NetworkUtils.isConnected(StepTwo.this)) {
                                getOTP(accountNumber.getText().toString(), customermobile, OTP.getText().toString(), otpendpoint);
                            } else {
                                noInternetDialog();
                            }
                        } else {
                            OTP.setError("Please enter the one time password sent via SMS");
                            //ResponseDialogs.warningDialogLovely(StepTwo.this, "Enter OTP", "Please enter the one time password sent via SMS");
                        }
                    } else {
                        if (NetworkUtils.isConnected(StepTwo.this)) {
                            validateAccount(accountNumber.getText().toString(), endpoint);
                        } else {
                            noInternetDialog();
                        }
                        //showOTPField();
                    }

                } else {
                    accountNumber.setError("This value is required.");
                    //ResponseDialogs.warningDialogLovely(StepTwo.this, "Error", "Please enter your account number");
                }
            }
        });
    }

    private void showOTPField() {
        isOTPEntered = true;
        password_fields.setVisibility(View.VISIBLE);
        account_fields.setVisibility(View.GONE);
    }

    private void validateAccount(final String accountNumber, final String endpoint) {

        showLoadingProgress();
        String params = accountNumber;
        String session_id = UUID.randomUUID().toString();
        String url = "";
        try {
            url = SecurityLayer.beforeLogin(params, session_id, endpoint, this);
        } catch (Exception e) {
            Log.Error(e);
        }
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
                    obj = SecurityLayer.decryptBeforeLogin(obj, StepTwo.this);

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        customermobile = obj.optString("customermobileno");
                        if (obj.has("usernames")) {
                            JSONArray array = new JSONArray(obj.optString("usernames"));
                            for (int i = 0; i < array.length(); i++) {
                                String un = array.getString(i);
                                usernames.add(un);
                            }
                        }
                        if (endpoint.contains("wallet")) {
                            customermobile = accountNumber;
                            usernames.add(accountNumber);
                            session.setString(SecurityQuestionsModel.KEY_STATUS, obj.optString(SecurityQuestionsModel.KEY_STATUS));
                            session.setString(SecurityQuestionsModel.KEY_SELID, obj.optString("username"));
                        }
                        showOTPField();
                    } else {
                        ResponseDialogs.warningDialogLovely(StepTwo.this, "Error", obj.optString(Constants.KEY_MSG));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    private void getOTP(String accountNumber, String mobileNumber, String OTP, final String endpoint) {
        //@Path("/otpvalidation/{actno}/{mobno}/{otp}")

        showLoadingProgress();
        String urlparam;
        if (endpoint.contains("wallet")) {
//@Path("/walletotpvalidation/{mobno}/{otp}")
            urlparam = accountNumber + "/" + OTP;
        } else {
            urlparam = accountNumber + "/" + mobileNumber + "/" + OTP;
        }
        String url = "";

        try {
            url = SecurityLayer.beforeLogin(urlparam, UUID.randomUUID().toString(), endpoint, getApplicationContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("otpvalidation", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptBeforeLogin(obj, getApplicationContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        onContinueClick(endpoint);
                    } else {
                        if (BuildConfig.DEBUG) {
                            onContinueClick(endpoint);
                        } else {
                            ResponseDialogs.warningDialogLovely(StepTwo.this, "Error", obj.optString(Constants.KEY_MSG));

                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("otpvalidation", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    public void onContinueClick(String endpoint) {

        Intent intent = new Intent(this, UsernameSelection.class);
        session.setString(SecurityQuestionsModel.KEY_ACCOUNT, accountNumber.getText().toString());
        session.setString(SecurityQuestionsModel.KEY_CUSTMOBILE, customermobile);
        session.setStringArray(SecurityQuestionsModel.KEY_USERNAMES, usernames);
        session.setString(KEY_REG_TYPE, selected + "");//0 = bank, 1 = wallet

        if (endpoint.contains("wallet")) {
            intent = new Intent(this, PINSetting.class);
            session.setString(SecurityQuestionsModel.KEY_CUSTMOBILE, customermobile);
            session.setStringArray(SecurityQuestionsModel.KEY_USERNAMES, usernames);
            session.setString(SecurityQuestionsModel.KEY_QUESTIONS, "NA");
            //session.setString(SecurityQuestionsModel.KEY_STATUS,obj.optString(SecurityQuestionsModel.KEY_STATUS));
        }

        startActivity(intent);
    }

}
