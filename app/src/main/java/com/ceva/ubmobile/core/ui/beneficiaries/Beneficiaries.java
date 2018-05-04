package com.ceva.ubmobile.core.ui.beneficiaries;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.BeneficiaryAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.core.ui.transfers.TransfersControllerActivity;
import com.ceva.ubmobile.models.BankAccount;
import com.ceva.ubmobile.models.Beneficiary;
import com.ceva.ubmobile.models.ShowCaseModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.ScalingUtilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Beneficiaries extends BaseActivity implements View.OnClickListener {
    UBNSession session;
    List<String> accountList;
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    ArrayAdapter<String> dataAdapter;
    int jk = 0;
    String KEY_BEN_TYPE = "UNION";
    // @BindView(R.id.searchBtn)
    //ImageButton searchBtn;
    BeneficiaryAdapter.MyAdapterListener listener;
    private List<Beneficiary> beneficiaryList = new ArrayList<>();
    private List<String> beneficiaryNames = new ArrayList<>();
    private List<Beneficiary> filteredBeneficiaryList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BeneficiaryAdapter mAdapter;
    private Context context;
    private ImageButton btnAdd;
    private String accountNumber;


    public Beneficiaries() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiaries);
        setToolbarTitle("Manage Beneficiaries");
        ButterKnife.bind(this);
        context = this;

        session = new UBNSession(context);

        //searchView.setVisibility(View.GONE);
        radioGroup.check(R.id.rb_union);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.rb_union) {
                    fetchBeneficiaries(session.getUserName(), "UNION");
                    KEY_BEN_TYPE = "UNION";
                } else if (i == R.id.rb_other) {
                    fetchBeneficiaries(session.getUserName(), "OTHER");
                    KEY_BEN_TYPE = "OTHER";
                } else {
                    fetchBeneficiaries(session.getUserName(), "INTERNATIONAL");
                    KEY_BEN_TYPE = "INTERNATIONAL";
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.beneficiary_list);

        listener = new BeneficiaryAdapter.MyAdapterListener() {

            @Override
            public void iconDeleteOnClick(View v, int position, final Beneficiary beneficiary) {
                ///Log.debug("the tag id ");
                //final Beneficiary beneficiary = beneficiaryList.get(position);
                Log.debug("beneficiaryID " + beneficiary.getBeneficiaryId());
                Log.debug("beneficiaryType " + beneficiary.getType());
                Log.debug("beneficiaryBank " + beneficiary.getBankCode());
                Log.debug("tag view", v.getTag().toString());
                final String destBankCode = beneficiary.getType();

                Log.debug("beneficiaryBankMod " + destBankCode);
                String content = "Are you sure you want to delete " + beneficiary.getName() + "?";

                Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_warning_white_48, getResources());
                new LovelyStandardDialog(context)
                        .setTopColorRes(R.color.union_red)
                        .setButtonsColorRes(R.color.midnight_blue)
                        .setIcon(icon)
                        .setTitle("Confirmation Required!")
                        .setMessage(content)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Proceed", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                deleteBeneficiary(session.getUserName(), beneficiary.getBeneficiaryId(), beneficiary.getType());
                            }
                        })
                        .show();
            }

            @Override
            public void iconTransferOnClick(View v, int position, final Beneficiary beneficiary) {
                // final Beneficiary beneficiary = beneficiaryList.get(position);
                Log.debug("beneficiaryID " + beneficiary.getBeneficiaryId());
                Log.debug("beneficiaryType " + beneficiary.getType());
                Log.debug("beneficiaryBank " + beneficiary.getBankCode());
                final String destBankCode = beneficiary.getType();

                Log.debug("beneficiaryBankMod " + destBankCode);
                String content = "Would you like to make a funds transfer to " + beneficiary.getName() + "?";

                Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_transfers_white, getResources());
                new LovelyStandardDialog(context)
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColorRes(R.color.midnight_blue)
                        .setIcon(icon)
                        .setTitle("Confirmation Required!")
                        .setMessage(content)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Proceed", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Beneficiaries.this, TransfersControllerActivity.class);
                                if (beneficiary.getType().equals("UNION")) {
                                    intent.putExtra(Constants.KEY_FRAG_POSITION, "1");
                                    intent.putExtra(Beneficiary.KEY_BEN_ID, beneficiary.getBeneficiaryId());
                                    intent.putExtra(Beneficiary.KEY_BEN_BANKCODE, beneficiary.getBankCode());
                                } else if (beneficiary.getType().equals("OTHER")) {
                                    intent.putExtra(Constants.KEY_FRAG_POSITION, "2");
                                    intent.putExtra(Beneficiary.KEY_BEN_ID, beneficiary.getBeneficiaryId());
                                    intent.putExtra(Beneficiary.KEY_BEN_BANKCODE, beneficiary.getBankCode());
                                }
                                startActivity(intent);
                                //deleteBeneficiary(session.getUserName(), beneficiary.getBeneficiaryId(),beneficiary.getType());
                            }
                        })
                        .show();
            }
        };
        mAdapter = new BeneficiaryAdapter(beneficiaryList, this, listener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        searchView.setQueryHint("Search beneficiary by name");

        dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, beneficiaryNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        //searchView.setAdapter(dataAdapter);

        String accountsListString = session.pref.getString(Constants.KEY_FULLINFO, null);
        Gson gson = new Gson();
        accountList = new ArrayList<>();
        List<BankAccount> accounts = gson.fromJson(accountsListString, new TypeToken<ArrayList<BankAccount>>() {
        }.getType());

        btnAdd = (ImageButton) findViewById(R.id.beneficiary_add);
        btnAdd.setOnClickListener(this);
        // savedBeneficiaries();
        if (NetworkUtils.isConnected(context)) {
            fetchBeneficiaries(session.getUserName(), KEY_BEN_TYPE);
        } else {
            ResponseDialogs.warningStatic(context.getString(R.string.error), context.getString(R.string.error_no_internet_connection), context);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_beneficiaries, menu);
        //MenuItem menuItem = menu.findItem(R.id.action_add);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    final View menuItem = findViewById(R.id.action_add);
                    ShowCaseModel item = new ShowCaseModel(menuItem, "Tap here to add beneficiaries", "Add Beneficiary");
                    List<ShowCaseModel> showCaseModelList = new ArrayList<>();
                    showCaseModelList.add(item);
                    presentShowcaseSequence(showCaseModelList, "add_ben");
                } catch (Exception e) {

                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent i = new Intent(Beneficiaries.this, AddBeneficiary.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void fetchBeneficiaries(final String username, String type) {
        showLoadingProgress();
        beneficiaryList.clear();
        beneficiaryNames.clear();

        String urlparam = username + "/" + type;
        String url = SecurityLayer.genURLCBC("fetchsavedbeneficiaries", urlparam, this);
        String data = null;

        final String finaltype = type;

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        // customermobile = obj.optString("customermobileno");
                        JSONArray array = new JSONArray(obj.optString("savedbeneficiaries"));
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject un = array.getJSONObject(i);

                            String bankName = "";
                            if (finaltype.equals("UNION")) {
                                bankName = "Union Bank";
                            } else {
                                bankName = un.optString("destinationBankName");
                            }
                            Beneficiary beneficiary = new Beneficiary(un.optString("accountName"), un.optString("beneficiaryAccount"), bankName, finaltype, un.optString("destinationBank"), un.optString("beneficiaryId"));
                            beneficiaryList.add(beneficiary);
                            beneficiaryNames.add(beneficiary.getName() + " (" + bankName + ")");
                            String bank;

                        }

                    }

                    mAdapter.notifyDataSetChanged();
                    recyclerView.setHasFixedSize(true);
                    mAdapter = new BeneficiaryAdapter(beneficiaryList, getApplicationContext(), listener);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    recyclerView.setAdapter(mAdapter);

                } catch (Exception e) {
                    SecurityLayer.generateToken(getApplicationContext());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    private void deleteBeneficiary(final String username, final String beneficiaryId, String type) {
        showLoadingProgress();
        String params = username + "/" + beneficiaryId + "/" + type;

        String url = SecurityLayer.genURLCBC("deletebeneficiariesbybenid", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dismissProgress();
                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();
                    String status;
                    Log.debug("full", full);
                    Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_check_circle_white, getResources());
                    int color = R.color.colorPrimary;
                    if (responsecode.equals("00")) {
                        status = context.getString(R.string.success);
                        //ResponseDialogs.warningStatic(context.getString(R.string.success), responsemessage, context);
                        //fetchBeneficiaries(username);

                    } else {
                        status = context.getString(R.string.error);
                        icon = ScalingUtilities.iconBitmap(R.drawable.ic_sadsmiley_white, getResources());
                        color = R.color.union_red;
                        //ResponseDialogs.warningStatic(context.getString(R.string.error), responsemessage, context);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }

                    new LovelyStandardDialog(context)
                            .setTopColorRes(color)
                            .setButtonsColorRes(R.color.midnight_blue)
                            .setIcon(icon)
                            .setTitle(status)
                            .setMessage(responsemessage)
                            .setPositiveButton("Close", null)
                            .setOnButtonClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    beneficiaryList.clear();
                                    mAdapter.notifyDataSetChanged();
                                    session.setBeneficiary(beneficiaryList);
                                    fetchBeneficiaries(username, KEY_BEN_TYPE);
                                }
                            })
                            .show();
                /*final SweetAlertDialog prgDialog = new SweetAlertDialog(context);
                prgDialog.setTitleText(status)
                        .setContentText(responsemessage)
                        .setConfirmText("Close")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                beneficiaryList.clear();
                                mAdapter.notifyDataSetChanged();
                                session.setBeneficiary(beneficiaryList);
                                fetchBeneficiaries(username,"UNION");

                                //((Activity)context).finish();
                            }
                        }).changeAlertType(SweetAlertDialog.NORMAL_TYPE);
                prgDialog.show();*/
                } catch (Exception e) {
                    SecurityLayer.generateToken(getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed

                Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                dismissProgress();
                // startDashBoard();
            }
        });
    }

    private void savedBeneficiaries() {
        List<Beneficiary> benList = session.getBeneficiaries();
//        Log.debug("saved benename:" + benList.get(0).getName());
        beneficiaryList.clear();

        for (int j = 0; j < benList.size(); j++) {

            Beneficiary am = new Beneficiary(benList.get(j).getName(), benList.get(j).getAccountNumber(), benList.get(j).getBank(), benList.get(j).getType(), benList.get(j).getBankCode(), benList.get(j).getBeneficiaryId());
            beneficiaryList.add(am);
        }
        /*Beneficiary am = new Beneficiary("Toochi Okpalaugo", "0023475201", "UBA", "");
        beneficiaryList.add(am);
        am = new Beneficiary("Yusuf Isedu", "0053475401", "FCMB", "");
        beneficiaryList.add(am);
        am = new Beneficiary("Anthony Tonwapiri", "+2349076069591", "Airtel", "");
        beneficiaryList.add(am);
        am = new Beneficiary("Folorunsho Orimoloye", "414228428642813", "Umpqua Bank", "");
        beneficiaryList.add(am);*/
        Collections.sort(beneficiaryList, new Comparator<Beneficiary>() {
            @Override
            public int compare(Beneficiary lhs, Beneficiary rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.beneficiary_add) {
            Intent intent = new Intent(context, AddBeneficiary.class);
            startActivity(intent);

        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashBoard.class));
    }
}
