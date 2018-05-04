package com.ceva.ubmobile.core.omniview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.InvestmentAdapter;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.models.InvestmentModel;

import java.util.ArrayList;
import java.util.List;

public class ViewLoans extends BaseActivity {
    List<InvestmentModel> investmentList = new ArrayList<>();
    InvestmentAdapter mAdapter;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_loans);
        setToolbarTitle("Loans");

        recyclerView = (RecyclerView) findViewById(R.id.rvCards);
        mAdapter = new InvestmentAdapter(investmentList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        dummyLoan();
    }

    private void dummyLoan() {
        //loan account, amount, balance, maturity
        investmentList.clear();

        InvestmentModel item = new InvestmentModel("682N0005534521", "N500,000.00", "N600,000.00", "2017-05-10");
        //investmentList.add(item);

        item = new InvestmentModel("682N0005534521", "N800,000.00", "N900,000.00", "2017-05-10");
        //investmentList.add(item);

        item = new InvestmentModel("682N0005534521", "N600,000.00", "N500,000.00", "2017-05-10");
        //investmentList.add(item);

        item = new InvestmentModel("682N0005534521", "N600,000.00", "N500,000.00", "2017-05-10");
        // investmentList.add(item);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashBoard.class));
    }
}
