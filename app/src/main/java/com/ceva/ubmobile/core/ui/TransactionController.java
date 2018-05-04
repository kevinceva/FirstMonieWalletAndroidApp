package com.ceva.ubmobile.core.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.models.BankAccount;
import com.ceva.ubmobile.security.UBNSession;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class TransactionController extends BaseActivity {
    Spinner accountListSpinner;
    UBNSession session;
    int fragPosition = 0;
    String title;
    List<String> accountList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_controller);
        String titleExtra = getIntent().getStringExtra(Constants.KEY_CONTROL_TITLE);
        if (titleExtra == null) {
            title = "Transfers";
        } else {
            title = titleExtra;
        }
        setToolbarTitle(title);
        session = new UBNSession(this);
        //initAccounts();//brian - 28/10
    }

    private void initAccounts() {

        String accountsListString = session.pref.getString(Constants.KEY_FULLINFO, null);
        Gson gson = new Gson();
        accountList = new ArrayList<>();
        List<BankAccount> accounts = gson.fromJson(accountsListString, new TypeToken<ArrayList<BankAccount>>() {
        }.getType());
        int k = accounts.size();
        for (int j = 0; j < k; j++) {
            accountList.add(accounts.get(j).getAccountNumber());
        }
        accountListSpinner = (Spinner) findViewById(R.id.tx_accounts);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, accountList);
        accountListSpinner.setAdapter(adapter);
        fragPosition = getIntent().getIntExtra(Constants.KEY_CONTROL_POS, 0);

        accountSelection();

    }

    private void fragmentController(int pos, String accountNumber) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        if (pos == 0) {
            setToolbarTitle(title);
            // fragment = new Beneficiaries();
        } else if (pos == 1) {
            setToolbarTitle(title);
            // fragment = new AddBeneficiary();
        } else {
            setToolbarTitle(title);
            // fragment = new Beneficiaries();
        }
        bundle.putString(Constants.KEY_ACCOUNTNUMBER, accountNumber);
        fragment.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.containerFragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void accountSelection() {
        accountListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                fragmentController(fragPosition, accountList.get(position));
                Log.debug("account obtained" + accountList.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        title = savedInstanceState.getString(Constants.KEY_CONTROL_TITLE);
        fragPosition = savedInstanceState.getInt(Constants.KEY_CONTROL_POS);
    }

}
