package com.ceva.ubmobile.core.ui.merchantpayments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.BillerModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferenceEntry extends BaseActivity {
    @BindView(R.id.btnStatus)
    Button btnStatus;
    @BindView(R.id.reference)
    EditText reference;
    UBNSession session;
    @BindView(R.id.account)
    Spinner accountFrom;
    List<String> accountList = new ArrayList<>();
    @BindView(R.id.merchantName)
    TextView merchantName;
    String merchantname;
    String merchantcode;
    private List<BillerModel> billerList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference_entry);
        ButterKnife.bind(this);

        session = new UBNSession(this);
        setToolbarTitle("Pay Merchants");
        /*final String merchantname = getIntent().getStringExtra(BillerModel.KEY_BILLER_NAME).trim();
        final String merchantcode = getIntent().getStringExtra(BillerModel.KEY_BILLER_ID).trim();*/

        fetchMerchantsList(session.getUserName());

        accountList.addAll(session.getAccountNumbersNoDOM());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_row, accountList);
        accountFrom.setAdapter(adapter);

        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String refcode = reference.getText().toString();
                if (TextUtils.isEmpty(refcode)) {
                    reference.setError("Please enter the reference code");
                } else {
                    if (NetworkUtils.isConnected(ReferenceEntry.this)) {
                        String accountDebit = accountFrom.getSelectedItem().toString();
                        String[] accountSplit = accountDebit.split("-");
                        String accountNo = accountSplit[(accountSplit.length - 1)].trim();

                        fetchStatus(session.getUserName(), merchantcode, merchantname, refcode, accountNo);
                    } else {
                        noInternetDialog();
                    }
                }
            }
        });

    }

    private void fetchStatus(String username, final String merchantcode, final String merchantname, String merchantreferencecode, final String accountNumber) {
        //@Path("/getbookingstatus/{username}/{merchantcode}/{merchantname}/{merchantrefrencecode}")

        showLoadingProgress();

        String params = username + "/" + merchantcode + "/" + merchantname + "/" + merchantreferencecode;
        String url = SecurityLayer.genURLCBC("getbookingstatus", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("getMerchantsList", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);

                    if (responsecode.equals("00")) {
                        String cevaref = obj.optString("cevarefrencecode");
                        Intent intent = new Intent(ReferenceEntry.this, PayMerchant.class);

                        intent.putExtra(BillerModel.KEY_BILLER_ID, merchantcode);
                        intent.putExtra(BillerModel.KEY_BILLER_NAME, merchantname);
                        intent.putExtra(BillerModel.KEY_CUSTOM_FIELD1, cevaref);
                        intent.putExtra("account", accountNumber);
                        intent.putExtra("payload", obj.toString());
                        startActivity(intent);

                    } else {

                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, ReferenceEntry.this);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("billerfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    private void fetchMerchantsList(String username) {
        //@Path("/getbillers/{username}")
        billerList.clear();
        showLoadingProgress();

        String params = username;
        String url = SecurityLayer.genURLCBC("getMerchants", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("getMerchantsList", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {
                        JSONArray objData = new JSONArray(obj.optString("MERCHANTS"));
                        int m = objData.length();
                        Log.debug("number of billers " + objData.length());

                        for (int j = 0; j < m; j++) {
                            JSONObject billerDet = objData.getJSONObject(j);

                            BillerModel biller = new BillerModel(billerDet.optString("MERCHANT_NAME"),
                                    billerDet.optString("MERCHANT_CODE"),
                                    billerDet.optString("MERCHANT_CODE"),
                                    billerDet.optString("MERCHANT_CODE"),
                                    billerDet.optString("MERCHANT_CODE"),
                                    billerDet.optString("MERCHANT_NAME"));
                            billerList.add(biller);

                        }
                        merchantname = billerList.get(0).getBillerName();
                        merchantcode = billerList.get(0).getBillerID();
                        merchantName.setText(merchantname);

                        //ben_accountName = objData.optString("accountName");
                        // Log.debug("beneficiaryName",ben_accountName);
                        //showConfirm(beneficiary_type, ben_accountName, beneficiciary_accNumber, beneficiary_bank);
                    } else {

                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, ReferenceEntry.this);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("billerfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

}
