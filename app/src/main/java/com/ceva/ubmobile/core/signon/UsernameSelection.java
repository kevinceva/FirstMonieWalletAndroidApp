package com.ceva.ubmobile.core.signon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsernameSelection extends BaseActivity {
    RadioGroup usernameGroup;
    List<String> usernames = new ArrayList<>();
    LinearLayout custom;
    String selectedUserName = null;
    EditText custUser;
    boolean isCustom = false;
    String accountnumber = null, customermobile = null;
    ArrayList<SecurityQuestionsModel> securityQuestions = new ArrayList<>();
    Button button;
    UBNSession session;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usernameselection);
        setToolbarTitle("Username Selection");
        session = new UBNSession(this);

        usernames = session.getStringArray(SecurityQuestionsModel.KEY_USERNAMES);
        accountnumber = session.getString(SecurityQuestionsModel.KEY_ACCOUNT);
        customermobile = session.getString(SecurityQuestionsModel.KEY_CUSTMOBILE);

        custom = findViewById(R.id.custom);
        custUser = findViewById(R.id.custUser);

        usernameGroup = findViewById(R.id.usernameGroup);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCustom) {
                    selectedUserName = custUser.getText().toString();
                }
                if (Utility.isNotNull(selectedUserName)) {
                    if (NetworkUtils.isConnected(UsernameSelection.this)) {
                        validateUsername(selectedUserName.trim(), customermobile);
                    } else {
                        noInternetDialog();
                    }
                } else {
                    ResponseDialogs.warningDialogLovely(UsernameSelection.this, "Error", "Please select or enter your desired username.");

                }

            }
        });
        setUsernames();

    }

    private void setUsernames() {
        //for (int row = 0; row < 1; row++) {
        //RadioGroup ll = new RadioGroup(this);
        int row = 4;
        int number = usernames.size();
        if (number > 0) {
            for (int i = 0; i < number; i++) {
                RadioButton rdbtn = new RadioButton(this);
                rdbtn.setId((row * 2) + i);
                rdbtn.setText(usernames.get(i));
                usernameGroup.addView(rdbtn);
            }
        }
        final int lastid = (row * 2) + number;
        RadioButton rdbtn = new RadioButton(this);
        rdbtn.setId(lastid);
        rdbtn.setText("Custom Username");
        usernameGroup.addView(rdbtn);

        usernameGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == lastid) {
                    custom.setVisibility(View.VISIBLE);
                    isCustom = true;
                } else {
                    custom.setVisibility(View.GONE);
                    isCustom = false;
                    RadioButton selectedRadio = findViewById(i);
                    selectedUserName = selectedRadio.getText().toString();

                }
            }
        });

        //((ViewGroup) findViewById(R.id.radiogroup)).addView(ll);
        //}
    }


    private void validateUsername(final String userName, String mobileNumber) {
        securityQuestions.clear();
        //@Path("/isUserExist/{user}/{mobileno}")

        showLoadingProgress();

        String urlparam = userName + "/" + mobileNumber;
        String url = "";

        try {
            url = SecurityLayer.beforeLogin(urlparam, UUID.randomUUID().toString(), "isUserExist", getApplicationContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("isUserExist", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptBeforeLogin(obj, getApplicationContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {

                        session.setString(SecurityQuestionsModel.KEY_SELID, userName);
                        session.setString(SecurityQuestionsModel.KEY_QUESTIONS, obj.optString(SecurityQuestionsModel.KEY_QUESTIONS));
                        session.setString(SecurityQuestionsModel.KEY_STATUS, obj.optString(SecurityQuestionsModel.KEY_STATUS));
                        /*for (int i = 0; i < array.length(); i++) {
                            JSONObject un = array.getJSONObject(i);
                            SecurityQuestionsModel item = new SecurityQuestionsModel(un.optString(SecurityQuestionsModel.KEY_QST), un.optString(SecurityQuestionsModel.KEY_QST_ID));
                            securityQuestions.add(item);
                        }*/
                        onContinueClick();
                    } else {
                        ResponseDialogs.warningDialogLovely(UsernameSelection.this, "Error", obj.optString(Constants.KEY_MSG));
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
                com.ceva.ubmobile.core.ui.Log.debug("isUserExist", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    public void onContinueClick() {
        startActivity(new Intent(this, PINSetting.class));
    }
}
