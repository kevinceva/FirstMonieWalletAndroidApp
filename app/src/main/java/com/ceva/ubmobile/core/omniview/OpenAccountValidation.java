package com.ceva.ubmobile.core.omniview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.BankAccountResponse;
import com.ceva.ubmobile.network.ApiClient;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;

import org.json.JSONObject;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenAccountValidation extends BaseActivity implements View.OnClickListener {
    TextView confirm;
    String productNames = "", productCodes = "";
    String selectedProducts;
    String refID = "";
    String customerID = "";
    Button btnContinue;
    UBNSession session;
    EditText token;
    SweetAlertDialog prog;
    String generatedAccount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_account_validation);
        setToolbarTitle("Confirm");
        session = new UBNSession(this);

        confirm = findViewById(R.id.confirmed);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        selectedProducts = extras.getString(Constants.KEY_PRODUCT_BUNDLE);
        token = findViewById(R.id.tran_pin);

        if (selectedProducts != null) {
            productCodes = extras.getString(Constants.KEY_PRODUCT_CODES);
            confirm.setText(selectedProducts);
        } else {
            onBackPressed();
        }
        btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (Utility.isNotNull(token.getText().toString())) {
            if (NetworkUtils.isConnected(this)) {
                if (id == R.id.btnContinue) {
                    List<String> accountList = session.getAccountNumbers();
                    String[] account = accountList.get(0).split("-");
                    int max = account.length - 1;
                    fetchBeneficiaryDetails(account[max].trim(), session.getUserName());
                } else {
                    ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_no_internet_connection), this);
                }
            }
        } else {
            ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_enter_token), this);
        }

    }

    public void showSuccess() {
        LinearLayout ln = findViewById(R.id.token_fields);
        ImageView success = findViewById(R.id.success_image);
        Button btn = findViewById(R.id.btnContinue);
        btn.setVisibility(View.GONE);
        ln.setVisibility(View.GONE);
        success.setVisibility(View.VISIBLE);
    }

    public void accountOpen(String accountName, String dateOfBirth, String productCode, String custPhone, String initiatorID, String gender, String branchCode, String rmCode, String custid, String verifierID, String username, String refid, String title, String pin) {
        // customeraccountopen/fname/mname/lname/gender/dateOfBirth/mobno/productCode/initiatorID/branch/otp
        Log.debug("calling updating balances");
        String params = accountName + "/" + dateOfBirth + "/" + productCode + "/" + custPhone + "/" + initiatorID + "/" + gender + "/" + branchCode + "/" + rmCode + "/" + custid + "/" + verifierID + "/" + username + "/" + refid + "/" + title + "/" + pin;
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<BankAccountResponse> call = apiService.setAccountOpenRequest(params);

        call.enqueue(new Callback<BankAccountResponse>() {
            @Override
            public void onResponse(Call<BankAccountResponse> call, Response<BankAccountResponse> response) {
                String responsecode = response.body().getRespcode();
                String responsemessage = response.body().getResponseDesc();
                com.ceva.ubmobile.core.ui.Log.debug("ubnresponse", responsemessage);
                Bundle bundle = new Bundle();
                if (Utility.isNotNull(responsecode)) {
                    if (responsecode.equals("0")) {
                        prog.dismiss();
                        generatedAccount = response.body().getOpenAccountNumber();
                        String msg = responsemessage + "\nAccount Number: " + generatedAccount;

                        ResponseDialogs.successToActivity(getString(R.string.success),
                                msg, OpenAccountValidation.this, DashBoard.class, bundle);

                        //Log.debug("ubnaccountspass", "Number of accounts" + accounts.size());
                        //showToast("Accounts updated...");

                   /* for(int j = 0; j < accounts.size(); j++){
                        fetchBeneficiaries(accounts.get(j).getAccountNumber());
                        if( j == (accounts.size() - 1)){
                            prog.dismiss();
                            startDashBoard();
                        }
                    }*/

                    } else {
                        prog.dismiss();
                        ResponseDialogs.failToActivity(getString(R.string.error), responsemessage, OpenAccountValidation.this, DashBoard.class, bundle);
                    }
                } else {
                    prog.dismiss();
                    ResponseDialogs.failToActivity(getString(R.string.error), responsemessage, OpenAccountValidation.this, DashBoard.class, bundle);
                }
            }

            @Override
            public void onFailure(Call<BankAccountResponse> call, Throwable t) {
                // Log error here since request failed
                prog.dismiss();
                ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_server), OpenAccountValidation.this);
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));

            }
        });
    }

    private void fetchBeneficiaryDetails(String accountNumber, String username) {
        prog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText(getString(R.string.label_pleasewait)).setContentText("Loading...");
        prog.show();
        String params = "fetchAccountInfo/" + accountNumber + "/" + username;
        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(params);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                try {
                    JSONObject obj = new JSONObject(response.body());
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();
                    if (responsecode.equals("00")) {

                        refID = obj.optString("refid");
                        customerID = obj.optString("custid");//232/5427114/003596147/CEVA
                        Log.debug("ubnrefId:" + refID);
                        String title = null;
                        if (obj.optString("gender").substring(0, 1).equals("M")) {
                            title = "MR";
                        } else {
                            title = "Mrs";
                        }
                        //COLLINSTOCHUKWUOKPALAUGO/20MAY1978/SA_001/08036010740/CEVA/M/232/5427114/003596147/CEVA
                        accountOpen(obj.optString("accountName"),
                                obj.optString("dateOfBirth").replace("-", ""),
                                productCodes,
                                obj.optString("custPhone"),
                                session.getUserName(),
                                obj.optString("gender").substring(0, 1),
                                "232",
                                "5427114",
                                customerID,
                                session.getUserName(),
                                session.getUserName(),
                                refID,
                                title,
                                token.getText().toString());
                        // @Path("/extaccountopen/{accountName}/{dateOfBirth}/{productCode}/{custPhone}/{initiatorID}/{gender}/{branchCode}/{rmCode}/{custid}/{verifierID}/{username}/{refid}/{title}/{pin}")
                        //TO-DO
                    } else {
                        prog.dismiss();
                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, OpenAccountValidation.this);
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
                prog.dismiss();
                ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_server), OpenAccountValidation.this);
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

}
