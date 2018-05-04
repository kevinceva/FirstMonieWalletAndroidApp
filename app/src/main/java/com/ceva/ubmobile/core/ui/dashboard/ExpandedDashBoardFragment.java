package com.ceva.ubmobile.core.ui.dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.AccountsAdapter;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.AccountsModel;
import com.ceva.ubmobile.models.BankAccount;
import com.ceva.ubmobile.security.UBNSession;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class ExpandedDashBoardFragment extends Fragment {
    public static final String ACCOUNTS = "accountStrings";
    UBNSession session;
    AccountsAdapter mAdapter;
    @BindView(R.id.rvCards)
    RecyclerView accountsView;
    private List<BankAccount> accounts = new ArrayList<>();
    private List<AccountsModel> txList = new ArrayList<>();

    public ExpandedDashBoardFragment() {
    }

    public static ExpandedDashBoardFragment newInstance(String accountStrings) {
        Bundle bundle = new Bundle();
        bundle.putString(ACCOUNTS, accountStrings);

        ExpandedDashBoardFragment fragment = new ExpandedDashBoardFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String accountsListString = getArguments().getString(ACCOUNTS);
            Gson gson = new Gson();
            accounts = gson.fromJson(accountsListString, new TypeToken<ArrayList<BankAccount>>() {
            }.getType());
        }
    }

    private void feedAccounts() {
        int m = accounts.size();//27/10/2016
        txList.clear();
        for (int j = 0; j < m; j++) {

            List<AccountsModel> acc = accounts.get(j).getCustomerInfo();
            String accno = accounts.get(j).getAccountNumber();
            AccountsModel am = new AccountsModel(acc.get(0).getAccountType().toUpperCase(), accno, acc.get(0).getAccountBalance(), acc.get(0).getAccountCurrency(), acc.get(0).isWallet(), null);
            txList.add(am);
            Log.debug(accno);
        }

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);
        ButterKnife.bind(this, rootView);
        mAdapter = new AccountsAdapter(txList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        accountsView.setLayoutManager(mLayoutManager);
        accountsView.setItemAnimator(new DefaultItemAnimator());
        accountsView.setAdapter(mAdapter);
        feedAccounts();
        return rootView;
    }
}
