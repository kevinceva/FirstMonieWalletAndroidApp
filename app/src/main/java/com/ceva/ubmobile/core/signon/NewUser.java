package com.ceva.ubmobile.core.signon;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.SecurityQuestionsModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewUser extends BaseActivity {

    @BindView(R.id.password_fields)
    LinearLayout password_fields;
    @BindView(R.id.account_fields)
    LinearLayout account_fields;
    @BindView(R.id.general_info)
    LinearLayout general_info;
    @BindView(R.id.BVN)
    EditText BVN;
    @BindView(R.id.OTP)
    EditText OTP;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.swipeSelector)
    SwipeSelector mSwipeSelector;
    boolean isOTPEntered;
    String fname, lname, mname, dateOfBirth, phoneNumber, productCode, branchID;

    //ArrayList<String> usernames = new ArrayList<>();
    //String customermobile = null;
    UBNSession session;
    //@Path("/customeraccountopen/{fname}/{mname}/{lname}/{gender}/{dateOfBirth}/{mobno}/{productCode}/{initiatorID}/{branch}/{otp}")


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        ButterKnife.bind(this);
        setToolbarTitle("Account Validation");
        session = new UBNSession(this);
//setProducts();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.isNotNull(BVN.getText().toString())) {
                    if (isOTPEntered) {
                        if (Utility.isNotNull(OTP.getText().toString())) {
                            if (NetworkUtils.isConnected(NewUser.this)) {
                                // getOTP(BVN.getText().toString(), customermobile, OTP.getText().toString());
                            } else {
                                noInternetDialog();
                            }
                        } else {
                            ResponseDialogs.warningDialogLovely(NewUser.this, "Enter OTP", "Please enter the one time password sent via SMS");
                        }
                    } else {
                        if (NetworkUtils.isConnected(NewUser.this)) {
                            // validateAccount(BVN.getText().toString());
                            validateBVN(BVN.getText().toString());
                        } else {
                            noInternetDialog();
                        }
                        //showOTPField();
                    }

                } else {
                    ResponseDialogs.warningDialogLovely(NewUser.this, "Error", "Please enter your account number");
                }
            }
        });
    }

    private void setProducts() {
        title.setText("Product Selection");
        number.setText("2");
        mSwipeSelector.setVisibility(View.VISIBLE);
        account_fields.setVisibility(View.GONE);
        mSwipeSelector.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, "Union Gold", "Description for slide one."),
                new SwipeItem(1, "Union Silver", "Description for slide two."),
                new SwipeItem(2, "Union Bronze", "Description for slide three.")
        );
    }

    private void showOTPField() {
        isOTPEntered = true;
        password_fields.setVisibility(View.VISIBLE);
        account_fields.setVisibility(View.GONE);
    }

    private void validateBVN(String bvnNumber) {
//@Path("/customerbvnInfo/{bvn}")
        showLoadingProgress();

        String urlparam = "customerbvnInfo/" + bvnNumber;
        String data = null;

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparam);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        //@Path("/customeraccountopen/{fname}/{mname}/{lname}/{gender}/{dateOfBirth}/{mobno}/{productCode}/{initiatorID}/{branch}/{otp}")
//{"DateOfBirth":"20-MAY-78","EnrollmentBank":"32","MiddleName":"TOCHUKWU",
// "FirstName":"COLLINS","PhoneNumber":"8036010740","LastName":"OKPALAUGO"}

                        //customermobile = obj.optString("customermobileno");

                        dateOfBirth = obj.optString("DateOfBirth");
                        mname = obj.optString("MiddleName");
                        fname = obj.optString("FirstName");
                        phoneNumber = obj.optString("PhoneNumber");
                        lname = obj.optString("LastName");

                        if (mname == null) {
                            mname = "NA";
                        }
                        if (lname == null) {
                            lname = "NA";
                        }

                        String content = "Firstname: <b>" + fname + "</b><br/>";
                        content += "Middle name: <b>" + mname + "</b><br/>";
                        content += "Surname: <b>" + lname + "</b><br/>";
                        content += "Date of Birth: <b>" + dateOfBirth + "</b><br/>";
                        content += "Phone number: <b>" + phoneNumber;
                        //showOTPField();

                        new LovelyStandardDialog(NewUser.this)
                                .setTopColorRes(R.color.colorPrimary)
                                .setButtonsColorRes(R.color.midnight_blue)
                                .setIcon(R.drawable.ic_bulb_white)
                                .setTitle("Confirmation")
                                .setMessage(Html.fromHtml(content))
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Continue", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        setProducts();
                                    }
                                })
                                .show();
                    } else {
                        ResponseDialogs.warningDialogLovely(NewUser.this, "Error", obj.optString(Constants.KEY_MSG));
                    }

                } catch (Exception e) {
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

    private void getOTP(String BVN, String mobileNumber, String OTP) {
        //@Path("/otpvalidation/{actno}/{mobno}/{otp}")

        showLoadingProgress();

        String urlparam = "otpvalidation/" + BVN + "/" + mobileNumber + "/" + OTP;
        String data = null;

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparam);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("otpvalidation", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        onContinueClick();
                    } else {
                        ResponseDialogs.warningDialogLovely(NewUser.this, "Error", obj.optString(Constants.KEY_MSG));
                        //onContinueClick();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("otpvalidation", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    public void onContinueClick() {
        Intent intent = new Intent(this, UsernameSelection.class);

        session.setString(SecurityQuestionsModel.KEY_ACCOUNT, BVN.getText().toString());
        //session.setString(SecurityQuestionsModel.KEY_CUSTMOBILE, customermobile);
        // session.setStringArray(SecurityQuestionsModel.KEY_USERNAMES, usernames);

        startActivity(intent);
    }

}
