package com.ceva.ubmobile.core.omniview.accountopening;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.ConfirmAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.BankProductModel;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.NumberUtilities;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountConfirmation extends Fragment {
    @BindView(R.id.btnProceed)
    Button btnProceed;
    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.title)
    TextView title;

    String titleSt;
    String numberSt;
    UBNSession session;

    @BindView(R.id.transactionProgressBar)
    ProgressBar progressBar;

    @BindView(R.id.transaction_success_fail)
    ImageView statusImage;

    @BindView(R.id.transactionMessage)
    TextView transactionMessage;

    @BindView(R.id.confirm_list)
    RecyclerView confirmList;


    @BindView(R.id.loadingLayout)
    LinearLayout loadingLayout;

    List<ConfirmModel> confirmModelList = new ArrayList<>();
    ConfirmAdapter confirmAdapter;
    @BindView(R.id.tran_pin)
    EditText tranpin;

    public AccountConfirmation() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titleSt = getArguments().getString(BankProductModel.KEY_TITLE);
            numberSt = getArguments().getString(BankProductModel.KEY_NUMBER);
        }
        session = new UBNSession(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.existing_fragment_account_confirmation, container, false);
        ButterKnife.bind(this, rootView);

        title.setText(titleSt);
        number.setText(numberSt);
        loadingLayout.setVisibility(View.GONE);
        confirmAdapter = new ConfirmAdapter(confirmModelList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        confirmList.setLayoutManager(mLayoutManager);
        confirmList.setItemAnimator(new DefaultItemAnimator());
        confirmList.setAdapter(confirmAdapter);

        ConfirmModel item = new ConfirmModel("Account Name ", session.getString(BankProductModel.KEY_FNAME));
        confirmModelList.add(item);
        // item = new ConfirmModel("Phone Number ", session.getString(BankProductModel.KEY_PHONE));
        //confirmModelList.add(item);

        item = new ConfirmModel("Account Type ", session.getString(BankProductModel.KEY_PRODUCT_TYPE));
        confirmModelList.add(item);

        item = new ConfirmModel("Initial Deposit ", NumberUtilities.getWithDecimalPlusCurrency(Double.parseDouble(session.getString("initialdeposit"))));
        confirmModelList.add(item);
        if (session.getBoolean("is_startdate_req")) {
            item = new ConfirmModel("Monthly Debit Date ", session.getString("monthlyDebit"));
            confirmModelList.add(item);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(tranpin.getText().toString())) {
                    ((ExistingAccountOpen) getActivity()).warningDialog("Please enter your transaction Pin");
                } else {

                    if (NetworkUtils.isConnected(getContext())) {

                        accountOpen(session.getString(BankProductModel.KEY_FNAME),
                                session.getString(BankProductModel.KEY_DOB),
                                session.getString(BankProductModel.KEY_PRODUCT_CODE),
                                session.getString(BankProductModel.KEY_PHONE),
                                session.getUserName(),
                                session.getString(BankProductModel.KEY_GENDER).substring(0, 1),
                                session.getString("initialdeposit"),
                                //Constants.KIFUNGUO_CHA_WENYEPESA,
                                "NA",
                                session.getString(BankProductModel.KEY_BVN),
                                session.getUserName(),
                                session.getUserName(),
                                session.getString("refid"),
                                "NA",
                                tranpin.getText().toString(),
                                session.getString("accountNumber"), session.getString("monthlyDebit"));

                    } else {
                        ((ExistingAccountOpen) getActivity()).noInternetDialog();
                    }
                }

            }
        });

        return rootView;
    }

    public void accountOpen(String accountName, String dateOfBirth, String productCode, String custPhone, String initiatorID, String gender, String amount, String rmCode, String custid, String verifierID, String username, String refid, String title, String pin, String fromaccount, String debitDate) {
        // @Path("/extaccountopen/{accountName}/{dateOfBirth}/{productCode}/{custPhone}/{initiatorID}/{gender}/{amount}/{rmCode}/{custid}/{verifierID}/{username}/{refid}/{title}/{pin}/{fromAcccountNumber}/{startdatefordebit}
        ((ExistingAccountOpen) getActivity()).showLoadingProgress();
        String params = accountName + "/" + dateOfBirth + "/" + productCode + "/" + custPhone + "/" + initiatorID + "/" + gender + "/" + amount + "/" + rmCode + "/" + custid + "/" + verifierID + "/" + username + "/" + refid + "/" + title + "/" + pin + "/" + fromaccount + "/" + debitDate;
        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);

        String url = SecurityLayer.genURLCBC("extaccountopen", params, getContext());
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                ((ExistingAccountOpen) getActivity()).dismissProgress();
                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);

                    if (responsecode.equals("00")) {
                        String generated = obj.optString("accountNO");

                        ResponseDialogs.successToActivity("Success!", "New Account has been opened. Your account number is " + generated, getContext(), DashBoard.class, new Bundle());

                    } else {
                        ResponseDialogs.warningStatic("Error", responsemessage + "\n" + obj.optString("responseMessage"), getContext());
                    }

                } catch (Exception e) {
                    Log.Error(e);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                ((ExistingAccountOpen) getActivity()).dismissProgress();
                ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_server), getContext());
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));

            }
        });
    }

}
