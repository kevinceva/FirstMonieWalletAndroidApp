package com.ceva.ubmobile.core.ui.transfers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ceva.ubmobile.BuildConfig;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.ConfirmAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.core.ui.Transfers;
import com.ceva.ubmobile.core.ui.beneficiaries.AddBeneficiary;
import com.ceva.ubmobile.models.Banks;
import com.ceva.ubmobile.models.Beneficiary;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.network.ApiClient;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;
import com.ceva.ubmobile.utils.NumberUtilities;
import com.ceva.ubmobile.core.ui.widgets.CustomSearchableSpinner;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OtherBanks.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OtherBanks#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherBanks extends Fragment {
    public static final String FRAG_TITLE = "Other Banks";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Context context;
    RadioButton savedBen, newBen;
    RadioGroup rGroup;
    List<String> benListString = new ArrayList<>();
    List<Beneficiary> benList = new ArrayList<>();
    UBNSession session;
    Spinner accountFrom;
    String chosenBenName = "";
    String chosenBenAccNo = "";
    String chosenBank = "", chosenBankName = "";
    CustomSearchableSpinner spinner_banks;
    List<Banks> banksList = new ArrayList<>();
    List<String> bankNames = new ArrayList<>();
    String[] bankCodes = {};
    EditText fromDate, toDate;
    Switch schedule_switch;
    Spinner frequencySpinner;
    ArrayAdapter<String> adapter_ben;
    boolean isBenChecked = false;
    CheckBox saveBen;
    CustomSearchableSpinner accountTo;
    String tranBenId = null;
    String tranBankCode = null;
    int selpos = 0;
    RecyclerView confirmationRecyler;
    List<ConfirmModel> confirmItems = new ArrayList<>();
    ConfirmAdapter confirmAdapter;
    EditText tran_pin;
    LinearLayout loadingLayout;
    @BindView(R.id.token_auth)
    LinearLayout token_auth;
    @BindView(R.id.auth_mode)
    RadioGroup auth_mode;
    @BindView(R.id.auth_code)
    EditText auth_code;
    boolean isTokenRequired = false;
    View rootView;
    String AUTH_MODE_TYPE = Constants.KEY_AUTH_TOKEN;
    int track = 0;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private boolean isReady = false;
    private LinearLayout form_fields, schedule_fields;
    private TextView debit_account_txt, beneficiary_name_txt, beneficiary_account_txt, beneficiary_amount_txt, narration_txt, beneficiary_bank_txt;
    private String accountfrm, account_to, amountString, narrationString;
    private LinearLayout saved_fields, new_fields;
    private boolean isSaved = true;
    private int chosenDebit = 0;
    private int chosenBen = 0;
    private boolean isScheduled = false;
    private RelativeLayout confirm_fields;

    public OtherBanks() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OtherBanks.
     */
    // TODO: Rename and change types and number of parameters
    public static OtherBanks newInstance(String param1, String param2) {
        OtherBanks fragment = new OtherBanks();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            tranBenId = getArguments().getString(Beneficiary.KEY_BEN_ID);
            tranBankCode = getArguments().getString(Beneficiary.KEY_BEN_BANKCODE);
            Log.debug("beneficiaryID on within  ", tranBenId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_other_banks, container, false);
        ButterKnife.bind(this, rootView);
        saveBen = rootView.findViewById(R.id.save_ben_chk);
        saveBen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                isBenChecked = b;

            }
        });
        saveBen.setVisibility(View.GONE);
        context = getActivity();
        session = new UBNSession(context);
        //if(track > 0) {

        //}
        // track++;
        int selbank = 0;
        try {
            banksList = session.getBanks();

            int m = banksList.size();
            bankCodes = new String[m];
            selbank = 0;
            Log.debug("bankssaved", "" + m);
            for (int j = 0; j < m; j++) {
                bankNames.add(banksList.get(j).getBankName());
                bankCodes[j] = banksList.get(j).getBankCode();
                if (tranBankCode != null) {
                    if (tranBankCode.equals(bankCodes[j])) {
                        selbank = j;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        spinner_banks = rootView.findViewById(R.id.beneficiary_bank);
        spinner_banks.setPositiveButton(getString(R.string.label_ok));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, bankNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_banks.setAdapter(dataAdapter);
        spinner_banks.setSelection(selbank);

        form_fields = rootView.findViewById(R.id.form_fields);
        confirm_fields = rootView.findViewById(R.id.confirm_fields);
        loadingLayout = rootView.findViewById(R.id.loadingLayout);
        token_auth.setVisibility(View.GONE);
        auth_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.rb_sms) {
                    AUTH_MODE_TYPE = Constants.KEY_AUTH_SMS;
                    if (NetworkUtils.isConnected(getContext())) {
                        ((TransfersControllerActivity) getActivity()).generateSMSToken(session.getUserName(), session.getPhoneNumber(), Constants.KEY_FUNDTRANS_WITHIN);
                    } else {
                        ((TransfersControllerActivity) getActivity()).noInternetDialog();
                    }
                } else {
                    AUTH_MODE_TYPE = Constants.KEY_AUTH_TOKEN;
                }
            }
        });

        confirmationRecyler = rootView.findViewById(R.id.confirm_list);
        confirmAdapter = new ConfirmAdapter(confirmItems, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        confirmationRecyler.setLayoutManager(mLayoutManager);
        confirmationRecyler.setItemAnimator(new DefaultItemAnimator());
        confirmationRecyler.setAdapter(confirmAdapter);

        saved_fields = rootView.findViewById(R.id.saved_fields);
        new_fields = rootView.findViewById(R.id.new_fields);
        schedule_fields = rootView.findViewById(R.id.schedule_fields);
        schedule_fields.setVisibility(View.GONE);

        fromDate = rootView.findViewById(R.id.fromDate);
        toDate = rootView.findViewById(R.id.toDate);
        tran_pin = rootView.findViewById(R.id.tran_pin);
        schedule_switch = rootView.findViewById(R.id.schedule_switch);
        frequencySpinner = rootView.findViewById(R.id.frequency);

        getDateIntoEditText(fromDate);
        getDateIntoEditText(toDate);

        schedule_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isScheduled = true;
                    schedule_fields.setVisibility(View.VISIBLE);
                } else {
                    isScheduled = false;
                    schedule_fields.setVisibility(View.GONE);
                }
            }
        });

        rGroup = rootView.findViewById(R.id.radioGroup);
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    if (checkedId == R.id.rd_savedBen) {

                        showSaved();
                        saveBen.setVisibility(View.GONE);

                    } else {

                        showNewBen();
                    }
                }
            }
        });
        savedBen = rootView.findViewById(R.id.rd_savedBen);
        newBen = rootView.findViewById(R.id.rd_newBen);

        final List<String> accountList = session.getAccountNumbersNoDOM();

        accountFrom = rootView.findViewById(R.id.tx_accounts_from);
        accountTo = rootView.findViewById(R.id.tx_accounts_to);
        accountTo.setPrompt("Search beneficiary");
        accountTo.setTitle("Beneficiaries");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_row, accountList);
        adapter_ben = new ArrayAdapter<String>(getContext(), R.layout.spinner_row, benListString);
        accountFrom.setAdapter(adapter);
        accountTo.setAdapter(adapter_ben);

        final EditText amountTxt = rootView.findViewById(R.id.txamount);
        final EditText accountTxt = rootView.findViewById(R.id.txaccountNo);
        final EditText narrationTxt = rootView.findViewById(R.id.tx_reference);
        Button btncontinue = rootView.findViewById(R.id.btnContinue);
        btncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //Toast.makeText(getContext(),"clicked",Toast.LENGTH_LONG).show();
                    String[] fromsplit = accountList.get(accountFrom.getSelectedItemPosition()).split("-");
                    int max = fromsplit.length;
                    max = max - 1;

                    accountfrm = fromsplit[max].trim();
                    chosenDebit = accountFrom.getSelectedItemPosition();
                    if (accountfrm != null) {
                        if (isSaved) {
                            chosenBen = accountTo.getSelectedItemPosition();
                            String[] tosplit = accountTo.getSelectedItem().toString().split("-");
                            max = tosplit.length;
                            max = max - 1;
                            account_to = tosplit[max].trim();
                            chosenBank = benList.get(chosenBen).getBankCode();
                            chosenBankName = benList.get(chosenBen).getBank();

                        } else {
                            chosenBank = bankCodes[spinner_banks.getSelectedItemPosition()];
                            chosenBankName = spinner_banks.getSelectedItem().toString();
                            account_to = accountTxt.getText().toString();
                        }
                        amountString = amountTxt.getText().toString();
                        narrationString = narrationTxt.getText().toString();
                        if (narrationString.length() == 0) {
                            narrationString = "NA";
                        }
                        try {
                            narrationString = URLEncoder.encode(narrationString, "UTF-8");
                        } catch (Exception e) {

                        }
                        if (isReady) {
                            if (NetworkUtils.isConnected(getActivity())) {
                                if (isScheduled) {
                                    String startDate = fromDate.getText().toString();
                                    String endDate = toDate.getText().toString();
                                    String frequency = frequencySpinner.getSelectedItem().toString();
                                    if (Utility.isNotNull(startDate)) {
                                        if (Utility.isNotNull(endDate)) {
                                            startDate = startDate.replace("/", "");
                                            endDate = endDate.replace("/", "");
                                            invokeScheduledTransfer(session.getUserName(), startDate, endDate, amountString, chosenBenName, account_to, chosenBank, frequency, accountfrm, session.getAccountName(), "OTHER");
                                        } else {
                                            ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.prompt_end_date), getContext());
                                        }
                                    } else {
                                        ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.prompt_start_date), getContext());
                                    }
                                } else {
                                    String bankcode = chosenBank;

                                    try {
                                        narrationString = URLEncoder.encode(narrationString, "utf-8");
                                    } catch (Exception e) {

                                    }
                                    if (isTokenRequired) {
                                        if (!TextUtils.isEmpty(auth_code.getText().toString())) {
                                            invokeFundsTransfer(session.getUserName(), chosenBenName, account_to, amountString, accountfrm, session.getAccountName(), narrationString, bankcode, tran_pin.getText().toString(), AUTH_MODE_TYPE, narrationString, auth_code.getText().toString());
                                        } else {
                                            ((TransfersControllerActivity) getActivity()).warningDialog("Please enter security code!");
                                        }
                                    } else {
                                        invokeFundsTransfer(session.getUserName(), chosenBenName, account_to, amountString, accountfrm, session.getAccountName(), narrationString, bankcode, tran_pin.getText().toString(), Constants.KEY_AUTH_NO_AUTH, narrationString, Constants.KEY_AUTH_NO_AUTH);
                                    }
                                }
                            } else {
                                ((TransfersControllerActivity) getActivity()).noInternetDialog();
                                // Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (isSaved) {

                                chosenBenName = benList.get(chosenBen).getName();
                                chosenBenAccNo = benList.get(chosenBen).getAccountNumber();
                                chosenBankName = benList.get(chosenBen).getBank();
                       /* if (Utility.isNotNull(tran_pin.getText().toString())) {
                            if (Utility.isNotNull(amountString)) {
                                showConfirm();
                            } else {
                                ResponseDialogs.warningStatic(getString(R.string.error), "Please enter amount", getContext());
                            }
                        } else {
                            ResponseDialogs.warningStatic(getString(R.string.error), "Please enter your transaction Pin.", getContext());
                        }*/
                                if (chosenBenAccNo != null) {
                                    if (Utility.isNotNull(tran_pin.getText().toString())) {
                                        if (Utility.isNotNull(amountString)) {
                                            if (accountfrm.equals(chosenBenAccNo)) {
                                                ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_account), getContext());
                                            } else {
                                                //fetchBeneficiaryDetails(account_to, session.getUserName(),chosenBank);
                                                showConfirm();
                                            }
                                        } else {
                                            ResponseDialogs.warningStatic(getString(R.string.error), "Please enter amount", getContext());
                                        }
                                    } else {
                                        ResponseDialogs.warningStatic(getString(R.string.error), "Please enter your transaction Pin.", getContext());
                                    }
                                } else {
                                    ResponseDialogs.warningStatic(getString(R.string.error), "Please enter beneficiary details", getContext());

                                }
                            } else {
                        /*if (Utility.isNotNull(amountString)) {
                            fetchBeneficiaryDetails(account_to, session.getUserName(), chosenBank);
                        } else {
                            ResponseDialogs.warningStatic(getString(R.string.error), "Please enter amount", getContext());
                        }*/
                                if (!TextUtils.isEmpty(account_to)) {
                                    if (Utility.isNotNull(tran_pin.getText().toString())) {
                                        if (Utility.isNotNull(amountString)) {
                                            if (accountfrm.equals(chosenBenAccNo)) {
                                                ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_account), getContext());
                                            } else {
                                                fetchBeneficiaryDetails(account_to, session.getUserName(), chosenBank);
                                            }
                                        } else {
                                            ResponseDialogs.warningStatic(getString(R.string.error), "Please enter amount", getContext());
                                        }
                                    } else {
                                        ResponseDialogs.warningStatic(getString(R.string.error), "Please enter your transaction Pin.", getContext());
                                    }
                                } else {
                                    ResponseDialogs.warningStatic(getString(R.string.error), "Please enter beneficiary details", getContext());

                                }

                            }

                            // if (accountfrm.equals(account_to)) {
                            //  invokeFundsTransfer(accountfrm, account_to, amountString);
                            // Toast.makeText(getActivity(), "same account transfer not permitted", Toast.LENGTH_LONG).show();
                            // } else {

                            // }
                        }
                    } else {
                        ResponseDialogs.warningStatic(getString(R.string.error), "Please select debit account", getContext());
                    }
                    //startActivity(new Intent(getActivity(),TransferConfirm.class));
                } catch (Exception e) {
                    //FirebaseCrash.report(e);
                    Log.Error(e);
                    ResponseDialogs.failStatic(getString(R.string.error), getString(R.string.error_server), getContext());
                }
            }
        });

        Button btnCancel = rootView.findViewById(R.id.btncancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isReady) {
                    //showForm();
                    /*FragmentManager fragmentManager = getFragmentManager();
                    assert fragmentManager != null;
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    OtherBanks fragment = new OtherBanks();
                    fragmentTransaction.add(R.id.containerFragment,fragment, "otherbank");
                    fragmentTransaction.commit();*/
                    Fragment frg = null;
                    frg = getFragmentManager() != null ? getFragmentManager().findFragmentByTag("otherbank") : null;
                    assert getFragmentManager() != null;
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(frg);
                    ft.attach(frg);
                    ft.commit();
                } else {
                    //getActivity().onBackPressed();
                    startActivity(new Intent(getContext(), Transfers.class));
                }
            }
        });

        if (tranBenId == null) {

            showForm();
        } else {

            showSaved();
        }
        fetchBeneficiaries(session.getUserName(), "OTHER");

//confirm fields
        /*debit_account_txt = (TextView) rootView.findViewById(R.id.debit_account_txt);
        beneficiary_name_txt = (TextView) rootView.findViewById(R.id.beneficiary_name_txt);
        beneficiary_account_txt = (TextView) rootView.findViewById(R.id.beneficiary_account_txt);
        beneficiary_amount_txt = (TextView) rootView.findViewById(R.id.beneficiary_amount_txt);
        beneficiary_bank_txt = (TextView) rootView.findViewById(R.id.beneficiary_bank_txt);
        narration_txt = (TextView) rootView.findViewById(R.id.narration_txt);*/

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void showConfirm() {

        isReady = true;
        String amt = Constants.KEY_NAIRA + amountString;
        ((TransfersControllerActivity) getActivity()).setActionBarTitle("Confirm Transaction");
        ((TransfersControllerActivity) getActivity()).hideSpinner(amt);

        loadingLayout.setVisibility(View.GONE);

        confirmItems.clear();
        ConfirmModel item = new ConfirmModel("Debit Account", accountFrom.getSelectedItem().toString());
        confirmItems.add(item);
        //txtFrom.setText(accountfrm);
        item = new ConfirmModel("Beneficiary Name", chosenBenName);
        confirmItems.add(item);
        item = new ConfirmModel("Beneficiary Account", chosenBenAccNo);
        confirmItems.add(item);
        item = new ConfirmModel("Credit Amount", NumberUtilities.getWithDecimalPlusCurrency(Double.parseDouble(amountString)));
        confirmItems.add(item);
        item = new ConfirmModel("Beneficiary Bank", chosenBankName);
        confirmItems.add(item);
        // txtNarration.setText(narrationString);

        try {

            item = new ConfirmModel("Narration", URLDecoder.decode(narrationString, "utf-8"));
            confirmItems.add(item);
        } catch (Exception e) {
            Log.Error(e);
        }

        confirm_fields.setVisibility(View.VISIBLE);
        form_fields.setVisibility(View.GONE);
        Utility.hideKeyboardFrom(getContext(), rootView);

        if (NetworkUtils.isConnected(getContext())) {
            checkIfTokenRequired(session.getUserName(), amountString, Constants.KEY_FUNDTRANS_OTHER, tran_pin.getText().toString());
        } else {
            ((TransfersControllerActivity) getActivity()).noInternetDialog();
        }
    }

    private void showForm() {
        isReady = false;
        ((TransfersControllerActivity) getActivity()).setActionBarTitle(FRAG_TITLE);
        ((TransfersControllerActivity) getActivity()).showSpinner();
        confirm_fields.setVisibility(View.GONE);
        form_fields.setVisibility(View.VISIBLE);
        confirmItems.clear();
        confirmAdapter.notifyDataSetChanged();
        showNewBen();

    }

    private void showNewBen() {
        isSaved = false;
        new_fields.setVisibility(View.VISIBLE);
        saved_fields.setVisibility(View.GONE);
        saveBen.setVisibility(View.VISIBLE);

    }

    private void showSaved() {
        isSaved = true;
        new_fields.setVisibility(View.GONE);
        saved_fields.setVisibility(View.VISIBLE);

    }

    private void noSavedBeneficiary() {
        rGroup.check(R.id.rd_newBen);
        Toast.makeText(context, getString(R.string.error_no_saved_beneficiaries), Toast.LENGTH_LONG).show();

        SweetAlertDialog prgDialog = new SweetAlertDialog(context);
        prgDialog.setTitleText(getString(R.string.notice))
                .setContentText(getString(R.string.prompt_save_beneficiaries))
                .setConfirmText(getString(R.string.label_create_beneficiary))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        startActivity(new Intent(context, AddBeneficiary.class));
                    }
                })
                .setCancelText(getString(R.string.label_just_proceed))
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        isSaved = false;
                        showNewBen();
                    }
                })
                .changeAlertType(SweetAlertDialog.NORMAL_TYPE);
        prgDialog.show();
    }

    private void invokeFundsTransfer(final String username, final String beneficiaryAccountName, final String beneficiaryAccountNumber, final String amt, final String sourceaccountno,
                                     final String originatorName, String transactionLocation, String destBankCode, String pin, String authtype, String transdesc, String authvalue) {
        if (transactionLocation == null || transactionLocation.equals("") || transactionLocation.length() == 0) {
            transactionLocation = "NA";
        }
        destBankCode = destBankCode.replace("999", "");
        ((TransfersControllerActivity) getActivity()).showLoadingProgress();
        //@Path("/fundtransferother/{username}/{beneficiaryAccountName}/{beneficiaryAccountNumber}/{amt}/{sourceaccountno}/{originatorName}/{transactionLocation}/{destBankCode}")
        /*if (!Constants.NET_PATH.contains(Constants.HOSTNAME)) {
            int m = banksList.size();
            for (int j = 0; j < m; j++) {
                if(destBankCode.equals(banksList.get(j).getCbnBankCode())){
                    destBankCode = "999"+banksList.get(j).getCbnBankCode();
                }
            }
        }*/
        String params = "";
        //@Path("/fundtransferother/{username}/{beneficiaryAccountName}/{beneficiaryAccountNumber}/{amt}/{sourceaccountno}/{originatorName}/{transactionLocation}/{destBankCode}/{pin}/{authtype}/{transdesc}/{authvalue}
        try {
            params = username + "/" + URLEncoder.encode(beneficiaryAccountName, "utf-8") + "/" + beneficiaryAccountNumber + "/" + amt + "/" + sourceaccountno + "/" + URLEncoder.encode(originatorName, "utf-8") + "/" + transactionLocation + "/" + destBankCode + "/" + pin + "/" + authtype + "/" + transdesc + "/" + authvalue;
        } catch (Exception e) {
            Log.Error(e);
        }
        String url = SecurityLayer.genURLCBC("fundtransferother", params, getContext());

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        final String finalDestBankCode = destBankCode;
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                ((TransfersControllerActivity) getActivity()).dismissProgress();
                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getContext());

                    String responsecode = obj.optString("respcode");

                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String respDesc = obj.optString(Constants.KEY_BANK_MSG);
                    Bundle bundle = new Bundle();
                    Log.debug("ubnresponse", responsemessage);
                    if (responsecode.equals("00")) {
                        if (isBenChecked) {
                            new LovelyStandardDialog(context)
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setButtonsColorRes(R.color.midnight_blue)
                                    .setIcon(R.drawable.ic_check_circle_white)
                                    .setTitle("Success")
                                    .setMessage(responsemessage)
                                    .setPositiveButton("Continue", null)
                                    .setOnButtonClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ((TransfersControllerActivity) getActivity()).setBeneficiary(beneficiaryAccountName, beneficiaryAccountNumber, finalDestBankCode, chosenBankName, username, "OTHER");

                                        }
                                    })
                                    .show();
                        } else {
                            ResponseDialogs.successToActivity("Success", responsemessage, context, Transfers.class, bundle);
                        }
                        ((TransfersControllerActivity) getActivity()).updateBalances(username);

                    } else if (responsecode.equals("7007")) {
                        ResponseDialogs.limitError(getContext(), "Info", responsemessage, Transfers.class);
                    } else {

                        ResponseDialogs.failToActivity("Error", respDesc, context, Transfers.class, bundle);
                        //ResponseDialogs.failStatic(getString(R.string.error), responsemessage, context);
                    }

                } catch (Exception e) {
                    SecurityLayer.generateToken(getContext());
                    Log.Error(e);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed

                Log.debug("ubnaccountsfail", t.toString());
                SecurityLayer.generateToken(getContext());
                // showToast(getString(R.string.error_500));
                ((TransfersControllerActivity) getActivity()).dismissProgress();

            }
        });
    }

    private void fetchBeneficiaryDetails(final String accountNumber, String username, String bankCode) {
        ((TransfersControllerActivity) getActivity()).showLoadingProgress();
        //@Path("/nipenquiry/{username}/{accountnumber}/{destinationbankcode}")
        /*if (!Constants.NET_PATH.contains(Constants.HOSTNAME)) {
            List<Banks> banksList = session.getBanks();
            int m = banksList.size();
            for (int j = 0; j < m; j++) {
                if (bankCode.equals(banksList.get(j).getBankCode())) {
                    bankCode = "999" + banksList.get(j).getCbnBankCode();
                }
            }
        }*/
        String params = username + "/" + accountNumber + "/" + bankCode;

        String url = SecurityLayer.genURLCBC("nipenquiry", params, getContext());

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                ((TransfersControllerActivity) getActivity()).dismissProgress();
                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);

                    String full = response.raw().body().toString();

                    Log.debug("ubnresponse", full);
                    if (responsecode.equals("00")) {
                        JSONObject objData = obj.getJSONObject("NIPENQDATA");
                        chosenBenName = objData.optString("accountName");
                        chosenBenAccNo = accountNumber;
                        if (Utility.isNotNull(chosenBenName)) {
                            showConfirm();
                        } else {
                            ResponseDialogs.warningStatic("Error", "Sorry, we are unable to retrieve the account name for the provided account number", getContext());
                        }
                        // ben_accountName = response.body().getBeneficiaryAccountName();
                        // showConfirm(beneficiary_type,ben_accountName,beneficiciary_accNumber,beneficiary_bank);
                    } else {
                        /*if (BuildConfig.DEBUG) {
                            chosenBenName = "Brian Towett";
                            chosenBenAccNo = accountNumber;
                            showConfirm();
                        } else {
                            ResponseDialogs.warningStatic(context.getString(R.string.error), responsemessage, context);
                            //warningDialog(responsemessage);
                            //startDashBoard();
                        }*/
                        ResponseDialogs.warningStatic(context.getString(R.string.error), responsemessage, context);
                    }
                } catch (Exception e) {
                    SecurityLayer.generateToken(getContext());
                    Log.Error(e);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                ((TransfersControllerActivity) getActivity()).dismissProgress();
                ResponseDialogs.warningStatic(context.getString(R.string.error), getString(R.string.error_500), context);
                SecurityLayer.generateToken(getContext());
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    private void invokeScheduledTransfer(final String username, final String startDate, final String endDate, final String amt, final String beneficiaryAccountName, final String beneficiaryAccountNumber,
                                         final String destBankCode, final String frequency, final String sourceaccountno, final String originatorName, final String type) {
        ((TransfersControllerActivity) getActivity()).showLoadingProgress();
        //@Path("/schduleTransfer/{username}/{startDate}/{endDate}/{amt}/
        // {beneficiaryAccountName}/{beneficiaryAccountNumber}/{destBankCode}/{frequency}/
        // {sourceaccountno}/{originatorName}/{type}")
        String params = username + "/" + startDate + "/" + endDate + "/" + amt + "/" + beneficiaryAccountName + "/" + beneficiaryAccountNumber + "/" + destBankCode + "/" + frequency + "/" + sourceaccountno + "/" + originatorName + "/" + type;

        String url = SecurityLayer.genURLCBC("schduleTransfer", params, getContext());

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                ((TransfersControllerActivity) getActivity()).dismissProgress();
                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String respDesc = obj.optString(Constants.KEY_BANK_MSG);
                    Bundle bundle = new Bundle();
                    Log.debug("ubnresponse", responsemessage);
                    if (responsecode.equals("00")) {
                        ((TransfersControllerActivity) getActivity()).updateBalances(username);
                        ResponseDialogs.successToActivity(responsemessage, respDesc, context, Transfers.class, bundle);
                    } else {
                        ResponseDialogs.failToActivity(responsemessage, respDesc, context, Transfers.class, bundle);
                        //ResponseDialogs.failStatic(getString(R.string.error), responsemessage, context);
                    }
                } catch (Exception e) {
                    SecurityLayer.generateToken(getContext());
                    Log.Error(e);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.generateToken(getContext());
                Log.debug("ubnaccountsfail", t.toString());
                // showToast(getString(R.string.error_500));
                ((TransfersControllerActivity) getActivity()).dismissProgress();

            }
        });
    }

    protected void formatDateEditText(Calendar myCalendar, EditText editText) {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        editText.setText(sdf.format(myCalendar.getTime()));
    }

    public void getDateIntoEditText(final EditText editText) {
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                formatDateEditText(myCalendar, editText);
            }

        };

        editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void fetchBeneficiaries(String userName, String type) {
        benListString.clear();
        benList.clear();
        ((TransfersControllerActivity) getActivity()).showLoadingProgress();

        String urlparam = userName + "/" + type;
        String url = SecurityLayer.genURLCBC("fetchsavedbeneficiaries", urlparam, getContext());

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                ((TransfersControllerActivity) getActivity()).dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        // customermobile = obj.optString("customermobileno");
                        JSONArray array = new JSONArray(obj.optString("savedbeneficiaries"));
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject un = array.getJSONObject(i);
                            if (un.optString("beneficiaryId").equals(tranBenId)) {
                                selpos = i;
                            }
                            Beneficiary beneficiary = new Beneficiary(un.optString("accountName"), un.optString("beneficiaryAccount"), un.optString("destinationBankName"), "OTHER", un.optString("destinationBank"), un.optString("beneficiaryId"));
                            benList.add(beneficiary);
                            String bank;
                            if (un.optString("destinationBankName") != null) {
                                bank = "(" + un.optString("destinationBankName") + ")";
                            } else {
                                bank = "";
                            }
                            benListString.add(un.optString("accountName") + bank + " - " + un.optString("beneficiaryAccount"));
                        }
                        adapter_ben.notifyDataSetChanged();
                        accountTo.setSelection(selpos);

                    } else {
                        //ResponseDialogs.warningDialogLovely(getContext(), "Error", obj.optString(Constants.KEY_MSG));
                    }

                } catch (Exception e) {
                    SecurityLayer.generateToken(getContext());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                ((TransfersControllerActivity) getActivity()).dismissProgress();
                SecurityLayer.generateToken(getContext());
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    private void checkIfTokenRequired(String username, String amount, String service, String pin) {
        //@Path("/isTokenAuthRequired/{userid}/{amount}/{servicetype}/{pin}")
        ((TransfersControllerActivity) getActivity()).showLoadingProgress();
        String params = username + "/" + amount + "/" + service + "/" + pin;
        String session_id = UUID.randomUUID().toString();
        String url = SecurityLayer.genURLCBC("isTokenAuthRequired", params, getContext());

        // String urlparam = "accountnovallidatin/" + SecurityLayer.generalEncrypt(accountNumber);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                ((TransfersControllerActivity) getActivity()).dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getContext());
                    // isTokenRequired = true;
                    //token_auth.setVisibility(View.VISIBLE);

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {

                        if (obj.optString("isTokenAuthRequired").equals("Y")) {
                            isTokenRequired = true;
                            token_auth.setVisibility(View.VISIBLE);
                        } else if (obj.optString("isTokenAuthRequired").equals("N")) {
                            isTokenRequired = false;
                            token_auth.setVisibility(View.GONE);
                        } else {
                            ResponseDialogs.warningDialogLovely(getContext(), "Error", obj.optString(Constants.KEY_MSG));
                        }

                    } else {
                        ResponseDialogs.warningDialogLovely(getContext(), "Error", obj.optString(Constants.KEY_MSG));
                        showForm();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showForm();
                    ResponseDialogs.warningDialogLovely(getContext(), "Error", getString(R.string.error_500));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                ((TransfersControllerActivity) getActivity()).dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                showForm();
                ResponseDialogs.warningDialogLovely(getContext(), "Error", getString(R.string.error_500));
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
