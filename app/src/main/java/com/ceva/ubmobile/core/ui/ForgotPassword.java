package com.ceva.ubmobile.core.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends BaseActivity {
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.btncontinue)
    Button btnContinue;

    @BindView(R.id.new_password)
    EditText new_password;
    @BindView(R.id.confirm_password)
    EditText confirm_password;
    @BindView(R.id.code)
    EditText code;

    @BindView(R.id.password_field)
    LinearLayout password_field;
    @BindView(R.id.username_field)
    LinearLayout username_field;

    boolean isUsernameEntered = false;

    String accountNo, mobileNo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        setToolbarTitle(getString(R.string.passwordrecovery));
        password_field.setVisibility(View.GONE);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isConnected(getApplicationContext())) {
                    if (isUsernameEntered) {
                        if (!TextUtils.isEmpty(new_password.getText().toString())) {
                            if (!TextUtils.isEmpty(confirm_password.getText().toString())) {
                                if (!TextUtils.isEmpty(code.getText().toString())) {
                                    if (new_password.getText().toString().equals(confirm_password.getText().toString())) {
                                        String pass = null, user = null;
                                        try {
                                            pass = URLEncoder.encode(new_password.getText().toString(), "UTF-8");
                                            user = URLEncoder.encode(username.getText().toString().trim(), "UTF-8");

                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                        invokePasswordRecovery(user, pass, mobileNo, accountNo, code.getText().toString());
                                    } else {
                                        warningDialog("Password mismatch");
                                    }
                                } else {
                                    warningDialog("Please enter code sent via SMS");
                                }
                            } else {
                                warningDialog("Please confirm password");
                            }
                        } else {
                            warningDialog("Please enter a new password");
                        }
                    } else {

                        if (TextUtils.isEmpty(username.getText().toString())) {
                            warningDialog("Please enter your username");
                        } else {
                            validateUsername(username.getText().toString().trim());
                        }

                    }
                } else {
                    noInternetDialog();
                }
            }
        });

    }

    private void showPasswordFields() {
        isUsernameEntered = true;
        username_field.setVisibility(View.GONE);
        password_field.setVisibility(View.VISIBLE);
        btnContinue.setText("Submit");
    }

    private void validateUsername(String username) {
        //@Path("/otpvalidation/{actno}/{mobno}/{otp}")

        showLoadingProgress();

        String urlparam = username;
        String url = "";

        try {
            url = SecurityLayer.beforeLogin(urlparam, UUID.randomUUID().toString(), "forgetpassword", getApplicationContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptBeforeLogin(obj, getApplicationContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        accountNo = obj.optString("accountno");
                        mobileNo = obj.optString("mobileno");
                        showPasswordFields();
                    } else {
                        ResponseDialogs.warningDialogLovely(ForgotPassword.this, "Error", obj.optString(Constants.KEY_MSG));
                        //onContinueClick();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    warningDialog(getString(R.string.error_server));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("otpvalidation", t.toString());
                warningDialog(getString(R.string.error_server));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    private void invokePasswordRecovery(String username, String password, String phone, String accountNumber, String otp) {
        //forgetpasswordconfirm/collins/Collins1/2348036010740/0005534521/646901
        //@Path("/forgetpasswordconfirm/{userid}/{password}/{mobileno}/{actno}/{otp}

        showLoadingProgress();

        String url = "";

        try {

            //password = URLEncoder.encode(password, "UTF-8");

            String params = username + "/" + password + "/" + phone + "/" + accountNumber + "/" + otp;
            url = SecurityLayer.beforeLogin(params, UUID.randomUUID().toString(), "forgetpasswordconfirm", getApplicationContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptBeforeLogin(obj, getApplicationContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        ResponseDialogs.successToActivity("Success", obj.optString(Constants.KEY_MSG), ForgotPassword.this, Sign_In.class, new Bundle());
                    } else {
                        ResponseDialogs.warningDialogLovely(ForgotPassword.this, "Error", obj.optString(Constants.KEY_MSG));
                        //onContinueClick();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    warningDialog(getString(R.string.error_server));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("otpvalidation", t.toString());
                warningDialog(getString(R.string.error_server));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }
}
