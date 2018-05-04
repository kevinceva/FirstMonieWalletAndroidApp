package com.ceva.ubmobile.core.signon;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExistingUser extends BaseActivity {
    RadioGroup userGroup;
    EditText tran_pin, conftranpin;
    UBNSession session;
    Button button;
    String selectedAccount;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new UBNSession(this);
        context = this;
        setContentView(R.layout.activity_existing_user);
        setToolbarTitle("Sign On");
        userGroup = (RadioGroup) findViewById(R.id.usernameGroup);
        userGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                RadioButton selectedRadio = (RadioButton) findViewById(i);
                String[] account = selectedRadio.getText().toString().split("-");
                int m = account.length;

                selectedAccount = account[m - 1].trim();
                Log.debug("selected account" + selectedAccount);

            }
        });
        tran_pin = (EditText) findViewById(R.id.tran_pin);
        conftranpin = (EditText) findViewById(R.id.conftranpin);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tranpinSt = tran_pin.getText().toString();
                String conftranpinSt = conftranpin.getText().toString();

                if (Utility.isNotNull(tranpinSt)) {
                    if (Utility.isNotNull(conftranpinSt)) {
                        if (tranpinSt.equals(conftranpinSt)) {
                            setTransactionPin(selectedAccount, session.getUserName(), tranpinSt);
                        } else {
                            ResponseDialogs.warningDialogLovely(context, "Error", "Please enter identical values for transaction pin and confirm transaction pin field.");

                        }
                    } else {
                        ResponseDialogs.warningDialogLovely(context, "Confirm Pin", "Please re-enter your transaction pin.");
                    }
                } else {
                    ResponseDialogs.warningDialogLovely(context, "Transaction Pin Required", "Please enter your transaction pin.");
                }

            }

        });

        setAccounts();

    }

    private void setAccounts() {
        //for (int row = 0; row < 1; row++) {
        //RadioGroup ll = new RadioGroup(this);
        int row = 4;
        int number = session.getAccountNumbers().size();
        if (number > 0) {
            for (int i = 0; i < number; i++) {

                RadioButton rdbtn = new RadioButton(this);
                rdbtn.setId(i);
                rdbtn.setText(session.getAccountNumbers().get(i));

                userGroup.addView(rdbtn);
                if (i == 0) {
                    rdbtn.setChecked(true);
                }
            }

        }

        //((ViewGroup) findViewById(R.id.radiogroup)).addView(ll);
        //}
    }

    private void setTransactionPin(String accountNumber, final String userid, final String pin) {
        //@Path("/intbankingCustregister/{acctnumber}/{userid}/{txnpin}")

        showLoadingProgress();

        String param = accountNumber + "/" + userid + "/" + pin;
        String url = SecurityLayer.genURLCBC("intbankingCustregister", param, getApplicationContext());

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("intbankingCustregister", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        ResponseDialogs.successToActivity("Registered!", obj.optString(Constants.KEY_MSG), context, DashBoard.class, new Bundle());

                    } else {
                        ResponseDialogs.warningDialogLovely(context, "Error", obj.optString(Constants.KEY_MSG));
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
                com.ceva.ubmobile.core.ui.Log.debug("intbankingCustregister", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

}
