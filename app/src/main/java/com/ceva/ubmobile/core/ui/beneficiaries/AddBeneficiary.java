package com.ceva.ubmobile.core.ui.beneficiaries;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.Banks;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;
import com.satsuware.usefulviews.LabelledSpinner;
import com.ceva.ubmobile.core.ui.widgets.CustomSearchableSpinner;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBeneficiary extends BaseActivity implements OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Context context;
    int bankposition = 0;
    LinearLayout entry;
    List<Banks> banksList = new ArrayList<>();
    UBNSession session;
    CustomSearchableSpinner spinner_banks;
    List<String> bankNames = new ArrayList<>();
    String ben_accountName = "";
    LinearLayout banks_field, confirm_fields;
    String beneficiary_type;
    String beneficiciary_accNumber;
    String beneficiary_bankcode = "";
    String beneficiary_bankShortName = "";
    Button confirm;
    private String accountNumber, beneficiary_account, beneficiary_bank, beneficiary_name;
    private EditText editAcc;
    private RadioGroup radioGroup;
    private LabelledSpinner spinner_type;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean isWithin = true;
    private boolean isForex = false;
    private boolean isOther = false;
    private boolean isReady = false;


    public AddBeneficiary() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beneficiary);
        // Inflate the layout for this fragment

        setToolbarTitle(getString(R.string.page_add_beneficiary));
        session = new UBNSession(this);
        banksList = session.getBanks();

        int k = banksList.size();
        Log.debug("bankssaved", "" + k);
        for (int j = 0; j < k; j++) {
            bankNames.add(banksList.get(j).getBankName());
        }
        entry = findViewById(R.id.entry_fields);
        spinner_banks = findViewById(R.id.beneficiary_bank);
        spinner_banks.setPositiveButton(getString(R.string.label_ok));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, bankNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_banks.setAdapter(dataAdapter);

        entry.setVisibility(View.VISIBLE);
        context = this;
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.check(R.id.rb_within);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_within) {
                    withinBank();
                } else if (i == R.id.rb_other) {
                    OtherBanks();
                } else {
                    forexBeneficiary();
                }
            }
        });
        editAcc = findViewById(R.id.beneficiary_account);

        banks_field = findViewById(R.id.bank_fields);
        confirm_fields = findViewById(R.id.confirm_fields);
        withinBank();
        confirm = findViewById(R.id.btnContinue);
        confirm.setOnClickListener(this);
        //spinner_bank.setOnItemChosenListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnContinue) {
            Log.debug("tumefikahapa");
            beneficiciary_accNumber = editAcc.getText().toString();
            if (Utility.isNotNull(beneficiciary_accNumber)) {
                if (NetworkUtils.isConnected(context)) {
                    if (isReady) {
                        setBeneficiary(ben_accountName, beneficiciary_accNumber, beneficiary_bankcode, beneficiary_bankShortName, session.getUserName(), beneficiary_type);
                    } else {
                        if (isWithin) {
                            fetchBeneficiaryDetails(beneficiciary_accNumber, session.getUserName());
                        } else {
                            beneficiary_bankcode = banksList.get(spinner_banks.getSelectedItemPosition()).getBankCode();
                            beneficiary_bank = banksList.get(spinner_banks.getSelectedItemPosition()).getBankName();
                            beneficiary_bankShortName = banksList.get(spinner_banks.getSelectedItemPosition()).getShortName();
                            beneficiary_type = "OTHER";
                            fetchNIPBeneficiaryDetails(session.getUserName(), beneficiciary_accNumber, beneficiary_bankcode);
                            //ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.title_activity_coming_soon), this);
                        }
                    }
                } else {
                    ResponseDialogs.warningStatic(getString(R.string.label_no_internet), getString(R.string.error_no_internet_connection), this);
                }
            } else {
                ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.label_accountnumbermissing), this);
            }
            /*if (NetworkUtils.isConnected(context)) {
                //beneficiary_name = editName.getText().toString();
                beneficiary_account = editAcc.getText().toString();

                if (Utility.isNotNull(beneficiary_name) && Utility.isNotNull(beneficiary_account) && bankposition != 0) {
                    //xxxxxxxxxxx/0035040115/999058/GUARANTYTRUSTBANK/0047204327
                    setBeneficiary(beneficiary_name, beneficiary_account, "999058", beneficiary_bank, accountNumber);
                }
            }*/
        }
    }

    private void withinBank() {
        isWithin = true;
        isForex = false;
        isOther = false;

        beneficiary_type = "UNION";
        beneficiary_bank = "Union Bank";
        beneficiary_bankcode = "032";
        beneficiary_bankShortName = "UBN";
        banks_field.setVisibility(View.GONE);
        LinearLayout currency_fields = findViewById(R.id.currency_fields);
        currency_fields.setVisibility(View.GONE);
    }

    private void OtherBanks() {
        isWithin = false;
        //isForex = false;
        isOther = true;
        isForex = false;
        beneficiary_type = "OTHER";
        banks_field.setVisibility(View.VISIBLE);
        LinearLayout currency_fields = findViewById(R.id.currency_fields);
        currency_fields.setVisibility(View.GONE);
    }

    private void forexBeneficiary() {
        isWithin = false;
        //isForex = false;
        isOther = false;
        isForex = true;
        beneficiary_type = "INTERNATIONAL";
        banks_field.setVisibility(View.VISIBLE);
        LinearLayout currency_fields = findViewById(R.id.currency_fields);
        currency_fields.setVisibility(View.VISIBLE);
    }

    private void setBeneficiary(String accountName, String beneficiaryAccount, String destinationBank, String destinationBankName, final String username, String type) {
        showLoadingProgress();
        //@Path("/savebeneficiariesdata/{accountname}/{beneficiaryaccount}/{destinationbank}/{destinationbankname}/{username}/{type}")
        String params = "";
        try {
            params = URLEncoder.encode(accountName, "utf-8") + "/" + beneficiaryAccount + "/" + destinationBank + "/" + destinationBankName + "/" + username + "/" + type;
        } catch (Exception e) {
            Log.Error(e);
        }
        Log.debug("params:" + params);
        String url = SecurityLayer.genURLCBC(Constants.KEY_SAVEBENEFICIARY_ENDPOINT, params, this);
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

                    if (responsecode.equals("00")) {

                        showSuccess();
                        //fetchBeneficiaries(username);

                    } else {
                        ResponseDialogs.failStatic(context.getString(R.string.error), responsemessage, context);
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
                dismissProgress();

            }
        });

    }

    private void fetchBeneficiaryDetails(String accountNumber, String username) {
        showLoadingProgress();
        String params = accountNumber + "/" + username;
        String url = SecurityLayer.genURLCBC(Constants.KEY_FETCHACCOUNTINFO_ENDPOINT, params, this);

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

                    if (responsecode.equals("00")) {

                        ben_accountName = obj.optString(Constants.KEY_BEN_ACCOUNTNAME).trim();

                        if (ben_accountName.length() == 0) {
                            warningDialog("No Account name found");
                        } else {

                            showConfirm(beneficiary_type, ben_accountName, beneficiciary_accNumber, beneficiary_bank);
                        }
                    } else {

                        ResponseDialogs.warningStatic(context.getString(R.string.error), responsemessage, context);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    SecurityLayer.generateToken(getApplicationContext());
                    Log.debug(e.toString());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                SecurityLayer.generateToken(getApplicationContext());
                showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    private void fetchNIPBeneficiaryDetails(String username, String accountNumber, String bankCode) {
        //nipenquiry/sravan/0004498938/999058
        showLoadingProgress();
        //only for test purposes
        if (Log.DEBUG_MODE) {
            List<Banks> banksList = session.getBanks();
            int m = banksList.size();
            for (int j = 0; j < m; j++) {
                if (bankCode.equals(banksList.get(j).getBankCode())) {
                    //bankCode = "999" + banksList.get(j).getCbnBankCode();
                }
            }
        }
        String params = username + "/" + accountNumber + "/" + bankCode;

        String url = SecurityLayer.genURLCBC("nipenquiry", params, this);

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

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {
                        JSONObject objData = obj.getJSONObject("NIPENQDATA");
                        ben_accountName = objData.optString("accountName");
                        Log.debug("beneficiaryName", ben_accountName);
                        if (ben_accountName.length() == 0) {
                            warningDialog("No Account name found");
                        } else {

                            showConfirm(beneficiary_type, ben_accountName, beneficiciary_accNumber, beneficiary_bank);
                        }
                    } else {

                        ResponseDialogs.warningStatic(context.getString(R.string.error), responsemessage, context);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    SecurityLayer.generateToken(getApplicationContext());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                showToast(getString(R.string.error_500));
                SecurityLayer.generateToken(getApplicationContext());
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    private void showConfirm(String type, String name, String account, String bank) {
        isReady = true;
        LinearLayout acct = findViewById(R.id.account_fields);
        acct.setVisibility(View.GONE);
        banks_field.setVisibility(View.GONE);
        confirm_fields.setVisibility(View.VISIBLE);
        TextView txtType = findViewById(R.id.beneficiary_type_txt);
        TextView txtName = findViewById(R.id.beneficiary_name_txt);
        TextView txtAccount = findViewById(R.id.beneficiary_account_txt);
        TextView txtBank = findViewById(R.id.beneficiary_bank_txt);

        txtType.setText(type);
        txtName.setText(name);
        txtAccount.setText(account);
        txtBank.setText(bank);
    }

    private void showSuccess() {
        confirm_fields.setVisibility(View.VISIBLE);
        LinearLayout acct = findViewById(R.id.success_fields);
        acct.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.GONE);
    }

}
