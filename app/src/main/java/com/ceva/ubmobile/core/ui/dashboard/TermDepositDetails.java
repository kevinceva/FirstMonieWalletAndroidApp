package com.ceva.ubmobile.core.ui.dashboard;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.ConfirmAdapter;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.utils.NumberUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermDepositDetails extends BaseActivity {
    @BindView(R.id.productName)
    TextView productName;
    @BindView(R.id.accountNumber)
    TextView accountNumber;
    @BindView(R.id.confirm_list)
    RecyclerView confirmationRecyler;
    ConfirmAdapter confirmAdapter;
    List<ConfirmModel> confirmItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_details);
        ButterKnife.bind(this);
        setToolbarTitle("Investment Details");
        confirmAdapter = new ConfirmAdapter(confirmItems, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        confirmationRecyler.setLayoutManager(mLayoutManager);
        confirmationRecyler.setItemAnimator(new DefaultItemAnimator());
        confirmationRecyler.setAdapter(confirmAdapter);

        String payload = getIntent().getStringExtra("payload").trim();

        JSONObject object = null;
        try {
            object = new JSONObject(payload);
            productName.setText(object.optString("product"));
            accountNumber.setText(object.optString("accountnumber"));
            confirmItems.addAll(printJsonObject(object));
            confirmAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public List<ConfirmModel> printJsonObject(JSONObject jsonObj) throws JSONException {

        List<ConfirmModel> confirmModelList = new ArrayList<>();

        ConfirmModel item = new ConfirmModel("Account Number", jsonObj.optString("accountnumber"));
        confirmModelList.add(item);

        item = new ConfirmModel("Customer Name", jsonObj.optString("customerName"));
        confirmModelList.add(item);

        item = new ConfirmModel("Booking Date", jsonObj.optString("bookingdate"));
        confirmModelList.add(item);

        item = new ConfirmModel("Maturity Date", jsonObj.optString("maturitydate"));
        confirmModelList.add(item);

        item = new ConfirmModel("Tenor", jsonObj.optString("tenor"));
        confirmModelList.add(item);

        item = new ConfirmModel("Amount", NumberUtilities.getWithDecimalPlusCurrency(jsonObj.optDouble("amount")));
        confirmModelList.add(item);

        item = new ConfirmModel("Accrued Interest", NumberUtilities.getWithDecimalPlusCurrency(jsonObj.optDouble("accruedInterest")));
        confirmModelList.add(item);

       /* item = new ConfirmModel("Effective Rate", jsonObj.optString("effectiveRate")+"%");
        confirmModelList.add(item);

        item = new ConfirmModel("Maturity Date", NumberUtilities.getDate(jsonObj.optLong("maturityDate")));
        confirmModelList.add(item);*/

        return confirmModelList;

    }

}


