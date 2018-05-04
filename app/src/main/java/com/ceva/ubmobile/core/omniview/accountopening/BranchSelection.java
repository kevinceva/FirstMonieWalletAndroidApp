package com.ceva.ubmobile.core.omniview.accountopening;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.SimpleListAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.BankProductModel;
import com.ceva.ubmobile.models.Branches;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.core.ui.widgets.CustomSearchableSpinner;

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
public class BranchSelection extends Fragment {
    List<Branches> branchesList = new ArrayList<>();
    List<String> branchNames = new ArrayList<>();
    ArrayAdapter<String> branch_adapter;
    @BindView(R.id.bank_branches)
    CustomSearchableSpinner bank_branches;
    @BindView(R.id.branchView)
    RecyclerView branchView;
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
    SimpleListAdapter mAdapter;
    @BindView(R.id.searchbtn)
    ImageButton searchbtn;

    int jk = 0;

    public BranchSelection() {
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
        View rootView = inflater.inflate(R.layout.ntb_fragment_branch_selection, container, false);
        ButterKnife.bind(this, rootView);
        title.setText(titleSt);
        number.setText(numberSt);
        branch_adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_row, branchNames);
        mAdapter = new SimpleListAdapter(branchNames, getContext());

        if (branchNames.size() == 0) {
            if (NetworkUtils.isConnected(getContext())) {
                getBranches();
            } else {
                ((ExistingAccountOpen) getActivity()).noInternetDialog();
            }
        } else {
            branchesList = session.getBranches();
            for (int j = 0; j < branchesList.size(); j++) {
                branchNames.add(branchesList.get(j).getbranchName());
            }

        }
        bank_branches.setAdapter(branch_adapter);

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bank_branches.performClick();
            }
        });

        bank_branches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (jk > 0) {

                }
                jk++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        branchView.setLayoutManager(mLayoutManager);
        branchView.setItemAnimator(new DefaultItemAnimator());
        branchView.setAdapter(mAdapter);
        branchView.setVisibility(View.GONE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = bank_branches.getSelectedItemPosition();
                Branches branch = branchesList.get(pos);
                session.setString(BankProductModel.KEY_BRANCHNAME, branch.getbranchName());
                session.setString(BankProductModel.KEY_BRANCH_CODE, branch.getbranchCode());

                Bundle bundle = new Bundle();
                bundle.putString(BankProductModel.KEY_TITLE, "Confirm Details");
                bundle.putString(BankProductModel.KEY_NUMBER, "5");

                Fragment fragment = new AccountConfirmation();
                fragment.setArguments(bundle);
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment, fragment);
                transaction.addToBackStack("Confirm Details");
                transaction.commit();
            }
        });

        return rootView;
    }

    private void getBranches() {
        ((ExistingAccountOpen) getActivity()).showLoadingProgress();
        //@Path("/loadbanksOrbranches/{username}/{type}")
        String urlparam = session.getUserName() + "/LOAD_BRANCHES";
        String url = "";

        String endpoint = "loadbanksOrbranches";

        url = SecurityLayer.genURLCBC(endpoint, urlparam, getContext());

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

                        JSONArray array = obj.getJSONArray("branchesdata");

                        for (int k = 0; k < array.length(); k++) {
                            JSONObject branchObj = array.getJSONObject(k);
                            Branches branch = new Branches(branchObj.optString(Branches.BR_KEY_BRANCHCODE), branchObj.optString(Branches.BR_KEY_BRANCHNAME));
                            branchesList.add(branch);
                            branchNames.add(branch.getbranchName());
                        }
                        branch_adapter.notifyDataSetChanged();
                        bank_branches.setTitle("Select Branch");
                        mAdapter.notifyDataSetChanged();

                        // description = obj.optString("description");
                        //product = obj.optString("accountType");
                        //productDescription.setText(description);
                        //productName.setText(product);

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
