package com.ceva.ubmobile.core.ui;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.github.pinball83.maskededittext.MaskedEditText;

import org.json.JSONObject;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DebitCardValidation extends BaseActivity {
    @BindView(R.id.cardNumber)
    MaskedEditText cardNumber;
    @BindView(R.id.expDate)
    MaskedEditText expDate;
    @BindView(R.id.cvv)
    EditText cvv;
    @BindView(R.id.cardPin)
    EditText cardPin;
    @BindView(R.id.btnValidate)
    Button btnValidate;
    UBNSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debit_card_validation);
        ButterKnife.bind(this);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        setToolbarTitle("Profile Activation");
        session = new UBNSession(this);
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardNumberSt = cardNumber.getUnmaskedText();
                String expDateSt = expDate.getUnmaskedText();
                String cvvSt = cvv.getText().toString();
                String cardPinst = cardPin.getText().toString();

                if (TextUtils.isEmpty(cardNumberSt)) {
                    warningDialog("Please enter your card number");
                } else {
                    if (TextUtils.isEmpty(expDateSt)) {
                        warningDialog("Please enter the card expiry date");
                    } else {
                        if (TextUtils.isEmpty(cvvSt)) {
                            warningDialog("Please enter cvv");
                        } else {
                            if (TextUtils.isEmpty(cardPinst)) {
                                warningDialog("Please enter your card PIN");
                            } else {
                                if (NetworkUtils.isConnected(DebitCardValidation.this)) {

                                    if (expDateSt.length() == 4) {

                                        String[] dates = expDateSt.split("(?!^)");
                                        expDateSt = dates[2] + dates[3] + dates[0] + dates[1];

                                        validateAccount(session.getUserName(), cardNumberSt, cardPinst, expDateSt, cvvSt);
                                    } else {
                                        warningDialog("Please enter a valid expiry date");
                                    }

                                } else {
                                    noInternetDialog();
                                }
                            }
                        }
                    }
                }
            }
        });

    }

    private void validateAccount(String username, String cardNumber, String cardpin, String expDate, String cvv) {
        //@Path("/debitcardvalidation/{username}/{dbcardno}/{dbcardpin}/{dbexpdate}/{dbcvv2}
        showLoadingProgress();

        String params = username + "/" + cardNumber + "/" + cardpin + "/" + expDate + "/" + cvv;
        String session_id = UUID.randomUUID().toString();
        String url = SecurityLayer.genURLCBC("debitcardvalidation", params, DebitCardValidation.this);
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
                    obj = SecurityLayer.decryptTransaction(obj, DebitCardValidation.this);

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        ResponseDialogs.successToActivity("Success", obj.optString(Constants.KEY_MSG), DebitCardValidation.this, Profile.class, new Bundle());
                    } else {
                        ResponseDialogs.warningDialogLovely(DebitCardValidation.this, "Error", obj.optString(Constants.KEY_MSG));
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
                Log.debug("ubnaccountsfail", t.toString());
                warningDialog(getString(R.string.error_server));
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }
}
