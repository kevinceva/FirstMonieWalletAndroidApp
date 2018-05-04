package com.ceva.ubmobile.core.omniview.accountopening;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.models.BankProductModel;

public class ExistingAccountOpen extends BaseActivity {
    Fragment fragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_to_bank);
        setToolbarTitle("Welcome");
        Bundle bundle = new Bundle();
        bundle.putString(BankProductModel.KEY_TITLE, "Select Product");
        bundle.putString(BankProductModel.KEY_NUMBER, "1");

        fragment = new ProductSelection();
        fragment.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        //transaction.addToBackStack("Select product");
        transaction.commit();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        // fragPosition = savedInstanceState.getInt(Constants.KEY_FRAG_POSITION);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

}
