package com.ceva.ubmobile.core.omniview.accountopening;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.BankProductModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.NumberUtilities;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerDetails extends Fragment {
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
    @BindView(R.id.account)
    Spinner account;

    //@BindView(R.id.tran_pin)
    // EditText tranpin;

    @BindView(R.id.initialDeposit)
    EditText initialDeposit;
    @BindView(R.id.monthlyDebit)
    EditText monthlyDebit;
    @BindView(R.id.btnSub)
    Button btnSub;
    @BindView(R.id.btnAdd)
    Button btnAdd;
    @BindView(R.id.dateLayout)
    LinearLayout dateLayout;

    String customerID, refID;
    int current = 1;


    public CustomerDetails() {
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
        View rootView = inflater.inflate(R.layout.existing_fragment_customer_details, container, false);
        ButterKnife.bind(this, rootView);
        title.setText(titleSt);
        number.setText(numberSt);

        List<String> accountList = session.getAccountNumbersNoDOM();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_row, accountList);
        account.setAdapter(adapter);
        initialDeposit.setText(session.getString("initialdeposit"));
        initialDeposit.setEnabled(false);
        monthlyDebit.setEnabled(false);
        monthlyDebit.setText(current + "");

        if (session.getBoolean("is_startdate_req")) {
            dateLayout.setVisibility(View.VISIBLE);
        } else {
            dateLayout.setVisibility(View.GONE);
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (current != 31) {
                    current++;
                } else {
                    current = 1;
                }
                monthlyDebit.setText(current + "");
            }
        });

        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current == 1) {
                    current = 31;
                } else {
                    current--;
                }
                monthlyDebit.setText(current + "");
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (NetworkUtils.isConnected(getContext())) {
                    // session.setString("tranpin", tranpin.getText().toString());
                    String[] tosplit = account.getSelectedItem().toString().split("-");
                    int max = tosplit.length;
                    max = max - 1;

                    String acc = tosplit[max].trim();

                    fetchAccountDetails(NumberUtilities.getNumbersOnly(acc), session.getUserName());
                } else {
                    ((ExistingAccountOpen) getActivity()).noInternetDialog();
                }
                // }
            }
        });
        return rootView;
    }


    private void fetchAccountDetails(final String accountNumber, String username) {
        ((ExistingAccountOpen) getActivity()).showLoadingProgress();
        String params = accountNumber + "/" + username;

        String url = SecurityLayer.genURLCBC("fetchAccountInfo", params, getContext());
        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();
                    ((ExistingAccountOpen) getActivity()).dismissProgress();
                    if (responsecode.equals("00")) {

                        refID = obj.optString("refid");
                        customerID = obj.optString("custid");//232/5427114/003596147/CEVA

                        session.setString("accountNumber", accountNumber);
                        session.setString(BankProductModel.KEY_FNAME, obj.optString("accountName"));
                        // session.setString(BankProductModel.KEY_MIDNAME, obj.optString(BankProductModel.KEY_MIDNAME));
                        session.setString(BankProductModel.KEY_GENDER, obj.optString("gender"));
                        session.setString(BankProductModel.KEY_DOB, obj.optString("dateOfBirth"));
                        session.setString(BankProductModel.KEY_PHONE, obj.optString("custPhone"));
                        session.setString("refid", refID);
                        session.setString(BankProductModel.KEY_BVN, customerID);
                        if (session.getBoolean("is_startdate_req")) {
                            session.setString("monthlyDebit", monthlyDebit.getText().toString());
                        } else {
                            session.setString("monthlyDebit", "0");
                        }

                        Log.debug("ubnrefId:" + refID);

                        Bundle bundle = new Bundle();
                        bundle.putString(BankProductModel.KEY_TITLE, "Confirm Details");
                        bundle.putString(BankProductModel.KEY_NUMBER, "4");

                        Fragment fragment = new AccountConfirmation();
                        fragment.setArguments(bundle);
                        FragmentManager manager = getFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, fragment);
                        transaction.addToBackStack("confirmdetails");
                        transaction.commit();
                    } else {

                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, getContext());
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
                ((ExistingAccountOpen) getActivity()).dismissProgress();
                ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_server), getContext());
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

}
