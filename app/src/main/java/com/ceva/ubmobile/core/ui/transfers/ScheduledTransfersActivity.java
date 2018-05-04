package com.ceva.ubmobile.core.ui.transfers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.ScheduleAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.core.ui.Transfers;
import com.ceva.ubmobile.models.ScheduleModel;
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

public class ScheduledTransfersActivity extends BaseActivity {
    List<ScheduleModel> scheduleList = new ArrayList<>();
    ScheduleAdapter mAdapter;
    RecyclerView recyclerView;
    UBNSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_transfers);
        setToolbarTitle("Scheduled Transfers");
        session = new UBNSession(this);

        recyclerView = findViewById(R.id.rvCards);
        mAdapter = new ScheduleAdapter(scheduleList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        fetchSchedules(session.getUserName());
    }

    private void dummySchedule() {
        hideSmoothProgress();
        scheduleList.clear();

        ScheduleModel item = new ScheduleModel("2017-01-04", "2017-09-04", "Weekly", "5,900.00", "Sravan Kumar", "Stanbic IBTC", "004972424", "04942824282");
        scheduleList.add(item);

        item = new ScheduleModel("2017-01-04", "2017-09-04", "Monthly", "5,900.00", "Yusuf Isedu", "Diamond", "004972424", "000359753");
        scheduleList.add(item);

        item = new ScheduleModel("2017-01-04", "2017-09-04", "Weekly", "5,900.00", "Sravan Kumar", "Stanbic IBTC", "004972424", "04942824282");
        scheduleList.add(item);

        item = new ScheduleModel("2017-01-04", "2017-09-04", "Weekly", "5,900.00", "Sravan Kumar", "Stanbic IBTC", "004972424", "04942824282");
        scheduleList.add(item);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Transfers.class));
    }

    private void fetchSchedules(String username) {
        //fetchscheduledTras/Collins
        showSmoothProgress();
        //only for test purposes

        String params = "fetchscheduledTras/" + username;

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
                    scheduleList.clear();

                    if (responsecode.equals("00")) {
                        JSONArray objData = new JSONArray(obj.optString("scheduledtransdata"));

                        int k = objData.length();
                        for (int i = 0; i < k; i++) {
                            JSONObject data = objData.getJSONObject(i);
                            ScheduleModel model = new ScheduleModel(data.optString(
                                    ScheduleModel.KEY_START_DATE),
                                    data.optString(ScheduleModel.KEY_END_DATE),
                                    data.optString(ScheduleModel.KEY_FREQUENCY),
                                    data.optString(ScheduleModel.KEY_AMOUNT),
                                    data.optString(ScheduleModel.KEY_BEN_NAME),
                                    data.optString(ScheduleModel.KEY_DEST_BANK),
                                    data.optString(ScheduleModel.KEY_ORIGIN),
                                    data.optString(ScheduleModel.KEY_BEN_ACC));
                            scheduleList.add(model);
                        }
                        mAdapter.notifyDataSetChanged();

                    } else {

                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, ScheduledTransfersActivity.this);
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
