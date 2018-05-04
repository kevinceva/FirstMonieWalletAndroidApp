package com.ceva.ubmobile.core.ui.merchantpayments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.ConfirmAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.BillerModel;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.NumberUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayMerchant extends BaseActivity {
    @BindView(R.id.confirm_list)
    RecyclerView confirmationRecyler;
    ConfirmAdapter confirmAdapter;
    List<ConfirmModel> confirmItems = new ArrayList<>();
    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.btnPay)
    Button btnPay;
    @BindView(R.id.pin)
    EditText pin;
    UBNSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_merchant);
        ButterKnife.bind(this);
        setToolbarTitle("Confirm Details");
        session = new UBNSession(this);

        confirmAdapter = new ConfirmAdapter(confirmItems, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        confirmationRecyler.setLayoutManager(mLayoutManager);
        confirmationRecyler.setItemAnimator(new DefaultItemAnimator());
        confirmationRecyler.setAdapter(confirmAdapter);

        final String merchantname = getIntent().getStringExtra(BillerModel.KEY_BILLER_NAME).trim();
        final String merchantcode = getIntent().getStringExtra(BillerModel.KEY_BILLER_ID).trim();
        final String cevaref = getIntent().getStringExtra(BillerModel.KEY_CUSTOM_FIELD1).trim();
        final String accountNo = getIntent().getStringExtra("account").trim();
        String payload = getIntent().getStringExtra("payload").trim();

        try {
            JSONObject jsonObject = new JSONObject(payload);
            confirmItems.addAll(printJsonObject(jsonObject));
            confirmAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.Error(e.toString());
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pinStr = pin.getText().toString();
                if (TextUtils.isEmpty(pinStr)) {
                    pin.setError("Please enter your PIN");
                } else {
                    if (NetworkUtils.isConnected(PayMerchant.this)) {
                        doPayment(session.getUserName(), merchantcode, cevaref, pinStr, accountNo);
                    } else {
                        noInternetDialog();
                    }
                }
            }
        });

    }

    public List<ConfirmModel> printJsonObject(JSONObject jsonObj) throws JSONException {

        Iterator<?> keys = jsonObj.keys();
        List<ConfirmModel> confirmModelList = new ArrayList<>();

        JSONObject obj = jsonObj.optJSONObject("merchantrespose");

        double fee = Double.parseDouble(jsonObj.optString("fee"));
        String feeSt = NumberUtilities.getWithDecimalPlusCurrency(fee);
        //JSONObject
        JSONObject merch = obj.optJSONObject("Object");

        Log.debug("merchant response", merch.toString());
        ConfirmModel confirm = new ConfirmModel("Full Name", merch.optString("FullName"));
        confirmModelList.add(confirm);

        confirm = new ConfirmModel("Departure Date", merch.optString("DepartureDate"));
        confirmModelList.add(confirm);

        confirm = new ConfirmModel("Departure Time", merch.optString("DepartureTime"));
        confirmModelList.add(confirm);

        confirm = new ConfirmModel("Seat Number", merch.optString("SeatNumber"));
        confirmModelList.add(confirm);

        confirm = new ConfirmModel("Route Name", merch.optString("RouteName"));
        confirmModelList.add(confirm);

        double amount = Double.parseDouble(merch.optString("Amount"));

        confirm = new ConfirmModel("Amount", NumberUtilities.getWithDecimalPlusCurrency(amount));
        confirmModelList.add(confirm);

        confirm = new ConfirmModel("Fee", feeSt);
        confirmModelList.add(confirm);

        /*while (keys.hasNext()) {
            String key = (String) keys.next();
            String keyStr = (String) key;
            Object keyvalue = jsonObj.get(key);

            System.out.println("key: " + keyStr + " value: " + keyvalue);



            if ( keyvalue instanceof JSONObject) {
                printJsonObject((JSONObject) keyvalue);
            }
            else if(keyvalue instanceof JSONArray){

            }
            else{
                String[] r = keyStr.split("(?=\\p{Upper})");
                String label = TextUtils.join(" ",r);
                if(keyStr.equals("respcode") || keyStr.equals("respdesc") || keyStr.equals("cevarefrencecode")){
                    //Do nothing
                }else{
                    ConfirmModel confirm = new ConfirmModel(label, keyvalue.toString());
                    confirmModelList.add(confirm);
                }

            }
        }*/
        return confirmModelList;


       /* for (Object key : jsonObj.keys()) {
            //based on you key types
            String keyStr = (String) key;
            Object keyvalue = jsonObj.get(keyStr);

            //Print key and value
            System.out.println("key: " + keyStr + " value: " + keyvalue);

            //for nested objects iteration if required
            if (keyvalue instanceof JSONObject)
                printJsonObject((JSONObject) keyvalue);
        }*/
    }

    private void doPayment(String username, final String merchantcode, final String cevacode, String pin, String accountno) {
        //@Path("/processonholdpayment/{username}/{merchantcode}/{cevarefrencecode}/{pin}/{custactno}")

        showLoadingProgress();

        String params = username + "/" + merchantcode + "/" + cevacode + "/" + pin + "/" + accountno;
        String url = SecurityLayer.genURLCBC("processonholdpayment", params, this);

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
                        ResponseDialogs.infoToActivity(getString(R.string.success), responsemessage, PayMerchant.this, DashBoard.class, new Bundle());

                    } else {

                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, PayMerchant.this);
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
