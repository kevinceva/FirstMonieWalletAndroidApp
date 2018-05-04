package com.ceva.ubmobile.core.ui.transfers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.ConfirmAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.core.ui.Transfers;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;
import com.ceva.ubmobile.utils.NumberUtilities;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
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
 * Activities that contain this fragment must implement the
 * {@link LinkedAccounts.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LinkedAccounts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LinkedAccounts extends Fragment {
    public static final String FRAG_TITLE = "Linked Accounts";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Context context;
    UBNSession session;
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
    Button btncontinue;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private boolean isReady = false;
    private LinearLayout form_fields;
    private RelativeLayout confirm_fields;
    private TextView txtFrom, txtTo, txtAmount, txtNarration;
    private String accountfrm, account_to, amountString, narrationString;


    public LinkedAccounts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LinkedAccounts.
     */
    // TODO: Rename and change types and number of parameters
    public static LinkedAccounts newInstance(String param1, String param2) {
        LinkedAccounts fragment = new LinkedAccounts();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_linked_accounts, container, false);
        ButterKnife.bind(this, rootView);
        context = getActivity();
        session = new UBNSession(getContext());
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
        /*txtFrom = (TextView) rootView.findViewById(R.id.txtFrom);
        txtTo = (TextView) rootView.findViewById(R.id.txtTo);
        txtAmount = (TextView) rootView.findViewById(R.id.txtAmount);
        txtNarration = (TextView) rootView.findViewById(R.id.txtNarration);*/

        confirmationRecyler = rootView.findViewById(R.id.confirm_list);
        confirmAdapter = new ConfirmAdapter(confirmItems, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        confirmationRecyler.setLayoutManager(mLayoutManager);
        confirmationRecyler.setItemAnimator(new DefaultItemAnimator());
        confirmationRecyler.setAdapter(confirmAdapter);

        List<String> accountList = session.getAccountNumbersNoDOM();
        final Spinner accountFrom = rootView.findViewById(R.id.tx_accounts_from);
        final Spinner accountTo = rootView.findViewById(R.id.tx_accounts_to);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_row, accountList);
        accountFrom.setAdapter(adapter);
        accountTo.setAdapter(adapter);

        final EditText amountTxt = rootView.findViewById(R.id.txamount);
        final EditText narrationTxt = rootView.findViewById(R.id.tx_reference);
        tran_pin = rootView.findViewById(R.id.tran_pin);

        btncontinue = rootView.findViewById(R.id.btnContinue);
        btncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),"clicked",Toast.LENGTH_LONG).show();
                accountfrm = accountFrom.getSelectedItem().toString();
                account_to = accountTo.getSelectedItem().toString();
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
                        String[] fromsplit = accountfrm.split("-");
                        int max = fromsplit.length;
                        max = max - 1;

                        accountfrm = fromsplit[max].trim();

                        String[] tosplit = account_to.split("-");
                        max = tosplit.length;
                        max = max - 1;

                        account_to = tosplit[max].trim();
                        try {
                            narrationString = URLEncoder.encode(narrationString, "utf-8");
                        } catch (Exception e) {

                        }
                        if (isTokenRequired) {
                            if (!TextUtils.isEmpty(auth_code.getText().toString())) {
                                invokeFundsTransfer(accountfrm, account_to, amountString, session.getUserName(), "UB", narrationString, tran_pin.getText().toString(), AUTH_MODE_TYPE, narrationString, auth_code.getText().toString());
                            } else {
                                ((TransfersControllerActivity) getActivity()).warningDialog("Please enter security code!");
                            }
                        } else {

                            invokeFundsTransfer(accountfrm, account_to, amountString, session.getUserName(), "UB", narrationString, tran_pin.getText().toString(), Constants.KEY_AUTH_NO_AUTH, narrationString, Constants.KEY_AUTH_NO_AUTH);
                        }
                    } else {
                        ((TransfersControllerActivity) getActivity()).noInternetDialog();
                    }
                } else {
                    if (Utility.isNotNull(tran_pin.getText().toString())) {
                        if (Utility.isNotNull(amountString)) {
                            if (accountfrm.equals(account_to)) {
                                ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_account), getContext());
                            } else {
                                showConfirm();
                            }
                        } else {
                            ResponseDialogs.warningStatic(getString(R.string.error), "Please enter amount", getContext());
                        }
                    } else {
                        ResponseDialogs.warningStatic(getString(R.string.error), "Please enter your transaction Pin.", getContext());
                    }

                    // if (accountfrm.equals(account_to)) {
                    //  invokeFundsTransfer(accountfrm, account_to, amountString);
                    // Toast.makeText(getActivity(), "same account transfer not permitted", Toast.LENGTH_LONG).show();
                    // } else {

                    // }
                }
                //startActivity(new Intent(getActivity(),TransferConfirm.class));
            }
        });

        Button btnCancel = rootView.findViewById(R.id.btncancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isReady) {
                    showForm();
                } else {
                    getActivity().onBackPressed();
                }
            }
        });
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
        ConfirmModel item = new ConfirmModel("Debit Account", accountfrm);
        confirmItems.add(item);
        //txtFrom.setText(accountfrm);
        item = new ConfirmModel("Beneficiary Account", account_to);
        confirmItems.add(item);
        // txtTo.setText(account_to);
//        txtAmount.setText(amt);
        item = new ConfirmModel("Credit Amount", NumberUtilities.getWithDecimalPlusCurrency(Double.parseDouble(amountString)));
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
            checkIfTokenRequired(session.getUserName(), amountString, Constants.KEY_FUNDTRANS_WITHIN, tran_pin.getText().toString());
        } else {
            ((TransfersControllerActivity) getActivity()).noInternetDialog();
        }
    }


    private void showForm() {
        isReady = false;
        ((TransfersControllerActivity) getActivity()).setActionBarTitle(FRAG_TITLE);
        ((TransfersControllerActivity) getActivity()).showSpinner();
        confirm_fields.setVisibility(View.GONE);
        confirmItems.clear();
        form_fields.setVisibility(View.VISIBLE);

    }

    private void invokeFundsTransfer(final String accountFrom, final String accountTo, final String amount, final String userName, final String type, String description, String pin, String authtype, String transdesc, String authvalue) {
        ((TransfersControllerActivity) getActivity()).showLoadingProgress();
        if (description == null || description.equals("") || description.length() == 0) {
            description = "NA";
        }
        btncontinue.setEnabled(false);
        //@Path("/fundtransfer/{fromAcccountNumber}/{toAcccountNumber}/{amount}/{branchCode}/{username}/{type}/{description}/{pin}/{authtype}/{transdesc}/{authvalue}
        String params = accountFrom + "/" + accountTo + "/" + amount + "/682/" + userName + "/" + type + "/" + description + "/" + pin + "/" + authtype + "/" + transdesc + "/" + authvalue;

        String url = SecurityLayer.genURLCBC(Constants.KEY_FUNDTRANSFER_ENDPOINT, params, getContext());

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
                        ((TransfersControllerActivity) getActivity()).updateBalances(userName);
                        ResponseDialogs.successToActivity(responsemessage, respDesc, context, Transfers.class, bundle);
                    } else if (responsecode.equals("7007")) {
                        ResponseDialogs.limitError(getContext(), "Info", responsemessage, Transfers.class);
                    } else {
                        ResponseDialogs.failStatic(getString(R.string.error), responsemessage, context);
                        btncontinue.setEnabled(true);
                    }

                } catch (Exception e) {
                    SecurityLayer.generateToken(getContext());
                    Log.Error(e);
                    ResponseDialogs.failToActivity(getString(R.string.error), getString(R.string.error_server), context, Transfers.class, new Bundle());

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed

                Log.Error(t.toString());
                SecurityLayer.generateToken(getContext());
                // showToast(getString(R.string.error_500));
                ((TransfersControllerActivity) getActivity()).dismissProgress();
                ResponseDialogs.failToActivity(getString(R.string.error), getString(R.string.error_server), context, Transfers.class, new Bundle());

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
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
