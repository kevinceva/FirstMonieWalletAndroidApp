package com.ceva.ubmobile.core.ui;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.ConfirmAdapter;
import com.ceva.ubmobile.models.ConfirmModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmationActivity extends BaseActivity {
    String data;
    @BindView(R.id.confirm_list)
    RecyclerView confirmationRecyler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_activiy);
        ButterKnife.bind(this);
        setToolbarTitle("Confirm");
        data = getIntent().getStringExtra("data");
        Log.debug("confirmactivity:" + data);
        buildPage(data);
    }

    private void buildPage(String data) {
        List<ConfirmModel> confirmItems = new ArrayList<>();
        ConfirmAdapter confirmAdapter = new ConfirmAdapter(confirmItems, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        confirmationRecyler.setLayoutManager(mLayoutManager);
        confirmationRecyler.setItemAnimator(new DefaultItemAnimator());
        confirmationRecyler.setAdapter(confirmAdapter);
        try {
            JSONObject object = new JSONObject(data);
            JSONArray confirms = object.optJSONArray("confirmation");
            for (int j = 0; j < confirms.length(); j++) {

                String[] multi = confirms.optString(j).split("\\|");

                confirmItems.add(new ConfirmModel(multi[0], multi[1]));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
