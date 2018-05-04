package com.ceva.ubmobile.core.omniview.accountopening;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

import org.json.JSONArray;
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
public class AccountDetails extends Fragment {
    @BindView(R.id.btnProceed)
    Button btnProceed;
    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.productName)
    TextView productName;
    @BindView(R.id.productDescription)
    TextView productDescription;
    @BindView(R.id.benefitsList)
    ListView benefitsList;
    @BindView(R.id.rates)
    TextView rates;

    String titleSt;
    String numberSt;
    String description;
    String product;
    String productCode;
    UBNSession session;


    public AccountDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titleSt = getArguments().getString(BankProductModel.KEY_TITLE);
            numberSt = getArguments().getString(BankProductModel.KEY_NUMBER);
            productCode = getArguments().getString(BankProductModel.KEY_PRODUCT_CODE);
        }
        session = new UBNSession(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.ntb_fragment_account_details, container, false);
        ButterKnife.bind(this, rootView);
        title.setText(titleSt);
        number.setText(numberSt);

        //if(product == null){
        if (NetworkUtils.isConnected(getContext())) {
            getProductDetails(productCode);
        } else {
            ((ExistingAccountOpen) getActivity()).noInternetDialog();
        }
        // }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                session.setString(BankProductModel.KEY_PRODUCT_TYPE, product);
                session.setString(BankProductModel.KEY_PRODUCT_CODE, productCode);

                Bundle bundle = new Bundle();
                bundle.putString(BankProductModel.KEY_TITLE, "Customer Details");
                bundle.putString(BankProductModel.KEY_NUMBER, "3");

                Fragment fragment = new CustomerDetails();
                fragment.setArguments(bundle);
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment, fragment);
                transaction.addToBackStack("customerdetails");
                transaction.commit();
            }
        });
        return rootView;
    }

    private void getProductDetails(String prdCode) {
        ((ExistingAccountOpen) getActivity()).showLoadingProgress();
        //@Path("/getprdtypedetailsbycode/{userid}/{prdcode}")
        String urlparam = session.getUserName() + "/" + prdCode;
        String url = SecurityLayer.genURLCBC("getprdtypedetailsbycode", urlparam, getContext());

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                ((ExistingAccountOpen) getActivity()).dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        JSONObject detObj = obj.getJSONObject("PRD_DET");

                        description = detObj.optString("description")
                                + "\n\n" + Html.fromHtml("<strong>Validity of Account:</strong> ") + detObj.optString("ValidityOfAct") + "\n\n";

                        JSONArray rewards = detObj.getJSONArray("QuarterlyReward");
                        List<String> benefits = new ArrayList<>();
                        if (rewards != null) {
                            if (rewards.length() > 0)
                                for (int i = 0; i < rewards.length(); i++) {
                                    String item = rewards.optString(i);
                                    benefits.add(item);

                                }
                        }
                        JSONArray monthSavings = detObj.optJSONArray("MonSavAppIntrate");

                        String ratesString = "";
                        for (int i = 0; i < monthSavings.length(); i++) {
                            JSONObject ob = monthSavings.optJSONObject(i);
                            ratesString += "Monthly Savings:" + ob.optString("Monthly_Savings") + "\n";
                            ratesString += "Interest Rate:" + ob.optString("Applicable_interest_rate") + "\n";
                        }

                        rates.setText(ratesString);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                R.layout.account_benefits_row, R.id.benefit, benefits);
                        benefitsList.setAdapter(adapter);
                        product = detObj.optString("accountType");
                        productDescription.setText(description);
                        productName.setText(product);
                        session.setString("initialdeposit", NumberUtilities.getNumbersOnly(detObj.optString("initalAmount")));

                        if (detObj.optString("is_startdate_req").equals("Y")) {
                            session.setBoolean("is_startdate_req", true);
                        } else {
                            session.setBoolean("is_startdate_req", false);
                        }

                    } else {
                        ResponseDialogs.warningDialogLovely(getContext(), "Error", obj.optString(Constants.KEY_MSG));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                ((ExistingAccountOpen) getActivity()).dismissProgress();
                Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

}
