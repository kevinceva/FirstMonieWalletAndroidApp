package com.ceva.ubmobile.core.ui.transfers;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.RecyclerItemClickListener;
import com.ceva.ubmobile.adapter.TransferHistoryAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.models.TransactionsModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransfersHistory extends BaseActivity {
    @BindView(R.id.transactions)
    RecyclerView recyclerView;
    private TransferHistoryAdapter mAdapter;
    private List<TransactionsModel> txList = new ArrayList<>();
    @BindView(R.id.startDay)
    LinearLayout dateView;
    @BindView(R.id.startDate)
    TextView dateText;
    List<String> receiptData = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfers_history);
        setToolbarTitle("Receipts");
        ButterKnife.bind(this);
        getDateTextView(dateText, this, "dd-MMM-yyyy");

        //hideSmoothProgress();
        mAdapter = new TransferHistoryAdapter(txList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ArrayList<ConfirmModel> confirmModelList = new ArrayList<>();
                        try {
                            //TO DO
                            JSONObject obj = new JSONObject(receiptData.get(position));

                            ConfirmModel item = new ConfirmModel("Reference Number", obj.optString("tranrefno"));
                            confirmModelList.add(item);

                            item = new ConfirmModel("Debit Account", obj.optString("debitact"));
                            confirmModelList.add(item);

                            if (obj.has("debitactname")) {
                                item = new ConfirmModel("Debit Account Name", obj.optString("debitactname"));
                                confirmModelList.add(item);
                            }

                            item = new ConfirmModel("Beneficiary Account", obj.optString("benact"));
                            confirmModelList.add(item);

                            if (!obj.optString("reqtype").equalsIgnoreCase("PAY_BILL")) {
                                item = new ConfirmModel("Beneficiary Account Name", obj.optString("benactname"));
                                confirmModelList.add(item);

                                item = new ConfirmModel("Beneficiary Bank", obj.optString("benbankname"));
                                confirmModelList.add(item);
                            }

                            item = new ConfirmModel("Details", obj.optString("details"));
                            confirmModelList.add(item);

                            item = new ConfirmModel("Amount", obj.optString("amount"));
                            confirmModelList.add(item);

                            item = new ConfirmModel("Transaction Date", obj.optString("trnsdate"));
                            confirmModelList.add(item);

                            item = new ConfirmModel("Transaction Status", obj.optString("status"));
                            confirmModelList.add(item);

                            item = new ConfirmModel("Channel", obj.optString("channel"));
                            confirmModelList.add(item);

                        } catch (Exception e) {
                            Log.Error(e);
                        }

                        Bundle bundle = new Bundle();

                        //bundle.putParcelable("confirmList", (ConfirmModel) confirmModelList);
                        bundle.putParcelableArrayList("confirmList", confirmModelList);

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        TransferDetails transferDetails = new TransferDetails();
                        transferDetails.setArguments(bundle);
                        transferDetails.setCancelable(false);

                        // The device is smaller, so show the fragment fullscreen
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        // For a little polish, specify a transition animation
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        // To make it fullscreen, use the 'content' root view as the container
                        // for the fragment, which is always the root view for the activity

                        transaction.add(android.R.id.content, transferDetails)
                                .addToBackStack(null).commitAllowingStateLoss();
                    }
                }));

        dateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (NetworkUtils.isConnected(getApplicationContext())) {
                    invokeGetReceipts(session.getUserName(), dateText.getText().toString().replace("/", ""));
                } else {
                    noInternetDialog();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /*private void dummy() {
        //txList.clear();
        TransactionsModel model = new TransactionsModel("Airtime Top-Up/Airtime Recharge on [2348033808725]", "07-JUL-2017", "D", "5,000.00",
                "5,000.00", "5,000.00", "124343335", "NGN");
        txList.add(model);
        model = new TransactionsModel("Fund Transfer to Collins/Airtime Recharge on [2348033808725]", "06-JUL-2017", "D", "5,000.00",
                "5,000.00", "5,000.00", "124343335", "NGN");
        txList.add(model);
        model = new TransactionsModel("Fund Transfer to Ronald/Airtime Recharge on [2348033808725]", "05-JUL-2017", "D", "5,000.00",
                "5,000.00", "5,000.00", "124343335", "NGN");
        txList.add(model);
        mAdapter.notifyDataSetChanged();
    }*/

    private void invokeGetReceipts(String userid, final String date) {
        showLoadingProgress();
        //@Path("/transactionreceipt/{userid}/{fromdate}
        String params = userid + "/" + parseDateToddMMyyyy(date);

        Log.debug("params", params);
        String url = SecurityLayer.genURLCBC("transactionreceipt", params, this);
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

                        txList.clear();
                        receiptData.clear();
                        JSONArray array = obj.getJSONArray("transdataarr");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject data = array.getJSONObject(i);

                            receiptData.add(data.toString());

                            TransactionsModel model = new TransactionsModel(data.optString("details"),
                                    date, "D",
                                    data.optString("amount"),
                                    data.optString("withdraw"),
                                    data.optString("lodgement"),
                                    data.optString("na"),
                                    data.optString("NGN"));
                            txList.add(model);

                            Log.debug("stored json", receiptData.get(i));
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

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "dd-MMM-yyyy";
        String outputPattern = "ddMMyyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.debug("formatted date", str);
        return str;
    }
}
