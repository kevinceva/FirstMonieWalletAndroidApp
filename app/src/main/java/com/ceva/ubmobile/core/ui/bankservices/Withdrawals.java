package com.ceva.ubmobile.core.ui.bankservices;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.NumberUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Withdrawals extends BaseActivity {
    @BindView(R.id.accountNumber)
    Spinner accountNumber;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id.phoneNumber)
    EditText phoneNumber;
    @BindView(R.id.checkBox)
    CheckBox isSelf;
    @BindView(R.id.buttonGenerate)
    Button buttonGenerate;
    UBNSession session;
    boolean isReady = false;

    @BindView(R.id.textTotal)
    TextView textTotal;

    @BindView(R.id.btnSub)
    TextView btnSub;
    @BindView(R.id.btnAdd)
    TextView btnAdd;

    int standard = 1000;
    int minimum = 1000;
    int qty = 1;
    private boolean isWallet = false;
    private String REQUEST_TYPE = "SELF";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawals);
        ButterKnife.bind(this);
        session = new UBNSession(this);
        setToolbarTitle(getString(R.string.cardless_withdrawals));
        initializeconfirm();

        hideTransactionPin();

        List<String> accountList = session.getAccountNumbersNoDOM();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, accountList);
        accountNumber.setAdapter(adapter);
        isSelf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    phoneNumber.setText(session.getPhoneNumber());
                    REQUEST_TYPE = "SELF";
                } else {
                    phoneNumber.setText("");
                    REQUEST_TYPE = "THIRDPARTY";
                }
            }
        });
        amount.setText("1");
        String txt = "Total: " + NumberUtilities.getWithDecimalPlusCurrency(1000d);

        textTotal.setText(txt);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amt = 0;
                int max = 20;
                qty++;
                try {
                    amount.setText(String.valueOf(qty));
                    if (qty <= max) {
                        amt = Integer.parseInt(amount.getText().toString()) * standard;
                    } else {
                        showToast("Maximum amount exceeded");
                        amount.setText(String.valueOf(20));
                    }
                } catch (Exception e) {
                    Log.Error(e);
                }

                String txt = "Total: " + NumberUtilities.getWithDecimalPlusCurrency(amt);

                textTotal.setText(txt);
            }
        });
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amt = 0;
                int min = 0;
                try {
                    if (Integer.parseInt(amount.getText().toString()) > min) {
                        qty--;
                        amount.setText(String.valueOf(qty));
                        amt = Integer.parseInt(amount.getText().toString()) * standard;
                    } else {
                        amount.setText(String.valueOf(0));
                    }
                } catch (Exception e) {
                    Log.Error(e);
                }

                String txt = "Total: " + NumberUtilities.getWithDecimalPlusCurrency(amt);

                textTotal.setText(txt);
            }
        });
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int amt = 0;
                try {
                    if (Integer.parseInt(charSequence.toString()) <= 20) {
                        amt = Integer.parseInt(charSequence.toString()) * standard;
                    } else {
                        showToast("Maximum amount exceeded");
                        amount.setText(String.valueOf(20));
                    }
                } catch (Exception e) {
                    Log.Error(e);
                }

                String txt = "Total: " + NumberUtilities.getWithDecimalPlusCurrency(amt);

                textTotal.setText(txt);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        buttonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isReady) {

                    if (transaction_pin_base.getText().toString().isEmpty()) {
                        warningDialog("Please enter your transaction pin");
                    } else {
                        if (NetworkUtils.isConnected(Withdrawals.this)) {
                            isWallet = accountNumber.getSelectedItem().toString().toLowerCase().contains(Constants.KEY_WALLET);

                            String[] accs = accountNumber.getSelectedItem().toString().split("-");
                            String acc = accs[accs.length - 1].trim();
                            int amt = Integer.parseInt(amount.getText().toString()) * standard;
                            buttonGenerate.setVisibility(View.GONE);
                            generateToken(session.getUserName(), phoneNumber.getText().toString(), String.valueOf(amt), transaction_pin_base.getText().toString(), acc);
                            hideTransactionPin();

                        } else {
                            noInternetDialog();
                        }
                    }
                } else {
                    if (amount.getText().toString().isEmpty()) {
                        warningDialog("Please enter amount");
                    } else {
                        if (phoneNumber.getText().toString().isEmpty()) {
                            warningDialog("Please enter phone number of recipient");
                        } else {
                            ConfirmModel item = new ConfirmModel("Debit Account", accountNumber.getSelectedItem().toString());
                            confirmItems.add(item);
                            item = new ConfirmModel("Amount", NumberUtilities.getWithDecimalPlusCurrency(Double.parseDouble(amount.getText().toString()) * standard));
                            confirmItems.add(item);
                            item = new ConfirmModel("Phone Number", NumberUtilities.getNumbersOnlyNoDecimal(phoneNumber.getText().toString()));
                            confirmItems.add(item);

                            showFancyConfirm();
                            showTransactionPin();
                            isReady = true;

                        }
                    }
                }
            }
        });

    }

    private void generateToken(final String username, String mobile, final String amount, final String pin, final String accountno) {
        //@Path("/generatetoken/{username}/{subscriberId}/{amount}/{accountNo}/{pin}")
        //@Path("/generatewallettoken/{username}/{subscriberId}/{bensubscriberId}/{amount}/{pin}/{typeofrequest}")
        btnSub.setEnabled(false);
        showConfirmProgress();
        mobile = NumberUtilities.getNumbersOnlyNoDecimal(mobile);

        String params = username + "/" + mobile + "/" + amount + "/" + accountno + "/" + pin;
        String endpoint = "generatetoken";
        if (isWallet) {
            params = username + "/" + session.getPhoneNumber() + "/" + mobile + "/" + amount + "/" + pin + "/" + REQUEST_TYPE;
            endpoint = "generatewallettoken";
        }
        Log.debug("params: ", params);

        String url = SecurityLayer.genURLCBC(endpoint, params, getApplicationContext());

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
                        //ResponseDialogs.successToActivity(getString(R.string.success), responsemessage, PayDueBills.this, DashBoard.class,new Bundle());

                    } else if (responsecode.equals("7007")) {
                        ResponseDialogs.limitError(Withdrawals.this, "Info", responsemessage, DashBoard.class);
                        showTransactionComplete(1, responsemessage);
                    } else {
                        showTransactionComplete(1, responsemessage);
                        //ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, PayDueBills.this);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    showTransactionComplete(1, getString(R.string.error_server));
                }
                buttonClose_base.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                //dismissProgress();
                showTransactionComplete(1, getString(R.string.error_server));
                buttonClose_base.setVisibility(View.VISIBLE);
                com.ceva.ubmobile.core.ui.Log.debug("billerfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }

        });

    }
}




