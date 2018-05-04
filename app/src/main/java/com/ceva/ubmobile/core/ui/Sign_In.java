package com.ceva.ubmobile.core.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ceva.ubmobile.BuildConfig;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.UBNApplication;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.signon.ExistingUser;
import com.ceva.ubmobile.core.signon.PasswordSetting;
import com.ceva.ubmobile.models.AccountsModel;
import com.ceva.ubmobile.models.BankAccount;
import com.ceva.ubmobile.models.Beneficiary;
import com.ceva.ubmobile.models.SecurityQuestionsModel;
import com.ceva.ubmobile.models.ShowCaseModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityConstants;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.ImageUtils;
import com.ceva.ubmobile.utils.ScalingUtilities;
import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;
import com.github.ajalt.reprint.core.Reprint;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mattprecious.swirl.SwirlView;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import cn.pedant.SweetAlert.SweetAlertDialog;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Sign_In extends BaseActivity implements ImageUtils.ImageAttachmentListener {

    public static Boolean isVisible = false;
    UBNSession ubnSession;
    EditText username, password;
    SweetAlertDialog prog;
    @BindView(R.id.profile_image)
    ImageView profile_image;
    @BindView(R.id.pulsator)
    PulsatorLayout pulsator;
    ImageUtils imageutils;
    @BindView(R.id.btnHome)
    ImageView btnHome;
    @BindView(R.id.btnBio)
    Button btnBio;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    boolean running = false;
    private List<Beneficiary> beneficiaryList = new ArrayList<>();
    private Bitmap bitmap;
    private String file_name;
    @BindView(R.id.login_fields)
    ScrollView login_fields;
    @BindView(R.id.bioView)
    LinearLayout bioView;
    @BindView(R.id.loginSwitch)
    SwitchCompat loginSwitch;
    @BindView(R.id.bioSwitch)
    SwitchCompat bioSwitch;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @BindView(R.id.swirl)
    SwirlView swirl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_alt);
        ButterKnife.bind(this);
        //setToolbarTitle("Sign In");
        ubnSession = new UBNSession(this);
        imageutils = new ImageUtils(this);
        FirebaseAnalytics.getInstance(this).setCurrentScreen(this, getString(R.string.screen_sign_in), null);

        username = findViewById(R.id.jina);
        password = findViewById(R.id.siri);

        Button sign = findViewById(R.id.btnSignIn);
        if (Constants.NET_URL.contains(Constants.HOSTNAME)) {
            sign.setText("Sign In");
        } else if (Constants.NET_URL.contains(Constants.UAT_HOST)) {
            sign.setText("Sign In(UAT)");
        } else {
            sign.setText("Sign In(PREPROD)");
        }
        if (ubnSession.getUserName() != null) {

            if (UBNApplication.isBioPresent()) {
                fab.setVisibility(View.VISIBLE);
                //fab.setVisibility(View.GONE);
            } else {
                fab.setVisibility(View.GONE);
            }
        }
        btnBio.setVisibility(View.GONE);
        if (ubnSession.getUserName() != null) {
            username.setText(ubnSession.getUserName());
            username.setEnabled(false);
        }
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isConnected(getApplicationContext())) {
                    String userStr, passwordStr;
                    //startDashBoard();
                    userStr = username.getText().toString().trim();
                    passwordStr = password.getText().toString().trim();

                    if (TextUtils.isEmpty(userStr)) {
                        username.setError("Please enter your username");
                    } else {
                        if (TextUtils.isEmpty(passwordStr)) {
                            password.setError("Please enter your password");
                        } else {
                            //invokeGenLogin(userStr, passwordStr, Constants.KEY_LOGIN_ENDPOINT);
                            startDashBoard();
                        }
                    }

                } else {
                    noInternetDialog();
                }

            }
        });
        /*Button newuser = (Button) findViewById(R.id.btnNew);
        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isConnected(getApplicationContext())) {
                    startActivity(new Intent(getApplicationContext(), TermsAndConditions.class));
                } else {
                    warningDialog(getString(R.string.error_no_internet_connection));
                }

            }
        });*/

        if (ubnSession.getString(ImageUtils.KEY_PROFILE_IMAGE) == null) {
            pulsator.start();
        } else {
            File imgFile = new File(ubnSession.getString(ImageUtils.KEY_PROFILE_IMAGE));
            Uri mImageUri = Uri.fromFile(imgFile);

            if (imgFile.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(mImageUri.getPath());

                profile_image.setImageBitmap(myBitmap);

            }
        }

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageutils.imagepicker(0);
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Sign_In.this, LandingPage.class));
            }
        });
        ubnSession.setString(KEY_TIMER, String.valueOf(0));

        List<ShowCaseModel> views = new ArrayList<>();
        views.add(new ShowCaseModel(profile_image, "Tap this icon to upload picture"));
        //views.add(new ShowCaseModel(btnHome, "Tap \"Home\" to go back to the previous page"));
        presentShowcaseSequence(views, "sign_in");

        if (session.isBioEnabled() && UBNApplication.isBioPresent()) {
            login_fields.setVisibility(View.GONE);
            bioView.setVisibility(View.VISIBLE);
            startTraditionalBio();
            fab.setVisibility(View.GONE);
        } else {
            login_fields.setVisibility(View.VISIBLE);
            bioView.setVisibility(View.GONE);
            /*if (UBNApplication.isBioPresent()) {
                fab.setVisibility(View.VISIBLE);
            }*/
        }

        if (UBNApplication.isBioPresent() && !session.isBioEnabled() && ubnSession.getString(SecurityLayer.KEY_APP_ID) != null) {
            // bioSwitch.setVisibility(View.VISIBLE);
            bioSwitch.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        } else {
            bioSwitch.setVisibility(View.GONE);
        }

        loginSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    login_fields.setVisibility(View.VISIBLE);
                    bioView.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                    if (running) {
                        cancel();
                    }
                } else {
                    login_fields.setVisibility(View.GONE);
                    bioView.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);
                }
            }
        });

        bioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (NetworkUtils.isConnected(Sign_In.this)) {
                    enableOrDisableBio(b, session.getUserName());
                } else {
                    noInternetDialog();
                }
            }
        });

        fab.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            swirl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (running) {
                        cancel();
                    }
                    startTraditionalBio();
                }
            });
        }

        bioSwitch.setVisibility(View.GONE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageutils.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        this.bitmap = file;
        this.file_name = filename;
        profile_image.setImageBitmap(file);

        String path = Constants.KEY_IMAGE_PATH;

        ubnSession.setString(ImageUtils.KEY_PROFILE_IMAGE, path + filename);
        imageutils.createImage(file, filename, path, false);
        Log.debug("image_path", path + filename);
        pulsator.stop();

    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */

    private void invokeGenLogin(final String username, String password, String endpoint) {

        //showFullLoadingProgress();
        //showLoadingProgress();
        showAviProgressDialog();
        ubnSession.setString(KEY_TIMER, String.valueOf(0));
        //@Path("/userauthentication/{username}/{password}/{deviceip}/{imeino}")

        String params = username + "/" + password;//+"/"+ NetworkUtils.getImei(this)+"/"+NetworkUtils.getImei(this);
        String url = "";
        try {
            if (ubnSession.getString(SecurityLayer.KEY_APP_ID) == null) {
                Log.debug("first login");

                url = SecurityLayer.firstLogin(params, UUID.randomUUID().toString(), endpoint, this);
            } else if (ubnSession.getString("devicestatus") != null && !ubnSession.getString("devicestatus").equals("N")) {
                Log.debug("first login");
                Log.debug("stored device status: ", ubnSession.getString("devicestatus"));

                ubnSession.setString(SecurityLayer.KEY_APP_ID, null);
                url = SecurityLayer.firstLogin(params, UUID.randomUUID().toString(), endpoint, this);
            } else {
                Log.debug("general login");
                //url = SecurityLayer.firstLogin(params, UUID.randomUUID().toString(), endpoint, this);
                url = SecurityLayer.generalLogin(params, UUID.randomUUID().toString(), this, endpoint);
            }
        } catch (Exception e) {
            Log.Error(e);
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Log.debug("authentication", response.body());

                try {
                    JSONObject obj = new JSONObject(response.body());

                    if (ubnSession.getString(SecurityLayer.KEY_APP_ID) != null) {
                        obj = SecurityLayer.decryptGeneralLogin(obj, Sign_In.this);
                        // obj = SecurityLayer.decryptFirstTimeLogin(obj, Sign_In.this);
                    } else {
                        obj = SecurityLayer.decryptFirstTimeLogin(obj, Sign_In.this);
                    }

                    //Log.debug("ubnresponse", obj.toString());
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String status = obj.optString(SecurityQuestionsModel.KEY_STATUS);
                    ubnSession.setString(SecurityQuestionsModel.KEY_STATUS, status);
                    switch (responsecode) {
                        case "00":

                            if (!ubnSession.getBoolean(SecurityConstants.KEY_IS_GENERAL)) {
                                ubnSession.setBoolean(SecurityConstants.KEY_IS_GENERAL, true);
                            }
                            if (obj.has("campaign_note")) {
                                if (TextUtils.isEmpty(obj.optString("campaign_note"))) {
                                    ubnSession.setCampaignNote(null);
                                } else {
                                    ubnSession.setCampaignNote(obj.optString("campaign_note"));
                                }
                            }

                            ubnSession.setString("lastlogin", obj.optString("lastlogin"));
                            //capture account details
                            JSONArray balInfo = obj.getJSONArray("balInfo");
                            List<BankAccount> accounts = new ArrayList<BankAccount>();

                            for (int k = 0; k < balInfo.length(); k++) {
                                JSONObject balObj = balInfo.getJSONObject(k);

                                JSONArray custInfo = balObj.getJSONArray("custAcctInfo");
                                List<AccountsModel> accountsModelList = new ArrayList<AccountsModel>();
                                for (int j = 0; j < custInfo.length(); j++) {
                                    JSONObject objAcc = custInfo.getJSONObject(j);
                                    boolean isWallet = false;
                                    isWallet = balObj.optString("accounttype").equalsIgnoreCase("wallet");
                                    AccountsModel accountsModel = new AccountsModel(objAcc.optString("accountproduct"), objAcc.optString("accountNo"), objAcc.optString("availabletowithdraw"), balObj.optString("accountcurrency"), isWallet, balObj.optString("accountname"));
                                    accountsModelList.add(accountsModel);
                                }

                                BankAccount bankAccount = new BankAccount(balObj.optString("firstname"),
                                        balObj.optString("lastname"),
                                        balObj.optString("mobilenumber"),
                                        balObj.optString("accountnumber"),
                                        balObj.optString("accountcurrency"),
                                        Double.parseDouble("0.0"),
                                        balObj.optString("customeremail"),
                                        balObj.optString("accountstatus"),
                                        balObj.optString("accountname"),
                                        accountsModelList);

                                accounts.add(bankAccount);
                            }
                            ubnSession.setAccountsData(Constants.KEY_FULLINFO, accounts);
                            ubnSession.setUserName(username);
                            Log.debug("ubnaccountName", accounts.get(0).getAccountName());
                            ubnSession.setAccountName(accounts.get(0).getAccountName());
                            ubnSession.setPhoneNumber(accounts.get(0).getPhoneNumber());

                            if (obj.has("preapprovedloandata")) {
                                JSONObject loanob = obj.optJSONObject("preapprovedloandata");
                                if (loanob.has("eligibilityStatusResponse")) {
                                    if (loanob.optJSONObject("eligibilityStatusResponse").optString("responseCode").equals("00")) {
                                        session.setString(DashBoard.KEY_PREAPPROVED, loanob.optJSONArray("eligibilityResponse").toString());
                                    } else {
                                        session.setString(DashBoard.KEY_PREAPPROVED, null);
                                    }
                                } else {
                                    session.setString(DashBoard.KEY_PREAPPROVED, null);
                                }
                            } else {
                                session.setString(DashBoard.KEY_PREAPPROVED, null);
                            }

                            switch (ubnSession.getString(SecurityQuestionsModel.KEY_STATUS)) {
                                case "F":

                                    //dismissProgress();
                                    dismissAviProgress();

                                    if (BuildConfig.DEBUG) {
                                        //startDashBoard();
                                        startActivity(new Intent(Sign_In.this, ExistingUser.class));//set transaction pin
                                    } else {
                                        startActivity(new Intent(Sign_In.this, ExistingUser.class));//set transaction pin
                                    }
                                    break;
                                case "C":

                                    dismissProgress();
                                    startActivity(new Intent(Sign_In.this, PasswordSetting.class));//change default password to new password

                                    break;
                                default:
                                    String devstatus = obj.optString("devicerepstatus");
                                    ubnSession.setString("devicestatus", devstatus);

                                    switch (devstatus) {
                                        case "O":

                                            //dismissProgress();
                                            dismissAviProgress();
                                            Intent intent = new Intent(Sign_In.this, DeviceReplacementOTP.class);
                                            intent.putExtra("title", "Device Registration");
                                            intent.putExtra("status", "O");
                                            startActivity(intent);

                                            break;
                                        case "D":

                                            //dismissProgress();
                                            dismissAviProgress();
                                            //show pop up
                                            Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_bulb_white, getResources());
                                            new LovelyStandardDialog(Sign_In.this)
                                                    .setTopColor(ImageUtils.getColorByThemeAttr(getApplicationContext(),R.attr._ubnColorPrimaryDark, Color.BLUE))
                                                    .setButtonsColorRes(R.color.midnight_blue)
                                                    .setIcon(icon)
                                                    .setTitle("Device Replacement")
                                                    .setMessage("Do you want to register this device for use in mobile banking?")
                                                    .setNegativeButton("NO", null)
                                                    .setPositiveButton("YES", view -> {
                                                        Intent intent1 = new Intent(Sign_In.this, DeviceReplacementOTP.class);
                                                        intent1.putExtra("title", "Device Replacement");
                                                        intent1.putExtra("status", "D");
                                                        startActivity(intent1);
                                                    })
                                                    .show();

                                            break;
                                        case "N":
                                            startDashBoard();
                                            break;
                                        default:
                                            startDashBoard();
                                            break;
                                    }
                                    break;
                            }

                            break;
                        case "U":

                            //dismissProgress();
                            dismissAviProgress();
                            updateDialog(responsemessage);

                            break;
                        default:
                            if (BuildConfig.DEBUG && SecurityLayer.isDemo) {
                                startDashBoard();
                                //dismissAviProgress();
                                //warningDialog(responsemessage);
                            } else {
                                //startActivity(new Intent(Sign_In.this, PasswordSetting.class));
                                //dismissProgress();
                                dismissAviProgress();
                                warningDialog(responsemessage);
                                //startDashBoard();
                            }
                            break;
                    }

                } catch (Exception e) {

                    //dismissProgress();
                    dismissAviProgress();
                    warningDialog(getString(R.string.error_server));
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                //dismissProgress();
                dismissAviProgress();
                warningDialog(getString(R.string.error_server));
                com.ceva.ubmobile.core.ui.Log.debug("authentication", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }


    private void startDashBoard() {

        //dismissProgress();
        dismissAviProgress();
        startActivity(new Intent(this, DashBoard.class));
        //startActivity(new Intent(this, ExpandedDashBoard.class));
        ubnSession.setBoolean(UBNSession.KEY_LOGIN_STATUS, true);
        finish();
    }

    public void startPasswordRecovery(View v) {
        startActivity(new Intent(this, ForgotPassword.class));
        // finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LandingPage.class));
    }

    public void startTraditionalBio() {
        running = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            swirl.setState(SwirlView.State.ON, true);
        }
        fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_close_white_24dp));
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.coordinator_layout),
                "Place your finger on the sensor", Snackbar.LENGTH_SHORT);
        mySnackbar.setAction("CANCEL", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
        mySnackbar.show();
        Reprint.authenticate(new AuthenticationListener() {
            @Override
            public void onSuccess(int moduleTag) {
                showSuccess();
            }

            @Override
            public void onFailure(AuthenticationFailureReason failureReason, boolean fatal,
                                  CharSequence errorMessage, int moduleTag, int errorCode) {
                showError(failureReason, fatal, errorMessage, errorCode);
            }
        });
    }

    private void cancel() {
        //result.setText("Cancelled");
        running = false;
        fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fingerprint_white_24dp));
        Reprint.cancelAuthentication();
    }

    private void showSuccess() {
        //result.setText("Success");
        fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fingerprint_white_24dp));
        running = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            swirl.setState(SwirlView.State.ON, true);
        }
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.coordinator_layout),
                "Validated", Snackbar.LENGTH_SHORT);
        //mySnackbar.setAction(R.string.undo_string, new MyUndoListener());

        mySnackbar.show();
        //startDashBoard();
        if (NetworkUtils.isConnected(this)) {
            if (BuildConfig.DEBUG) {
                invokeGenLogin(session.getUserName(), SecurityLayer.getSHA256(NetworkUtils.getImei(this)), "biouserauthentication");
            } else {
                invokeGenLogin(session.getUserName(), SecurityLayer.getSHA256(session.getString(SecurityLayer.KEY_IMEI)), "biouserauthentication");
            }
        }
    }

    private void showError(AuthenticationFailureReason failureReason, boolean fatal,
                           CharSequence errorMessage, int errorCode) {
        //result.setText(errorMessage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            swirl.setState(SwirlView.State.ERROR, true);
        }
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.coordinator_layout),
                errorMessage, Snackbar.LENGTH_SHORT);
        //mySnackbar.setAction(R.string.undo_string, new MyUndoListener());
        mySnackbar.show();
        if (fatal) {

            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fingerprint_white_24dp));
            running = false;
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    swirl.setState(SwirlView.State.ON, true);
                }
            }
        }, 2000);

    }

    @Override
    public void onPause() {
        super.onPause();
        cancel();
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        if (running) {
            cancel();
        } else {
            login_fields.setVisibility(View.GONE);
            bioView.setVisibility(View.VISIBLE);
            startTraditionalBio();
            fab.setVisibility(View.GONE);
            //startTraditionalBio();
        }
    }

    private void enableOrDisableBio(final boolean state, String username) {
        //@Path("/registerBio/{userid}/{biostatus}")
        showLoadingProgress();
        String bioStatus = "NOBIO";
        if (state) {
            bioStatus = "BIO";
        } else {
            bioStatus = "NOBIO";
        }

        String params = username + "/" + bioStatus;
        String session_id = UUID.randomUUID().toString();
        String url = SecurityLayer.genURLCBC("registerBio", params, this);
        // String urlparam = "accountnovallidatin/" + SecurityLayer.generalEncrypt(accountNumber);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, Sign_In.this);

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        if (state) {
                            session.enableorDisableBio(true);
                            bioSwitch.setVisibility(View.GONE);
                        } else {
                            session.enableorDisableBio(false);
                        }
                        ResponseDialogs.successToActivity("Success", obj.optString(Constants.KEY_MSG), Sign_In.this, Sign_In.class, new Bundle());
                    } else {
                        ResponseDialogs.warningDialogLovely(Sign_In.this, "Error", obj.optString(Constants.KEY_MSG));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    warningDialog(getString(R.string.error_server));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                Log.debug("ubnaccountsfail", t.toString());
                warningDialog(getString(R.string.error_server));
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

}
