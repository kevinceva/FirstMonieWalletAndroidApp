package com.ceva.ubmobile.core.ui.bankservices;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.Branches;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;
import com.ceva.ubmobile.core.ui.widgets.CustomSearchableSpinner;

import org.json.JSONObject;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankDraftRequest extends BaseActivity implements View.OnClickListener {
    CustomSearchableSpinner sp_branches;
    Spinner sp_accounts;
    EditText amount, beneficiary_name, security_word, token_key;
    String strAmount, strBenName, strSecurity, strAccount, strBranchCode, strBranchName, strToken;
    boolean isReady = false;
    LinearLayout input, confirm, security;
    int chosenBank = 0;
    UBNSession session;
    boolean isFinal = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankdraftrequest);
        setToolbarTitle(getString(R.string.page_bank_draft));

        session = new UBNSession(this);
        amount = findViewById(R.id.amount);
        beneficiary_name = findViewById(R.id.beneficiary_name);
        security_word = findViewById(R.id.security_word);
        token_key = findViewById(R.id.token_key);

        input = findViewById(R.id.input_fields);
        confirm = findViewById(R.id.confirm_fields);
        security = findViewById(R.id.security_fields);

        List<String> accountList = session.getAccountNumbers();

        sp_accounts = findViewById(R.id.sp_accounts);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, accountList);
        sp_accounts.setAdapter(adapter);

        List<String> branchNames = session.getBranchNames();
        ArrayAdapter<String> branch_adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, branchNames);

        sp_branches = findViewById(R.id.sp_branches);
        sp_branches.setAdapter(branch_adapter);
        sp_branches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chosenBank = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button btnConfirm = findViewById(R.id.btnContinue);
        btnConfirm.setOnClickListener(this);
        Button btnCancel = findViewById(R.id.btncancel);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btncancel) {
            if (isReady) {

                showInput();

            } else {
                onBackPressed();
            }
        }
        if (id == R.id.btnContinue) {
            if (isReady) {
                if (isFinal) {
                    strToken = token_key.getText().toString();
                    if (Utility.isNotNull(strToken)) {
                        if (NetworkUtils.isConnected(this)) {
                            requestBankDraft(strBenName, strAmount, strToken, strAccount, strSecurity, session.getUserName());
                        } else {
                            ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_no_internet_connection), this);
                        }
                    } else {
                        ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_enter_token), this);
                    }
                } else {
                    showSecurity();
                }
            } else {
                List<Branches> branches = session.getBranches();
                strAmount = amount.getText().toString();
                strBenName = beneficiary_name.getText().toString();
                strSecurity = security_word.getText().toString();
                strBranchCode = branches.get(chosenBank).getbranchCode();
                strBranchName = branches.get(chosenBank).getbranchName();
                strAccount = sp_accounts.getSelectedItem().toString();
                if (Utility.isNotNull(strAmount) && Utility.isNotNull(strBenName) && Utility.isNotNull(strAccount)) {
                    showConfirm();
                } else {
                    ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.label_fill_all), this);
                }
            }
        }
    }

    private void showInput() {
        isReady = false;
        input.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.GONE);
        security.setVisibility(View.GONE);
    }

    private void showConfirm() {
        isReady = true;
        input.setVisibility(View.GONE);
        confirm.setVisibility(View.VISIBLE);
        security.setVisibility(View.GONE);

        TextView txtAccount = findViewById(R.id.account_txt);
        txtAccount.setText(strAccount);
        TextView txtBeneficiary = findViewById(R.id.beneficiary_name_txt);
        txtBeneficiary.setText(strBenName);
        TextView txtAmount = findViewById(R.id.amount_txt);
        txtAmount.setText(strAmount);
        TextView txtBranch = findViewById(R.id.branch_txt);
        txtBranch.setText(strSecurity);

    }

    private void showSecurity() {
        isFinal = true;
        input.setVisibility(View.GONE);
        confirm.setVisibility(View.GONE);
        security.setVisibility(View.VISIBLE);
    }

    private void requestBankDraft(String beneficiaryName, String amount, String token, String accountNo, String securityWord, String username) {
        //beneficiaryName/amount/token/accountNo/securityWord/username
        final SweetAlertDialog prog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText(getString(R.string.label_bank_tagline)).setContentText(getString(R.string.label_processing));
        prog.setCustomImage((R.mipmap.ic_launcher));
        prog.show();
        Log.debug("bankdraftrequest");
        String params = beneficiaryName + "/" + amount + "/" + token + "/" + accountNo + "/" + securityWord + "/" + username;
        String url = SecurityLayer.genURLCBC(Constants.KEY_BANKDRAFTREQUEST_ENDPOINT, params, this);
        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);

                    // com.ceva.ubmobile.core.ui.Log.debug("ubnresponse", responsemessage);
                    Bundle bundle = new Bundle();
                    if (responsecode.equals("00")) {

                        ResponseDialogs.successToActivity(getString(R.string.success), responsemessage, BankDraftRequest.this, DashBoard.class, bundle);

                    } else {
                        ResponseDialogs.failToActivity(getString(R.string.error), responsemessage, BankDraftRequest.this, DashBoard.class, bundle);
                    }
                } catch (Exception e) {
                    Log.Error(e);
                    SecurityLayer.generateToken(getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed

                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                showToast(getString(R.string.error_500));
                SecurityLayer.generateToken(getApplicationContext());

            }
        });

    }
}
