package com.ceva.ubmobile.core.ui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.BottomMenuAdapter;
import com.ceva.ubmobile.adapter.ConfirmAdapter;
import com.ceva.ubmobile.adapter.RecyclerItemClickListener;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.lifestyle.ProductDetails;
import com.ceva.ubmobile.core.lifestyle.deals.InsideOffers;
import com.ceva.ubmobile.core.lifestyle.doctors.DoctorAppoint;
import com.ceva.ubmobile.core.omniview.Investments;
import com.ceva.ubmobile.core.omniview.InviteFriend;
import com.ceva.ubmobile.core.omniview.ViewLoans;
import com.ceva.ubmobile.core.ui.bankservices.CashMenu;
import com.ceva.ubmobile.core.ui.beneficiaries.Beneficiaries;
import com.ceva.ubmobile.core.ui.billers.BillPayments;
import com.ceva.ubmobile.core.ui.billers.BillerCategories;
import com.ceva.ubmobile.core.ui.merchantpayments.NairabetRef;
import com.ceva.ubmobile.core.ui.widgets.CustomDialogFragment;
import com.ceva.ubmobile.core.ui.locator.RequestAgent;
import com.ceva.ubmobile.core.ui.merchantpayments.RevPayRef;
import com.ceva.ubmobile.core.ui.mvisa.MvisaMenu;
import com.ceva.ubmobile.core.ui.widgets.FingerPrintDialog;
import com.ceva.ubmobile.models.AccountsModel;
import com.ceva.ubmobile.models.BankAccount;
import com.ceva.ubmobile.models.Banks;
import com.ceva.ubmobile.models.BottomMenuItem;
import com.ceva.ubmobile.models.Branches;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.models.DealItem;
import com.ceva.ubmobile.models.SecurityQuestionsModel;
import com.ceva.ubmobile.models.ShowCaseModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.security.ScreenReceiver;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;
import com.ceva.ubmobile.utils.ScalingUtilities;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.zercomsystems.android.unionatmlocator.activities.MainActivityATM;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by brian on 21/09/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static final String KEY_TIMER = "timer";
    public static final long DISCONNECT_TIMEOUT = 60000;//3 minutes
    public static final int YOUR_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 700001;
    public boolean isAccountRefreshed = false;
    public List<ConfirmModel> confirmItems = new ArrayList<>();
    public boolean isTransactionComplete = false;
    public String HTTP_DATA = null;
    public EditText transaction_pin_base;
    public Button buttonClose_base;
    public DealItem dealItem;
    public int LIFE_COUNT = 0;
    public FirebaseAnalytics mFirebaseAnalytics;
    public View rootView;
    @Nullable
    @BindView(R.id.confirm_fields)
    protected RelativeLayout confirm_fields;
    @Nullable
    @BindView(R.id.page_fields)
    protected LinearLayout page_fields;
    @Nullable
    @BindView(R.id.token_fields)
    protected LinearLayout token_auth;
    protected UBNSession session;
    List<BottomMenuItem> menuItemList = new ArrayList<BottomMenuItem>();
    BottomMenuAdapter menuAdapter;
    RecyclerView menuView;
    LinearLayoutManager layoutManager;
    RecyclerView confirmationRecyler;
    ConfirmAdapter confirmAdapter;
    Button close;
    ProgressBar transactionProgress;
    int STATUS_SUCCESS = 0;
    int STATUS_FAIL = 1;
    TextView transactionMessage;
    ImageView transactionSuccessFailImage;
    ScreenReceiver screenReceiver;
    //IntentFilter filter;
    CustomDialogFragment loadingFragmentDialog;
    private LovelyProgressDialog mProgressDialog;
    private ACProgressFlower fullPageDialog;
    private List<String> accountNumbers = new ArrayList<String>();
    private Timer timer;
    private long lastInteractionTime;
    private long previousTime = 0;

    public static boolean isLocationServicesAvailable(Context context) {
        int locationMode = 0;
        String locationProviders;
        boolean isAvailable = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            isAvailable = (locationMode != Settings.Secure.LOCATION_MODE_OFF);
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            isAvailable = !TextUtils.isEmpty(locationProviders);
        }

        boolean coarsePermissionCheck = (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        boolean finePermissionCheck = (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        return isAvailable && (coarsePermissionCheck || finePermissionCheck);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        MultiDex.install(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // previousTime = (new Date().getTime()) / 1000;
        //Log.debug("ubnreceiver timer diff on start" + previousTime);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeButtonEnabled(true);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        rootView = getWindow().getDecorView().getRootView();

        //screenReceiver = new ScreenReceiver();  // creating receive SCREEN_OFF and SCREEN_ON broadcast msgs from the device.

        session = new UBNSession(getApplicationContext());
        if (session.getBoolean(UBNSession.KEY_LOGIN_STATUS)) {
            Log.debug("LOGIN STATUS", " LOGGED IN");
        } else {
            Log.debug("LOGIN STATUS", " LOGGED OUT");
        }

        /*if (session.getBoolean(UBNSession.KEY_APP_CLOSE)) {
            // if (session.getBoolean(UBNSession.KEY_LOGIN_STATUS)) {
            try {
                session.setBoolean(UBNSession.KEY_APP_CLOSE, false);
                session.setBoolean(UBNSession.KEY_LOGIN_STATUS, false);
                finish();
                System.exit(0);
            } catch (Exception e) {
                Log.Error(e);
            }

            // }
        }*/

    }

    /*public static Handler disconnectHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    public Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect
            Log.debug("timer logging out");
            UBNSession session = new UBNSession(getApplicationContext());
            if (session.getBoolean(UBNSession.KEY_LOGIN_STATUS)) {
                session.setBoolean(UBNSession.KEY_APP_CLOSE, true);
                session.setBoolean(UBNSession.KEY_LOGIN_STATUS, false);
                //Toast.makeText(getApplicationContext(), "You have been logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }

        }
    };

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        Log.debug("resetting timer");
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer() {
        Log.debug("stop timer");
        disconnectHandler.removeCallbacks(disconnectCallback);
    }*/
    /*public void startUserInactivityDetectThread() {
        Log.debug("ubnreceiver starting thread");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.debug("ubnreceiver running thread");
                try {
                    while (true) {
                        double last = getLastInteractionTime();
                        //Log.debug("ubnreceiver looping "+last);
                        try {
                            Thread.sleep(5000); // checks every 5sec for inactivity
                        } catch (InterruptedException e) {
                            Log.debug(e.toString());
                        }
                        if (screenReceiver.isScreenOff || last > 360) {
                            //...... means USER has been INACTIVE over a period of
                            // and you do your stuff like log the user out
                            //unregisterReceiver(screenReceiver);
                            //screenReceiver.startReceiver();
                            Log.debug("UBNReceiver screen inactivity called");
                            Log.debug("logging off...");


                            //unregisterReceiver(screenReceiver);
                            UBNSession session = new UBNSession(getApplicationContext());
                            if (session.getBoolean(UBNSession.KEY_LOGIN_STATUS)) {
                                PackageManager pm = getApplicationContext().getPackageManager();
                                ComponentName componentName = new ComponentName(BaseActivity.this, ScreenReceiver.class);
                                pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                        PackageManager.DONT_KILL_APP);
                                session.setBoolean(UBNSession.KEY_APP_CLOSE, true);
                                session.setBoolean(UBNSession.KEY_LOGIN_STATUS, false);
                                //Toast.makeText(getApplicationContext(), "You have been logged out", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("EXIT", true);
                                startActivity(intent);
                                finish();
                                System.exit(0);
                                Log.debug("UBNReceiver stopped");
                            }


                        }
                        long secs = (new Date().getTime()) / 1000;
                        long diff = secs - previousTime;

                        setLastInteractionTime(diff);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.debug("ubnreceiver error");
                }
            }
        }).start();
    }

    public long getLastInteractionTime() {
        return lastInteractionTime;
    }

    public long setLastInteractionTime(long lastInteractionTime) {
        //Log.debug("ubnreceiver last interaction"+lastInteractionTime);
        return this.lastInteractionTime = lastInteractionTime;

    }*/
    public void checkLoginState() {
        Log.debug("calling logged state");
        UBNSession session = new UBNSession(this);
        if (session.getBoolean(UBNSession.KEY_LOGIN_STATUS)) {
            long elapsed = Long.parseLong(session.getString(KEY_TIMER));
            if (elapsed > 0) {
                long secs = (new Date().getTime()) / 1000;
                long diff = 0;
                if (secs >= elapsed) {
                    diff = secs - elapsed;
                    Log.debug("logged time diff seconds " + diff);
                    if (diff > 300) {

                        // After logout redirect user to Loing Activity
                        Log.debug("Logging user out...");
                        session.setBoolean(UBNSession.KEY_APP_CLOSE, true);
                        session.setBoolean(UBNSession.KEY_LOGIN_STATUS, false);
                        Toast.makeText(getApplicationContext(), "You have been logged out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //intent.putExtra("EXIT", true);
                        startActivity(intent);
                        //finish();
                        //System.exit(0);
                    }
                }
            }
        }
    }

    @Override
    public void onUserInteraction() {

        try {
            checkLoginState();
        } catch (Exception e) {
            Log.Error(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            checkLoginState();

        } catch (Exception e) {
            Log.Error(e);
        }
    }

   /*@Override
    public void onPause() {
        super.onPause();

        timer = new Timer();
        Log.debug("timermaneno", "Invoking logout timer");
        LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
       // timer.schedule(logoutTimeTask, 300000); //auto logout in 5 minutes
        timer.schedule(logoutTimeTask, DISCONNECT_TIMEOUT); //auto logout in 10sec
    }
    @Override
    public void onResume() {
        super.onResume();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        Log.debug("Main", "cancel timer on resume");
    }
    @Override
    public void onUserInteraction() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        Log.debug("Main", "cancel timer user interaction");
    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Log.debug("timer logging out");
            UBNSession session = new UBNSession(getApplicationContext());
            if (session.getBoolean(UBNSession.KEY_LOGIN_STATUS)) {
                session.setBoolean(UBNSession.KEY_APP_CLOSE, true);
                session.setBoolean(UBNSession.KEY_LOGIN_STATUS, false);
                //Toast.makeText(getApplicationContext(), "You have been logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        }
    }*/

    @Override
    public void onPause() {
        super.onPause();
        try {
            UBNSession session = new UBNSession(this);
            long secs = (new Date().getTime()) / 1000;
            Log.debug("Seconds Logged", Long.toString(secs));
            session.setString(KEY_TIMER, String.valueOf(secs));

        } catch (Exception e) {
            Log.Error(e);
        }
    }

    public void initializeconfirm() {
        transactionProgress = findViewById(R.id.transactionProgressBar);
        confirm_fields = findViewById(R.id.confirm_fields);
        page_fields = findViewById(R.id.page_fields);
        token_auth = findViewById(R.id.token_auth);
        confirmationRecyler = findViewById(R.id.confirm_list);
        confirmAdapter = new ConfirmAdapter(confirmItems, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        confirmationRecyler.setLayoutManager(mLayoutManager);
        confirmationRecyler.setItemAnimator(new DefaultItemAnimator());
        confirmationRecyler.setAdapter(confirmAdapter);
        close = findViewById(R.id.closeButton);
        transactionMessage = findViewById(R.id.transactionMessage);
        transactionSuccessFailImage = findViewById(R.id.transaction_success_fail);

        transactionMessage.setVisibility(View.GONE);
        transactionProgress.setVisibility(View.GONE);
        token_auth.setVisibility(View.GONE);

    }

    public void showAuthentication() {

    }

    public void showFancyConfirm() {
        Utility.hideKeyboard(this);
        ButterKnife.bind(this);
        confirm_fields.setVisibility(View.VISIBLE);
        page_fields.setVisibility(View.GONE);
    }

    public void hideFancyConfirm() {
        confirm_fields.setVisibility(View.GONE);
        page_fields.setVisibility(View.VISIBLE);
    }

    public void showConfirmProgress() {
        transactionMessage.setVisibility(View.VISIBLE);
        transactionMessage.setText("Processing request");
        transactionProgress.setVisibility(View.VISIBLE);
    }


  /*  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);

        // Get the SearchView and set the searchable configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            finish();

        } else if (itemId == R.id.menu_settings) {
            //startActivity(new Intent(this, SettingsActivity.class));

        } else if (itemId == R.id.menu_search) {
            onSearchRequested();

        }

        return super.onOptionsItemSelected(item);
    }*/

    public void showTransactionComplete(int status, String message) {
        isTransactionComplete = true;
        if (status == STATUS_SUCCESS) {
            transactionSuccessFailImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_blue));
        } else {
            transactionSuccessFailImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_fail_red));

        }
        transactionMessage.setText(message);

        transactionProgress.setVisibility(View.GONE);
        transactionMessage.setVisibility(View.VISIBLE);
        transactionSuccessFailImage.setVisibility(View.VISIBLE);

    }

    public void showLoadingProgress() {
        /*if (mProgressDialog != null)
            dismissProgress();

        mProgressDialog = new LovelyProgressDialog(this);

        Bitmap unscaledBitmap = ScalingUtilities.decodeResource(getResources(), R.drawable.union_horse_white,
                400, 400, ScalingUtilities.ScalingLogic.CROP);

        // Part 2: Scale image
        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, 400,
                400, ScalingUtilities.ScalingLogic.CROP);
        unscaledBitmap.recycle();
        try {
            mProgressDialog.setIcon(scaledBitmap)
                    .setTitle("Processing Request")
                    .setTitleGravity(Gravity.CENTER)
                    .setTopColorRes(R.color.colorPrimary)
                    .show();
        } catch (Exception e) {

        }*/
        showAviProgressDialog();
    }

    public void showFullLoadingProgress() {
        if (fullPageDialog != null && fullPageDialog.isShowing())
            dismissFullProgress();

        fullPageDialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.DKGRAY).build();
        fullPageDialog.show();
    }

    public void dismissFullProgress() {
        if (fullPageDialog != null && fullPageDialog.isShowing()) {
            fullPageDialog.dismiss();
            fullPageDialog = null;
        }
    }

    public void showLoadingProgress(String message) {
        if (mProgressDialog != null)
            dismissProgress();

        mProgressDialog = new LovelyProgressDialog(this);

        Bitmap unscaledBitmap = ScalingUtilities.decodeResource(getResources(), R.drawable.union_horse_white,
                400, 400, ScalingUtilities.ScalingLogic.CROP);

        // Part 2: Scale image
        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, 400,
                400, ScalingUtilities.ScalingLogic.CROP);
        unscaledBitmap.recycle();

        mProgressDialog.setIcon(scaledBitmap)
                .setTitle(message)
                .setTitleGravity(Gravity.CENTER)
                .setTopColorRes(R.color.colorPrimary)
                .show();
    }

    public void showLoadingProgress(String message, Call call) {
        if (message == null) {
            message = "Processing Request";
        }
        if (mProgressDialog != null)
            dismissProgress();

        mProgressDialog = new LovelyProgressDialog(this);

        Bitmap unscaledBitmap = ScalingUtilities.decodeResource(getResources(), R.drawable.union_horse_white,
                400, 400, ScalingUtilities.ScalingLogic.CROP);

        // Part 2: Scale image
        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, 400,
                400, ScalingUtilities.ScalingLogic.CROP);
        unscaledBitmap.recycle();
        mProgressDialog.setIcon(scaledBitmap)
                .setTitle(message)
                .setTitleGravity(Gravity.CENTER)
                .setTopColorRes(R.color.colorPrimary)
                .show();
    }

    protected void runButtons() {

    }

    public void openSavedBeneficiaries() {
        Intent intent = new Intent(this, TransactionController.class);
        intent.putExtra(Constants.KEY_CONTROL_POS, Constants.KEY_TX_BEN);
        intent.putExtra(Constants.KEY_CONTROL_TITLE, "Manage Beneficiaries");
        startActivity(intent);
    }

    public void onPayClick(View v) {
        Log.debug("clicked", "got clicked");
        startActivity(new Intent(BaseActivity.this, BillPayments.class));
    }

    public void onAccountClick(View v) {
        startActivity(new Intent(BaseActivity.this, DashBoard.class));
    }

    public void onTransfersClick(View v) {
        startActivity(new Intent(BaseActivity.this, Transfers.class));
    }

    public void onHelpClick(View v) {
        startActivity(new Intent(BaseActivity.this, MvisaMenu.class));
    }

    public void onComingSoon(View v) {
        startActivity(new Intent(BaseActivity.this, ComingSoon.class));
    }

    public void dismissProgress() {
        /*if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }*/
        dismissAviProgress();
    }

    protected void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } catch (Exception e) {
            Log.Error(e);
        }

    }

    protected void BottomMenu() {
        menuView = findViewById(R.id.rvMenu);
        menuAdapter = new BottomMenuAdapter(menuItemList, this);
        layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        menuView.setLayoutManager(layoutManager);
        menuView.setAdapter(menuAdapter);

//Send money > airtime recharge > pay bills > cardless withdrawal > Manage Beneficiaries > Investments > block card  > help
        BottomMenuItem item = new BottomMenuItem("Home", R.drawable.ic_wallet_white);
        menuItemList.add(item);
        item = new BottomMenuItem("Send Money", R.drawable.ic_transfers_white);
        menuItemList.add(item);
        item = new BottomMenuItem("Airtime Recharge", R.drawable.ic_phonehand_white);
        menuItemList.add(item);

        item = new BottomMenuItem("Pay Bills", R.drawable.ic_wallet_white);
        menuItemList.add(item);

        item = new BottomMenuItem("Cardless Withdrawal", R.drawable.ic_pay_white);
        menuItemList.add(item);

        item = new BottomMenuItem("Manage Beneficiaries", R.drawable.ic_people_white);
        menuItemList.add(item);

        item = new BottomMenuItem("Invite Friend", R.drawable.ic_invite_friend);
        menuItemList.add(item);

        if (Constants.NET_URL.contains(Constants.UAT_HOST)) {

            item = new BottomMenuItem("Investments", R.drawable.ic_help_white);
            // menuItemList.add(item);
            item = new BottomMenuItem("My Loans", R.drawable.ic_help_white);
            //menuItemList.add(item);
            item = new BottomMenuItem("Lifestyle", R.drawable.ic_shopping_cart_white);
            menuItemList.add(item);
            item = new BottomMenuItem("Doctor Appointments", R.drawable.ic_nurse_white);
            // menuItemList.add(item);

            item = new BottomMenuItem("Help", R.drawable.ic_help_white);
            // menuItemList.add(item);
            item = new BottomMenuItem("Flight Booking", R.drawable.ic_help_white);
            //menuItemList.add(item);
        }
        item = new BottomMenuItem("Request Agent", R.drawable.ic_dsa_white);
        menuItemList.add(item);

        item = new BottomMenuItem("LASG RevPay", R.drawable.ic_pay_white);
        menuItemList.add(item);

        item = new BottomMenuItem("Nairabet", R.drawable.ic_pay_white);
        menuItemList.add(item);

        menuAdapter.notifyDataSetChanged();

        menuView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Class mclass = null;
                        Bundle bundle = new Bundle();

                        BottomMenuItem menu = menuItemList.get(position);

                        String p = menu.getTitle();

                        if (p.equals("Home")) {
                            mclass = DashBoard.class;
                        } else if (p.equals("Airtime Recharge")) {
                            mclass = AirtimeTopUp.class;
                        } else if (p.equals("Send Money")) {
                            mclass = Transfers.class;
                        } else if (p.equals("Pay Bills")) {
                            mclass = BillerCategories.class;
                        } else if (p.equals("Cardless Withdrawal")) {
                            mclass = CashMenu.class;
                        } else if (p.equals("Manage Beneficiaries")) {
                            mclass = Beneficiaries.class;
                        } else if (p.equals("Investments")) {
                            mclass = Investments.class;
                        } else if (p.equals("My Loans")) {
                            mclass = ViewLoans.class;
                        } else if (p.equals("Doctor Appointments")) {
                            mclass = DoctorAppoint.class;
                        } else if (p.equals("Request Agent")) {
                            mclass = RequestAgent.class;
                        } else if (p.equals("Lifestyle")) {
                            mclass = InsideOffers.class;
                        } else if (p.equals("Product")) {
                            mclass = ProductDetails.class;
                        } else if (p.equals("Invite Friend")) {
                            mclass = InviteFriend.class;
                        } else if (p.equals("LASG RevPay")) {
                            mclass = RevPayRef.class;
                        } else if (p.equals("Nairabet")) {
                            mclass = NairabetRef.class;
                        }

                        Intent intent = new Intent(getApplicationContext(), mclass);
                        startActivity(intent);

                    }
                })
        );
        ImageButton next = findViewById(R.id.btn_right);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuView.getLayoutManager().scrollToPosition(layoutManager.findLastVisibleItemPosition() + 1);
            }
        });
        ImageButton prev = findViewById(R.id.btn_left);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuView.getLayoutManager().scrollToPosition(layoutManager.findFirstVisibleItemPosition() - 1);
            }
        });

        /*case R.id.next:
        mRecyclerView.getLayoutManager().scrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
        break;
        case R.id.pre:
        mRecyclerView.getLayoutManager().scrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() - 1);
        break;*/

    }

    protected void setToolbarTitle(String title) {
        initializeToolbar();

        TextView text = findViewById(R.id.main_toolbar_title);
        if (title.toLowerCase().contains("atm")) {
            title = "ATM + Branch Locator";
            text.setText(title);
        } else {
            text.setText(Utility.camelCase(title));
        }

        if (findViewById(R.id.bottom_menu) != null) {
            BottomMenu();
        }
    }

    protected void hideSmoothProgress() {
        /*SmoothProgressBar sp = (SmoothProgressBar) findViewById(R.id.smooth_progress);
        sp.setVisibility(View.GONE);*/
    }

    protected void showSmoothProgress() {
        /*SmoothProgressBar sp = (SmoothProgressBar) findViewById(R.id.smooth_progress);
        sp.setVisibility(View.VISIBLE);*/
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        //Snackbar.make(rootView, "hello", Snackbar.LENGTH_LONG).setActionTextColor(getResources().getColor(R.color.white)).show();
    }

    protected void showAlert(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    public void warningDialog(String message) {

        ResponseDialogs.failStatic(getString(R.string.app_name), message, this);

    }

    public void noInternetDialog() {
        ResponseDialogs.noInternet(this);
    }

    protected void fetchBanks(final String username, final String service) {
        //final SweetAlertDialog prog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE).setTitleText(getString(R.string.label_pleasewait)).setContentText("Loading...");
        //prog.show();
        //showLoadingProgress();
        final UBNSession session = new UBNSession(this);
        final String params = username + "/" + service;
        String url = SecurityLayer.genURLCBC(Constants.KEY_FETCHBANKSORBRANCHES_ENDPOINT, params, getApplicationContext());
        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //dismissProgress();
                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    Log.debug("banksstart", "========================");
                    if (responsecode.equals("00")) {
                        if (service.equals("LOAD_BANKS")) {
                            JSONArray array = obj.getJSONArray(Banks.BK_KEY_BANKSDATA);
                            List<Banks> banksList = new ArrayList<Banks>();
                            for (int k = 0; k < array.length(); k++) {
                                JSONObject bankObj = array.getJSONObject(k);
                                Banks bank = new Banks(bankObj.optString(Banks.BK_KEY_NIBSSCODE), bankObj.optString(Banks.BK_KEY_CBNBANKCODE), bankObj.optString(Banks.BK_KEY_BANKNAME), bankObj.optString(Banks.BK_KEY_SHORTNAME));
                                banksList.add(bank);
                            }
                            session.setBanks(banksList);

                            if (session.getBranches() == null) {
                                fetchBanks(username, Branches.BR_KEY_BRANCHES_PARAM);
                            }

                        } else {
                            JSONArray array = obj.getJSONArray("branchesdata");
                            List<Branches> branchesList = new ArrayList<Branches>();
                            for (int k = 0; k < array.length(); k++) {
                                JSONObject branchObj = array.getJSONObject(k);
                                Branches branch = new Branches(branchObj.optString(Branches.BR_KEY_BRANCHCODE), branchObj.optString(Branches.BR_KEY_BRANCHNAME));
                                branchesList.add(branch);
                            }
                            session.setBranches(branchesList);

                        }

                        Log.debug("banksend", "========================");
                  /*  for (int j = 0; j < k; j++) {
                        Beneficiary ben = new Beneficiary(list.get(j).getName(), list.get(j).getAccountNumber(), list.get(j).getBank(), "");
                        com.ceva.ubmobile.core.ui.Log.debug("added" + list.get(j).getName());
                        beneficiaryList.add(ben);
                    }*/
                        //mAdapter.notifyDataSetChanged();
                        //ubnSession.setAccountsData(Constants.KEY_FULLINFO, accounts);
                        // com.ceva.ubmobile.core.ui.Log.debug("total benefs", beneficiaryList.size() + "");
                        // ubnSession.setBeneficiary(beneficiaryList);
                        //prog.dismiss();

                    } else {
                        // prog.dismiss();
                        // ResponseDialogs.warningStatic(context.getString(R.string.error),responsemessage,context);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    Log.Error(e);
                    SecurityLayer.generateToken(getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                //dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                SecurityLayer.generateToken(getApplicationContext());
                showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

    protected void formatDateEditText(Calendar myCalendar, EditText editText) {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        editText.setText(sdf.format(myCalendar.getTime()));
    }

    protected String formatDate(Calendar myCalendar) {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        return sdf.format(myCalendar.getTime());
    }

    protected String formatDate(Calendar myCalendar, String myFormat) {

        //String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        return sdf.format(myCalendar.getTime());
    }

    public void getDateIntoEditText(final EditText editText, final Context context) {
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
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void getDateTextView(final TextView textView, final Context context) {
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                view.setMaxDate(System.currentTimeMillis());
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //formatDateEditText(myCalendar, textView);
                textView.setText(formatDate(myCalendar));
            }

        };

        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void getDateTextView(final TextView textView, final Context context, final String dateFormat) {
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                view.setMaxDate(System.currentTimeMillis());
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //formatDateEditText(myCalendar, textView);
                textView.setText(formatDate(myCalendar, dateFormat));
            }

        };

        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void showTokenField() {
        LinearLayout ln = findViewById(R.id.token_fields);
        ln.setVisibility(View.VISIBLE);
    }

    public void hideTokenField() {
        LinearLayout ln = findViewById(R.id.token_fields);
        ln.setVisibility(View.GONE);
    }

    public void showTransactionPin() {
        LinearLayout ln = findViewById(R.id.transactionpin_fields);
        transaction_pin_base = findViewById(R.id.tran_pin);
        buttonClose_base = findViewById(R.id.closeButton);
        buttonClose_base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ln.setVisibility(View.VISIBLE);
    }

    public void hideTransactionPin() {
        LinearLayout ln = findViewById(R.id.transactionpin_fields);
        ln.setVisibility(View.GONE);
    }

    public void DisableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
    }

    public void EnableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setEnabled(true);
    }

    public void updateBalances(String username) {
        //final SweetAlertDialog prog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Simpler Smarter Bank").setContentText("Loading...");
        //prog.setCustomImage((R.mipmap.ic_launcher));
        //prog.show();
        showLoadingProgress();
        String urlparam = username;
        String url = SecurityLayer.genURLCBC(Constants.KEY_BALINQUIRY_ENDPOINT, urlparam, this);
        String data = null;

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("authentication", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    Log.debug("ubnresponse", response.body());
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String status = obj.optString(SecurityQuestionsModel.KEY_STATUS);

                    if (responsecode.equals("00")) {
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
                                isWallet = objAcc.optString("accounttype").equalsIgnoreCase("wallet");
                                AccountsModel accountsModel = new AccountsModel(objAcc.optString("accountproduct"), objAcc.optString("accountNo"), objAcc.optString("availabletowithdraw"), balObj.optString("accountcurrency"), isWallet, balObj.optString("accountname"));
                                accountsModelList.add(accountsModel);
                            }

                            BankAccount bankAccount = new BankAccount(balObj.optString("firstname"),
                                    balObj.optString("lastname"),
                                    balObj.optString("mobilenumber"),
                                    balObj.optString("accountnumber"),
                                    balObj.optString("accountcurrency"),
                                    Double.parseDouble(balObj.optString("transferlimit")),
                                    balObj.optString("customeremail"),
                                    balObj.optString("accountstatus"),
                                    balObj.optString("accountname"),
                                    accountsModelList);

                            accounts.add(bankAccount);
                        }
                        new UBNSession(getApplicationContext()).setAccountsData(Constants.KEY_FULLINFO, accounts);
                        showToast("Accounts updated...");
                        isAccountRefreshed = true;

                    } else {
                        //startActivity(new Intent(Sign_In.this, PasswordSetting.class));
                        warningDialog(responsemessage);
                        //startDashBoard();
                    }

                } catch (Exception e) {
                    SecurityLayer.generateToken(getApplicationContext());
                    warningDialog(getString(R.string.error_server));
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                warningDialog(getString(R.string.error_server));
                com.ceva.ubmobile.core.ui.Log.debug("authentication", t.toString());
                SecurityLayer.generateToken(getApplicationContext());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    public String requestHttpService(final String endpoint, final String params) {
        //nipenquiry/sravan/0004498938/999058
        showLoadingProgress();

        String urlparam = endpoint + "/" + params;
        String data = null;

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparam);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                dismissProgress();

                HTTP_DATA = response.body();

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
        data = HTTP_DATA;
        return data;
    }

    public Bitmap getMarkerBitmapFromViewBase(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    //912373
    public void call() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "07007007000 "));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void call(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void email() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"customerservice@unionbankng.com"});
        //intent.putExtra(Intent.EXTRA_EMAIL, "customerservice@unionbankng.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Mobile Banking Support");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    public void facebook() {
        String url = "https://facebook.com/Unionbankng/";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void twitter() {
        String url2 = "https://twitter.com/unionbank_ng";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url2));
        startActivity(intent);
    }

    public void webPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void whatsapp() {
        final String phoneNumber = "+2349070070001";
        final UBNSession session = new UBNSession(getApplicationContext());

        if (session.getBoolean("whatsapp")) {
            Uri uri = Uri.parse("smsto:" + phoneNumber);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            i.setPackage("com.whatsapp");
            startActivity(i);

        } else {
            if (!contactExists(phoneNumber)) {
                Log.debug("contact not existing");
                Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_bulb_white, getResources());
                new LovelyStandardDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColorRes(R.color.midnight_blue)
                        .setIcon(icon)
                        .setTitle("ATTENTION")
                        .setMessage("To be able to communicate with us via Whatsapp, we shall add our number to your phone book.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("OK, Proceed", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                session.setBoolean("whatsapp", true);

                                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                                int rawContactInsertIndex = ops.size();
                                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
                                ops.add(ContentProviderOperation
                                        .newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Union Bank of Nigeria(UBN)") // Name of the person
                                        .build());
                                ops.add(ContentProviderOperation
                                        .newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(
                                                ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber) // Number of the person
                                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); // Type of mobile number
                                try {
                                    ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                } catch (RemoteException e) {
                                    // error
                                } catch (OperationApplicationException e) {
                                    // error
                                }

                                if (appInstalledOrNot("com.whatsapp")) {
                                    Uri uri = Uri.parse("smsto:" + phoneNumber);
                                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                                    i.setPackage("com.whatsapp");
                                } else {
                                    warningDialog("You do not have Whatsapp Installed. Please install it from the play store");
                                }
                            }
                        })
                        .show();

            } else {
                Log.debug("contact exists");

                if (appInstalledOrNot("com.whatsapp")) {
                    Uri uri = Uri.parse("smsto:" + phoneNumber);
                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                    i.setPackage("com.whatsapp");
                    startActivity(i);
                } else {
                    warningDialog("You do not have Whatsapp Installed. Please install it from the play store");
                }

            }
        }
    }

    public void contactUs() {
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("Cancel")
                //.setOtherButtonTitles("Whatsapp", "Call", "Email", "Facebook", "Twitter")
                .setOtherButtonTitles("Whatsapp", "Call", "Email")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {

                        switch (index) {
                            case 0:
                                whatsapp();
                                break;
                            case 1:
                                call();
                                break;
                            case 2:
                                email();
                                break;
                           /* case 3:
                                facebook();
                                break;
                            case 4:
                                twitter();
                                break;*/

                        }
                    }
                }).show();
    }

    public boolean contactExists(String number) {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.
                CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()) {
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (PhoneNumberUtils.compare(number, phoneNumber)) {
                return true;
            }
        }
        return false;
    }// contactExists

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void logOutDialog() {
        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_bulb_white, getResources());
        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.midnight_blue)
                .setIcon(icon)
                .setTitle("Exit")
                .setMessage("Would you like to exit the application?")
                .setNegativeButton("NO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //do nothing
                    }
                })
                .setPositiveButton("YES", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                    }
                })
                .show();
    }

    public void generateSMSToken(String username, String phoneno, String servicetype) {
        //@Path("/otpgenForTransactions/{userid}/{phoneno}/{servicetype}")
        //showLoadingProgress("Generating SMS Token");
        String params = username + "/" + phoneno + "/" + servicetype;
        String url = SecurityLayer.genURLCBC("otpgenForTransactions", params, this);

        // String urlparam = "accountnovallidatin/" + SecurityLayer.generalEncrypt(accountNumber);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                //dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {

                    } else {
                        showToast(obj.optString(Constants.KEY_MSG));
                        //ResponseDialogs.warningDialogLovely(getCallingActivity(), "Error", obj.optString(Constants.KEY_MSG));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                //dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    public void updateDialog(String content) {
        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_bulb_white, getResources());
        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.midnight_blue)
                .setIcon(icon)
                .setTitle("Update Available")
                .setMessage(content)
                .setNegativeButton("Close", null)
                .setPositiveButton("Update App", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                }).show();
    }

    public void presentShowcaseSequence(List<ShowCaseModel> views, String SHOWCASE_ID) {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
                //Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
            }
        });

        sequence.setConfig(config);

        //sequence.addSequenceItem(setup, "Click here if you do not have an online banking profile", "GOT IT");
        //sequence.addSequenceItem(login, "Click here if you have an online banking profile", "GOT IT");
        for (int i = 0; i < views.size(); i++) {
            ShowCaseModel view = views.get(i);
            MaterialShowcaseView.Builder builder = new MaterialShowcaseView.Builder(this)
                    .setTarget(view.getView())
                    .setDismissText("DISMISS")
                    .setContentTextColor(getResources().getColor(R.color.white))
                    //.setMaskColour(getResources().getColor(R.color.colorPrimaryOpaque))
                    .setDismissOnTargetTouch(true)
                    .setDismissOnTouch(true)
                    .setTargetTouchable(true)
                    .setContentText(view.getContent())
                    .withCircleShape();
            if (view.getTitle() != null) {
                // builder.setTitleText(view.getTitle());
            }

            sequence.addSequenceItem(
                    builder.build());
        }

        /*sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(agent_locator)
                        .setDismissText("GOT IT")
                        .setContentText("Click here to request an agent in your neighbourhood")
                        .withCircleShape()
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(deals)
                        .setDismissText("GOT IT")
                        .setContentText("Click here to find special deals crafted for you")
                        .withCircleShape()
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(news)
                        .setDismissText("GOT IT")
                        .setContentText("Want to have the latest news at your fingertips? This is the place.")
                        .withCircleShape()
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(atm)
                        .setDismissText("GOT IT")
                        .setContentText("Need to find an ATM or a branch? Click here")
                        .withCircleShape()
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(contact)
                        .setDismissText("GOT IT")
                        .setContentText("Need to get in touch? Click here .")
                        .withCircleShape()
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(weather)
                        .setDismissText("GOT IT")
                        .setContentText("We wouldn't want the rain to beat you. You can get all weather feeds here.")
                        .withCircleShape()
                        .build()
        );*/
        sequence.start();

    }

    public void openLocator() {
        startActivity(new Intent(this, MainActivityATM.class));
    }

    public void showAviProgressDialog() {
        if (!isFinishing()) {
            if (loadingFragmentDialog != null)
                dismissAviProgress();
            Utility.hideKeyboard(this);
            FragmentManager fragmentManager = getSupportFragmentManager();
            loadingFragmentDialog = new CustomDialogFragment();
            loadingFragmentDialog.setCancelable(false);

            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(android.R.id.content, loadingFragmentDialog)
                    .addToBackStack(null).commitAllowingStateLoss();
        }

    }

    public void dismissAviProgress() {
        if (!isFinishing()) {
            if (loadingFragmentDialog != null) {

                if (loadingFragmentDialog.isVisible()) {
                    loadingFragmentDialog.dismissAllowingStateLoss();
                    loadingFragmentDialog = null;
                }
            }
        }
    }

    public void downLoadFile(Uri uri, String fileName) {
        DownloadManager.Request r = new DownloadManager.Request(uri);
// This put the download in the same Download dir the browser uses
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

// When downloading music and videos they will be listed in the player
// (Seems to be available since Honeycomb only)
        //r.allowScanningByMediaScanner();

// Notify user when download is completed
// (Seems to be available since Honeycomb only)
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

// Start download
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        try {
            dm.enqueue(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showFingerPrintDialog(boolean state) {
        String bio;
        if (state) {
            bio = "true";
        } else {
            bio = "false";
        }
        Utility.hideKeyboard(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FingerPrintDialog fragment = new FingerPrintDialog();
        Bundle args = new Bundle();
        args.putString("param1", bio);
        fragment.setArguments(args);

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, fragment)
                .addToBackStack(null).commitAllowingStateLoss();
    }

}
