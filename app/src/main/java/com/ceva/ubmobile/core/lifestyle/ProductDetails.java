package com.ceva.ubmobile.core.lifestyle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.CardPagerAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.models.DealItem;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.NumberUtilities;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetails extends BaseActivity {
    @BindView(R.id.productName)
    TextView productName;
    @BindView(R.id.productPrice)
    TextView productPrice;
    @BindView(R.id.productDescription)
    TextView productDescription;
    @BindView(R.id.totaltxt)
    TextView productTotal;
    @BindView(R.id.productQty)
    EditText productQty;
    @BindView(R.id.productAddress)
    EditText productAddress;
    @BindView(R.id.btnAdd)
    Button btnAdd;
    @BindView(R.id.btnSub)
    Button btnSub;

    @BindView(R.id.account)
    Spinner account;

    int initialQty = 1;
    double initialPrice = 100;
    CardPagerAdapter pageAdapter;
    @BindView(R.id.btnBuy)
    Button btnBuy;
    boolean isReady = false;

    DealItem item;
    ArrayList<String> extra = new ArrayList<>();
    UBNSession session;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);
        setToolbarTitle("Details");
        hideTransactionPin();
        session = new UBNSession(this);

        List<String> accountList = session.getAccountNumbersNoDOM();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, accountList);
        account.setAdapter(adapter);

        if (getIntent().getStringArrayListExtra(DealItem.KEY_DEALS) != null) {

            extra = getIntent().getStringArrayListExtra(DealItem.KEY_DEALS);
            /*List of items
                                0 = product name
                                1 = product desc
                                2 = product price
                                3 = product image 1
                                4 = image 2
                                5 = discount price*/
            for (int i = 0; i < extra.size(); i++) {
                String price = Constants.KEY_NAIRA + extra.get(2);
                productName.setText(extra.get(0));
                productDescription.setText(extra.get(1));

                initialPrice = Double.parseDouble(extra.get(2));
                productPrice.setText(NumberUtilities.getWithDecimalPlusCurrency(initialPrice));
                productTotal.setText(NumberUtilities.getWithDecimalPlusCurrency(initialPrice));
            }
        }

        productQty.setText("1");
        productQty.setFocusable(false);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialQty = Integer.parseInt(productQty.getText().toString());
                initialQty++;
                productQty.setText(initialQty + "");
                double priceT = initialPrice * initialQty;
                productTotal.setText(NumberUtilities.getWithDecimalPlusCurrency(priceT));
            }
        });
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialQty = Integer.parseInt(productQty.getText().toString());
                initialQty--;
                if (initialQty >= 0) {
                    productQty.setText(initialQty + "");
                    double priceT = initialPrice * initialQty;
                    productTotal.setText(NumberUtilities.getWithDecimalPlusCurrency(priceT));
                }
            }
        });
        List<Fragment> fragments = getFragments();

        pageAdapter = new CardPagerAdapter(getSupportFragmentManager(), fragments);

        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);

        pager.setAdapter(pageAdapter);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        initializeconfirm();
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isReady) {
                    if (!productAddress.getText().toString().isEmpty()) {
                        // item = new DealItem(extra.get(3),extra.get(0),extra.get(2),extra.get(4),extra.get(5),extra.get(1));
                        ConfirmModel confirmModel = new ConfirmModel("Product", extra.get(0));
                        confirmItems.add(confirmModel);
                        confirmModel = new ConfirmModel("Quantity", productQty.getText().toString());
                        confirmItems.add(confirmModel);
                        confirmModel = new ConfirmModel("Price per unit", extra.get(2));
                        confirmItems.add(confirmModel);
                        double priceT = initialPrice * initialQty;

                        confirmModel = new ConfirmModel("Total ", NumberUtilities.getWithDecimalPlusCurrency(priceT));
                        confirmItems.add(confirmModel);
                        confirmModel = new ConfirmModel("Delivery Address ", productAddress.getText().toString());
                        confirmItems.add(confirmModel);

                        showFancyConfirm();
                        isReady = true;
                        showTransactionPin();
                    } else {
                        warningDialog("Please enter the delivery address");
                    }
                } else {
                    if (transaction_pin_base.getText().toString().isEmpty()) {
                        warningDialog("Please enter your transaction pin to continue");
                    } else {
                        btnBuy.setVisibility(View.GONE);
                        hideTransactionPin();
                        if (NetworkUtils.isConnected(ProductDetails.this)) {
                            String[] prods = extra.get(3).split("\\/");
                            String prodcutID = prods[prods.length - 2];
                            Log.debug("productID", prodcutID);
                            double priceT = initialPrice * initialQty;
                            completePurchase(session.getUserName(), session.getPhoneNumber(), prodcutID, productAddress.getText().toString(), extra.get(2), "0.00", String.valueOf(priceT),
                                    productQty.getText().toString(),
                                    NumberUtilities.getNumbersOnlyNoDecimal(account.getSelectedItem().toString()),
                                    "W",
                                    transaction_pin_base.getText().toString());
                        } else {
                            noInternetDialog();
                        }

                    }
                }
            }
        });

    }

    private List<Fragment> getFragments() {

        List<Fragment> fList = new ArrayList<Fragment>();

        if (extra.get(3) != null) {
            fList.add(ImageFragment.newInstance(extra.get(3)));
        }
        if (extra.get(4) != null) {
            fList.add(ImageFragment.newInstance(extra.get(4)));
        }
        if (extra.get(5) != null) {
            fList.add(ImageFragment.newInstance(extra.get(5)));
        }

        return fList;

    }

    private void completePurchase(final String username, final String mobile, final String productID, final String address, final String productamount, final String discountamount, final String finalamount, final String qty, final String accountNo, final String paymentmode, final String pin) {
        // @Path("/purchaseproduct/{username}/{mobile}/{productId}/{address}/{productamount}/{discountamount}/{finalamount}/{pin}")
        //@Path("/productpurchase/{username}/{mobile}/{productId}/{address}/{productamount}/{discountamount}/{finalamount}/{countofitem}/{accountNo}/{paymentmode}/{pin}")

        showConfirmProgress();

        String params = username + "/" + mobile + "/" + productID + "/" + address + "/" + productamount + "/" + discountamount + "/" + finalamount + "/" + qty + "/" + accountNo + "/" + paymentmode + "/" + pin;

        String url = SecurityLayer.genURLCBC("productpurchase", params, getApplicationContext());

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("purchaseproduct", response.body());
                //dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);
                    String full = response.raw().body().toString();

                    if (responsecode.equals("00")) {

                        showTransactionComplete(0, responsemessage);
                        //ResponseDialogs.successToActivity(getString(R.string.success), responsemessage, PayDueBills.this, DashBoard.class,new Bundle());

                    } else {
                        String msg = obj.optString("respMessage");
                        showTransactionComplete(1, msg);
                        //ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, PayDueBills.this);
                        //warningDialog(responsemessage);
                        //startDashBoard();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                buttonClose_base.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                //dismissProgress();
                showTransactionComplete(1, getString(R.string.error_server));
                buttonClose_base.setVisibility(View.VISIBLE);
                com.ceva.ubmobile.core.ui.Log.debug("billerfail", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }

        });

    }

}
