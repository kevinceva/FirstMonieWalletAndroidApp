package com.ceva.ubmobile.core.ui.dashboard;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.security.UBNSession;

public class ExpandedDashBoard extends BaseActivity {
    Toolbar toolbar;
    UBNSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_dash_board);
        toolbar = findViewById(R.id.toolbar);
        session = new UBNSession(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        BottomMenu();
        startAccounts();

    }

    private void startAccounts() {

        String accountsListString = session.pref.getString(Constants.KEY_FULLINFO, null);
        Bundle bundles = new Bundle();
        bundles.putString(ExpandedDashBoardFragment.ACCOUNTS, accountsListString);

        Fragment fragment = new ExpandedDashBoardFragment();
        fragment.setArguments(bundles);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        //transaction.addToBackStack(ExpandedDashBoardFragment.ACCOUNTS);
        transaction.commit();
    }

}
