package com.ceva.ubmobile.core.ui.billers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.Log;
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

public class PayDueBills extends BaseActivity {
    List<BillerPaymentModel> billerPaymentModelList = new ArrayList<>();
    List<String> productsList = new ArrayList<>();
    String customField1, customField2, billerName;
    EditText customField1Val, customField2Val, amount, fee, phoneNumber, tran_pin;
    Spinner accountFrom, productsSpinner;
    UBNSession session;
    Context context;
    ArrayAdapter<String> product_adapter;
    CheckBox phoneBox;
    Button buttonRecharge, buttonCancel;
    boolean isReady = false;
    @BindView(R.id.token_auth)
    LinearLayout token_auth;
    @BindView(R.id.auth_mode)
    RadioGroup auth_mode;
    @BindView(R.id.auth_code)
    EditText auth_code;
    String AUTH_MODE_TYPE = Constants.KEY_AUTH_TOKEN;

    boolean isTokenRequired = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paybills);
        ButterKnife.bind(this);
        setToolbarTitle("Pay Bills");
        context = this;
        session = new UBNSession(context);
        token_auth.setVisibility(View.GONE);
        auth_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.rb_sms) {
                    AUTH_MODE_TYPE = Constants.KEY_AUTH_SMS;
                    if (NetworkUtils.isConnected(PayDueBills.this)) {
                        generateSMSToken(session.getUserName(), session.getPhoneNumber(), Constants.KEY_FUNDTRANS_WITHIN);
                    } else {
                        noInternetDialog();
                    }
                } else {
                    AUTH_MODE_TYPE = Constants.KEY_AUTH_TOKEN;
                }
            }
        });

        final List<String> accountList = session.getAccountNumbersNoDOM();

        amount = (EditText) findViewById(R.id.amount);
        fee = (EditText) findViewById(R.id.fee);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        tran_pin = (EditText) findViewById(R.id.tran_pin);
        phoneBox = (CheckBox) findViewById(R.id.checkBox);

        phoneBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    phoneNumber.setText(session.getPhoneNumber());
                    DisableEditText(phoneNumber);
                } else {
                    phoneNumber.setText("");
                    EnableEditText(phoneNumber);
                }
            }
        });

        accountFrom = (Spinner) findViewById(R.id.account);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_row, accountList);

        accountFrom.setAdapter(adapter);

        productsSpinner = (Spinner) findViewById(R.id.productSpinner);
        product_adapter = new ArrayAdapter<String>(context, R.layout.spinner_row, productsList);

        productsSpinner.setAdapter(product_adapter);
        productsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BillerPaymentModel billerPaymentModel = billerPaymentModelList.get(i);
                if (!billerPaymentModel.getAmount().equals("0")) {
                    amount.setText(billerPaymentModel.getAmount());
                    DisableEditText(amount);
                } else {
                    EnableEditText(amount);
                }
                fee.setText(billerPaymentModel.getFee());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String billerID = getIntent().getStringExtra(BillerModel.KEY_BILLER_ID);
        customField1 = getIntent().getStringExtra(BillerModel.KEY_CUSTOM_FIELD1).trim();
        customField2 = getIntent().getStringExtra(BillerModel.KEY_CUSTOM_FIELD2).trim();
        billerName = getIntent().getStringExtra(BillerModel.KEY_BILLER_NAME).trim();

        TextView customfield1txt = (TextView) findViewById(R.id.customField1);
        customfield1txt.setText(customField1);

        customField1Val = (EditText) findViewById(R.id.customField1Val);
        customField1Val.setHint(customField1);
        customField2Val = (EditText) findViewById(R.id.customField2Val);

        Log.debug("customfield2val " + customField2);
        LinearLayout field_2 = (LinearLayout) findViewById(R.id.field_2);
        if (!customField2.equals("null")) {

            field_2.setVisibility(View.VISIBLE);
            TextView customfield2txt = (TextView) findViewById(R.id.customField2);
            customfield2txt.setText(customField2);

        } else {
            field_2.setVisibility(View.GONE);
        }
        initializeconfirm();

        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonRecharge = (Button) findViewById(R.id.buttonRecharge);
        buttonRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BillerPaymentModel billerPaymentModel = billerPaymentModelList.get(productsSpinner.getSelectedItemPosition());
                String operName = billerName;
                String amountStr = amount.getText().toString();
                String accountDebit = accountFrom.getSelectedItem().toString();
                String[] accountSplit = accountDebit.split("-");
                String accountNo = accountSplit[(accountSplit.length - 1)].trim();
                String phone = customField1Val.getText().toString();
                String feeStr = billerPaymentModel.getFee();
                String paymentCode = billerPaymentModel.getPaymentCode();
                String mobile = phoneNumber.getText().toString();
                String pins = tran_pin.getText().toString();

                if (isReady) {
                    if (NetworkUtils.isConnected(PayDueBills.this)) {
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
                        ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_no_internet_connection), PayDueBills.this);
                    }
                } else {

                    if (Utility.isNotNull(phone)) {
                        if (Utility.isNotNull(amountStr)) {
                            if (Utility.isNotNull(pins)) {

                                ConfirmModel confirmModel = new ConfirmModel("Biller", operName);
                                confirmItems.add(confirmModel);

                                confirmModel = new ConfirmModel("Utility/Product", billerPaymentModel.getPaymentName());
                                confirmItems.add(confirmModel);

                                confirmModel = new ConfirmModel(customField1, customField1Val.getText().toString());
                                confirmItems.add(confirmModel);

                                confirmModel = new ConfirmModel("Amount", NumberUtilities.getWithDecimalPlusCurrency(Double.parseDouble(amountStr)));
                                confirmItems.add(confirmModel);

                                confirmModel = new ConfirmModel("Fee", feeStr);
                                confirmItems.add(confirmModel);

                                confirmModel = new ConfirmModel("Mobile Number", mobile);
                                confirmItems.add(confirmModel);

                                confirmModel = new ConfirmModel("Account Debited", accountDebit);
                                confirmItems.add(confirmModel);

                                showFancyConfirm();
                                Utility.hideKeyboard(PayDueBills.this);
                                if (NetworkUtils.isConnected(PayDueBills.this)) {
                                    checkIfTokenRequired(session.getUserName(), amountStr, Constants.KEY_FUNDTRANS_PAYBILL, pins);
                                } else {
                                    hideFancyConfirm();
                                    noInternetDialog();
                                }
                                isReady = true;
                            } else {
                                ResponseDialogs.warningStatic(getString(R.string.error), "Please enter your transaction Pin.", PayDueBills.this);
                            }
                        } else {
                            ResponseDialogs.warningStatic(getString(R.string.error), "Please enter the amount.", PayDueBills.this);
                        }
                    } else {
                        ResponseDialogs.warningStatic(getString(R.string.error), "Please enter " + customField1, PayDueBills.this);
                    }

                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isReady) {
                    if (isTransactionComplete) {
                        startActivity(new Intent(PayDueBills.this, DashBoard.class));
                    } else {
                        hideFancyConfirm();
                    }
                } else {
                    startActivity(new Intent(PayDueBills.this, DashBoard.class));
                }
            }
        });
        fetchBillersDetails(session.getUserName(), billerID);

    }

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
                    obj = SecurityLayer.decryptTransaction(obj, PayDueBills.this);
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {

                        JSONArray objData = new JSONArray(obj.optString("billersdata"));
                        int m = objData.length();
                        Log.debug("number of billers " + objData.length());

                        for (int j = 0; j < m; j++) {
                            JSONObject billerDet = objData.getJSONObject(j);

                            BillerPaymentModel paymentModel = new BillerPaymentModel(billerDet.optString(BillerPaymentModel.KEY_BILL_AMOUNT),
                                    billerDet.optString(BillerPaymentModel.KEY_BILL_PAYNAME),
                                    billerDet.optString(BillerPaymentModel.KEY_BILL_FEE),
                                    billerDet.optString(BillerPaymentModel.KEY_BILL_ID),
                                    billerDet.optString(BillerPaymentModel.KEY_BILL_CODE));
                            billerPaymentModelList.add(paymentModel);

                            productsList.add(billerDet.optString(BillerPaymentModel.KEY_BILL_PAYNAME));
                            //Constants.KEY_NAIRA + Utility.stringNumberToDecimal(billerDet.optString(BillerPaymentModel.KEY_BILL_AMOUNT))

                        }

                        product_adapter.notifyDataSetChanged();

                    } else {

                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, PayDueBills.this);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("billerfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    //paybill
    private void invokePayBill(final String username, String amount, String paymentCode, String mobile, String custid, String accountno, String fee, String pin, String billername, String authtype, String transdesc, String authvalue) {
        //@Path("/payBills/{username}/{amount}/{paymentcode}/{mobile}/{custid}/{accountno}/{fee}")
        //@Path("/payBills/{username: .}/{amount: .}/{paymentcode: .}/{mobile: .}/{custid: .}/{accountno: .}/{fee: .*}/{pin: .}/{billername: .}/{authtype:.*}/{transdesc:.*}/{authvalue:.*}
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
                    obj = SecurityLayer.decryptTransaction(obj, PayDueBills.this);
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {
                        updateBalances(username);
                        showTransactionComplete(0, responsemessage);
                        //ResponseDialogs.successToActivity(getString(R.string.success), responsemessage, PayDueBills.this, DashBoard.class,new Bundle());

                    } else if (responsecode.equals("7007")) {
                        ResponseDialogs.limitError(PayDueBills.this, "Info", responsemessage, DashBoard.class);
                        showTransactionComplete(1, responsemessage);
                    } else if (responsecode.equals("03")) {
                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, PayDueBills.this);
                        hideFancyConfirm();
                    } else {
                        showTransactionComplete(1, responsemessage);
                        //ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, PayDueBills.this);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                    obj = SecurityLayer.decryptTransaction(obj, PayDueBills.this);
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
                            ResponseDialogs.warningDialogLovely(PayDueBills.this, "Error", obj.optString(Constants.KEY_MSG));
                        }

                    } else {
                        ResponseDialogs.warningDialogLovely(PayDueBills.this, "Error", obj.optString(Constants.KEY_MSG));
                        hideFancyConfirm();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hideFancyConfirm();
                    ResponseDialogs.warningDialogLovely(PayDueBills.this, "Error", getString(R.string.error_500));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                hideFancyConfirm();
                ResponseDialogs.warningDialogLovely(PayDueBills.this, "Error", getString(R.string.error_500));
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }
}

