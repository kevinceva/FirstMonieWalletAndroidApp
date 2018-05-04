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

public class PINChange extends BaseActivity {
    EditText oldpass, new_password, conf_password;
    Button button;
    UBNSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinchange);
        setToolbarTitle("Password Change");
        session = new UBNSession(this);

        oldpass = findViewById(R.id.oldpass);
        new_password = findViewById(R.id.new_password);
        conf_password = findViewById(R.id.confirm_password);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.isNotNull(oldpass.getText().toString())) {
                    if (Utility.isNotNull(new_password.getText().toString())) {
                        if (Utility.isNotNull(conf_password.getText().toString())) {
                            if (new_password.getText().toString().equals(conf_password.getText().toString())) {
                                if (NetworkUtils.isConnected(PINChange.this)) {
                                    changePIN(session.getUserName(), oldpass.getText().toString(), new_password.getText().toString());
                                } else {
                                    noInternetDialog();
                                }
                            } else {
                                ResponseDialogs.warningStatic("Error", "Please re-enter your password", PINChange.this);
                            }
                        } else {
                            ResponseDialogs.warningStatic("Error", "Please re-enter your password", PINChange.this);
                        }
                    } else {
                        ResponseDialogs.warningStatic("Error", "Please enter your desired new password", PINChange.this);
                    }
                } else {
                    ResponseDialogs.warningStatic("Error", "Please enter your current password", PINChange.this);
                }
            }
        });
    }

    private void changePIN(String userid, String oldpassword, String newpassword) {
        //@Path("/changepassword/{userid}/{oldpassword}/{newpassword}")
        showLoadingProgress();

        String urlparam = userid + "/" + oldpassword + "/" + newpassword;
        String url = SecurityLayer.genURLCBC("changepassword", urlparam, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("changepassword", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        //ResponseDialogs.success("Success",obj.optString(Constants.KEY_MSG),PINChange.this);
                        ResponseDialogs.successToActivity("Success", obj.optString(Constants.KEY_MSG), PINChange.this, Sign_In.class, new Bundle());
                    } else {
                        ResponseDialogs.warningDialogLovely(PINChange.this, "Error", obj.optString(Constants.KEY_MSG));
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
