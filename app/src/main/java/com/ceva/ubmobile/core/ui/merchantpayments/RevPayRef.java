package com.ceva.ubmobile.core.ui.merchantpayments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
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

public class RevPayRef extends BaseActivity {
    @BindView(R.id.reference)
    EditText reference;
    @BindView(R.id.btnStatus)
    Button btnStatus;
    UBNSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rev_pay_ref);
        ButterKnife.bind(this);
        setToolbarTitle("LASG RevPay");

        session = new UBNSession(this);

        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isConnected(RevPayRef.this)) {
                    if (TextUtils.isEmpty(reference.getText().toString())) {
                        reference.setError("Please enter the invoice/bill reference");
                    } else {
                        fetchStatus(session.getUserName(), reference.getText().toString());
                    }
                } else {
                    noInternetDialog();
                }
            }
        });

    }

    //@Path("/revpayvalidateguid/{userid}/{revpaywebguid}")
    private void fetchStatus(String username, final String refnumber) {

        showLoadingProgress();

        String params = username + "/" + refnumber;
        String url = SecurityLayer.genURLCBC("revpayvalidateguid", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);

                    if (responsecode.equals("00")) {
                        String data = obj.optString("validateguidresp");
                        JSONObject val = new JSONObject(data);
                        //val.put("webref",refnumber);
                        Intent intent = new Intent(RevPayRef.this, RevPayConfirm.class);
                        intent.putExtra("data", data);
                        intent.putExtra("webref", refnumber);
                        startActivity(intent);
                    } else {
                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, RevPayRef.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("Server Error", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

}
