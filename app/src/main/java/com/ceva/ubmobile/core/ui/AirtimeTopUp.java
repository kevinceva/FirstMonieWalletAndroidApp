package com.ceva.ubmobile.core.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.models.BillerModel;
import com.ceva.ubmobile.models.BillerPaymentModel;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;
import com.ceva.ubmobile.utils.NumberUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AirtimeTopUp extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    public static ArrayList<String> phoneValueArr = new ArrayList<String>();
    public static ArrayList<String> nameValueArr = new ArrayList<String>();
    UBNSession session;
    Spinner sp_operators;
    ArrayAdapter<String> operator_adapter;
    BillerPaymentModel billerPaymentModel;
    Button buttonRecharge, buttonCancel;
    boolean isReady = false;
    EditText amount, phoneNumber;
    Spinner accountFrom;
    List<String> accountList = new ArrayList<>();
    CheckBox phoneBox;
    EditText pin;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.name)
    AutoCompleteTextView name;
    @BindView(R.id.token_auth)
    LinearLayout token_auth;
    @BindView(R.id.auth_mode)
    RadioGroup auth_mode;
    @BindView(R.id.auth_code)
    EditText auth_code;
    @BindView(R.id.name_fields)
    LinearLayout name_fields;
    String AUTH_MODE_TYPE = Constants.KEY_AUTH_TOKEN;
    boolean isTokenRequired = false;
    String toNumberValue = "";
    boolean isContactsLoaded = false;
    private List<String> operators = new ArrayList<>();
    private String[] custid = {};
    private List<BillerModel> billerList = new ArrayList<>();
    private ArrayAdapter<String> contactsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airtimetopup);
        ButterKnife.bind(this);
        setToolbarTitle(getString(R.string.mobile_recharge));
        session = new UBNSession(this);
        token_auth.setVisibility(View.GONE);
        auth_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.rb_sms) {
                    AUTH_MODE_TYPE = Constants.KEY_AUTH_SMS;
                    if (NetworkUtils.isConnected(AirtimeTopUp.this)) {
                        generateSMSToken(session.getUserName(), session.getPhoneNumber(), Constants.KEY_FUNDTRANS_WITHIN);
                    } else {
                        noInternetDialog();
                    }
                } else {
                    AUTH_MODE_TYPE = Constants.KEY_AUTH_TOKEN;
                }
            }
        });

        accountList = session.getAccountNumbersNoDOM();
        accountFrom = findViewById(R.id.account);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_row, accountList);
        accountFrom.setAdapter(adapter);

        amount = findViewById(R.id.amount);
        phoneNumber = findViewById(R.id.phoneNumber);
        phoneBox = findViewById(R.id.checkBox);
        buttonCancel = findViewById(R.id.buttonCancel);

        phoneNumber.setText(session.getPhoneNumber());

        phoneBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    phoneNumber.setText(session.getPhoneNumber());
                } else {
                    phoneNumber.setText("");
                }
            }
        });

        sp_operators = findViewById(R.id.networkOperator);
        operator_adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, operators);
        sp_operators.setAdapter(operator_adapter);

        pin = findViewById(R.id.pin);

        sp_operators.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fetchBillersDetails(session.getUserName(), billerList.get(i).getBillerID());
                Log.debug("billerID ", billerList.get(i).getBillerID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fetchBillers(session.getUserName());

        //confirm manenos
        initializeconfirm();

        buttonRecharge = findViewById(R.id.buttonRecharge);
        buttonRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String operName = sp_operators.getSelectedItem().toString();
                    String amountStr = amount.getText().toString();
                    String accountDebit = accountFrom.getSelectedItem().toString();
                    String[] accountSplit = accountDebit.split("-");
                    String accountNo = accountSplit[(accountSplit.length - 1)].trim();
                    String phone = phoneNumber.getText().toString();
                    String feeStr = billerPaymentModel.getFee();
                    String paymentCode = billerPaymentModel.getPaymentCode();
                    String mobile = session.getPhoneNumber();
                    String pins = pin.getText().toString();

                    if (isReady) {
                        if (NetworkUtils.isConnected(AirtimeTopUp.this)) {
                            buttonRecharge.setVisibility(View.GONE);
                            buttonCancel.setVisibility(View.GONE);
                            buttonCancel.setText(getString(R.string.drawer_close));
                            try {
                                operName = URLEncoder.encode(operName, "utf-8");
                            } catch (Exception e) {

                            }
                            if (isTokenRequired) {

                                if (!TextUtils.isEmpty(auth_code.getText().toString())) {
                                    invokePayBill(session.getUserName(), amountStr, paymentCode, mobile, phone, accountNo, feeStr, pins, operName, AUTH_MODE_TYPE, operName, auth_code.getText().toString());

                                } else {
                                    warningDialog("Please input the security code.");
                                }

                            } else {
                                invokePayBill(session.getUserName(), amountStr, paymentCode, mobile, phone, accountNo, feeStr, pins, operName, Constants.KEY_AUTH_NO_AUTH, operName, Constants.KEY_AUTH_NO_AUTH);
                            }

                        } else {
                            ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_no_internet_connection), AirtimeTopUp.this);
                        }
                    } else {
                        if (!TextUtils.isEmpty(accountDebit)) {
                            if (Utility.isNotNull(phone)) {
                                if (Utility.isNotNull(amountStr)) {
                                    if (Utility.isNotNull(pins)) {
                                        if (!TextUtils.isEmpty(operName)) {
                                            confirmItems.clear();

                                            ConfirmModel confirmModel = new ConfirmModel("Account Debited", accountDebit);
                                            confirmItems.add(confirmModel);

                                            confirmModel = new ConfirmModel("Biller Category", "Airtime purchase");
                                            confirmItems.add(confirmModel);

                                            confirmModel = new ConfirmModel("Phone Number", phone);
                                            confirmItems.add(confirmModel);

                                            confirmModel = new ConfirmModel("Amount", NumberUtilities.getWithDecimalPlusCurrency(Double.parseDouble(amountStr)));
                                            confirmItems.add(confirmModel);

                                            confirmModel = new ConfirmModel("Network Operator", operName);
                                            confirmItems.add(confirmModel);

                                            showFancyConfirm();
                                            Utility.hideKeyboard(AirtimeTopUp.this);
                                            if (NetworkUtils.isConnected(AirtimeTopUp.this)) {
                                                isReady = true;
                                                checkIfTokenRequired(session.getUserName(), amountStr, Constants.KEY_FUNDTRANS_PAYBILL, pins);
                                            } else {
                                                noInternetDialog();
                                            }

                                        } else {
                                            ResponseDialogs.warningStatic(getString(R.string.error), "Please select a mobile operator.", AirtimeTopUp.this);
                                        }
                                    } else {
                                        ResponseDialogs.warningStatic(getString(R.string.error), "Please enter your transaction pin.", AirtimeTopUp.this);
                                    }
                                } else {
                                    ResponseDialogs.warningStatic(getString(R.string.error), "Please enter the amount.", AirtimeTopUp.this);
                                }
                            } else {
                                ResponseDialogs.warningStatic(getString(R.string.error), "Please enter phone number", AirtimeTopUp.this);
                            }
                        } else {
                            ResponseDialogs.warningStatic(getString(R.string.error), "Please select account to debit", AirtimeTopUp.this);
                        }

                    }
                } catch (Exception e) {
                    //FirebaseCrash.report(e);
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isReady) {
                    if (isTransactionComplete) {
                        startActivity(new Intent(AirtimeTopUp.this, DashBoard.class));
                        finish();
                    } else {
                        isTransactionComplete = false;
                        isReady = false;
                        hideFancyConfirm();
                    }
                } else {
                    startActivity(new Intent(AirtimeTopUp.this, DashBoard.class));
                }
            }
        });
        phoneNumber.setEnabled(false);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.rb_self) {
                    name_fields.setVisibility(View.GONE);
                    phoneNumber.setText(session.getPhoneNumber());
                    phoneNumber.setEnabled(false);
                }
                if (i == R.id.rb_contacts) {

                    if (!isContactsLoaded) {
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                readContactData();
                                isContactsLoaded = true;
                            }
                        }, 2000);
                    }
                    name_fields.setVisibility(View.VISIBLE);
                    phoneNumber.setText("");
                    phoneNumber.setEnabled(true);

                }
                if (i == R.id.rb_other) {
                    phoneNumber.setEnabled(true);
                    name_fields.setVisibility(View.GONE);
                    phoneNumber.setText("");
                }
            }
        });
        contactsAdapter = new ArrayAdapter<String>
                (AirtimeTopUp.this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        name.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        name.setAdapter(contactsAdapter);
        name.setOnItemSelectedListener(AirtimeTopUp.this);
        name.setOnItemClickListener(AirtimeTopUp.this);

    }

    private void readContactData() {
        if (!(this == null)) {
            ContentResolver cr = this.getContentResolver();
            Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
            if (cursor != null) {
                try {
                    final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    String name, number;
                    //showLoadingProgress("Loading contacts");
                    while (cursor.moveToNext()) {
                        name = cursor.getString(nameIndex);
                        number = cursor.getString(numberIndex);
                        number = number.replaceAll("\\s", "");
                        contactsAdapter.add(name);

                        // Add ArrayList names to adapter
                        phoneValueArr.add(number.toString());
                        nameValueArr.add(name.toString());
                    }
                } finally {
                    cursor.close();
                }

                ResponseDialogs.info("Info!", "We have successfully imported your contacts. Please search using your contact's name", this);
                // dismissProgress();
                /*Toast.makeText(this,
                        "You can select phone number from your contact list",
                        Toast.LENGTH_LONG).show();*/
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

        // Get Array index value for selected name
        int i = nameValueArr.indexOf("" + arg0.getItemAtPosition(arg2));

        // If name exist in name ArrayList
        if (i >= 0) {

            // Get Phone Number
            toNumberValue = phoneValueArr.get(i);
            String names = nameValueArr.get(i);
            name.setText(names);

            InputMethodManager imm = (InputMethodManager) this.getSystemService(
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            phoneNumber.setText(toNumberValue);

            // Show Alert
        /*    Toast.makeText(getActivity(),
                    "Position:" + arg2 + " Name:" + arg0.getItemAtPosition(arg2) + " Number:" + toNumberValue,
                    Toast.LENGTH_LONG).show();*/

            //Log.d("AutocompleteContacts",
            //"Position:" + arg2 + " Name:" + arg0.getItemAtPosition(arg2) + " Number:" + toNumberValue);

        }

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void fetchBillers(String username) {
        //@Path("/getbillers/{username}")
        billerList.clear();
        operators.clear();
        showLoadingProgress();

        String params = username + "/Airtime";
        String url = SecurityLayer.genURLCBC("getbillers", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("getBillers", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {
                        JSONArray objData = new JSONArray(obj.optString("billersdata"));
                        int m = objData.length();
                        Log.debug("number of billers " + objData.length());

                        for (int j = 0; j < m; j++) {
                            JSONObject billerDet = objData.getJSONObject(j);
                            if (billerDet.optString(BillerModel.KEY_SHORT_NAME).trim().equalsIgnoreCase("AIRTIME")) {

                                Log.debug("AirBiller " + billerDet.optString(BillerModel.KEY_BILLER_NAME));
                                operators.add(billerDet.optString(BillerModel.KEY_BILLER_NAME));
                                BillerModel biller = new BillerModel(billerDet.optString(BillerModel.KEY_CATEGORY),
                                        billerDet.optString(BillerModel.KEY_BILLER_ID),
                                        billerDet.optString(BillerModel.KEY_CUSTOM_FIELD1),
                                        billerDet.optString(BillerModel.KEY_CUSTOM_FIELD2),
                                        billerDet.optString(BillerModel.KEY_SHORT_NAME),
                                        billerDet.optString(BillerModel.KEY_BILLER_NAME));
                                billerList.add(biller);

                            }
                        }
                        operator_adapter.notifyDataSetChanged();
                        //ben_accountName = objData.optString("accountName");
                        // Log.debug("beneficiaryName",ben_accountName);
                        //showConfirm(beneficiary_type, ben_accountName, beneficiciary_accNumber, beneficiary_bank);
                    } else {
                        ResponseDialogs.warningDialogLovelyToActivity(AirtimeTopUp.this, getString(R.string.error), responsemessage, DashBoard.class);
                        //ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, AirtimeTopUp.this);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ResponseDialogs.warningDialogLovelyToActivity(AirtimeTopUp.this, getString(R.string.error), getString(R.string.error_server), DashBoard.class);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("billerfail", t.toString());
                ResponseDialogs.warningDialogLovelyToActivity(AirtimeTopUp.this, getString(R.string.error), getString(R.string.error_server), DashBoard.class);
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    //fetch biller details
    private void fetchBillersDetails(String username, String billerID) {
        //@Path("/getBillerDetails/{username}/{billerid}")

        showLoadingProgress();

        String params = username + "/" + billerID;

        String url = SecurityLayer.genURLCBC("getBillerDetails", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("getBillersDetails", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {

                        JSONArray objData = new JSONArray(obj.optString("billersdata"));
                        int m = objData.length();
                        Log.debug("number of billers " + objData.length());

                        for (int j = 0; j < m; j++) {
                            JSONObject billerDet = objData.getJSONObject(j);
                            if (billerDet.optString(BillerPaymentModel.KEY_BILL_AMOUNT).equals("0")) {
                                BillerPaymentModel paymentModel = new BillerPaymentModel(billerDet.optString(BillerPaymentModel.KEY_BILL_AMOUNT),
                                        billerDet.optString(BillerPaymentModel.KEY_BILL_PAYNAME),
                                        billerDet.optString(BillerPaymentModel.KEY_BILL_FEE),
                                        billerDet.optString(BillerPaymentModel.KEY_BILL_ID),
                                        billerDet.optString(BillerPaymentModel.KEY_BILL_CODE));
                                billerPaymentModel = paymentModel;
                            }
                        }

                        operator_adapter.notifyDataSetChanged();
                    } else {

                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, AirtimeTopUp.this);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ResponseDialogs.warningDialogLovelyToActivity(AirtimeTopUp.this, getString(R.string.error), getString(R.string.error_server), DashBoard.class);

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("billerfail", t.toString());
                ResponseDialogs.warningDialogLovelyToActivity(AirtimeTopUp.this, getString(R.string.error), getString(R.string.error_server), DashBoard.class);

                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    //paybill
    private void invokePayBill(final String username, String amount, String paymentCode, String mobile, String custid, String accountno, String fee, String pin, String billername, String authtype, String transdesc, String authvalue) {
        //@Path("/payBills/{username}/{amount}/{paymentcode}/{mobile}/{custid}/{accountno}/{fee}")
        //@Path("/payBills/{username: .}/{amount: .}/{paymentcode: .}/{mobile: .}/{custid: .}/{accountno: .}/{fee: .*}/{pin: .}/{billername: .}/{transdesc: .*}/

        //showLoadingProgress();
        buttonRecharge.setEnabled(false);
        showConfirmProgress();

        mobile = mobile.replace("+", "");
        custid = custid.replace("+", "");

        String params = username + "/" + amount + "/" + paymentCode + "/" + mobile + "/" + custid + "/" + accountno + "/" + fee + "/" + pin + "/" + billername + "/" + authtype + "/" + transdesc + "/" + authvalue;

        String url = SecurityLayer.genURLCBC("payBills", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("paybills", response.body());
                //dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {
                        updateBalances(username);
                        showTransactionComplete(0, responsemessage);
                        //ResponseDialogs.successToActivity(getString(R.string.success), responsemessage, AirtimeTopUp.this, DashBoard.class,new Bundle());

                    } else if (responsecode.equals("7007")) {
                        showTransactionComplete(1, responsemessage);
                        ResponseDialogs.limitError(AirtimeTopUp.this, "Info", responsemessage, DashBoard.class);

                    } else {
                        showTransactionComplete(1, responsemessage);
                        //ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, AirtimeTopUp.this);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ResponseDialogs.warningDialogLovelyToActivity(AirtimeTopUp.this, getString(R.string.error), getString(R.string.error_server), DashBoard.class);

                }
                buttonCancel.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                //dismissProgress();
                showTransactionComplete(1, getString(R.string.error_server));
                buttonCancel.setVisibility(View.VISIBLE);
                com.ceva.ubmobile.core.ui.Log.debug("billerfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }

        });

    }

    private void checkIfTokenRequired(String username, String amount, String service, String pin) {
        //@Path("/isTokenAuthRequired/{userid}/{amount}/{servicetype}/{pin}")
        showLoadingProgress();
        String params = username + "/" + amount + "/" + service + "/" + pin;
        String session_id = UUID.randomUUID().toString();
        String url = SecurityLayer.genURLCBC("isTokenAuthRequired", params, this);

        // String urlparam = "accountnovallidatin/" + SecurityLayer.generalEncrypt(accountNumber);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, AirtimeTopUp.this);
                    //isTokenRequired = true;
                    //token_auth.setVisibility(View.VISIBLE);

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {

                        if (obj.optString("isTokenAuthRequired").equals("Y")) {
                            isTokenRequired = true;
                            token_auth.setVisibility(View.VISIBLE);
                        } else if (obj.optString("isTokenAuthRequired").equals("N")) {
                            isTokenRequired = false;
                            token_auth.setVisibility(View.GONE);
                        } else {
                            ResponseDialogs.warningDialogLovely(AirtimeTopUp.this, "Error", obj.optString(Constants.KEY_MSG));
                        }

                    } else {
                        ResponseDialogs.warningDialogLovely(AirtimeTopUp.this, "Error", obj.optString(Constants.KEY_MSG));
                        hideFancyConfirm();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hideFancyConfirm();
                    ResponseDialogs.warningDialogLovely(AirtimeTopUp.this, "Error", getString(R.string.error_500));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                hideFancyConfirm();
                ResponseDialogs.warningDialogLovely(AirtimeTopUp.this, "Error", getString(R.string.error_500));
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }
}
