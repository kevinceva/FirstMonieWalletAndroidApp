package com.ceva.ubmobile.core.ui.bankservices;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.Branches;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.core.ui.widgets.CustomSearchableSpinner;

import org.json.JSONObject;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChequeBookRequest extends BaseActivity implements View.OnClickListener {
    UBNSession session;
    Spinner accountFrom, num_pages, pickup;
    CustomSearchableSpinner bankBranches;
    List<Branches> branchesList;
    boolean isReady = false;
    String accountNum, pagenum, pickup_str, branchCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chequebookrequest);
        session = new UBNSession(this);
        setToolbarTitle(getString(R.string.page_cheque_book));

        List<String> accountList = session.getAccountNumbers();
        accountFrom = findViewById(R.id.account_number);
        num_pages = findViewById(R.id.pages);
        pickup = findViewById(R.id.pickup_option);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, accountList);
        accountFrom.setAdapter(adapter);

        branchesList = session.getBranches();
        Log.debug("number of branches: " + branchesList.size());
        List<String> branchNames = session.getBranchNames();
        ArrayAdapter<String> branch_adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, branchNames);

        bankBranches = findViewById(R.id.bank_branches);
        bankBranches.setAdapter(branch_adapter);
        bankBranches.setTitle("Select bank branch");
        bankBranches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                branchCode = branchesList.get(i).getbranchCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);

        Button btnCancel = findViewById(R.id.btncancel);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnContinue) {
            if (isReady) {
                if (NetworkUtils.isConnected(this)) {
                    String[] acctsplit = accountNum.split("-");
                    int max = acctsplit.length - 1;
                    accountNum = acctsplit[max].trim();
                    requestChequeBook(pagenum, accountNum, branchCode, pickup_str.replace(" ", ""), session.getUserName());
                } else {
                    ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_no_internet_connection), this);
                }
            } else {
                showConfirm();
            }
        } else if (id == R.id.btncancel) {
            if (isReady) {
                showInput();
            } else {
                onBackPressed();
            }
        }
    }

    private void showInput() {
        LinearLayout input_fields = findViewById(R.id.input_fields);
        input_fields.setVisibility(View.VISIBLE);

        LinearLayout confirm_fields = findViewById(R.id.confirm_fields);
        confirm_fields.setVisibility(View.GONE);
    }

    private void showConfirm() {
        isReady = true;

        accountNum = accountFrom.getSelectedItem().toString();
        pagenum = num_pages.getSelectedItem().toString();
        pickup_str = pickup.getSelectedItem().toString();

        TextView acctxt = findViewById(R.id.account_txt);
        TextView pages_txt = findViewById(R.id.page_num_txt);
        TextView picktxt = findViewById(R.id.pickup_txt);
        TextView branchtxt = findViewById(R.id.branch_txt);

        acctxt.setText(accountNum);
        pages_txt.setText(pagenum);
        picktxt.setText(pickup_str);
        branchtxt.setText(bankBranches.getSelectedItem().toString());

        LinearLayout input_fields = findViewById(R.id.input_fields);
        input_fields.setVisibility(View.GONE);

        LinearLayout confirm_fields = findViewById(R.id.confirm_fields);
        confirm_fields.setVisibility(View.VISIBLE);
    }

    private void requestChequeBook(String pages, String accountNo, String branchCode, String delivery, String username) {
        //address/selectBooklet/accountNo/chooseBranch/deliveryoption/username
        //address/selectBooklet/accountNo/chooseBranch/deliveryoption/username
        final SweetAlertDialog prog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText(getString(R.string.label_bank_tagline)).setContentText(getString(R.string.label_processing));
        prog.setCustomImage((R.mipmap.ic_launcher));
        prog.show();
        Log.debug("chequebookrequest");
        String params = "na/" + pages + "/" + accountNo + "/" + branchCode + "/" + delivery + "/null/null/" + username;
        String urlparam = SecurityLayer.genURLCBC("chequebookrequest", params, this);
        String data = null;

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparam);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("otpvalidation", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        ResponseDialogs.success("Success", obj.optString(Constants.KEY_MSG), ChequeBookRequest.this);
                    } else {
                        ResponseDialogs.warningDialogLovely(ChequeBookRequest.this, "Error", obj.optString(Constants.KEY_MSG));
                        //onContinueClick();
                    }

                } catch (Exception e) {
                    SecurityLayer.generateToken(getApplicationContext());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.generateToken(getApplicationContext());
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                showToast(getString(R.string.error_500));

            }
        });

    }
}
