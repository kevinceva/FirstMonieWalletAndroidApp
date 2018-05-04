package com.ceva.ubmobile.core.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ceva.ubmobile.BuildConfig;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.lifestyle.deals.HomePageDeals;
import com.ceva.ubmobile.core.news_simple.SimpleNews;
import com.ceva.ubmobile.core.signon.TermsAndConditions;
import com.ceva.ubmobile.core.signon.newtobank.NewToBank;
import com.ceva.ubmobile.core.ui.locator.RequestAgent;
import com.ceva.ubmobile.models.DealItem;
import com.ceva.ubmobile.models.ShowCaseModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.rampo.updatechecker.UpdateChecker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.martykan.forecastie.activities.WeatherActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandingPage extends BaseActivity implements ViewPagerEx.OnPageChangeListener, BaseSliderView.OnSliderClickListener {
    // private SliderLayout mDemoSlider;
    private static final String SHOWCASE_ID = "sequence example";
    UBNSession session;
    @BindView(R.id.setup)
    LinearLayout setup;
    @BindView(R.id.login)
    LinearLayout login;
    @BindView(R.id.account)
    LinearLayout account;
    @BindView(R.id.agent_locator)
    LinearLayout agent_locator;
    @BindView(R.id.deals)
    LinearLayout deals;
    @BindView(R.id.news)
    LinearLayout news;
    @BindView(R.id.atm)
    LinearLayout atm;
    @BindView(R.id.contact)
    LinearLayout contact;
    @BindView(R.id.weather)
    LinearLayout weather;
    ArrayList<DealItem> items = new ArrayList<>();
    @BindView(R.id.badge)
    TextView badge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_kelechi);
        ButterKnife.bind(this);

        session = new UBNSession(this);
        FirebaseAnalytics.getInstance(this).setCurrentScreen(this, getString(R.string.screen_landing_page), null);
        /*if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            try {
                finish();
                System.exit(0);
            } catch (Exception e) {
                Log.Error(e);
            }
        }*/

        /*if (session.getBoolean(UBNSession.KEY_APP_CLOSE)) {
            if (session.getBoolean(UBNSession.KEY_LOGIN_STATUS)) {
                try {
                    session.setBoolean(UBNSession.KEY_APP_CLOSE, false);
                    session.setBoolean(UBNSession.KEY_LOGIN_STATUS, false);
                    finish();
                    System.exit(0);
                } catch (Exception e) {
                    Log.Error(e);
                }
            }
        }*/
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        Log.debug("Datestamp" + ts);
        badge.setVisibility(View.GONE);
        try {
            if (session.getString("time") != null) {
                Long previous = Long.parseLong(session.getString("time"));
                //3000(millliseconds in a second)*60(seconds in a minute)*5(number of minutes)=300000
                if (Math.abs(previous - tsLong) > 129600000) {
                    fetchProducts();
                    session.setString("time", ts);
                } else {
                    //server is not within 5 minutes of current system time
                    getSavedProducts(session.getString("saved_deals"));
                }

            } else {
                session.setString("time", ts);
                fetchProducts();
            }
        } catch (Exception e) {
            badge.setVisibility(View.GONE);
        }

        new UpdateChecker(this);
        UpdateChecker.start(); // If you are in a Fragment
        List<ShowCaseModel> views = new ArrayList<>();
        views.add(new ShowCaseModel(setup, "Tap \"Get started\" if you have a Union Bank account but do not have access to UnionOnline. Tap anywhere to continue this tutorial", "Get Started"));
        views.add(new ShowCaseModel(login, "Tap \"Log In\" if you have UnionOnline activated. Tap anywhere to dismiss", "Log In"));
        views.add(new ShowCaseModel(account, "Tap \"Open Account\" to open an account with us", "Open Account"));
        presentShowcaseSequence(views, "landing_page");
        //presentShowcaseSequence();

        try {
            Log.debug("starting context360");
           /* if(session.getUserName()!=null) {

                ScioContext.initializeSDK(this, getString(R.string.c360_key), NotificationSettings.SenderId, "COLLINS", false);
            }else{
                ScioContext.initializeSDK(this, getString(R.string.c360_key), NotificationSettings.SenderId, "COLLINS", false);
            }*/
        } catch (Exception e) {
            Log.Error(e);
        }

        //fetchProducts();

    }


    public void onClickSetup(View v) {

        startActivity(new Intent(this, TermsAndConditions.class));
        //comingSoon();
    }

    public void onClickNewAccount(View v) {
        // if (!Constants.NET_URL.contains(Constants.HOSTNAME)) {
        startActivity(new Intent(this, NewToBank.class));
        // } else {
        //   comingSoon();
        // }
        //startActivity(new Intent(this, NewToBank.class));
    }

    public void onClickLocator(View v) {

        //session.setUserName("COLLINS");
        if (session.getUserName() != null) {
            startActivity(new Intent(this, RequestAgent.class));
        } else {
            ResponseDialogs.info("SIGN IN", "Please sign into your UBN account to enjoy Agent services. If you do not have a Union Bank account, click Open Account in the menu.", this);
        }

    }

    public void onClickLogin(View v) {
        startActivity(new Intent(this, Sign_In.class));
        //this.finish();
    }

    public void onClickNews(View v) {
        startActivity(new Intent(this, SimpleNews.class));
        //this.finish();
    }

    public void onClickATM(View v) {
        // startActivity(new Intent(this, ATMLocator.class));
        //this.finish();
        openLocator();
    }

    public void onClickContact(View v) {
        //startActivity(new Intent(this,ContactUs.class));
        contactUs();
        //this.finish();
    }

    public void onClickDeals(View v) {
        //if (!Constants.NET_URL.contains(Constants.HOSTNAME)) {
        startActivity(new Intent(this, HomePageDeals.class));
        //   } else {
        //comingSoon();
        //  }
        //this.finish();
    }

    public void onClickWeather(View v) {
        startActivity(new Intent(this, WeatherActivity.class));
        //this.finish();
    }

    @Override
    public void onBackPressed() {
        //do nothing
        logOutDialog();
    }

    private void comingSoon() {
        Toast.makeText(this, "Coming soon...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    /*private void presentShowcaseSequence() {

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

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(setup)
                        .setDismissText("GOT IT")
                        .setDismissOnTargetTouch(true)
                        .setDismissOnTouch(true)
                        .setTargetTouchable(true)
                        .setContentText("Click here if you have a Union Bank account but do not have access to UnionOnline")
                        .withCircleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(login)
                        .setDismissText("GOT IT")
                        .setDismissOnTargetTouch(true)
                        .setDismissOnTouch(true)
                        .setTargetTouchable(true)
                        .setContentText("Click here if you have UnionOnline activated")
                        .withCircleShape()
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(account)
                        .setDismissText("GOT IT")
                        .setDismissOnTargetTouch(true)
                        .setDismissOnTouch(true)
                        .setTargetTouchable(true)
                        .setContentText("Click here to open a Union Bank account")
                        .withCircleShape()
                        .build()
        );
        *//*sequence.addSequenceItem(
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
        );*//*
        sequence.start();

    }*/
    private void getSavedProducts(String deals) {

        try {
            items.clear();
            JSONObject obj = new JSONObject(deals);
            JSONArray array = new JSONArray(obj.optString("OFFER_LIST"));

            if (obj.optString(Constants.KEY_CODE).equals("00")) {
                for (int i = 0; i < array.length(); i++) {

                    JSONObject jitem = array.getJSONObject(i);
                    String imageUrl = Constants.NET_URL + Constants.IMAGE_DOWNLOAD_PATH + jitem.optString("PRODUCT_ID") + "/" + jitem.optString("IMAGE") + ".jpg";
                    String imageUrl2 = Constants.NET_URL + Constants.IMAGE_DOWNLOAD_PATH + jitem.optString("PRODUCT_ID") + "/" + jitem.optString("IMAGE2") + ".jpg";
                    String imageUrl3 = Constants.NET_URL + Constants.IMAGE_DOWNLOAD_PATH + jitem.optString("PRODUCT_ID") + "/" + jitem.optString("IMAGE3") + ".jpg";
                    Log.debug(imageUrl);

                            /*ShopItem item = new ShopItem(jitem.optString("PRODUCT_NAME"),
                                    imageUrl, jitem.optString("PRODUCT_CURRENT_PRICE"),Utility.camelCase(jitem.optString("ORGANIZATIONNAME")));
                            shopItemList.add(item);*/

                    // double savings = Double.parseDouble(jitem.optString("PRODUCT_ORIGINAL_PRICE")) - Double.parseDouble(jitem.optString("PRODUCT_CURRENT_PRICE"));

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
                    items.add(dealitem);

                }
                if (items.size() > 0) {
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(items.size() + "");

                } else {
                    badge.setVisibility(View.GONE);
                }

                session.setString("saved_deals", obj.toString());

            } else {

                //ResponseDialogs.warningDialogLovelyToActivity(HomePageDeals.this, "Error", obj.optString(Constants.KEY_MSG), LandingPage.class);
            }
        } catch (Exception e) {
            Log.Error(e);
        }
    }

    private void fetchProducts() {
        items.clear();
        //showLoadingProgress();
        String params = "D";
        String url = "";
        String endpoint = "onlineoffersdeallistBeforelogin";
        if (BuildConfig.DEBUG) {
            endpoint = "onlineoffersdeallist";
        }
        try {
            url = SecurityLayer.beforeLogin(params, UUID.randomUUID().toString(), endpoint, getApplicationContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dismissProgress();
                Log.debug("onlineSubCategoryList", response.body());
                //dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptBeforeLogin(obj, getApplicationContext());

                    JSONArray array = new JSONArray(obj.optString("OFFER_LIST"));

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject jitem = array.getJSONObject(i);
                            String imageUrl = Constants.NET_URL + Constants.IMAGE_DOWNLOAD_PATH + jitem.optString("PRODUCT_ID") + "/" + jitem.optString("IMAGE") + ".jpg";
                            String imageUrl2 = Constants.NET_URL + Constants.IMAGE_DOWNLOAD_PATH + jitem.optString("PRODUCT_ID") + "/" + jitem.optString("IMAGE2") + ".jpg";
                            String imageUrl3 = Constants.NET_URL + Constants.IMAGE_DOWNLOAD_PATH + jitem.optString("PRODUCT_ID") + "/" + jitem.optString("IMAGE3") + ".jpg";
                            Log.debug(imageUrl);

                            /*ShopItem item = new ShopItem(jitem.optString("PRODUCT_NAME"),
                                    imageUrl, jitem.optString("PRODUCT_CURRENT_PRICE"),Utility.camelCase(jitem.optString("ORGANIZATIONNAME")));
                            shopItemList.add(item);*/

                            // double savings = Double.parseDouble(jitem.optString("PRODUCT_ORIGINAL_PRICE")) - Double.parseDouble(jitem.optString("PRODUCT_CURRENT_PRICE"));

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
                            items.add(dealitem);

                        }
                        if (items.size() > 0) {
                            badge.setVisibility(View.VISIBLE);
                            badge.setText(items.size() + "");

                        } else {
                            badge.setVisibility(View.GONE);
                        }

                        session.setString("saved_deals", obj.toString());

                    } else {
                        badge.setVisibility(View.GONE);
                        //ResponseDialogs.warningDialogLovelyToActivity(HomePageDeals.this, "Error", obj.optString(Constants.KEY_MSG), LandingPage.class);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //ResponseDialogs.warningDialogLovelyToActivity(HomePageDeals.this, "Error", getString(R.string.error_server), LandingPage.class);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                //dismissProgress();
                badge.setVisibility(View.GONE);
                //com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                //ResponseDialogs.warningDialogLovelyToActivity(HomePageDeals.this, "Error", getString(R.string.error_server), LandingPage.class);
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

}
