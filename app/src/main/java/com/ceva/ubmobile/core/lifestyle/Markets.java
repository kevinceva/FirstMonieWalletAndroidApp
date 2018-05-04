package com.ceva.ubmobile.core.lifestyle;


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
import com.ceva.ubmobile.adapter.MarketsAdapter;
import com.ceva.ubmobile.adapter.RecyclerItemClickListener;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.MarketsModel;
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

public class Markets extends BaseActivity {
    Context context;
    UBNSession session;
    private List<MarketsModel> billerList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MarketsAdapter mAdapter;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markets);
        setToolbarTitle("Lifestyle");
        context = this;
        session = new UBNSession(context);
        recyclerView = findViewById(R.id.biller_list);
        searchView = findViewById(R.id.searchView);
        mAdapter = new MarketsAdapter(billerList, context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(context, MarketsSubCategories.class);

                        intent.putExtra(MarketsModel.KEY_CAT_DESC, billerList.get(position).getCategory());
                        intent.putExtra(MarketsModel.KEY_CAT_ID, billerList.get(position).getId());

                        startActivity(intent);
                    }
                }));

        fetchCategories(session.getUserName());

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

    private void fetchCategories(String username) {
        //@Path("/getbillers/{username}")
        billerList.clear();
        showLoadingProgress();

        String params = username;

        String url = SecurityLayer.genURLCBC("onlineCategoryList", params, getApplicationContext());

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("onlineCategoryList", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {
                        JSONArray objData = new JSONArray(obj.optString("CATEGORY_LIST"));
                        int m = objData.length();
                        Log.debug("#categories " + objData.length());

                        for (int j = 0; j < m; j++) {
                            JSONObject billerDet = objData.getJSONObject(j);

                            MarketsModel marketsModel = new MarketsModel(billerDet.optString("CATEGORY_DESC"), billerDet.optString("CATEGORY_ID"));

                            billerList.add(marketsModel);

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
               /* Collections.sort(billerList, new Comparator<MarketsModel>() {
                    @Override
                    public int compare(MarketsModel lhs, MarketsModel rhs) {
                        return lhs.getBillerName().compareTo(rhs.getBillerName());
                    }
                });*/
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
