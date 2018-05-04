package com.ceva.ubmobile.core.ui.transfers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.ConfirmationActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.Beneficiary;
import com.ceva.ubmobile.models.TransactionProcessor;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.NumberTextWatcher;
import com.ceva.ubmobile.utils.NumberUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithinBankActivity extends BaseActivity {
    @BindView(R.id.radioGroup)
    RadioGroup rGroup;
    @BindView(R.id.tx_accounts_from)
    Spinner accountFrom;
    @BindView(R.id.tx_accounts_to)
    Spinner txAccountTo;
    @BindView(R.id.txaccountNo)
    EditText txaccountNo;
    @BindView(R.id.txamount)
    EditText txAmount;
    @BindView(R.id.tx_reference)
    EditText txNarration;
    @BindView(R.id.save_ben_chk)
    CheckBox saveBenCheck;
    @BindView(R.id.tran_pin)
    EditText txTranPin;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.btnContinue)
    Button btnContinue;
    @BindView(R.id.new_fields)
    LinearLayout newFields;
    @BindView(R.id.saved_fields)
    LinearLayout savedFields;
    boolean isSaved = false;
    String chosenBenName, chosenBenAccNo, srcAccount;
    UBNSession session;
    ArrayAdapter<String> adapter_ben;

    List<String> benListString = new ArrayList<>();
    List<Beneficiary> benList = new ArrayList<>();
    String tranBenId = null;
    int selpos = 0;
    private boolean isReady = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_within_bank);
        setToolbarTitle(getString(R.string.screen_withinbank));
        ButterKnife.bind(this);
        session = new UBNSession(this);

        tranBenId = getIntent().getStringExtra(Beneficiary.KEY_BEN_ID);

        final List<String> accountList = session.getAccountNumbersNoDOM();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, accountList);
        accountFrom.setAdapter(adapter);

        adapter_ben = new ArrayAdapter<String>(this, R.layout.spinner_row, benListString);
        txAccountTo.setAdapter(adapter_ben);

        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.rd_newBen) {
                    showNewEntry();
                } else {
                    showSavedBeneficiary();
                }
            }
        });
        txAmount.addTextChangedListener(new NumberTextWatcher(txAmount));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenBenAccNo = txaccountNo.getText().toString().trim();

                String amount = NumberTextWatcher.trimCommaOfString(txAmount.getText().toString().trim());
                String transactionpin = txTranPin.getText().toString().trim();
                String[] fromsplit = accountList.get(accountFrom.getSelectedItemPosition()).split("-");
                int max = fromsplit.length;
                max = max - 1;

                srcAccount = fromsplit[max].trim();
                String narration = txNarration.getText().toString().trim();
                if (TextUtils.isEmpty(narration)) {
                    narration = "NA";
                }
                if (!isSaved) {
                    if (NetworkUtils.isConnected(WithinBankActivity.this)) {

                        if (TextUtils.isEmpty(chosenBenAccNo)) {
                            txaccountNo.setError("Please enter beneficiary account number");
                        } else {
                            if (!TextUtils.isEmpty(amount)) {
                                if (!TextUtils.isEmpty(transactionpin)) {
                                    fetchBeneficiaryDetails(chosenBenAccNo, session.getUserName());
                                } else {
                                    txTranPin.setError("Please enter your transaction PIN");
                                }
                            } else {
                                txAmount.setError("Please enter amount to send");
                            }
                        }
                        //fetchBeneficiaryDetails();
                    }
                } else {

                }
            }
        });

    }

    private void showConfirm() {
        String narration = txNarration.getText().toString().trim();
        if (TextUtils.isEmpty(narration)) {
            narration = "NA";
        }
        List<String> confirms = new ArrayList<>();
        confirms.add("Debit Account|" + accountFrom.getSelectedItem().toString());
        confirms.add("Beneficiary Name|" + chosenBenName);
        confirms.add("Beneficiary Account|" + chosenBenAccNo);
        confirms.add("Credit Amount|" + NumberUtilities.getWithDecimalPlusCurrency(Double.parseDouble(NumberTextWatcher.trimCommaOfString(txAmount.getText().toString().trim()))));
        confirms.add("Description|" + narration);

        ////@Path("/fundtransfer/{fromAcccountNumber}/{toAcccountNumber}/{amount}/{branchCode}/{username}/{type}/{description}/{pin}/{authtype}/{transdesc}/{authvalue}
        List<String> params = new ArrayList<>();
        params.add(srcAccount);
        params.add(chosenBenAccNo);
        params.add(txAmount.getText().toString().trim());
        params.add("682");
        params.add(session.getUserName());
        params.add("UB");
        params.add(narration);
        params.add(txTranPin.getText().toString().trim());

        TransactionProcessor processor = new TransactionProcessor(Constants.KEY_FUNDTRANSFER_ENDPOINT,
                1 + "",
                Constants.KEY_FUNDTRANS_WITHIN,
                confirmItems,
                params,
                txTranPin.getText().toString().trim(),
                true,
                "Send Money");

        JSONObject object = new JSONObject();
        try {
            object.putOpt("endpoint", Constants.KEY_FUNDTRANSFER_ENDPOINT);
            object.putOpt("service", Constants.KEY_FUNDTRANS_WITHIN);
            object.putOpt("pin", txTranPin.getText().toString().trim());
            object.putOpt("button_text", "Send Money");
            JSONArray confirm = new JSONArray(confirms);
            object.putOpt("confirmation", confirm);
            JSONArray paramsArr = new JSONArray(params);
            object.putOpt("params", paramsArr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = object.toString();
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);

    }

    /**
     * show saved beneficiary fields
     * that is the accounts to drop down
     */
    private void showSavedBeneficiary() {
        isSaved = true;
        savedFields.setVisibility(View.VISIBLE);
        newFields.setVisibility(View.GONE);
    }

    /**
     * New cash recipient
     */
    private void showNewEntry() {
        isSaved = false;
        savedFields.setVisibility(View.GONE);
        newFields.setVisibility(View.VISIBLE);
    }

    /**
     * fetch new beneficiary details
     *
     * @param accountNumber
     * @param username
     */
    private void fetchBeneficiaryDetails(final String accountNumber, String username) {
        showLoadingProgress();

        String params = accountNumber + "/" + username;

        String url = SecurityLayer.genURLCBC(Constants.KEY_FETCHACCOUNTINFO_ENDPOINT, params, getApplicationContext());

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dismissProgress();
                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);

                    String full = response.raw().body().toString();
                    chosenBenName = obj.optString(Constants.KEY_BEN_ACCOUNTNAME);
                    chosenBenAccNo = accountNumber;
                    Log.debug("ubnresponse", full);
                    if (responsecode.equals("00")) {

                        if (!TextUtils.isEmpty(chosenBenName)) {
                            if (srcAccount.equals(chosenBenAccNo)) {
                                ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_account), getApplicationContext());
                            } else {
                                if (chosenBenName.length() == 0) {
                                    warningDialog("No Account name found");
                                } else {

                                    showConfirm();
                                }

                            }
                        } else {
                            ResponseDialogs.warningStatic("Error", "Sorry, we are unable to retrieve the account name for the provided account number", getApplicationContext());
                        }
                        // ben_accountName = response.body().getBeneficiaryAccountName();
                        // showConfirm(beneficiary_type,ben_accountName,beneficiciary_accNumber,beneficiary_bank);
                    } else {

                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, getApplicationContext());
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    SecurityLayer.generateToken(getApplicationContext());
                    Log.Error(e);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_500), getApplicationContext());
                SecurityLayer.generateToken(getApplicationContext());
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    /**
     * call to fetch beneficiaries
     *
     * @param userName
     * @param type
     */
    private void fetchBeneficiaries(String userName, String type) {
        Log.debug("calling fetchben");
        benListString.clear();
        benList.clear();
        showLoadingProgress();

        String urlparam = userName + "/" + type;
        String url = SecurityLayer.genURLCBC("fetchsavedbeneficiaries", urlparam, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        // customermobile = obj.optString("customermobileno");
                        JSONArray array = new JSONArray(obj.optString("savedbeneficiaries"));

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject un = array.getJSONObject(i);
                            if (tranBenId != null) {
                                if (un.optString("beneficiaryId").equals(tranBenId)) {
                                    selpos = i;
                                    Log.debug("match found ", tranBenId);
                                }
                            }
                            Beneficiary beneficiary = new Beneficiary(un.optString("accountName"), un.optString("beneficiaryAccount"), "Union Bank", "Union", "NA", un.optString("beneficiaryId"));
                            benList.add(beneficiary);
                            benListString.add(un.optString("accountName") + " - " + un.optString("beneficiaryAccount"));
                        }

                        adapter_ben.notifyDataSetChanged();
                        txAccountTo.setSelection(selpos);

                    } else {
                        //ResponseDialogs.warningDialogLovely(getApplicationContext(), "Error", obj.optString(Constants.KEY_MSG));
                    }

                } catch (Exception e) {
                    SecurityLayer.generateToken(getApplicationContext());
                    e.printStackTrace();
                    showToast(getString(R.string.error_server));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                SecurityLayer.generateToken(getApplicationContext());
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                showToast(getString(R.string.error_server));
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

}
