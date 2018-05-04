package com.ceva.ubmobile.core.omniview;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.InvestmentAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.InvestmentModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.security.UBNSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Investments extends BaseActivity {
    List<InvestmentModel> investmentList = new ArrayList<>();
    InvestmentAdapter mAdapter;
    RecyclerView recyclerView;
    UBNSession session;
    LinearLayout noInvestment;
    LinearLayout investment_row;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investments);
        setToolbarTitle(getString(R.string.page_investments));
        session = new UBNSession(this);

        recyclerView = findViewById(R.id.rvCards);
        mAdapter = new InvestmentAdapter(investmentList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        noInvestment = findViewById(R.id.noInvestment);
        investment_row = findViewById(R.id.investment_row);
        // dummyInvest();
        List<String> account = session.getAccountNumbers();

        String[] acc = account.get(0).split("-");

        String mainacc = acc[acc.length - 1].trim();
        fetchInvestment(session.getUserName(), mainacc);

    }

    private void dummyInvest() {

        investmentList.clear();

        InvestmentModel item = new InvestmentModel("Staff - 0005534521", "description goes here.", "N500,000.00", "A lot of information can be stored here. Hopefully not too long");
        investmentList.add(item);

        item = new InvestmentModel("Staff - 0005534521", "description goes here.", "N500,000.00", "A lot of information can be stored here. Hopefully not too long");
        investmentList.add(item);

        item = new InvestmentModel("Staff - 0005534521", "description goes here.", "N500,000.00", "A lot of information can be stored here. Hopefully not too long");
        investmentList.add(item);

        item = new InvestmentModel("Staff - 0005534521", "description goes here.", "N500,000.00", "A lot of information can be stored here. Hopefully not too long");
        investmentList.add(item);

        mAdapter.notifyDataSetChanged();
    }

    private void fetchInvestment(String username, String accountno) {
        //@Path("/viewInvestments/{username}/{accountno}")
        showSmoothProgress();
        //only for test purposes

        String params = "viewInvestments/" + username + "/" + accountno;

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(params);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                hideSmoothProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {
                        JSONArray objData = new JSONArray(obj.optString("investments"));

                        int k = objData.length();
                        for (int i = 0; i < k; i++) {
                            JSONObject data = objData.getJSONObject(i);
                            InvestmentModel model = new InvestmentModel(data.optString("accountnumber"),
                                    data.optString("product"),
                                    data.optString("amount"),
                                    data.optString("description"));
                            investmentList.add(model);
                        }
                        if (investmentList.size() > 0) {

                            noInvestment.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            noInvestment.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        mAdapter.notifyDataSetChanged();

                    } else {

                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, Investments.this);
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
                hideSmoothProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }
}
