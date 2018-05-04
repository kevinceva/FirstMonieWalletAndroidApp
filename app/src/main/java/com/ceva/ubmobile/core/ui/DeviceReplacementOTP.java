package com.ceva.ubmobile.core.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceReplacementOTP extends BaseActivity {
    UBNSession session;
    String title = null;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.otp)
    TextInputEditText otp;
    @BindView(R.id.otp_gen)
    TextView otp_gen;

    String originalAppID = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_replacement_otp);
        ButterKnife.bind(this);

        session = new UBNSession(this);

        title = getIntent().getStringExtra("title");
        if (title == null) {
            setToolbarTitle("Device Replacement");
        } else {
            setToolbarTitle(title);
        }
        originalAppID = session.getString(SecurityLayer.KEY_APP_ID);

        //if (getIntent().getStringExtra("status").equalsIgnoreCase("D")) {
        if (NetworkUtils.isConnected(this)) {
            generateOTP(session.getUserName(), session.getPhoneNumber());
        } else {
            noInternetDialog();
        }
        // }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(otp.getText().toString())) {
                    warningDialog("Please enter OTP sent via SMS");
                } else {
                    if (NetworkUtils.isConnected(DeviceReplacementOTP.this)) {
                        validateOTP(session.getUserName(), session.getPhoneNumber(), otp.getText().toString());
                    } else {
                        noInternetDialog();
                    }
                }
            }
        });

        otp_gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateOTP(session.getUserName(), session.getPhoneNumber());
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Sign_In.class));
    }

    private void generateOTP(String username, String phoneNUmber) {
//@Path("/otpgenForDeviceReplacement/{username}/{phoneno}/{securitydata}")
        showLoadingProgress();
        String params = username + "/" + phoneNUmber;
        String url = SecurityLayer.genURLCBC("otpgenForDeviceReplacement", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, DeviceReplacementOTP.this);

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {

                    } else {
                        ResponseDialogs.warningDialogLovely(DeviceReplacementOTP.this, "Error", obj.optString(Constants.KEY_MSG));
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

    private void validateOTP(String username, String phoneNUmber, String otp) {
//@Path("/devicerepotpvalidation/{username}/{phoneno}/{otp}/{securitydata}")
        showLoadingProgress();
        String params = username + "/" + phoneNUmber + "/" + otp;
        String url = SecurityLayer.genURLCBC("devicerepotpvalidation", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, DeviceReplacementOTP.this);

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        session.setString("devicestatus", "N");
                        session.setString(SecurityLayer.KEY_APP_ID, originalAppID);
                        ResponseDialogs.successToActivity("Registered!", "This device has been successfully linked to your profile. "
                                , DeviceReplacementOTP.this, DashBoard.class, new Bundle());
                    } else {
                        ResponseDialogs.warningDialogLovely(DeviceReplacementOTP.this, "Error", obj.optString(Constants.KEY_MSG));
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

}
