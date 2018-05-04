package com.ceva.ubmobile.core.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionPinChange extends BaseActivity {
    EditText oldpin, new_pin, conf_pin;
    Button button;
    UBNSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactionpinchange);
        setToolbarTitle("Pin Change");
        session = new UBNSession(this);

        oldpin = findViewById(R.id.oldpin);
        new_pin = findViewById(R.id.new_pin);
        conf_pin = findViewById(R.id.confirm_pin);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.isNotNull(oldpin.getText().toString())) {
                    if (Utility.isNotNull(new_pin.getText().toString())) {
                        if (Utility.isNotNull(conf_pin.getText().toString())) {
                            if (new_pin.getText().toString().equals(conf_pin.getText().toString())) {
                                if (NetworkUtils.isConnected(TransactionPinChange.this)) {
                                    String old = oldpin.getText().toString();
                                    String newp = new_pin.getText().toString();

                                    changePIN(session.getUserName(), old, newp, session.getPhoneNumber());
                                } else {
                                    noInternetDialog();
                                }
                            } else {
                                ResponseDialogs.warningStatic("Error", "Please re-enter your pin", TransactionPinChange.this);
                            }
                        } else {
                            ResponseDialogs.warningStatic("Error", "Please re-enter your pin", TransactionPinChange.this);
                        }
                    } else {
                        ResponseDialogs.warningStatic("Error", "Please enter your desired new pin", TransactionPinChange.this);
                    }
                } else {
                    ResponseDialogs.warningStatic("Error", "Please enter your current pin", TransactionPinChange.this);
                }
            }
        });
    }

    private void changePIN(String userid, String oldpinword, String newpin, String mobile) {
        //@Path("/customerupdatepin/{userid}/{newpin}/{oldpin}/{mobileno}")
        showLoadingProgress();

        String urlparam = userid + "/" + newpin + "/" + oldpinword + "/" + mobile;
        String url = SecurityLayer.genURLCBC("customerupdatepin", urlparam, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("changepin", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        ResponseDialogs.success("Success", obj.optString(Constants.KEY_MSG), TransactionPinChange.this);
                    } else {
                        ResponseDialogs.warningDialogLovely(TransactionPinChange.this, "Error", obj.optString(Constants.KEY_MSG));
                        //onContinueClick();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    SecurityLayer.generateToken(getApplicationContext());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                SecurityLayer.generateToken(getApplicationContext());
                com.ceva.ubmobile.core.ui.Log.debug("otpvalidation", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }
}
