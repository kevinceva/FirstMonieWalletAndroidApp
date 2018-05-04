package com.ceva.ubmobile.core.ui.billers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.SearchView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.BillerAdapter;
import com.ceva.ubmobile.adapter.RecyclerItemClickListener;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.BillerModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Billers extends BaseActivity {
    Context context;
    UBNSession session;
    String category;
    private List<BillerModel> billerList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BillerAdapter mAdapter;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billers);
        category = getIntent().getStringExtra(BillerModel.KEY_BILLER_NAME);
        setToolbarTitle(category);
        context = this;
        session = new UBNSession(context);
        recyclerView = findViewById(R.id.biller_list);
        searchView = findViewById(R.id.searchView);
        mAdapter = new BillerAdapter(billerList, context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int pos) {
                        Intent intent = new Intent(context, PayDueBills.class);

                        BillerModel biller = (BillerModel) view.getTag();

                        intent.putExtra(BillerModel.KEY_BILLER_ID, biller.getBillerID());
                        intent.putExtra(BillerModel.KEY_BILLER_NAME, biller.getBillerName());
                        intent.putExtra(BillerModel.KEY_CUSTOM_FIELD1, biller.getCustomField1());
                        intent.putExtra(BillerModel.KEY_CUSTOM_FIELD2, biller.getCustomField2());
                        startActivity(intent);
                    }
                }));

        try {
            //TO DO
            category = URLEncoder.encode(category, "utf-8");
        } catch (Exception e) {
            Log.Error(e);
        }
        fetchBillers(session.getUserName(), category);
        //searchView.setVisibility(View.GONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    mAdapter.getFilter().filter("");
                } else {
                    mAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

    }

    private void fetchBillers(String username, String category) {
        //@Path("/getbillers/{username}")
        billerList.clear();
        showLoadingProgress();

        String params = username + "/" + category;
        String url = SecurityLayer.genURLCBC("getbillers", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("getBillers", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {
                        JSONArray objData = new JSONArray(obj.optString("billersdata"));
                        int m = objData.length();
                        Log.debug("number of billers " + objData.length());

                        for (int j = 0; j < m; j++) {
                            JSONObject billerDet = objData.getJSONObject(j);

                            Log.debug("AirBiller " + billerDet.optString(BillerModel.KEY_BILLER_NAME));

                            BillerModel biller = new BillerModel(billerDet.optString(BillerModel.KEY_CATEGORY),
                                    billerDet.optString(BillerModel.KEY_BILLER_ID),
                                    billerDet.optString(BillerModel.KEY_CUSTOM_FIELD1),
                                    billerDet.optString(BillerModel.KEY_CUSTOM_FIELD2),
                                    billerDet.optString(BillerModel.KEY_SHORT_NAME),
                                    billerDet.optString(BillerModel.KEY_BILLER_NAME));
                            billerList.add(biller);

                        }

                        //ben_accountName = objData.optString("accountName");
                        // Log.debug("beneficiaryName",ben_accountName);
                        //showConfirm(beneficiary_type, ben_accountName, beneficiciary_accNumber, beneficiary_bank);
                    } else {

                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, context);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Collections.sort(billerList, new Comparator<BillerModel>() {
                    @Override
                    public int compare(BillerModel lhs, BillerModel rhs) {
                        return lhs.getBillerName().compareTo(rhs.getBillerName());
                    }
                });
                mAdapter.notifyDataSetChanged();

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
