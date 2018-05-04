package com.ceva.ubmobile.core.ui.transfers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.core.ui.Transfers;
import com.ceva.ubmobile.models.Beneficiary;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransfersControllerActivity extends BaseActivity {
    Fragment fragment = null;
    Bundle bundleExt;
    int track = 0;
    private int fragPosition = 1;
    private Spinner tx_types;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {

            setContentView(R.layout.activity_transfers_controller);
            Log.debug("calling on create view for activity");
            bundleExt = getIntent().getExtras();
            assert bundleExt != null;
            fragPosition = Integer.parseInt(bundleExt.getString(Constants.KEY_FRAG_POSITION));
            tx_types = findViewById(R.id.tx_types);
            tx_types.setSelection(fragPosition);
            tx_types.setVisibility(View.GONE);
            transactionTypeSelection();
            fragmentController();
            hideSpinner();
        } else {
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {

            startActivity(new Intent(this, Transfers.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        // fragPosition = savedInstanceState.getInt(Constants.KEY_FRAG_POSITION);
    }

    public void fragmentController() {
        Bundle bundles = new Bundle();
        String benID = null;
        Log.debug("track", "" + track);
        UBNSession session = new UBNSession(this);
        session.setInt("track", track);
        hideSpinner();

        if (bundleExt.getString(Beneficiary.KEY_BEN_ID) != null) {
            benID = bundleExt.getString(Beneficiary.KEY_BEN_ID);
            Log.debug("beneficiaryID on transfers ", benID);
        }
        bundles.putString(Beneficiary.KEY_BEN_ID, benID);

        if (fragPosition == 0) {
            //startActivity(new Intent(this, OwnAccountsActivity.class));
            //finish();
            setToolbarTitle("Linked Accounts");
            fragment = new LinkedAccounts();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.containerFragment, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        } else if (fragPosition == 1) {
            setToolbarTitle("Union Accounts");
            fragment = new WithinBank();
            fragment.setArguments(bundles);
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.containerFragment, fragment, "withinbank");
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        } else if (fragPosition == 2) {
            setToolbarTitle("Other Banks");
            fragment = new OtherBanks();
            String bankCode = null;
            if (bundleExt.getString(Beneficiary.KEY_BEN_BANKCODE) != null) {
                bankCode = bundleExt.getString(Beneficiary.KEY_BEN_BANKCODE);
                Log.debug("bankcode on transfers ", bankCode);
            }
            bundles.putString(Beneficiary.KEY_BEN_BANKCODE, bankCode);
            fragment.setArguments(bundles);
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.containerFragment, fragment, "otherbank");
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        } else if (fragPosition == 3) {

            startActivity(new Intent(this, SavedBeneficiaryTransfer.class));
            finish();

        } else if (fragPosition == 4) {

            setToolbarTitle(WalletToWallet.FRAG_TITLE);
            fragment = new WalletToWallet();
            fragment.setArguments(bundles);
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.containerFragment, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();

        } else {
            Toast.makeText(this, "Coming soon...", Toast.LENGTH_LONG).show();
            /*setToolbarTitle("UBN Accounts");
            fragment = new WithinBank();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.containerFragment, fragment);
            transaction.addToBackStack(null);
            transaction.commit();*/
        }
        track++;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void transactionTypeSelection() {
        tx_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                //Bundle b = new Bundle();
                if (position > 0) {
                    fragPosition = position;
                    fragmentController();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }


    public void setActionBarTitle(String title) {

        setToolbarTitle(title);
    }

    public void hideSpinner() {

        LinearLayout spinner_fields = findViewById(R.id.spinner_fields);
        spinner_fields.setVisibility(View.GONE);
    }

    public void hideSpinner(String title) {
       /* LinearLayout confirm_fields = (LinearLayout) findViewById(R.id.confirm_fields);
        LinearLayout spinner_fields = (LinearLayout) findViewById(R.id.spinner_fields);
        TextView textView = (TextView) findViewById(R.id.txtconf);
        textView.setText(title);
        spinner_fields.setVisibility(View.GONE);
        confirm_fields.setVisibility(View.GONE);*/

    }

    public void showSpinner() {
        /*LinearLayout confirm_fields = (LinearLayout) findViewById(R.id.confirm_fields);
        LinearLayout spinner_fields = (LinearLayout) findViewById(R.id.spinner_fields);

        spinner_fields.setVisibility(View.GONE);
        confirm_fields.setVisibility(View.GONE);*/

    }

    public void setBeneficiary(String accountName, String beneficiaryAccount, String destinationBank, String destinationBankName, final String username, String type) {
        //showLoadingProgress();
        showToast("Updating accounts...");
        //@Path("/savebeneficiariesdata/{accountname}/{beneficiaryaccount}/{destinationbank}/{destinationbankname}/{username}/{type}")
        String params = accountName + "/" + beneficiaryAccount + "/" + destinationBank + "/" + destinationBankName + "/" + username + "/" + type;
        Log.debug("params:" + params);
        String url = SecurityLayer.genURLCBC(Constants.KEY_SAVEBENEFICIARY_ENDPOINT, params, this);
        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //dismissProgress();
                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);

                    if (responsecode.equals("00")) {
                    /*Bundle bundle = new Bundle();
                    bundle.putInt(Constants.KEY_CONTROL_POS, Constants.KEY_TX_BEN);
                    bundle.putString(Constants.KEY_CONTROL_TITLE, "Saved Beneficiaries");
                    ResponseDialogs.successToActivity(context.getString(R.string.success),
                            responsemessage, context, TransactionController.class, bundle);*/
                        ResponseDialogs.successToActivity("Beneficiary Saved", responsemessage, TransfersControllerActivity.this, Transfers.class, new Bundle());
                        //fetchBeneficiaries(username);

                    } else {
                        ResponseDialogs.failStatic(getString(R.string.error), responsemessage, TransfersControllerActivity.this);
                    }
                } catch (Exception e) {
                    Log.Error(e);
                    SecurityLayer.generateToken(getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed

                Log.debug("ubnaccountsfail", t.toString());
                SecurityLayer.generateToken(getApplicationContext());
                showToast(getString(R.string.error_500));
                // dismissProgress();

            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Transfers.class));
    }
}
