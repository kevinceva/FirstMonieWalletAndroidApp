package com.ceva.ubmobile.core.signon.newtobank;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.LandingPage;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.BankProductModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductSelection extends Fragment {
    @BindView(R.id.products)
    RadioGroup products;
    @BindView(R.id.btnProceed)
    Button btnProceed;
    List<String> productNames = new ArrayList<>();
    List<BankProductModel> bankProductModelList = new ArrayList<>();

    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.title)
    TextView title;

    String titleSt;
    String numberSt;
    UBNSession session;
    int selected = 0;


    public ProductSelection() {
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
        final View rootView = inflater.inflate(R.layout.ntb_fragment_product_selection, container, false);
        ButterKnife.bind(this, rootView);
        try {
            bankProductModelList.clear();
            title.setText(titleSt);
            number.setText(numberSt);
            if (bankProductModelList.size() == 0) {
                if (NetworkUtils.isConnected(getContext())) {
                    getProducts();
                } else {
                    ((NewToBank) getActivity()).noInternetDialog();
                }
            }
            products.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                                                        RadioButton selectedRadio = rootView.findViewById(i);

                                                        try {
                                                            for (int k = 0; k < bankProductModelList.size(); k++) {
                                                                if (selectedRadio.getText().toString().equals(bankProductModelList.get(k).getProductType())) {
                                                                    selected = k;
                                                                }
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
            );
            btnProceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putString(BankProductModel.KEY_TITLE, "Product Details");
                        bundle.putString(BankProductModel.KEY_NUMBER, "2");
                        bundle.putString(BankProductModel.KEY_PRODUCT_CODE, bankProductModelList.get(selected).getProductCode());

                        Fragment fragment = new AccountDetails();
                        fragment.setArguments(bundle);
                        FragmentManager manager = getFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, fragment);
                        transaction.addToBackStack("product details");
                        transaction.commit();
                    } catch (Exception e) {
                        Log.Error(e);
                    }

                }
            });
        } catch (Exception e) {
            Log.Error(e);
        }
        return rootView;
    }

    private void getProducts() {
        try {

            ((NewToBank) getActivity()).showLoadingProgress();
            //@Path("/getproducts/{userid}")
            String urlparam = "getproducts/NEW";
            String url = "";

            try {
                url = SecurityLayer.beforeLogin("NEW", UUID.randomUUID().toString(), "getproductsfornew", getContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
            Call<String> call = apiService.setGenericRequestRaw(url);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        Log.debug("nipservice", response.body());
                        ((NewToBank) getActivity()).dismissProgress();

                        JSONObject obj = new JSONObject(response.body());

                        obj = SecurityLayer.decryptBeforeLogin(obj, getContext());

                        if (obj.optString(Constants.KEY_CODE).equals("00")) {

                            JSONArray array = new JSONArray(obj.optString("productlist"));
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject item = array.getJSONObject(i);
                                BankProductModel model = new BankProductModel(item.optString(BankProductModel.KEY_PRODUCT_CODE), item.optString(BankProductModel.KEY_PRODUCT_TYPE));
                                bankProductModelList.add(model);
                                productNames.add(model.getProductType());

                            }

                            if (bankProductModelList.size() > 0) {
                                for (int i = 0; i < bankProductModelList.size(); i++) {
                                    RadioButton rdbtn = new RadioButton(getContext());
                                    rdbtn.setId(i);
                                    rdbtn.setText(productNames.get(i));
                                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, 0);
                                    params.setMargins(0, 4, 0, 4);
                                    products.addView(rdbtn);

                                    View view = new View(getContext());
                                    view.setBackgroundColor(getResources().getColor(R.color.asbestos));

                                    products.addView(view);

                                    if (i == 0) {
                                        products.check(i);
                                    }
                                }
                            } else {
                                ResponseDialogs.warningDialogLovelyToActivity(getContext(), getString(R.string.error), getString(R.string.error_server), LandingPage.class);
                            }

                        } else {
                            ResponseDialogs.warningDialogLovely(getContext(), "Error", obj.optString(Constants.KEY_MSG));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        ResponseDialogs.warningDialogLovelyToActivity(getContext(), getString(R.string.error), getString(R.string.error_server), LandingPage.class);

                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Log error here since request failed
                    ((NewToBank) getActivity()).dismissProgress();
                    com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                    ResponseDialogs.warningDialogLovelyToActivity(getContext(), getString(R.string.error), getString(R.string.error_server), LandingPage.class);

                    //showToast(getString(R.string.error_500));
                    // prog.dismiss();
                    // startDashBoard();
                }
            });
        } catch (Exception e) {
            ResponseDialogs.warningDialogLovelyToActivity(getContext(), getString(R.string.error), getString(R.string.error_server), LandingPage.class);

        }
    }

}
