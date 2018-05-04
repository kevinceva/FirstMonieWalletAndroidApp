package com.ceva.ubmobile.core.signon.newtobank;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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

import com.ceva.ubmobile.BuildConfig;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.ConfirmAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.signon.TermsAndConditions;
import com.ceva.ubmobile.core.ui.LandingPage;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.BankProductModel;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.ImageUtils;
import com.ceva.ubmobile.utils.ScalingUtilities;
import com.github.pinball83.maskededittext.MaskedEditText;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
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

    @BindView(R.id.otp)
    EditText otp;

    @BindView(R.id.loadingLayout)
    LinearLayout loadingLayout;

    List<ConfirmModel> confirmModelList = new ArrayList<>();
    ConfirmAdapter confirmAdapter;

    public static final String FIRSTACCOUNT = "hiiniaccountyakwanza";
    String totalAmount, feeAmount, cevaRef;

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
        View rootView = inflater.inflate(R.layout.ntb_fragment_account_confirmation, container, false);
        ButterKnife.bind(this, rootView);

        title.setText(titleSt);
        number.setText(numberSt);
        loadingLayout.setVisibility(View.GONE);
        confirmAdapter = new ConfirmAdapter(confirmModelList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        confirmList.setLayoutManager(mLayoutManager);
        confirmList.setItemAnimator(new DefaultItemAnimator());
        confirmList.setAdapter(confirmAdapter);
        if (!session.getString(BankProductModel.KEY_PRODUCT_CODE).contains("WA")) {
            ConfirmModel item = new ConfirmModel("BVN ", session.getString(BankProductModel.KEY_BVN));
            confirmModelList.add(item);
        }

        ConfirmModel item = new ConfirmModel("First Name ", session.getString(BankProductModel.KEY_FNAME));
        confirmModelList.add(item);
        item = new ConfirmModel("Middle Name ", session.getString(BankProductModel.KEY_MIDNAME));
        confirmModelList.add(item);
        item = new ConfirmModel("Surname ", session.getString(BankProductModel.KEY_LNAME));
        confirmModelList.add(item);
        item = new ConfirmModel("Gender ", session.getString(BankProductModel.KEY_GENDER));
        confirmModelList.add(item);
        item = new ConfirmModel("Date of Birth ", session.getString(BankProductModel.KEY_DOB));
        confirmModelList.add(item);
        item = new ConfirmModel("Phone Number ", session.getString(BankProductModel.KEY_PHONE));
        confirmModelList.add(item);
        item = new ConfirmModel("Account Type ", session.getString(BankProductModel.KEY_PRODUCT_TYPE));
        confirmModelList.add(item);
        if (!session.getString(BankProductModel.KEY_PRODUCT_CODE).contains("WA")) {
            item = new ConfirmModel("Bank Branch ", session.getString(BankProductModel.KEY_BRANCHNAME));
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
                if (!TextUtils.isEmpty(otp.getText().toString())) {
                    if (NetworkUtils.isConnected(getContext())) {
                        if (session.getString(BankProductModel.KEY_PRODUCT_CODE).contains("WA")) {
                            openWalletAccount(cevaRef, session.getString(BankProductModel.KEY_FNAME),
                                    session.getString(BankProductModel.KEY_MIDNAME),
                                    session.getString(BankProductModel.KEY_LNAME),
                                    session.getString(BankProductModel.KEY_GENDER),
                                    session.getString(CustomerDetails.KEY_EMAIL),
                                    session.getString(BankProductModel.KEY_DOB),
                                    "NA",
                                    session.getString(BankProductModel.KEY_PHONE),
                                    otp.getText().toString(),
                                    session.getString(CustomerDetails.KEY_INITIAL_AMOUNT),
                                    feeAmount,
                                    totalAmount,
                                    String.valueOf(System.currentTimeMillis()),
                                    session.getString(CustomerDetails.KEY_ACCTYPE));

                        } else {
                            openAccount(
                                    cevaRef,
                                    session.getString(BankProductModel.KEY_FNAME),
                                    session.getString(BankProductModel.KEY_MIDNAME),
                                    session.getString(BankProductModel.KEY_LNAME),
                                    session.getString(BankProductModel.KEY_GENDER),
                                    session.getString(BankProductModel.KEY_DOB),
                                    session.getString(BankProductModel.KEY_PHONE),
                                    session.getString(BankProductModel.KEY_PRODUCT_CODE),
                                    session.getString(BankProductModel.KEY_BVN),
                                    session.getString(BankProductModel.KEY_BRANCH_CODE),
                                    otp.getText().toString(),
                                    session.getString(CustomerDetails.KEY_INITIAL_AMOUNT),
                                    session.getString(BankProductModel.KEY_BVN), feeAmount, totalAmount, String.valueOf(System.currentTimeMillis()));
                        }
                    } else {
                        ((NewToBank) Objects.requireNonNull(getActivity())).noInternetDialog();
                    }
                } else {
                    otp.setError("Please enter the OTP sent via SMS");
                    //((NewToBank) getActivity()).warningDialog("Please enter the OTP sent via SMS");
                }
            }
        });

        getAccessCode(session.getString(CustomerDetails.KEY_EMAIL), session.getString(CustomerDetails.KEY_INITIAL_AMOUNT));

        return rootView;
    }

    private void openAccount(String cevaref, String fname, String mname, String lname, String gender, String dob, String mobno, String productCode, String initiatorID, String branch, String otp,
                             String initial, String bvn, String fee, String total, String reqtime) {
        ((NewToBank) getActivity()).showLoadingProgress();
        ///customeraccountopen/fname/mname/lname/gender/dateOfBirth/mobno/productCode/initiatorID/branch/otp
        //@Path("/paystackcustomeractopenwithdeposit/{cevarefno}/{fname}/{mname}/{lname}/{gender}/{dateOfBirth}/{mobno}/{productCode}/{initiatorID}/{branch}/{otp}
        ///{initialamount}/{bvn}/{fee}/{totalamount}/{reqtime}")
        String urlparam = cevaref + "/" + fname + "/" + mname + "/" + lname + "/" + gender + "/" + dob + "/" + mobno + "/" + productCode + "/" + initiatorID + "/" + branch + "/" + otp
                + "/" + initial + "/" + bvn + "/" + fee + "/" + total + "/" + reqtime;
        String url = "";
        try {
            url = SecurityLayer.beforeLogin(urlparam, UUID.randomUUID().toString(), "paystackcustomeractopenwithdeposit", getContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                ((NewToBank) getActivity()).dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptBeforeLogin(obj, getContext());

                    if (obj.optString("responseCode").equals("0")) {
                        session.setString(FIRSTACCOUNT, obj.optString("accountNumber"));
                        String msg = obj.optString("responseMessage") + "\n" + "Account Number: " + obj.optString("accountNumber");
                        ResponseDialogs.successToActivity("All Set!", msg, getContext(), TermsAndConditions.class, new Bundle());

                    } else {

                        ResponseDialogs.warningDialogLovely(getContext(), "Error", obj.optString("respdesc") + "\n" + obj.optString("responseMessage"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ((NewToBank) getActivity()).warningDialog(getString(R.string.error_server));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                ((NewToBank) getActivity()).dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                ((NewToBank) getActivity()).warningDialog(getString(R.string.error_server));
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    /**
     * @param cevaref
     * @param fname
     * @param mname
     * @param lname
     * @param gender
     * @param email
     * @param dob
     * @param address
     * @param mobno
     * @param otp
     * @param initial
     * @param fee
     * @param total
     * @param reqtime
     * @param acctype
     */
    private void openWalletAccount(String cevaref, String fname, String mname, String lname, String gender, String email, String dob,
                                   String address, String mobno, String otp, String initial, String fee, String total, String reqtime, String acctype) {
        ((NewToBank) getActivity()).showLoadingProgress();
        //@Path("/walletregistration/{fname: .}/{mname: .}/{lname: .}/{gender: .}/{email: .}/{dob: .}/{address: .}/{mobileno: .}/{otp}")
        //@Path("/walletregistration/{cevarefrenceno}/{fname: .}/{mname: .}/{lname: .}/{gender: .}/{email: .}/{dob: .}/{address: .}/{mobileno: .}/{otp}"
        //			+ "/{initialamount}/{fee}/{totalamount}/{reqtime}/{acttype}")
        String urlparam = cevaref + "/" + fname + "/" + mname + "/" + lname + "/" + gender + "/" + email + "/" + dob + "/" + address + "/" + mobno + "/" + otp
                + "/" + initial + "/" + fee + "/" + total + "/" + reqtime + "/" + acctype;
        String url = "";
        try {
            url = SecurityLayer.beforeLogin(urlparam, UUID.randomUUID().toString(), "walletregistration", getContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                ((NewToBank) getActivity()).dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptBeforeLogin(obj, getContext());

                    if (obj.optString("respcode").equals("00")) {
                        String msg = obj.optString("respdesc") + "\n" + "Account Number: " + obj.optString("accountno") + "\n" + "Please proceed to setup your mobile banking login credentials";
                        // ResponseDialogs.successToActivity("All Set!", msg, getContext(), LandingPage.class, new Bundle());
                        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_check_circle_white, getResources());
                        new LovelyStandardDialog(getContext())
                                .setTopColor(ImageUtils.getColorByThemeAttr(getContext(),R.attr._ubnColorPrimaryDark, Color.BLUE))
                                .setButtonsColorRes(R.color.midnight_blue)
                                .setIcon(icon)
                                .setTitle("All Set!")
                                .setMessage(msg)
                                .setNegativeButton("LATER", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(getContext(), LandingPage.class));
                                    }
                                })
                                .setPositiveButton("PROCEED", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(getContext(), TermsAndConditions.class));
                                    }
                                }).show();

                    } else {

                        ResponseDialogs.warningDialogLovely(getContext(), "Error", obj.optString("respdesc") + "\n" + obj.optString("responseMessage"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ((NewToBank) getActivity()).warningDialog(getString(R.string.error_server));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                ((NewToBank) getActivity()).dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                ((NewToBank) getActivity()).warningDialog(getString(R.string.error_server));
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    private void getAccessCode(final String email, final String amount) {
        ((NewToBank) Objects.requireNonNull(getActivity())).showLoadingProgress();
        //@Path("/initializetransaction/{email}/{amount}")
        String urlparam = email + "/" + amount;
        String url = "";

        try {
            url = SecurityLayer.beforeLogin(urlparam, UUID.randomUUID().toString(), "initializetransaction", getContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Log.debug("nipservice", response.body());
                ((NewToBank) getActivity()).dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptBeforeLogin(obj, getContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        String access_code = obj.optJSONObject("data").optString("access_code");
                        String total_amount = obj.optString("totalamount");
                        String fee = obj.optString("fee");
                        String ref = obj.optString("cevarefno");
                        totalAmount = total_amount;
                        feeAmount = fee;
                        cevaRef = ref;
                        showPaymentDialog(getContext(), amount, email, access_code, fee, total_amount, ref);

                    } else {
                        ResponseDialogs.warningDialogLovelyToFragment(getContext(), getString(R.string.error), obj.optString(Constants.KEY_MSG), getBackFrag());
                        //ResponseDialogs.warningDialogLovely(getContext(), "Error", obj.optString(Constants.KEY_MSG));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ResponseDialogs.warningDialogLovelyToFragment(getContext(), getString(R.string.error), getString(R.string.error_500), getBackFrag());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                ((NewToBank) getActivity()).dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                ResponseDialogs.warningDialogLovelyToFragment(getContext(), getString(R.string.error), getString(R.string.error_500), getBackFrag());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    private void verifyTransaction(final String cevaref, String authval) {
        ((NewToBank) Objects.requireNonNull(getActivity())).showLoadingProgress();
        //@Path("/paystacktransverify/{cevareference}/{authval}")
        String urlparam = cevaref + "/" + authval;
        String url = "";

        try {
            url = SecurityLayer.beforeLogin(urlparam, UUID.randomUUID().toString(), "paystacktransverify", getContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Log.debug("nipservice", response.body());
                ((NewToBank) getActivity()).dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptBeforeLogin(obj, getContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        ResponseDialogs.successStatic(obj.optString(Constants.KEY_MSG), obj.optString("message"), getContext());

                    } else {
                        ResponseDialogs.warningDialogLovelyToFragment(getContext(), getString(R.string.error), getString(R.string.error_500), getBackFrag());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ResponseDialogs.warningDialogLovelyToFragment(getContext(), getString(R.string.error), getString(R.string.error_500), getBackFrag());

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                ((NewToBank) getActivity()).dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                ResponseDialogs.warningDialogLovelyToFragment(getContext(), getString(R.string.error), getString(R.string.error_500), getBackFrag());

                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    private void performCharge(Card card, String email, int amount, final String accesscode, final String cevaRef) {
        ((NewToBank) Objects.requireNonNull(getActivity())).showLoadingProgress();
        //create a Charge object
        Charge charge = new Charge();

        //set the card to charge
        charge.setCard(card);

        //call this method if you set a plan
        //charge.setPlan("PLN_yourplan");

        charge.setEmail(email); //dummy email address

        //charge.setAmount(amount); //test amount
        charge.setAccessCode(accesscode);

        PaystackSdk.chargeCard(getActivity(), charge, new Paystack.TransactionCallback() {

            @Override
            public void onSuccess(Transaction transaction) {
                ((NewToBank) Objects.requireNonNull(getActivity())).dismissProgress();
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
                String paymentReference = transaction.getReference();

                //Toast.makeText(getContext(), "Transaction Successful! payment reference: "
                //      + paymentReference, Toast.LENGTH_LONG).show();
                Log.debug("Transaction Successful! payment reference: " + paymentReference);
                verifyTransaction(cevaRef, accesscode);
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                ((NewToBank) Objects.requireNonNull(getActivity())).dismissProgress();
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //handle error here
                Log.debug(error.getMessage());
                ((NewToBank) Objects.requireNonNull(getActivity())).dismissProgress();
                ResponseDialogs.warningDialogLovelyToFragment(getContext(), getString(R.string.error), getString(R.string.error_500), getBackFrag());
            }
        });
    }

    private void showPaymentDialog(Context c, final String amount, final String email, final String accesscode, final String fee, final String total_amount, final String cevaRef) {
        PaystackSdk.setPublicKey(getString(R.string.kifunguu_cha_umma));
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        View mView = layoutInflaterAndroid.inflate(R.layout.card_entry_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
        alertDialogBuilderUserInput.setView(mView);

        final TextView amountDisplay = mView.findViewById(R.id.amountDisplay);
        final MaskedEditText cardNumber = mView.findViewById(R.id.cardNumber);
        final MaskedEditText expDate = mView.findViewById(R.id.expDate);
        final EditText cvv = mView.findViewById(R.id.cvv);
        final Button btnPay = mView.findViewById(R.id.btnPay);
        final Button btnCancel = mView.findViewById(R.id.btnCancel);

        String amtToShow = "Pay " + Constants.KEY_NAIRA + amount +
                " for new account opening. An additional fee of " +
                Constants.KEY_NAIRA + fee + " shall be charged. The total amount to be debited is " +
                Constants.KEY_NAIRA + total_amount;

        amountDisplay.setText(amtToShow);

        alertDialogBuilderUserInput
                .setCancelable(false);
                /*.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here
                        String amt = amount;


                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });*/

        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.cancel();
                goBack();
            }
        });
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String card = cardNumber.getUnmaskedText();
                String exp = expDate.getText().toString();
                final String cv = cvv.getText().toString();
                int month = 0;
                int year = 0;

                try {
                    String[] dates = exp.split("/");
                    month = Integer.parseInt(dates[0]);
                    year = Integer.parseInt(dates[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(card)) {
                    if (!TextUtils.isEmpty(exp)) {
                        if (!TextUtils.isEmpty(cv)) {
                            Card paycard = null;
                            if (BuildConfig.DEBUG && SecurityLayer.isDemo) {
                                paycard = new Card("4084084084084081", 11, 18, "408");
                            } else {
                                paycard = new Card(card, month, year, cv);
                            }
                            if (paycard.isValid()) {

                                performCharge(paycard, email, Integer.parseInt(amount), accesscode, cevaRef);
                                alertDialogAndroid.dismiss();
                            } else {
                                ResponseDialogs.warningStatic(getString(R.string.error), "The card supplied is invalid", getContext());
                            }
                        } else {
                            //((NewToBank) getActivity()).warningDialog("Enter your card cvv");
                            cvv.setError("Enter your card cvv");
                        }
                    } else {
                        //((NewToBank) getActivity()).warningDialog("Enter the expiry date of your card");
                        expDate.setError("Enter the expiry date of your card");
                    }
                } else {
                    //((NewToBank) getActivity()).warningDialog("Enter your card number");
                    cardNumber.setError("Enter your card number");
                }
            }
        });
        alertDialogAndroid.show();
    }

    private void showWarning(String message) {

        ((NewToBank) Objects.requireNonNull(getActivity())).warningDialog(message);
    }


    private void goBack() {
        Bundle bundle = new Bundle();
        bundle.putString(BankProductModel.KEY_TITLE, "Customer Details");
        bundle.putString(BankProductModel.KEY_NUMBER, "3");
        bundle.putString(BankProductModel.KEY_PRODUCT_CODE, session.getString(BankProductModel.KEY_PRODUCT_CODE));

        Fragment fragment = new CustomerDetails();
        fragment.setArguments(bundle);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.addToBackStack("customer details");
        transaction.commit();
    }

    private FragmentTransaction getBackFrag() {
        Bundle bundle = new Bundle();
        bundle.putString(BankProductModel.KEY_TITLE, "Customer Details");
        bundle.putString(BankProductModel.KEY_NUMBER, "3");
        bundle.putString(BankProductModel.KEY_PRODUCT_CODE, session.getString(BankProductModel.KEY_PRODUCT_CODE));

        Fragment fragment = new CustomerDetails();
        fragment.setArguments(bundle);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.addToBackStack("customer details");

        return transaction;
    }

}
