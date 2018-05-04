package com.ceva.ubmobile.core.ui;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RadioButton;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.MailBoxAdapter;
import com.ceva.ubmobile.models.MailBoxModel;
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

public class MailBox extends BaseActivity {
    UBNSession session;
    RadioButton rb_unread;
    private List<MailBoxModel> txList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MailBoxAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_box);
        setToolbarTitle("Mailbox");
        session = new UBNSession(this);
        rb_unread = findViewById(R.id.rd_unread);
        recyclerView = findViewById(R.id.mailbox);
        mAdapter = new MailBoxAdapter(txList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        // dummyMailBox();
        invokeGetMessages(session.getUserName());
    }

    /*private void dummyMailBox() {
        MailBoxModel model = new MailBoxModel("Local Funds Transfer - Successful ");
        txList.add(model);
        model = new MailBoxModel("Local Funds Transfer - Successful ");
        txList.add(model);
        model = new MailBoxModel("Balance enquiry - Successful ");
        txList.add(model);
        model = new MailBoxModel("Airtime purchase - Successful ");
        txList.add(model);
        model = new MailBoxModel("Bill payment - Successful ");
        txList.add(model);
        model = new MailBoxModel("Local Funds Transfer - Successful ");
        txList.add(model);
        model = new MailBoxModel("Local Funds Transfer - Successful ");
        txList.add(model);
        model = new MailBoxModel("Local Funds Transfer - Successful ");
        txList.add(model);
        model = new MailBoxModel("Local Funds Transfer - Successful ");
        txList.add(model);
        model = new MailBoxModel("Local Funds Transfer - Successful ");
        txList.add(model);

        mAdapter.notifyDataSetChanged();
    }*/
    private void invokeGetMessages(String userName) {
        txList.clear();
        showSmoothProgress();
        String params = userName;
        String url = SecurityLayer.genURLCBC("inbox", params, this);
        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                hideSmoothProgress();
                // Log.debug("inboxservice",response.body().toString());
                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    List<MailBoxModel> mailBoxModelList = new ArrayList<MailBoxModel>();

                    JSONArray array = obj.getJSONArray("INBOX");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject objI = array.getJSONObject(i);

                        txList.add(new MailBoxModel(objI.optString(MailBoxModel.MESSAGE), objI.optString(MailBoxModel.DATE)));
                    }
                    String label = "Inbox(" + txList.size() + ")";
                    rb_unread.setText(label);
                    mAdapter.notifyDataSetChanged();
                    //ministatement = response.body().getMinistatement();
                    // initMiniStatement();
                } catch (Exception e) {
                    Log.Error(e);
                    SecurityLayer.generateToken(getApplicationContext());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                //FirebaseCrash.report(t);
                hideSmoothProgress();
                SecurityLayer.generateToken(getApplicationContext());
            }
        });

    }
}
