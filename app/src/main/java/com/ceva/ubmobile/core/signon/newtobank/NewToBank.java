package com.ceva.ubmobile.core.signon.newtobank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.models.BankProductModel;

public class NewToBank extends BaseActivity {
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

}
