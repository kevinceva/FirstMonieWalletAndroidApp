package com.ceva.ubmobile.core.ui.transfers;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.models.ConfirmPageModel;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.NumberTextWatcher;
import com.ceva.ubmobile.utils.NumberUtilities;
import com.ceva.ubmobile.utils.StringUtilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OwnAccountsActivity extends BaseActivity {
    @BindView(R.id.btncancel)
    Button btnCancel;
    @BindView(R.id.btnContinue)
    Button btnContinue;
    @BindView(R.id.tx_accounts_from)
    Spinner spinAccountFrom;
    @BindView(R.id.tx_accounts_to)
    Spinner spinAccountsTo;
    @BindView(R.id.txamount)
    EditText txAmount;
    @BindView(R.id.tx_reference)
    EditText editNarration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_accounts);
        setToolbarTitle("My Accounts");
        ButterKnife.bind(this);
        txAmount.addTextChangedListener(new NumberTextWatcher(txAmount));
        List<String> accountList = session.getAccountNumbersNoDOM();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, accountList);
        spinAccountFrom.setAdapter(adapter);
        spinAccountsTo.setAdapter(adapter);

    }

    @OnClick(R.id.btnContinue)
    protected void onBtnContinueClick() {

        String amount = txAmount.getText().toString();
        String narration = editNarration.getText().toString();

        if (TextUtils.isEmpty(amount)) {
            txAmount.setError(getString(R.string.error_no_amount));
        } else {
            if (TextUtils.isEmpty(narration)) {
                narration = "NA";
                if (spinAccountsTo.getSelectedItemPosition() == spinAccountFrom.getSelectedItemPosition()) {
                    warningDialog(getString(R.string.error_same_account));
                } else {

                    Intent intent = new Intent(this, TransferConfirm.class);
                    intent.putExtra("confirm", setConfirm(spinAccountFrom.getSelectedItem().toString(),
                            spinAccountsTo.getSelectedItem().toString(),
                            amount,
                            session,
                            narration));
                    startActivity(intent);
                }
            }
        }
    }

    private ConfirmPageModel setConfirm(String acountFrom, String accountTo, String amount, UBNSession session, String narration) {
        //        //@Path("/fundtransfer/{fromAcccountNumber}/{toAcccountNumber}/{amount}/{branchCode}/{username}/
        // {type}/{description}/{pin}/{authtype}/{transdesc}/{authvalue}

        double amt = Double.parseDouble(NumberUtilities.getNumbersOnly(amount));
        Intent intent = new Intent(getApplicationContext(), OwnAccountsActivity.class);

        List<String> params = new ArrayList<>();
        params.add(NumberUtilities.getAccountNumberFromSpinner(acountFrom));
        params.add(NumberUtilities.getAccountNumberFromSpinner(accountTo));
        params.add(NumberUtilities.getWithDecimal(amt));
        params.add(Constants.KEY_HARDCODE_BRANCH);
        params.add(session.getUserName());
        params.add("UB");
        params.add(StringUtilities.getEncodedString(narration));
        params.add(ConfirmPageModel.KEY_PIN);
        params.add(ConfirmPageModel.KEY_AUTH);
        params.add(StringUtilities.getEncodedString(narration));
        params.add(ConfirmPageModel.KEY_AUTH_VAL);

        List<ConfirmModel> confirmModelList = new ArrayList<>();
        confirmModelList.add(new ConfirmModel("From Account", spinAccountFrom.getSelectedItem().toString()));
        confirmModelList.add(new ConfirmModel("To Account", spinAccountsTo.getSelectedItem().toString()));
        confirmModelList.add(new ConfirmModel("Amount", Constants.KEY_NAIRA + amount));
        confirmModelList.add(new ConfirmModel("Payment Reason", narration));

        Log.debug("confirm list", confirmModelList.size() + "");

        return new ConfirmPageModel(params,
                Constants.KEY_FUNDTRANSFER_ENDPOINT,
                intent, confirmModelList, Constants.KEY_FUNDTRANS_WITHIN, "U", "7|8|10", "Send Money");

    }

}
