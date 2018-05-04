package com.ceva.ubmobile.core.signon;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.SecurityQuestionsModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;

import org.json.JSONObject;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ceva.ubmobile.core.ui.Log.debug;

public class PasswordSetting extends BaseActivity {
    @BindView(R.id.pass)
    EditText pass;
    @BindView(R.id.confpass)
    EditText confpass;
    @BindView(R.id.conftranpin)
    EditText tranpin;
    @BindView(R.id.button)
    Button button;
    Context context;

    UBNSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setting);
        setToolbarTitle("Security Setup");
        ButterKnife.bind(this);
        context = this;
        session = new UBNSession(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stPass = pass.getText().toString();
                String stCon = confpass.getText().toString();
                String stTranpin = tranpin.getText().toString();

                if (Utility.isNotNull(stPass)) {
                    if (Utility.isNotNull(stCon)) {
                        if (Utility.isNotNull(stTranpin)) {
                            if (stPass.equals(stCon)) {
                                if (NetworkUtils.isConnected(context)) {
                                    //setDesiredPassword("kiptoo", stPass, stTranpin);
                                    setDesiredPassword(session.getUserName(), stPass, stTranpin);
                                } else {
                                    noInternetDialog();
                                }
                            } else {
                                ResponseDialogs.warningStatic("Error", "Password and confirm password must be identical!", context);
                            }
                        } else {
                            ResponseDialogs.warningStatic("Error", "Please enter your transaction pin", context);
                        }
                    } else {
                        ResponseDialogs.warningStatic("Error", "Please re-enter your desired password", context);
                    }
                } else {
                    ResponseDialogs.warningStatic("Error", "Please enter your desired password", context);
                }
            }
        });

    }

    private void setDesiredPassword(String username, final String password, final String pin) {
        //@Path("/desirepassword/{userid}/{password}/{pin}")

        showLoadingProgress();
        String url = "";
        String urlparam = username + "/" + password + "/" + pin;
        String data = null;
        try {
            if (session.getString(SecurityQuestionsModel.KEY_STATUS).equals("C")) {
                url = SecurityLayer.genURLCBC("desirepassword", urlparam, getApplicationContext());
            } else {
                url = SecurityLayer.beforeLogin(urlparam, UUID.randomUUID().toString(), "desirepassword", getApplicationContext());
            }
        } catch (Exception e) {
            Log.Error(e);
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                debug("desirepassword", response.body());
                dismissProgress();

                try {

                    JSONObject obj = new JSONObject(response.body());
                    if (session.getString(SecurityQuestionsModel.KEY_STATUS).equals("C")) {
                        obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    } else {
                        obj = SecurityLayer.decryptBeforeLogin(obj, getApplicationContext());
                    }

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {

                        // onContinueClick();
                        ResponseDialogs.successToActivity("Successful Registration", obj.optString(Constants.KEY_MSG), context, DashBoard.class, new Bundle());

                    } else {
                        ResponseDialogs.warningDialogLovely(PasswordSetting.this, "Error", obj.optString(Constants.KEY_MSG));
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
                debug("desirepassword/", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

}
