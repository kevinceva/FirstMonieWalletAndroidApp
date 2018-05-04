package com.ceva.ubmobile.core.lifestyle.deals;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.CardPagerAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.lifestyle.ImageFragment;
import com.ceva.ubmobile.core.signon.TermsAndConditions;
import com.ceva.ubmobile.core.signon.newtobank.NewToBank;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.core.ui.Sign_In;
import com.ceva.ubmobile.models.DealItem;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.ImageUtils;
import com.ceva.ubmobile.utils.ScalingUtilities;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageDetails extends BaseActivity {

    @BindView(R.id.productName)
    TextView productName;
    @BindView(R.id.productPrice)
    TextView productPrice;
    @BindView(R.id.productDescription)
    TextView productDescription;
    @BindView(R.id.businessName)
    TextView businessName;
    @BindView(R.id.tx_accounts_from)
    Spinner accountFrom;
    UBNSession session;
    @BindView(R.id.btnRedeem)
    Button btnRedeem;

    @BindView(R.id.productLink)
    TextView productLink;
    CardPagerAdapter pageAdapter;
    DealItem deal;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_details);
        setToolbarTitle("Deal");
        ButterKnife.bind(this);
        session = new UBNSession(this);
        ArrayList<String> extra = getIntent().getStringArrayListExtra(DealItem.KEY_DEALS);
        deal = getIntent().getParcelableExtra("deal");
        List<Fragment> fragments = getFragments();

        pageAdapter = new CardPagerAdapter(getSupportFragmentManager(), fragments);

        ViewPager pager = findViewById(R.id.viewpager);

        pager.setAdapter(pageAdapter);
        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        /*extras.add(item.getBusinessName());
        extras.add(item.getSavingsTag());
        extras.add(item.getImageUrl());
        extras.add(item.getProductName());
        extras.add(item.getDiscountPrice());*/
        productName.setText(deal.getProductName());
        productPrice.setText(deal.getSavingsTag());
        productDescription.setText(deal.getDescription());
        businessName.setText(deal.getBusinessName());
        productLink.setText(deal.getProductLink());
        productLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webPage(deal.getProductLink());
            }
        });

        if (TextUtils.isEmpty(deal.getProductLink())) {
            btnRedeem.setVisibility(View.GONE);
            Log.debug("product_url", deal.getProductLink());
        }
        productLink.setVisibility(View.VISIBLE);

        if (session.getUserName() != null) {
            accountFrom.setVisibility(View.VISIBLE);
            List<String> accountList = session.getAccountNumbersNoDOM();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, accountList);
            accountFrom.setAdapter(adapter);
            accountFrom.setVisibility(View.GONE);
        } else {
            accountFrom.setVisibility(View.GONE);
        }

        btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (session.getUserName() != null) {
                    String[] acc = accountFrom.getSelectedItem().toString().split("-");
                    int max = acc.length - 1;
                    if (NetworkUtils.isConnected(HomePageDetails.this)) {
                        try {
                            redeemCode(session.getUserName(), session.getPhoneNumber(), deal.getMerchantCode(), acc[max].trim(), deal);
                        } catch (Exception e) {
                            e.printStackTrace();
                            warningDialog(getString(R.string.error_server));
                        }
                    } else {
                        noInternetDialog();
                    }
                } else {
                    ActionSheet.createBuilder(HomePageDetails.this, HomePageDetails.this.getSupportFragmentManager())
                            .setCancelButtonTitle("Cancel")
                            .setOtherButtonTitles("I have UnionOnline/UnionMobile - Log In", "I have a Union Bank account - Register", "I don't have a Union Bank account - Open Account")
                            .setCancelableOnTouchOutside(true)
                            .setListener(new ActionSheet.ActionSheetListener() {
                                @Override
                                public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                                }

                                @Override
                                public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                                    if (index == 0) {
                                        startActivity(new Intent(HomePageDetails.this, Sign_In.class));
                                    } else if (index == 1) {
                                        startActivity(new Intent(HomePageDetails.this, TermsAndConditions.class));
                                    } else {
                                        startActivity(new Intent(HomePageDetails.this, NewToBank.class));
                                    }
                                }
                            }).show();
                }
            }

        });

    }

    private List<Fragment> getFragments() {

        List<Fragment> fList = new ArrayList<Fragment>();

        if (deal.getImageUrl() != null) {
            fList.add(ImageFragment.newInstance(deal.getImageUrl()));
        }
        if (deal.getSecondImage() != null) {
            fList.add(ImageFragment.newInstance(deal.getSecondImage()));
        }
        if (deal.getThirdImage() != null) {
            fList.add(ImageFragment.newInstance(deal.getThirdImage()));
        }

        return fList;

    }

    public void redeemCode(String username, String mobileNumber, String merchantCode, String accountNumber, final DealItem deal) throws Exception {
        //@Path("/sendMerchantCode/{username}/{mobilenumber}/{merchantcode}/{accountnumer}/
        showLoadingProgress();
        String params = username + "/" + mobileNumber + "/" + merchantCode + "/" + accountNumber;
        String url = SecurityLayer.beforeLogin(params, UUID.randomUUID().toString(), "sendMerchantCodeBeforeLogin", this);

        // String urlparam = "accountnovallidatin/" + SecurityLayer.generalEncrypt(accountNumber);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Log.debug("nipservice", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptBeforeLogin(obj, getApplicationContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_check_circle_white, getResources());
                        new LovelyStandardDialog(HomePageDetails.this)
                                .setTopColor(ImageUtils.getColorByThemeAttr(HomePageDetails.this,R.attr._ubnColorPrimaryDark, Color.BLUE))
                                .setButtonsColorRes(R.color.midnight_blue)
                                .setIcon(icon)
                                .setTitle("Success")
                                .setMessage("The Coupon code has been sent to your registered mobile number. Please click on Redeem Coupon to proceed.")
                                .setPositiveButton("REDEEM COUPON", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        webPage(deal.getProductLink());
                                    }
                                })
                                .setNegativeButton("CLOSE", null)
                                .show();
                    } else {
                        ResponseDialogs.warningDialogLovely(HomePageDetails.this, "Error", obj.optString(Constants.KEY_MSG));
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
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                warningDialog(getString(R.string.error_server));
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

}
