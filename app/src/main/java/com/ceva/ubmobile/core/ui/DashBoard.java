package com.ceva.ubmobile.core.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ceva.ubmobile.BuildConfig;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.AccountsAdapter;
import com.ceva.ubmobile.adapter.LoansAdapter;
import com.ceva.ubmobile.adapter.RecyclerItemClickListener;
import com.ceva.ubmobile.adapter.ShopOnDashboard;
import com.ceva.ubmobile.core.BioTCs;
import com.ceva.ubmobile.core.UBNApplication;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.lifestyle.deals.HomePageDetails;
import com.ceva.ubmobile.core.lifestyle.deals.InsideOffers;
import com.ceva.ubmobile.core.news_simple.SimpleNews;
import com.ceva.ubmobile.core.omniview.InviteFriend;
import com.ceva.ubmobile.core.omniview.accountopening.ExistingAccountOpen;
import com.ceva.ubmobile.core.ui.merchantpayments.ReferenceEntry;
import com.ceva.ubmobile.core.ui.merchantpayments.RevPayRef;
import com.ceva.ubmobile.core.ui.transfers.TransfersHistory;
import com.ceva.ubmobile.models.AccountsModel;
import com.ceva.ubmobile.models.BankAccount;
import com.ceva.ubmobile.models.Banks;
import com.ceva.ubmobile.models.Beneficiary;
import com.ceva.ubmobile.models.CommerceProduct;
import com.ceva.ubmobile.models.DealItem;
import com.ceva.ubmobile.models.SecurityQuestionsModel;
import com.ceva.ubmobile.models.ShopItem;
import com.ceva.ubmobile.models.ShowCaseModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;
import com.ceva.ubmobile.utils.ImageUtils;
import com.ceva.ubmobile.utils.NumberUtilities;
import com.ceva.ubmobile.utils.ScalingUtilities;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.ViewConfigurator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.martykan.forecastie.models.Weather;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class DashBoard extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, ShakeDetector.OnShakeListener {
    public static final int GREET_TIME_MILLIS = 4000;
    public static final String isFirstLogin = "thisisthefirsttime";
    public static final String KEY_PREAPPROVED = "preapprovedloans";
    RelativeLayout drawerView;
    RelativeLayout mainView;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView greetImage;
    boolean isWeather = false;
    ShopOnDashboard shopAdapter;
    List<ShopItem> shopItemList = new ArrayList<ShopItem>();
    List<DealItem> dealItemList = new ArrayList<>();
    List<CommerceProduct> commerceProducts = new ArrayList<>();
    RadioGroup rGroup;
    PieChart pieChart;
    List<PieEntry> entries = new ArrayList<>();
    List<Integer> pieColors = new ArrayList<>();
    boolean isChartVisible = false;
    // String[] colorPool = {"#1abc9c", "#9b59b6", "#e74c3c", "#2980b9", "#34495e", "#f39c12", "#27ae60", "#f1c40f","#1abc9c", "#9b59b6", "#e74c3c", "#2980b9", "#34495e", "#f39c12", "#27ae60", "#f1c40f","#1abc9c", "#9b59b6", "#e74c3c", "#2980b9", "#34495e", "#f39c12", "#27ae60", "#f1c40f","#1abc9c", "#9b59b6", "#e74c3c", "#2980b9", "#34495e", "#f39c12", "#27ae60", "#f1c40f","#1abc9c", "#9b59b6", "#e74c3c", "#2980b9", "#34495e", "#f39c12", "#27ae60", "#f1c40f","#1abc9c", "#9b59b6", "#e74c3c", "#2980b9", "#34495e", "#f39c12", "#27ae60", "#f1c40f","#1abc9c", "#9b59b6", "#e74c3c", "#2980b9", "#34495e", "#f39c12", "#27ae60", "#f1c40f","#1abc9c", "#9b59b6", "#e74c3c", "#2980b9", "#34495e", "#f39c12", "#27ae60", "#f1c40f","#1abc9c", "#9b59b6", "#e74c3c", "#2980b9", "#34495e", "#f39c12", "#27ae60", "#f1c40f"};
    LinearLayout moreview, update;
    CardView chartView;
    //WaveSwipeRefreshLayout mSwipeRefreshLayout;
    Weather todayWeather = new Weather();
    TextView todayTemperature;
    @Nullable
    @BindView(R.id.lastlogin)
    TextView lastlogin;
    boolean isStaffPresent = false;
    RecyclerView loansView;
    LoansAdapter loansAdapter;
    List<AccountsModel> loansList = new ArrayList<>();
    String defAcc = null;
    private List<AccountsModel> txList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AccountsAdapter mAdapter;
    private UBNSession ubnSession;
    private String customerName, customer_firstname;
    private List<BankAccount> accounts;
    private List<Beneficiary> beneficiaryList = new ArrayList<>();
    private Timer timer;
    private TimerTask timerTask;
    private RecyclerView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
        setContentView(R.layout.activity_dashboard_alt);

        ButterKnife.bind(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ubnSession = new UBNSession(this);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setTitle("");

        if (ubnSession.getBoolean(UBNSession.KEY_LOGIN_STATUS)) {
            loginViews();
        } else {
            startActivity(new Intent(this, Sign_In.class));
            showToast("You have been logged out.");
            finish();
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    YOUR_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        //ScioContext.initializeSDK(DashBoard.this, getString(R.string.c360_key), NotificationSettings.SenderId, ubnSession.getUserName(), false);

        /*try {
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {
                @Override
                public void run() {
                    ScioContext.initializeSDK(DashBoard.this, getString(R.string.c360_key), NotificationSettings.SenderId, ubnSession.getUserName(), false);

                }
            });

        } catch (Exception e) {
            FirebaseCrash.report(e);
        }*/

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case YOUR_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.debug("starting context360");

                    try {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //   ScioContext.initializeSDK(DashBoard.this, getString(R.string.c360_key), NotificationSettings.SenderId, ubnSession.getUserName(), false);

                            }
                        });

                    } catch (Exception e) {
                        // FirebaseCrash.report(e);
                    }
                }
            }
        }
    }

    public void loginViews() {

        greetImage = findViewById(R.id.greetImage);
        //chart
        pieChart = findViewById(R.id.piechart);

        //init accounts
        initAccounts();
        // mSwipeRefreshLayout = (WaveSwipeRefreshLayout)findViewById(R.id.main_swipe);
        recyclerView = findViewById(R.id.rvCards);
        loansView = findViewById(R.id.rvloans);
        listView = findViewById(R.id.rvlifestyle);
        loansAdapter = new LoansAdapter(loansList, this);
        shopAdapter = new ShopOnDashboard(shopItemList, this);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listView.setLayoutManager(layoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(shopAdapter);
        listView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = null;
                        Log.debug("list size", shopItemList.size() + "");

                        if (position == 0) {
                            intent = new Intent(DashBoard.this, ReferenceEntry.class);
                        } else if (position == 1) {
                            intent = new Intent(DashBoard.this, RevPayRef.class);
                        } else if (position == (shopItemList.size() - 1)) {
                            intent = new Intent(DashBoard.this, InsideOffers.class);
                            //showToast("Coming soon...");
                        } else {
                            DealItem item = dealItemList.get(position);
                            intent = new Intent(DashBoard.this, HomePageDetails.class);
                            ArrayList<String> extras = new ArrayList<String>();
                            extras.add(item.getBusinessName());
                            extras.add(item.getDescription());
                            extras.add(item.getSavingsTag());
                            extras.add(item.getImageUrl());
                            extras.add(item.getSecondImage());
                            extras.add(item.getThirdImage());

                            intent.putStringArrayListExtra(DealItem.KEY_DEALS, extras);
                            intent.putExtra("deal", item);

                        }
                        startActivity(intent);
                    }
                }));

        mAdapter = new AccountsAdapter(txList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        //showGreeting();

        moreview = findViewById(R.id.moreview);
        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBalances(ubnSession.getUserName());
            }
        });

        rGroup = findViewById(R.id.radioGroup);
        rGroup.check(R.id.rd_accounts);
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    if (checkedId == R.id.rd_accounts) {
                        showAccountsView();
                    } else if (checkedId == R.id.rd_loans) {

                        showLoans();
                    } else if (checkedId == R.id.rd_investments) {

                        showInvestments();
                    } else {
                        showLifeStyle();
                    }
                }
            }

        });

        //weather fields
        todayTemperature = findViewById(R.id.temperature);

        BottomMenu();
        feedAccounts();
        showAccountsView();
        //runButtons();
        //ShakeDetector.create(this, this);
        //ShakeDetector.start();
        //ShakeDetector.updateConfiguration(1.0f, 2);

        /*mSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                // Do work to refresh the list here.
                updateBalances(ubnSession.getUserName());
            }
        });*/

        //load static data

        if (ubnSession.getBanks() == null) {
            // if (ubnSession.getBanks().size() == 0) {
            fetchBanks(ubnSession.getUserName(), Banks.BK_KEY_BANKS_PARAM);
            //}
        } else {
            if (isStaffPresent) {
                // ScioContext.initializeSDK(DashBoard.this, getString(R.string.c360_key), NotificationSettings.SenderId, ubnSession.getUserName(), false);

            }
        }
        ShowCaseModel mv = new ShowCaseModel(menuView, "Swipe left or right to access more items on this menu");
        new MaterialShowcaseView.Builder(this)
                .setTarget(menuView)
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        if (UBNApplication.isBioPresent()) {
                            Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_fingerprint_white, getResources());
                            new LovelyStandardDialog(DashBoard.this)
                                    .setTopColor(ImageUtils.getColorByThemeAttr(DashBoard.this,R.attr._ubnColorPrimaryDark,Color.BLUE))
                                    .setButtonsColorRes(R.color.midnight_blue)
                                    .setIcon(icon)
                                    .setTitle("Fingerprint Login")
                                    .setMessage("Would you like to enable login using fingerprint? You can always enable/disable this feature under 'My Profile'")
                                    .setPositiveButton("YES", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(DashBoard.this, BioTCs.class));
                                        }
                                    })
                                    .setNegativeButton("LATER", null)
                                    .show();
                        }
                    }
                })
                .setDismissText("GOT IT")
                .setContentText(mv.getContent())
                .setDelay(3000) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse("menu_showcase3") // provide a unique ID used to ensure it is only shown once
                .withRectangleShape()
                .show();

        if (session.getString(KEY_PREAPPROVED) != null) {
            showPreApproved(session.getString(KEY_PREAPPROVED));
        }

    }

    private void showPreApproved(String data) {
        JSONArray array;
        String message = null;
        try {
            array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String amount = obj.optString("eligibleMaxLoanAmount");
                message = "You have been pre-qualified for a loan of up to " + amount + ". Do you wish to access this loan?";

                message += "\n\nAmount " + amount;
                message += "\nTenure " + obj.optString("eligibleLoanTenor");
                message += "\nMonthly Repayment " + obj.optString("monthlyInstallmentAmount");
                message += "\nRate " + obj.optString("eligibleInterestRate");
            }

        } catch (Exception e) {
            Log.Error(e);
        }

        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_bulb_white, getResources());
        //final Context context = this;
        new LovelyStandardDialog(this)
                .setTopColor(ImageUtils.getColorByThemeAttr(DashBoard.this,R.attr._ubnColorPrimaryDark,Color.BLUE))
                .setButtonsColorRes(R.color.midnight_blue)
                .setIcon(icon)
                .setTitle("Pre-Approved Loan")
                .setMessage(message)
                .configureMessageView(message2 -> message2.setTextSize(13.0f))
                .setNegativeButton("NO", null)
                .setPositiveButton("YES", v -> acceptPreapprovedLoan(session.getUserName()))
                .show();
    }

    //@Override
    public void onStop() {
        super.onStop();
        ShakeDetector.stop();
    }

    //@Override
    public void onDestroy() {
        super.onDestroy();
        ShakeDetector.destroy();
    }

    //@Override
    public void onPause() {
        super.onPause();
        ShakeDetector.stop();
        //timer.cancel();
    }

    // @Override
    public void onResume() {
        super.onResume();
        ShakeDetector.start();
        showGreeting(customer_firstname);
       /* try {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showGreeting(customer_firstname);
                            *//*if (isWeather) {
                                showGreeting(customer_firstname);
                            } else {
                                //showWeather();
                            }*//*
                        }
                    });

                    //Download file here and refresh
                }
            };
            timer.schedule(timerTask, GREET_TIME_MILLIS, (GREET_TIME_MILLIS * 2));
        } catch (IllegalStateException e) {
            Log.debug("Damn", "resume error");
        }*/
    }


    private void fetchLifestyle(final String username) {
        shopItemList.clear();

        ShopItem item = new ShopItem("GIGM", "more",
                "Pay for your tickets", getString(R.string.lorem_ipsum));
        shopItemList.add(item);

        dealItemList.add(new DealItem("", "Pay Merchants", ""));

        item = new ShopItem("LASG RevPay", "more1",
                "Revenue Payments", getString(R.string.lorem_ipsum));
        shopItemList.add(item);
        dealItemList.add(new DealItem("", "Pay Merchants", ""));

        showLoadingProgress();

        String params = "D";
        Log.debug("params: ", params);

        String url = null;
        String endpoint = "onlineoffersdeallistBeforelogin";

        if (BuildConfig.DEBUG) {
            endpoint = "onlineoffersdeallist";
        }
        try {
            url = SecurityLayer.beforeLogin(params, UUID.randomUUID().toString(), endpoint, this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("cancel", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptBeforeLogin(obj, getApplicationContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {
                        JSONArray array = obj.getJSONArray("OFFER_LIST");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jitem = array.getJSONObject(i);
                            String imageUrl = Constants.NET_URL + Constants.IMAGE_DOWNLOAD_PATH + jitem.optString("PRODUCT_ID") + "/" + jitem.optString("IMAGE") + ".jpg";
                            String imageUrl2 = Constants.NET_URL + Constants.IMAGE_DOWNLOAD_PATH + jitem.optString("PRODUCT_ID") + "/" + jitem.optString("IMAGE2") + ".jpg";
                            String imageUrl3 = Constants.NET_URL + Constants.IMAGE_DOWNLOAD_PATH + jitem.optString("PRODUCT_ID") + "/" + jitem.optString("IMAGE3") + ".jpg";
                            DealItem dealitem = new DealItem(
                                    imageUrl,
                                    "Sold By " + jitem.optString("ORGANIZATIONNAME"),
                                    "Save " + jitem.optString("DISCOUNT_AMOUNT") + "%",
                                    imageUrl2,
                                    imageUrl3,
                                    jitem.optString("PRODUCT_DESC"),
                                    jitem.optString("PRODUCT_ORIGINAL_PRICE"),
                                    jitem.optString("PRODUCT_CURRENT_PRICE"),
                                    jitem.optString("PRODUCT_NAME"), jitem.optString("MERCHANT_CODE"), jitem.optString("MERCHANT_URL"));
                            dealItemList.add(dealitem);

                            ShopItem item = new ShopItem(jitem.optString("PRODUCT_NAME"),
                                    imageUrl, dealitem.getSavingsTag(), jitem.optString("ORGANIZATIONNAME"));
                            shopItemList.add(item);

                        }
                        ShopItem item = new ShopItem("Find more deals and offers!", "more",
                                "View more", getString(R.string.lorem_ipsum));
                        shopItemList.add(item);

                        dealItemList.add(new DealItem("", "View More", ""));

                        shopAdapter.notifyDataSetChanged();

                        hideSmoothProgress();

                        //ResponseDialogs.successToActivity(getString(R.string.success), responsemessage, PayDueBills.this, DashBoard.class,new Bundle());

                    } else {
                        //showTransactionComplete(1, responsemessage);
                        ResponseDialogs.failStatic(getString(R.string.error), responsemessage, DashBoard.this);
                        //ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, PayDueBills.this);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    //showTransactionComplete(1, getString(R.string.error_server));
                    SecurityLayer.generateToken(getApplicationContext());
                    ResponseDialogs.failStatic(getString(R.string.error), getString(R.string.error_server), DashBoard.this);
                    Log.Error(e);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                //showTransactionComplete(1, getString(R.string.error_server));
                ResponseDialogs.failStatic(getString(R.string.error), getString(R.string.error_server), DashBoard.this);
                SecurityLayer.generateToken(getApplicationContext());
                //buttonClose_base.setVisibility(View.VISIBLE);
                com.ceva.ubmobile.core.ui.Log.debug("billerfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }

        });

    }


    private void feedAccounts() {

        int m = accounts.size();//27/10/2016
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT

        // mAdapter.notifyItemRangeRemoved(0,m);
        txList.clear();
        if (m > 0) {
            for (int j = 0; j < m; j++) {

                List<AccountsModel> acc = accounts.get(j).getCustomerInfo();
                if (acc.size() > 0) {
                    int color = generator.getRandomColor();
                    pieColors.add(color);
                    Log.debug("ubnaccno", "" + accounts.get(j).getAccountNumber());
                    String accno = accounts.get(j).getAccountNumber();
                    AccountsModel am = new AccountsModel(acc.get(0).getAccountType().toUpperCase(), accno, acc.get(0).getAccountBalance(), acc.get(0).getAccountCurrency(), acc.get(0).isWallet(), null);

                    if (acc.get(0).isWallet()) {
                        am = new AccountsModel(acc.get(0).getAccountType().toUpperCase(), acc.get(0).getAccountNumber(), acc.get(0).getAccountBalance(), acc.get(0).getAccountCurrency(), acc.get(0).isWallet(), null);

                    }

                    if (am.getAccountType().toLowerCase().contains("staff")) {
                        isStaffPresent = true;
                    }

                    txList.add(am);
                    String accsplit = acc.get(0).getAccountType();
                    float accAmount = Float.parseFloat(acc.get(0).getAccountBalance().replace(",", ""));

                    entries.add(new PieEntry(accAmount, accsplit));
                } else {
                    String accno = accounts.get(j).getAccountNumber();
                    AccountsModel am = new AccountsModel("INFO MISSING", accno, "XXXX.XX");
                    txList.add(am);
                }
                // fetchBeneficiaries(accounts.get(j).getAccountNumber());

            }
            setPieChart();
            hideSmoothProgress();
            mAdapter.notifyDataSetChanged();
        } else {

        }
    }

    private void showGreeting(String name) {
        isWeather = false;
        RelativeLayout rweather = findViewById(R.id.weather_fields);
        RelativeLayout rgreet = findViewById(R.id.greet_fields);
        rweather.setVisibility(View.GONE);
        rgreet.setVisibility(View.VISIBLE);
        TextView time = findViewById(R.id.timeGreeting);
        TextView date = findViewById(R.id.dashboardDate);
        TextView hr = findViewById(R.id.text_hr);
        TextView min = findViewById(R.id.text_min);
        TextView widget_date = findViewById(R.id.widget_date);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
        String formattedDate = df.format(c.getTime());
        date.setText(formattedDate);
        String greeting = "Good Morning";
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minOfDay = c.get(Calendar.MINUTE);
        String hour = "" + timeOfDay;
        String minute = "" + minOfDay;
        if (timeOfDay <= 9) {
            hour = "0" + timeOfDay;
        }
        if (minOfDay <= 9) {
            minute = "0" + minute;
        }
        hr.setText(hour);
        min.setText(minute);
        df = new SimpleDateFormat("EEE MMMM, dd");
        formattedDate = df.format(c.getTime());
        widget_date.setText(formattedDate);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Good Morning";
            greetImage.setImageDrawable(getResources().getDrawable(R.drawable.morning));
        } else if (timeOfDay >= 12 && timeOfDay < 18) {

            greeting = "Good Afternoon";
            greetImage.setImageDrawable(getResources().getDrawable(R.drawable.afternoon));
        } else {
            //Toast.makeText(this, "Good Evening", Toast.LENGTH_SHORT).show();
            greeting = "Good Evening";
            greetImage.setImageDrawable(getResources().getDrawable(R.drawable.night));
        }
        if (session.getCampaignNote() != null) {
            if (session.getCampaignNote().equalsIgnoreCase("NA")) {
                time.setText(greeting + " " + name);
            } else {
                time.setText(session.getCampaignNote());
            }
        } else {
            time.setText(greeting + " " + name);
        }
        // Bitmap blurred = BitmapFactory.decodeResource(getResources(), R.drawable.brian_bg);
        //blurred = BlurBuilder.blur(this,blurred);

        //greetImage.setImageBitmap(blurred);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.signOff) {
            startActivity(new Intent(this, LandingPage.class));
            ubnSession.setBoolean(UBNSession.KEY_LOGIN_STATUS, false);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         /*if (id == R.id.nav_beneficiaries) {
            startActivity(new Intent(this, Beneficiaries.class));
        } else*/
        if (id == R.id.nav_profile) {
            startActivity(new Intent(this, Profile.class));
        } /*else if (id == R.id.nav_profile_act) {
            startActivity(new Intent(this, DebitCardValidation.class));

        }*/ else if (id == R.id.nav_receipts) {
                startActivity(new Intent(this, TransfersHistory.class));
        }/*else if (id == R.id.nav_flutter) {
            startActivity(new Intent(this,FlutterTest.class));
        } else if (id == R.id.nav_merchant) {
            startActivity(new Intent(this, MerchantsList.class));
        }*/ else if (id == R.id.nav_locator) {
            //startActivity(new Intent(this, ATMLocator.class));
            openLocator();
        } else if (id == R.id.nav_news) {
            startActivity(new Intent(this, SimpleNews.class));
        } else if (id == R.id.nav_invite) {
            startActivity(new Intent(this, InviteFriend.class));
        } /*else if (id == R.id.nav_cheques) {
           // startActivity(new Intent(this, ChequeBookRequest.class));
             showToast("Coming soon");
        } else if (id == R.id.nav_bankdrafts) {
            //startActivity(new Intent(this, BankDraftRequest.class));
             showToast("Coming soon");
        }  else if (id == R.id.nav_inbox) {
            startActivity(new Intent(this, MailBox.class));
        } */ else if (id == R.id.nav_call) {
            call();
        } else if (id == R.id.nav_email) {
            email();
        } /*else if (id == R.id.nav_facebook) {
            facebook();
        } else if (id == R.id.nav_twitter) {
            twitter();
        } */ else if (id == R.id.nav_whatsapp) {
            whatsapp();
        }/*else if (id == R.id.nav_bancassurance) {
            startActivity(new Intent(this, InsuranceRequest.class));
        } else if (id == R.id.nav_donations) {
            startActivity(new Intent(this, MyDonations.class));
        }else if (id == R.id.nav_investments) {
            startActivity(new Intent(this, Investments.class));
        }else if (id == R.id.nav_newaccount) {
             startActivity(new Intent(this, AccountOpening.class));
         }else if (id == R.id.nav_social) {
            startActivity(new Intent(this, LinkSocialMedia.class));
        }
         else if (id == R.id.nav_cash) {
             // Handle the camera action
             startActivity(new Intent(this, CashMenu.class));
         }
         else if (id == R.id.nav_lifestyle) {
             startActivity(new Intent(this, LifeStyle.class));
         } else if (id == R.id.nav_cards) {
            startActivity(new Intent(this, CardManagement.class));
        }*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initDrawer(String customername) {
        String[] custsplit = customername.split(" ");
        if (custsplit.length > 1) {
            customername = custsplit[0] + "\n" + custsplit[1];
        }
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainView = findViewById(R.id.mainContent);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mainView.setTranslationX(slideOffset * drawerView.getWidth());
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();
            }
        };
        drawer.setDrawerListener(toggle);
        //toggle.syncState();

        View header = navigationView.getHeaderView(0);
        ButterKnife.bind(this, header);
        TextView custname = header.findViewById(R.id.nav_customer_name);
        lastlogin.setText(ubnSession.getString("lastlogin"));
        custname.setText(customername);

    }

    private void initAccounts() {
        String accountsListString = ubnSession.pref.getString(Constants.KEY_FULLINFO, null);
        Gson gson = new Gson();
        accounts = gson.fromJson(accountsListString, new TypeToken<ArrayList<BankAccount>>() {
        }.getType());

        customerName = accounts.get(0).getAccountName();//27/10/2016
        customer_firstname = accounts.get(0).getFirstanme();
        defAcc = accounts.get(0).getAccountNumber();
        Log.debug("ubnname", customerName);
        initDrawer(customerName);
        customer_firstname = Utility.firstLetterUppercase(customer_firstname);
        showGreeting(customer_firstname);
    }


    private void showAccountsView() {
        recyclerView.setVisibility(View.VISIBLE);
        moreview = findViewById(R.id.moreview);
        //if (!Constants.NET_URL.contains(Constants.HOSTNAME)) {
        moreview.setVisibility(View.VISIBLE);

        listView.setVisibility(View.GONE);
        update.setVisibility(View.VISIBLE);
        loansView.setVisibility(View.GONE);
    }

    private void showLifeStyle() {

        //if(LIFE_COUNT == 0) {
        fetchLifestyle(ubnSession.getUserName());
        // }
        recyclerView.setVisibility(View.GONE);
        moreview.setVisibility(View.GONE);
        update.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        loansView.setVisibility(View.GONE);

        //showAccountsView();
    }

    private void showLoans() {
        loansList.clear();
        setLoans();
        recyclerView.setVisibility(View.GONE);
        moreview.setVisibility(View.GONE);
        update.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        loansView.setVisibility(View.VISIBLE);

    }

    private void showInvestments() {
        loansList.clear();
        setInvestments();
        recyclerView.setVisibility(View.GONE);
        moreview.setVisibility(View.GONE);
        update.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        loansView.setVisibility(View.VISIBLE);

    }

    private void setLoans() {
        //getLoansLists(ubnSession.getUserName(), "0042168192");
        getLoansLists(ubnSession.getUserName(), defAcc);

    }

    private void setInvestments() {
        getInvestmentsLists(ubnSession.getUserName(), defAcc);
        /*List<AccountsModel> loansList = new ArrayList<>();
        AccountsModel loan = new AccountsModel("Term Deposit", "1234567890123", "42,000,000.00", "NGN", false);
        loansList.add(loan);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        loansView.setLayoutManager(mLayoutManager);
        loansView.setItemAnimator(new DefaultItemAnimator());
        loansView.setAdapter(loansAdapter);
        loansAdapter.notifyDataSetChanged();*/

    }

    private void setPieChart() {

        chartView = findViewById(R.id.chartView);

        PieDataSet set = new PieDataSet(entries, "Account Comparison");
        set.setColors(pieColors);
        PieData data2 = new PieData(set);
        pieChart.setData(data2);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setContentDescription("Account Comparison");
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(8);
        pieChart.setCenterTextSize(8);
        pieChart.invalidate(); // refresh
    }

    private void showChart() {
        isChartVisible = true;
        //hide accounts
        recyclerView.setVisibility(View.GONE);

        moreview.setVisibility(View.GONE);
        //hide lifestyle
        listView.setVisibility(View.GONE);

        chartView.setVisibility(View.VISIBLE);
        update.setVisibility(View.GONE);
    }

    private void hideChart() {
        isChartVisible = false;
        chartView.setVisibility(View.GONE);
        showAccountsView();

    }

    public void onNewAccountClick(View v) {
        startActivity(new Intent(this, ExistingAccountOpen.class));
        Log.debug("newaccountclick");
    }

    @Override
    public void OnShake() {
        Log.debug("shaken!!!");
        if (isChartVisible) {
            //hideChart();
            Log.debug("shaken!!!:hiding the chart");
        } else {
            //showChart();
            Log.debug("shaken!!!:showing");
        }
    }

    public void updateBalances(String username) {
        //final SweetAlertDialog prog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Simpler Smarter Bank").setContentText("Loading...");
        //prog.setCustomImage((R.mipmap.ic_launcher));
        //prog.show();
        showLoadingProgress();
        String urlparam = username;
        String data = null;

        String url = SecurityLayer.genURLCBC(Constants.KEY_BALINQUIRY_ENDPOINT, urlparam, getApplicationContext());

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
                                isWallet = balObj.optString("accounttype").equalsIgnoreCase("wallet");
                                AccountsModel accountsModel = new AccountsModel(objAcc.optString("accountproduct"),
                                        objAcc.optString("accountNo"),
                                        objAcc.optString("availabletowithdraw"),
                                        balObj.optString("accountcurrency"),
                                        isWallet, balObj.optString("accountname"));
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
                        showToast("Accounts updated...");

                    } else {
                        //startActivity(new Intent(DashBoard.this, PasswordSetting.class));
                        warningDialog(responsemessage);
                        //startDashBoard();
                    }

                } catch (Exception e) {
                    warningDialog(getString(R.string.error_server));
                    e.printStackTrace();
                    SecurityLayer.generateToken(getApplicationContext());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                warningDialog(getString(R.string.error_server));

                SecurityLayer.generateToken(getApplicationContext());
                com.ceva.ubmobile.core.ui.Log.debug("authentication", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    private void getLoansLists(String username, String accountNumber) {

        //@Path("/getLoanDetails/{username}/{accountno}

        showLoadingProgress();
        String params = username + "/" + accountNumber;
        String url = SecurityLayer.genURLCBC("getLoanDetails", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    Log.debug("loans:", obj.toString());

                    String respcode = obj.optString("respcode");
                    String respdesc = obj.optString("respdesc");

                    /*AccountsModel loan = new AccountsModel("Loan Account", "1234567890123", "42,000,000.00", "NGN", true);
                    loansList.add(loan);*/

                    if (respcode.equals("00")) {
                        JSONArray loansArray = new JSONArray(obj.optString("LoanDetails"));
                        for (int j = 0; j < loansArray.length(); j++) {
                            JSONObject loanObj = loansArray.optJSONObject(j);

                            AccountsModel loan = new AccountsModel(loanObj.optString("productName"),
                                    loanObj.optString("accountNumber"),
                                    loanObj.optString("balance"), loansArray.optJSONObject(j).toString(), true);
                            loansList.add(loan);
                            Log.debug("moja " + loansArray.optJSONObject(j).toString());
                        }
                    } else {
                        ResponseDialogs.warningStatic("Error", respdesc, DashBoard.this);
                    }

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(DashBoard.this);
                    loansView.setLayoutManager(mLayoutManager);
                    loansView.setItemAnimator(new DefaultItemAnimator());
                    loansView.setAdapter(loansAdapter);
                    loansAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    warningDialog(getString(R.string.error_server));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("otpvalidation", t.toString());
                warningDialog(getString(R.string.error_server));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    private void getInvestmentsLists(String username, String accountNumber) {

        //@Path("/getLoanDetails/{username}/{accountno}

        showLoadingProgress();
        String params = username + "/" + accountNumber;
        String url = SecurityLayer.genURLCBC("viewTdDetailsList", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    Log.debug("loans:", obj.toString());

                    String respcode = obj.optString("respcode");
                    String respdesc = obj.optString("respdesc");

                    /*AccountsModel loan = new AccountsModel("Loan Account", "1234567890123", "42,000,000.00", "NGN", true);
                    loansList.add(loan);*/

                    if (respcode.equals("00")) {
                        JSONArray loansArray = new JSONArray(obj.optString("viewTdDetails"));
                        for (int j = 0; j < loansArray.length(); j++) {
                            JSONObject loanObj = loansArray.optJSONObject(j);

                            String amount = NumberUtilities.getWithDecimal(Double.parseDouble(loanObj.optString("amount")));

                            AccountsModel loan = new AccountsModel(loanObj.optString("product"),
                                    loanObj.optString("accountnumber"),
                                    amount, loansArray.optJSONObject(j).toString(), false);
                            loansList.add(loan);
                            Log.debug("moja " + loansArray.optJSONObject(j).toString());
                        }
                    } else {
                        ResponseDialogs.warningStatic("Error", respdesc, DashBoard.this);
                    }

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(DashBoard.this);
                    loansView.setLayoutManager(mLayoutManager);
                    loansView.setItemAnimator(new DefaultItemAnimator());
                    loansView.setAdapter(loansAdapter);
                    loansAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    warningDialog(getString(R.string.error_server));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("otpvalidation", t.toString());
                warningDialog(getString(R.string.error_server));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    private void acceptPreapprovedLoan(String username) {
        //@Path("/registerForPreApprovedLoan/{username}")
        showLoadingProgress();

        String params = username;
        String session_id = UUID.randomUUID().toString();
        String url = SecurityLayer.genURLCBC("registerForPreApprovedLoan", params, this);
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
                    obj = SecurityLayer.decryptTransaction(obj, DashBoard.this);

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        session.setString(KEY_PREAPPROVED, null);
                        ResponseDialogs.successToActivity("Success", obj.optString(Constants.KEY_MSG), DashBoard.this, Sign_In.class, new Bundle());
                    } else {
                        ResponseDialogs.warningDialogLovely(DashBoard.this, "Error", obj.optString(Constants.KEY_MSG));
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
