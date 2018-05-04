package com.ceva.ubmobile.core.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.MiniStatementAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.models.AccountsModel;
import com.ceva.ubmobile.models.TransactionsModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MiniStatement extends BaseActivity {
    List<TransactionsModel> ministatement = new ArrayList<>();
    UBNSession session;
    AccountsModel accountsModel;
    private List<TransactionsModel> txList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MiniStatementAdapter mAdapter;
    private String accountBalance;
    private String accountDetails;
    private String accountNumber;
    private Button btnFullStatement;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ministatement);
        setToolbarTitle(getString(R.string.page_ministatement));
        session = new UBNSession(this);
        recyclerView = findViewById(R.id.transactions);
        String endpoint = Constants.KEY_MINISTATEMENT_ENDPOINT;



        try {
            accountsModel = getIntent().getExtras().getParcelable("AccountsModel");
            if (accountsModel.isWallet()) {
                endpoint = "walletministatement";
            }
        } catch (Exception e) {
            Log.debug(e.getMessage());
        }
        accountBalance = getIntent().getStringExtra(Constants.KEY_CLEAREDBALANCE);
        accountNumber = getIntent().getStringExtra(Constants.KEY_ACCOUNTNUMBER);
        accountDetails = getIntent().getStringExtra(Constants.KEY_ACCOUNTPRODUCT) + " - *" + accountNumber;
        btnFullStatement = findViewById(R.id.btnFullStatetement);

        mAdapter = new MiniStatementAdapter(txList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        //dummyTransactions();
        setAccountHeader();

        invokeGetMiniStatement(accountNumber, session.getUserName(), endpoint);
        btnFullStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), FullStatement.class);
                i.putExtra(Constants.KEY_ACCOUNTNUMBER, accountNumber);
                i.putExtra(Constants.KEY_CLEAREDBALANCE, accountBalance);
                i.putExtra(Constants.KEY_ACCOUNTPRODUCT, getIntent().getStringExtra(Constants.KEY_ACCOUNTPRODUCT));
                i.putExtra("AccountsModel", accountsModel);
                startActivity(i);
            }
        });

    }


    private void setAccountHeader() {
        TextView accBal = findViewById(R.id.acc_bal);
        accBal.setText(accountBalance);
        TextView accDet = findViewById(R.id.account_details);
        accDet.setText(accountDetails);
    }

    private void invokeGetMiniStatement(String accountNo, String userName, String endpoint) {
        showLoadingProgress();
        String params = accountNo + "/" + userName;
        if (accountsModel != null) {
            if (endpoint.contains("wallet")) {
                //@Path("/walletministatement/{accountnumber}/{username}/{mobilenumber}")
                params = accountsModel.getAccountNumber() + "/" + accountNo + "/" + userName;
                //params = "5287045695/2348082702825/2348082702825";
            }
        }
        Log.debug("params", params);
        String url = SecurityLayer.genURLCBC(endpoint, params, this);
        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                hideSmoothProgress();
                dismissProgress();
                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    Log.debug("ubnresponse", response.body());
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);

                    if (responsecode.equals("00")) {

                        if (obj.has("ministatement")) {
                            JSONArray array = obj.getJSONArray("ministatement");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject data = array.getJSONObject(i);
                                TransactionsModel model = new TransactionsModel(data.optString("desc"),
                                        data.optString("tdate"),
                                        data.optString("drcr"),
                                        data.optString("xybalance"),
                                        data.optString("withdraw"),
                                        data.optString("lodgement"),
                                        data.optString("na"),
                                        data.optString("actcurr"));
                                txList.add(model);
                            }
                        } else {
                            JSONArray statement = obj.optJSONObject("jbody").optJSONArray("statement");
                            for (int i = 0; i < statement.length(); i++) {
                                JSONObject data = statement.getJSONObject(i);
                                TransactionsModel model = new TransactionsModel(data.optString("narration"),
                                        data.optString("txnstamp"),
                                        data.optString("drcrflag"),
                                        data.optString("amount"),
                                        data.optString("withdraw"),
                                        data.optString("lodgement"),
                                        data.optString("na"),
                                        data.optString("currency"));
                                txList.add(model);
                            }
                        }

                    } else {
                        warningDialog(responsemessage);
                    }
                } catch (Exception e) {
                    Log.debug(e.toString());
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                Log.debug("ubnaccountsfail", t.toString());
                hideSmoothProgress();
                dismissProgress();
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashBoard.class));
    }

}
