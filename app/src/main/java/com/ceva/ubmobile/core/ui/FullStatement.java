package com.ceva.ubmobile.core.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.FullStatementAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.models.AccountsModel;
import com.ceva.ubmobile.models.FullStatementModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;

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

public class FullStatement extends BaseActivity {
    List<FullStatementModel> fullstatement = new ArrayList<>();
    UBNSession session;
    @BindView(R.id.startDate)
    TextView startDate;
    @BindView(R.id.endDate)
    TextView endDate;
    private List<FullStatementModel> txList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FullStatementAdapter mAdapter;
    private String accountBalance;
    private String accountDetails;
    private String accountNumber;
    private String accountType;
    private AccountsModel accountsModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_statement);
        ButterKnife.bind(this);
        setToolbarTitle("Statement");
        session = new UBNSession(this);
        try {
            accountsModel = getIntent().getExtras().getParcelable("AccountsModel");
            Log.debug("checking to see: " + accountsModel.getAccountName());
        } catch (Exception e) {
            Log.Error(e);
        }
        recyclerView = findViewById(R.id.transactions);
        accountBalance = getIntent().getStringExtra(Constants.KEY_CLEAREDBALANCE);
        accountNumber = getIntent().getStringExtra(Constants.KEY_ACCOUNTNUMBER);
        accountDetails = getIntent().getStringExtra(Constants.KEY_ACCOUNTPRODUCT) + " - *" + accountNumber.substring(6);
        accountType = getIntent().getStringExtra(Constants.KEY_ACCOUNTPRODUCT);
        getDateTextView(startDate, this);
        getDateTextView(endDate, this);

        endDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String enddate = endDate.getText().toString().trim();
                String startdate = startDate.getText().toString().trim();
                if (startdate == "") {
                    warningDialog("Please select the start date first");
                } else {
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                        Date date1 = formatter.parse(startdate);

                        Date date2 = formatter.parse(enddate);

                        if (date1.compareTo(date2) < 0) {
                            startdate = startdate.replace("/", "");
                            enddate = enddate.replace("/", "");

                            invokeGetFullStatement(session.getUserName(), accountNumber, startdate, enddate);

                        } else {
                            warningDialog("Please select a proper date range.");
                        }

                    } catch (ParseException e) {
                        Log.Error(e);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        /*LinearLayout daterange = (LinearLayout) findViewById(R.id.daterangelayout);
        daterange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                        new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                            @Override
                            public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                                       int yearStart, int monthStart,
                                                       int dayStart, int yearEnd,
                                                       int monthEnd, int dayEnd) {
                                // grab the date range, do what you want
                                String startDate;
                                String endDate;
monthStart = monthStart +1;
                                String stDay = String.valueOf(dayStart);
                                String stMon = String.valueOf(monthStart);
monthEnd = monthEnd + 1;
                                String enDay = String.valueOf(dayEnd);
                                String enMon = String.valueOf(monthEnd);


                                if(stDay.length() == 1){
                                    stDay = "0"+dayStart;
                                }
                                if(stMon.length() == 1){

                                    stMon = "0"+monthStart;
                                }

                                if(enDay.length() == 1){
                                    enDay ="0"+dayEnd;
                                }
                                if(enMon.length() == 1){

                                    enMon = "0"+monthEnd;
                                }
                                startDate = stDay+stMon+String.valueOf(yearStart);
                                endDate = enDay+enMon+String.valueOf(yearEnd);
                                TextView dateTxt = (TextView) findViewById(R.id.datera);

                                String range = String.valueOf(stDay + "/" + stMon+ "/" + yearStart) + " - " + String.valueOf(enDay + "/" + enMon + "/" + yearEnd);
                                dateTxt.setText(range);

                                invokeGetFullStatement(session.getUserName(), accountNumber, startDate, endDate);
                            }
                        });

                smoothDateRangePickerFragment.show(getFragmentManager(), "smoothDateRangePicker");
            }
        });*/

        mAdapter = new FullStatementAdapter(fullstatement, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        //dummyTransactions();
        setAccountHeader();
        hideSmoothProgress();

    }

    /*@Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MiniStatement.class);
        i.putExtra(Constants.KEY_ACCOUNTNUMBER, accountNumber);
        i.putExtra(Constants.KEY_CLEAREDBALANCE, accountBalance);
        i.putExtra(Constants.KEY_ACCOUNTPRODUCT, accountType);
        i.putExtra("AccountsModel", accountsModel);
        startActivity(i);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent i = new Intent(getApplicationContext(), MiniStatement.class);
            i.putExtra(Constants.KEY_ACCOUNTNUMBER, accountNumber);
            i.putExtra(Constants.KEY_CLEAREDBALANCE, accountBalance);
            i.putExtra(Constants.KEY_ACCOUNTPRODUCT, accountType);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setAccountHeader() {
        TextView accBal = findViewById(R.id.acc_type);

        accBal.setText(accountType);
        TextView accDet = findViewById(R.id.account_number);

        accDet.setText(accountNumber);
        accDet = findViewById(R.id.account_bal);

        accDet.setText(accountBalance);
    }

    private void invokeGetFullStatement(String username, String acctNumber, String startDate, String endDate) {
//@Path("/fullStatement/{username}/{acctNumber}/{startDate}/{endDate}")
        showSmoothProgress();
        showLoadingProgress();
        String params = username + "/" + acctNumber + "/" + startDate + "/" + endDate;
        Log.debug("params", params);
        String url = SecurityLayer.genURLCBC("fullStatement", params, this);
        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                hideSmoothProgress();
                dismissProgress();
                // fullstatement = response.body().getfullstatement();
                try {
                    fullstatement.clear();
                    JSONObject object = new JSONObject(response.body());
                    object = SecurityLayer.decryptTransaction(object, getApplicationContext());
                    if (object.optString(Constants.KEY_CODE).equals("00")) {
                        JSONObject stat = new JSONObject(object.optString("fullstatement"));
                        JSONArray array = new JSONArray(stat.optString("acctsumm"));

                        JSONObject ment = array.getJSONObject(0);
                        JSONArray array1 = new JSONArray(ment.optString("tranDetails"));
                        int k = array1.length();
                        for (int i = 0; i < k; i++) {
                            JSONObject ment1 = array1.getJSONObject(i);
                            FullStatementModel model = new FullStatementModel(ment1.optString(FullStatementModel.KEY_ST_BALANCE),
                                    ment1.optString(FullStatementModel.KEY_ST_DESC),
                                    ment1.optString(FullStatementModel.KEY_ST_WITHDRAWAL),
                                    ment1.optString(FullStatementModel.KEY_ST_DEPOSIT),
                                    ment1.optString(FullStatementModel.KEY_ST_DATE), ment1.optString("accountcurrency"));
                            fullstatement.add(model);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
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

}
