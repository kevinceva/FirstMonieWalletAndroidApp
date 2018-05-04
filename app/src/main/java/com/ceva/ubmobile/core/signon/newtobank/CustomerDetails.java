package com.ceva.ubmobile.core.signon.newtobank;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ceva.ubmobile.BuildConfig;
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
import com.ceva.ubmobile.utils.ImageUtils;
import com.ceva.ubmobile.utils.ScalingUtilities;
import com.github.pinball83.maskededittext.MaskedEditText;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;


import org.apache.commons.codec.binary.StringUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class CustomerDetails extends Fragment {
    public static final String KEY_INITIAL_AMOUNT = "amountyakwanza";
    public static final String KEY_EMAIL = "emailyapesa";
    public static final String KEY_ACCTYPE = "ainayaaccount";
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
    String productCode = "";
    UBNSession session;
    @BindView(R.id.BVN)
    EditText bvn;

    @BindView(R.id.gender)
    Spinner genderBtn;

    String gender = "F";

    @BindView(R.id.bvnTxt)
    TextView bvnTxt;
    @BindView(R.id.genderTxt)
    TextView genderTxt;
    String bvnEnding = "BVN";
    ArrayList<String> telcos = new ArrayList<>();
    ArrayAdapter telcoAdapter;
    @BindView(R.id.firstname)
    EditText firstName;
    @BindView(R.id.middlename)
    EditText middleName;
    @BindView(R.id.surname)
    EditText surname;
    @BindView(R.id.dateOfBirth)
    EditText dateOfBirth;
    @BindView(R.id.genderForOther)
    Spinner genderForOther;
    @BindView(R.id.otherFields)
    LinearLayout otherFields;
    boolean isOther = false;
    @BindView(R.id.initialDeposit)
    EditText initialDeposit;
    @BindView(R.id.email)
    EditText emailEdit;
    String firstname, lastname, middlename, dob;
    @BindView(R.id.genderForOtherView)
    LinearLayout genderForOtherView;


    public CustomerDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaystackSdk.initialize(getContext());
        if (getArguments() != null) {
            titleSt = getArguments().getString(BankProductModel.KEY_TITLE);
            numberSt = getArguments().getString(BankProductModel.KEY_NUMBER);
            productCode = getArguments().getString(BankProductModel.KEY_PRODUCT_CODE);
        }
        session = new UBNSession(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.ntb_fragment_customer_details, container, false);
        ButterKnife.bind(this, rootView);

        ((NewToBank) Objects.requireNonNull(getActivity())).getDateIntoEditText(dateOfBirth, getContext());
        title.setText(titleSt);
        number.setText(numberSt);
        otherFields.setVisibility(View.GONE);
        Log.debug("product code:" + productCode);
        if (productCode.contains("WA")) {
            bvnEnding = "Phone Number";
            bvnTxt.setText("Enter Your Phone Number");
            genderTxt.setText("Select Identification Method");
            telcoAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_row, telcos);
            genderBtn.setAdapter(telcoAdapter);
            genderForOtherView.setVisibility(View.VISIBLE);
            getTelcos();

        }
        genderBtn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = genderBtn.getSelectedItem().toString().charAt(0) + "";
                if (productCode.contains("WA")) {
                    if (i == 0) {
                        //Do nothing
                        showAirtelFields();
                    } else if (i == 1) {
                        showAccountFields();
                    } else if (i == 2) {
                        showBVNFields();
                    } else {
                        isOther = true;
                        otherFields.setVisibility(View.VISIBLE);
                        genderForOtherView.setVisibility(View.VISIBLE);
                    }
                } else {
                    otherFields.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                gender = "F";
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bvn.getText().toString().isEmpty()) {
                    ((NewToBank) getActivity()).warningDialog("Please enter your " + bvnEnding);
                } else {
                    String initial = initialDeposit.getText().toString();
                    String emailAdd = emailEdit.getText().toString();

                    if (!initial.isEmpty()) {
                        if (!emailAdd.isEmpty()) {
                            session.setString(KEY_INITIAL_AMOUNT, initial);
                            session.setString(KEY_EMAIL, emailAdd);
                            session.setString(KEY_ACCTYPE, genderBtn.getSelectedItem().toString());

                            if (NetworkUtils.isConnected(getContext())) {
                                /*if (BuildConfig.DEBUG && SecurityLayer.isDemo) {
                                    dummy();

                                } else {*/
                                    /*opratorList.put("Airtel");
                                    opratorList.put("UnionBankCustomer");
                                    opratorList.put("BVN");
                                    opratorList.put("Other");*/
                                if (NetworkUtils.isConnected(getContext())) {
                                    if (productCode.contains("WA")) {

                                        if (genderBtn.getSelectedItemPosition() == 0) {
                                            getCustInfoFromTelco(bvn.getText().toString(), genderBtn.getSelectedItem().toString());

                                        } else if (genderBtn.getSelectedItemPosition() == 1) {
                                            getCustInfoFromTelco(bvn.getText().toString(), genderBtn.getSelectedItem().toString());
                                        } else if (genderBtn.getSelectedItemPosition() == 2) {
                                            getBVNDetails(bvn.getText().toString(), genderForOther.getSelectedItem().toString().charAt(0) + "", "paystackcustomerwalletbvnInfo");
                                        } else {
                                            firstname = firstName.getText().toString();
                                            middlename = middleName.getText().toString();
                                            lastname = surname.getText().toString();
                                            dob = dateOfBirth.getText().toString();
                                            gender = genderForOther.getSelectedItem().toString().charAt(0) + "";

                                            dob = dob.replace("/", "-");

                                            if (!firstname.isEmpty()) {
                                                if (middlename.isEmpty()) {
                                                    middlename = "null";
                                                }
                                                if (!lastname.isEmpty()) {
                                                    if (!dob.isEmpty()) {
                                                        if (NetworkUtils.isConnected(getContext())) {
                                                            sendOTPForSelf(bvn.getText().toString(), firstname, middlename, lastname, dob, genderForOther.getSelectedItem().toString());
                                                        } else {
                                                            ((NewToBank) getActivity()).noInternetDialog();
                                                        }
                                                    } else {
                                                        dateOfBirth.setError("Select your date of birth");
                                                    }
                                                } else {
                                                    surname.setError("Please enter your surname");
                                                }
                                            } else {
                                                firstName.setError("Enter your first name");
                                            }

                                            //otherTelco(firstname, middlename, lastname, dob, bvn.getText().toString());
                                        }

                                    } else {

                                        //getBVNDetails(bvn.getText().toString(), gender, "paystackcustomerwalletbvnInfo");
                                        getBVNDetails(bvn.getText().toString(), gender, "customerbvnInfo");
                                    }
                                } else {
                                    ((NewToBank) getActivity()).noInternetDialog();
                                }
                                //}
                            }
                        } else {
                            emailEdit.setError("Please enter your email address");
                        }
                    } else {
                        initialDeposit.setError("Please enter amount you would like to deposit into this account");
                    }


                    /*if (BuildConfig.DEBUG && SecurityLayer.isDemo) {
                        dummy();

                    } else {
                        if (NetworkUtils.isConnected(getContext())) {
                            if (productCode.contains("WA")) {
                                getCustInfoFromTelco(bvn.getText().toString(), genderBtn.getSelectedItem().toString());
                            } else {
                                getBVNDetails(bvn.getText().toString(), gender);
                            }
                        } else {
                            ((NewToBank) getActivity()).noInternetDialog();
                        }
                    }*/
                }
            }
        });
        return rootView;
    }

    private void showBVNFields() {
        isOther = false;
        otherFields.setVisibility(View.GONE);
        bvnEnding = "BVN";
        bvnTxt.setText("Enter your " + bvnEnding);
        bvn.setHint("Enter your " + bvnEnding);
        genderForOtherView.setVisibility(View.GONE);
    }

    private void showAccountFields() {
        isOther = false;
        otherFields.setVisibility(View.GONE);
        bvnEnding = "Mobile Number";
        bvnTxt.setText("Enter your Registered " + bvnEnding);
        bvn.setHint("Enter your Registered " + bvnEnding);
        genderForOtherView.setVisibility(View.GONE);
    }

    private void showAirtelFields() {
        isOther = false;
        otherFields.setVisibility(View.GONE);
        bvnEnding = "Phone Number";
        bvnTxt.setText("Enter your " + bvnEnding);
        bvn.setHint("Enter your " + bvnEnding);
        genderForOtherView.setVisibility(View.GONE);
    }

    private void dummy() {
        Bundle bundle = new Bundle();

        session.setString(BankProductModel.KEY_FNAME, "Brian");
        session.setString(BankProductModel.KEY_MIDNAME, "Kiptoo");
        session.setString(BankProductModel.KEY_LNAME, "Towett");
        session.setString(BankProductModel.KEY_DOB, "03-03-1991");
        session.setString(BankProductModel.KEY_PHONE, "+254710584935");
        session.setString(BankProductModel.KEY_GENDER, gender);
        session.setString(BankProductModel.KEY_BVN, "+254710584935");
        session.setString(BankProductModel.KEY_BRANCHNAME, "NA");
        session.setString(BankProductModel.KEY_BRANCH_CODE, "NA");
        bundle.putString(BankProductModel.KEY_TITLE, "Confirm Details");
        bundle.putString(BankProductModel.KEY_NUMBER, "4");

        Fragment fragment = new AccountConfirmation();
        fragment.setArguments(bundle);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.addToBackStack("confirm details");
        transaction.commit();
    }

    private void otherTelco(String fname, String mname, String lname, String dob, String phone, String gender) {
        Bundle bundle = new Bundle();

        session.setString(BankProductModel.KEY_FNAME, fname);
        session.setString(BankProductModel.KEY_MIDNAME, mname);
        session.setString(BankProductModel.KEY_LNAME, lname);
        session.setString(BankProductModel.KEY_DOB, dob);
        session.setString(BankProductModel.KEY_PHONE, phone);
        session.setString(BankProductModel.KEY_GENDER, gender);
        session.setString(BankProductModel.KEY_BVN, phone);
        session.setString(BankProductModel.KEY_BRANCHNAME, "NA");
        session.setString(BankProductModel.KEY_BRANCH_CODE, "NA");
        bundle.putString(BankProductModel.KEY_TITLE, "Confirm Details");
        bundle.putString(BankProductModel.KEY_NUMBER, "4");

        Fragment fragment = new AccountConfirmation();
        fragment.setArguments(bundle);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = Objects.requireNonNull(manager).beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.addToBackStack("confirm details");
        transaction.commit();
    }


    private void showWarning(String message) {
        ((NewToBank) Objects.requireNonNull(getActivity())).warningDialog(message);
    }

    private void getTelcos() {
        ((NewToBank) getActivity()).showLoadingProgress();
        //@Path("/listoprator/{dummy}")
        String urlparam = "dmm";
        String url = "";

        try {
            url = SecurityLayer.beforeLogin(urlparam, UUID.randomUUID().toString(), "listaccountopentype", getContext());
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
                        telcos.clear();
                        String operators = obj.optString("listaccountopentype");
                        operators = operators.replace("\\", "");
                        operators = operators.replace("\"", "");
                        operators = operators.replace("[", "");
                        operators = operators.replace("]", "");
                        String[] ops = TextUtils.split(operators, ",");
                        Log.debug("stripped", operators);
                        telcos.addAll(Arrays.asList(ops));

                        telcoAdapter.notifyDataSetChanged();

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
                ((NewToBank) getActivity()).dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    private void getBVNDetails(final String bvn, final String gender, String endpoint) {
        ((NewToBank) Objects.requireNonNull(getActivity())).showLoadingProgress();
        //@Path("/customerbvnInfo/{bvn}")
        String urlparam = bvn;
        String url = "";

        try {
            url = SecurityLayer.beforeLogin(bvn, UUID.randomUUID().toString(), endpoint, getContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.debug("nipservice", response.body());
                ((NewToBank) getActivity()).dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptBeforeLogin(obj, getContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        session.setString(BankProductModel.KEY_FNAME, obj.optString(BankProductModel.KEY_FNAME));
                        session.setString(BankProductModel.KEY_MIDNAME, obj.optString(BankProductModel.KEY_MIDNAME));
                        session.setString(BankProductModel.KEY_LNAME, obj.optString(BankProductModel.KEY_LNAME));
                        session.setString(BankProductModel.KEY_DOB, obj.optString(BankProductModel.KEY_DOB));
                        session.setString(BankProductModel.KEY_PHONE, obj.optString(BankProductModel.KEY_PHONE));
                        session.setString(BankProductModel.KEY_GENDER, gender);
                        session.setString(BankProductModel.KEY_BVN, bvn);

                        String content = "First Name: " + obj.optString(BankProductModel.KEY_FNAME);
                        //content += "\nMiddle Name: " + obj.optString(BankProductModel.KEY_MIDNAME);
                        content += "\nSurname: " + obj.optString(BankProductModel.KEY_LNAME);
                        //content += "\nDate of Birth: " + obj.optString(BankProductModel.KEY_DOB);
                        //content += "\nPhone Number: 0" + obj.optString(BankProductModel.KEY_PHONE);

                        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_bulb_white, getResources());
                        new LovelyStandardDialog(getContext())
                                .setTopColor(ImageUtils.getColorByThemeAttr(getContext(),R.attr._ubnColorPrimaryDark,Color.BLUE))
                                .setButtonsColorRes(R.color.midnight_blue)
                                .setIcon(icon)
                                .setTitle("Are these your details?")
                                .setMessage(content)
                                .setNegativeButton("No", null)
                                .setPositiveButton("Yes, Proceed", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Bundle bundle = new Bundle();
                                        bundle.putString(BankProductModel.KEY_TITLE, "Select Branch");
                                        bundle.putString(BankProductModel.KEY_NUMBER, "4");

                                        Fragment fragment = new BranchSelection();
                                        fragment.setArguments(bundle);
                                        FragmentManager manager = getFragmentManager();
                                        FragmentTransaction transaction = manager.beginTransaction();
                                        transaction.replace(R.id.fragment, fragment);
                                        transaction.addToBackStack("select branch");
                                        transaction.commit();
                                    }
                                })
                                .show();

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
                ((NewToBank) getActivity()).dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }


    private void getCustInfoFromTelco(final String mobile, final String operator) {
        ((NewToBank) getActivity()).showLoadingProgress();
        //@Path("/fetchcustuserinfo/{mobileno}/{operator}")
        String urlparam = mobile + "/" + operator;
        String url = "";

        try {
            url = SecurityLayer.beforeLogin(urlparam, UUID.randomUUID().toString(), "fetchcustuserinfo", getContext());
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

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        session.setString(BankProductModel.KEY_FNAME, obj.optString("fname"));
                        session.setString(BankProductModel.KEY_MIDNAME, obj.optString("mname"));
                        session.setString(BankProductModel.KEY_LNAME, obj.optString("lname"));
                        session.setString(BankProductModel.KEY_DOB, obj.optString("dob"));
                        session.setString(BankProductModel.KEY_PHONE, obj.optString("mobileno"));
                        session.setString(BankProductModel.KEY_GENDER, obj.optString("gender"));
                        session.setString(BankProductModel.KEY_BVN, mobile);
                        session.setString(BankProductModel.KEY_BRANCHNAME, "NA");
                        session.setString(BankProductModel.KEY_BRANCH_CODE, "NA");

                        String content = "First Name: " + obj.optString("fname");
                        //content += "\nMiddle Name: " + obj.optString("mname");
                        content += "\nSurname: " + obj.optString("lname");
                        //content += "\nDate of Birth: " + obj.optString("dob");
                        //content += "\nPhone Number: " + mobile;
                        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_bulb_white, getResources());
                        new LovelyStandardDialog(getContext())
                                .setTopColor(ImageUtils.getColorByThemeAttr(getContext(),R.attr._ubnColorPrimaryDark, Color.BLUE))
                                .setButtonsColorRes(R.color.midnight_blue)
                                .setIcon(icon)
                                .setTitle("Are these your details?")
                                .setMessage(content)
                                .setNegativeButton("No", null)
                                .setPositiveButton("Yes, Proceed", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Bundle bundle = new Bundle();
                                        bundle.putString(BankProductModel.KEY_TITLE, "Confirm Details");
                                        bundle.putString(BankProductModel.KEY_NUMBER, "4");

                                        Fragment fragment = new AccountConfirmation();
                                        fragment.setArguments(bundle);
                                        FragmentManager manager = getFragmentManager();
                                        FragmentTransaction transaction = manager.beginTransaction();
                                        transaction.replace(R.id.fragment, fragment);
                                        transaction.addToBackStack("confirm details");
                                        transaction.commit();
                                    }
                                })
                                .show();


                        /*Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_bulb_white, getResources());
                        new LovelyStandardDialog(getContext())
                                .setTopColor(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                                .setButtonsColorRes(R.color.midnight_blue)
                                .setIcon(icon)
                                .setTitle("Are these your details?")
                                .setMessage(content)
                                .setNegativeButton("No", null)
                                .setPositiveButton("Yes, Proceed", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Bundle bundle = new Bundle();
                                        if (productCode.contains("WA")) {
                                            session.setString(BankProductModel.KEY_BRANCHNAME, "NA");
                                            session.setString(BankProductModel.KEY_BRANCH_CODE, "NA");
                                            bundle.putString(BankProductModel.KEY_TITLE, "Confirm Details");
                                            bundle.putString(BankProductModel.KEY_NUMBER, "4");

                                            Fragment fragment = new AccountConfirmation();
                                            fragment.setArguments(bundle);
                                            FragmentManager manager = getFragmentManager();
                                            FragmentTransaction transaction = manager.beginTransaction();
                                            transaction.replace(R.id.fragment, fragment);
                                            transaction.addToBackStack("confirm details");
                                            transaction.commit();
                                        } else {
                                            bundle.putString(BankProductModel.KEY_TITLE, "Select Branch");
                                            bundle.putString(BankProductModel.KEY_NUMBER, "4");

                                            Fragment fragment = new BranchSelection();
                                            fragment.setArguments(bundle);
                                            FragmentManager manager = getFragmentManager();
                                            FragmentTransaction transaction = manager.beginTransaction();
                                            transaction.replace(R.id.fragment, fragment);
                                            transaction.addToBackStack("select branch");
                                            transaction.commit();
                                        }

                                    }
                                })
                                .show();*/

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
                ((NewToBank) getActivity()).dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    private void sendOTPForSelf(final String mobile, final String firstname, final String middlename, final String lastname, final String dob, final String gender) {

        ((NewToBank) getActivity()).showLoadingProgress();
        //@Path("/sendotpforactopen/{mobileno}")
        String urlparam = mobile;
        String url = "";

        try {
            url = SecurityLayer.beforeLogin(urlparam, UUID.randomUUID().toString(), "sendotpforactopen", getContext());
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

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {

                        otherTelco(firstname, middlename, lastname, dob, mobile, gender);

                    } else {
                        if (BuildConfig.DEBUG && SecurityLayer.isDemo) {
                            otherTelco(firstname, middlename, lastname, dob, mobile, gender);
                        } else {
                            ResponseDialogs.warningDialogLovely(getContext(), "Error", obj.optString(Constants.KEY_MSG));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                ((NewToBank) getActivity()).dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

}
