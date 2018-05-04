package com.ceva.ubmobile.core.signon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.core.ui.Sign_In;
import com.ceva.ubmobile.models.SecurityQuestionsModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PINSetting extends BaseActivity {
    String STATUS = null;
    LinearLayout passwordFields;
    TextView tranPinTxt;
    UBNSession session;
    EditText password, confpass, tranpin, conftranpin;
    Button button;
    Context context;
    String accountType = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinsetting);
        setToolbarTitle("PIN Setting");
        session = new UBNSession(this);
        context = this;
        accountType = session.getString(StepTwo.KEY_REG_TYPE);
        Log.debug("account type", accountType);
        passwordFields = findViewById(R.id.password_fields);

        tranPinTxt = findViewById(R.id.tranPinTxt);

        STATUS = session.getString(SecurityQuestionsModel.KEY_STATUS);

        password = findViewById(R.id.pass);
        confpass = findViewById(R.id.confpass);
        tranpin = findViewById(R.id.tranpin);
        conftranpin = findViewById(R.id.conftranpin);

        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = password.getText().toString();
                String confpassSt = confpass.getText().toString();
                String tranpinSt = tranpin.getText().toString();
                String conftranpinSt = conftranpin.getText().toString();

                if (STATUS.equals("U")) {
                    tranpinSt = conftranpinSt;

                }
                if (Utility.isNotNull(pass)) {
                    if (Utility.isNotNull(confpassSt)) {
                        if (Utility.isNotNull(conftranpinSt)) {
                            if (pass.equals(confpassSt)) {
                                if (tranpinSt.equals(conftranpinSt)) {
                                    if (NetworkUtils.isConnected(context)) {
                                        if (STATUS.equals("U")) {
                                            String endpoint = "validatetxnpin";
                                            String regendpoint = "register";

                                            if (accountType.equals("1")) {
                                                endpoint = "walletvalidatetxnpin";
                                                regendpoint = "selfwalletregistration";
                                            }
                                            Log.debug("regendpoint if", regendpoint);
                                            validateTXPin(session.getString(SecurityQuestionsModel.KEY_CUSTMOBILE), tranpinSt, pass, endpoint, regendpoint);
                                        } else {

                                            session.setString(SecurityQuestionsModel.KEY_PASSWORD, pass);
                                            session.setString(SecurityQuestionsModel.KEY_PIN, tranpinSt);
                                            String regendpoint = "register";

                                            if (accountType.equals("1")) {

                                                regendpoint = "selfwalletregistration";
                                            }
                                            Log.debug("regendpoint else", regendpoint);

                                            registerUser(session.getString(SecurityQuestionsModel.KEY_ACCOUNT),
                                                    session.getString(SecurityQuestionsModel.KEY_SELID),
                                                    session.getString(SecurityQuestionsModel.KEY_PASSWORD),
                                                    session.getString(SecurityQuestionsModel.KEY_PIN),
                                                    "na",
                                                    "na",
                                                    "na",
                                                    session.getString(SecurityQuestionsModel.KEY_STATUS), regendpoint);
                                        }
                                    } else {
                                        ResponseDialogs.warningDialogLovely(context, "Error", getString(R.string.error_no_internet_connection));
                                    }
                                } else {
                                    ResponseDialogs.warningDialogLovely(context, "Error", "Please enter identical values for transaction pin and confirm transaction pin field.");

                                }
                            } else {
                                ResponseDialogs.warningDialogLovely(context, "Error", "Please enter identical values for password and confirm password field.");
                            }
                        } else {
                            ResponseDialogs.warningDialogLovely(context, "Transaction Pin Required", "Please enter your transaction pin.");
                        }
                    } else {
                        ResponseDialogs.warningDialogLovely(context, "Confirm Password required", "Please re-enter your password.");
                    }
                } else {
                    ResponseDialogs.warningDialogLovely(context, "Password required", "Please enter your desired password.");
                }

            }

        });

        if (STATUS.equals("U")) {
            passwordFields.setVisibility(View.GONE);
            //conftranpin.setVisibility(View.GONE);
            tranPinTxt.setText("Existing Transaction PIN");
            tranPinTxt.setHint("Enter transaction pin");
        }

    }

    private void validateTXPin(String mobileNumber, final String pin, final String password, String endpoint, final String regendpoint) {
        //@Path("/validatetxnpin/{mobileNo}/{pin}")

        showLoadingProgress();

        String urlparam = mobileNumber + "/" + pin;
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
                Log.debug("validatetxnpin", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptBeforeLogin(obj, getApplicationContext());

                    session.setString(SecurityQuestionsModel.KEY_PASSWORD, password);
                    session.setString(SecurityQuestionsModel.KEY_PIN, pin);

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {

                        //onContinueClick();
                        registerUser(session.getString(SecurityQuestionsModel.KEY_ACCOUNT),
                                session.getString(SecurityQuestionsModel.KEY_SELID),
                                session.getString(SecurityQuestionsModel.KEY_PASSWORD),
                                session.getString(SecurityQuestionsModel.KEY_PIN),
                                "na",
                                "na",
                                "na",
                                session.getString(SecurityQuestionsModel.KEY_STATUS), regendpoint);
                    } else {
                        ResponseDialogs.warningDialogLovely(PINSetting.this, "Error", obj.optString(Constants.KEY_MSG));
                        //onContinueClick();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("validatetxnpin", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    private void registerUser(String accountnumber, final String userName, String pin, String tranpin, String quest_id, String question, String answer, String status, String endpoint) {
        //@Path("/register/{acctnumber}/{userid}/{pin}/{txnpin}/{sqid}/{sqs}/{sqans}/{status}")

        showLoadingProgress();
        String urlparam = "";
        if (endpoint.contains("wallet")) {
//@Path("/selfwalletregistration/{mobileno}/{userid}/{password}/{pin}/{userstatus}")
            urlparam = accountnumber + "/" + userName + "/" + pin + "/" + tranpin + "/" + status;
        } else {
            urlparam = accountnumber + "/" + userName + "/" + pin + "/" + tranpin + "/" + quest_id + "/" + question + "/" + answer + "/" + status;

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
                Log.debug("register", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptBeforeLogin(obj, getApplicationContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        session.setUserName(userName);
                        ResponseDialogs.sucessDialogLovelyToActivity(PINSetting.this, "Successful Registration", obj.optString(Constants.KEY_MSG), Sign_In.class);

                    } else {
                        ResponseDialogs.warningDialogLovely(PINSetting.this, "Error", obj.optString(Constants.KEY_MSG));
                        //onContinueClick();
                    }

                    session.clearData(SecurityQuestionsModel.KEY_PASSWORD);
                    session.clearData(SecurityQuestionsModel.KEY_ACCOUNT);
                    session.clearData(SecurityQuestionsModel.KEY_PIN);
                    session.clearData(SecurityQuestionsModel.KEY_QUESTIONS);
                    session.clearData(SecurityQuestionsModel.KEY_CUSTMOBILE);
                    session.clearData(SecurityQuestionsModel.KEY_SELID);
                    session.clearData(SecurityQuestionsModel.KEY_STATUS);
                    session.clearData(SecurityQuestionsModel.KEY_USERNAMES);
                    session.clearData(SecurityQuestionsModel.KEY_QST);
                    session.clearData(SecurityQuestionsModel.KEY_QST_ID);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("register", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    public void onContinueClick() {
        startActivity(new Intent(this, SecurityQuestion.class));
    }
}
