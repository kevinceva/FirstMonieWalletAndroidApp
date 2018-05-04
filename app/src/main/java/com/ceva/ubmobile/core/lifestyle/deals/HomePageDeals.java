package com.ceva.ubmobile.core.lifestyle.deals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.HomePageDealsAdapter;
import com.ceva.ubmobile.adapter.RecyclerItemClickListener;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.LandingPage;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.DealItem;
import com.ceva.ubmobile.models.ShopItem;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageDeals extends BaseActivity {

    HomePageDealsAdapter adapter;
    List<DealItem> items = new ArrayList<>();
    List<ShopItem> shopItemList = new ArrayList<>();
    @BindView(R.id.emptyView)
    LinearLayout emptyView;
    UBNSession session;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_deals);
        setToolbarTitle("Hot Deals");
        ButterKnife.bind(this);
        session = new UBNSession(this);

        recyclerView = findViewById(R.id.deals);
        adapter = new HomePageDealsAdapter(items, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        DealItem item = items.get(position);

                        Log.debug("starting activity", item.getBusinessName());
                        Intent intent = new Intent(HomePageDeals.this, HomePageDetails.class);
                        ArrayList<String> extras = new ArrayList<String>();
                        extras.add(item.getBusinessName());
                        extras.add(item.getSavingsTag());
                        extras.add(item.getImageUrl());
                        extras.add(item.getProductName());
                        extras.add(item.getDiscountPrice());
                        intent.putExtra("deal", item);
                        //extras.add(item.getThirdImage());

                        intent.putStringArrayListExtra(DealItem.KEY_DEALS, extras);
                        startActivity(intent);
                        /*List of items
                                0 = product name
                                1 = product desc
                                2 = product price
                                3 = product image 1
                                4 = image 2
                                5 = image 3*/

                    }
                }));
        // items.clear();
        //shopItemList.clear();

        if (session.getString("saved_deals") != null) {
            getSavedDeals(session.getString("saved_deals"));
        } else {
            fetchProducts();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_homepage_deals, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            fetchProducts();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.debug("deals back");
        startActivity(new Intent(this, LandingPage.class));

    }

    private void getSavedDeals(String deals) {
        items.clear();
        try {

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
                    if (items.size() > 1) {
                        GridLayoutManager lLayout = new GridLayoutManager(HomePageDeals.this, 2);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(lLayout);
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    fetchProducts();
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.Error(e);
        }
    }

    private void fetchProducts() {
        items.clear();
        showLoadingProgress();
        String params = "D";
        String url = "";
        try {
            url = SecurityLayer.beforeLogin(params, UUID.randomUUID().toString(), "onlineoffersdeallistBeforelogin", getApplicationContext());
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
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    obj = SecurityLayer.decryptBeforeLogin(obj, getApplicationContext());

                    session.setString("saved_deals", obj.toString());

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
                            if (items.size() > 1) {
                                GridLayoutManager lLayout = new GridLayoutManager(HomePageDeals.this, 2);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(lLayout);
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        ResponseDialogs.warningDialogLovelyToActivity(HomePageDeals.this, "Error", obj.optString(Constants.KEY_MSG), LandingPage.class);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ResponseDialogs.warningDialogLovelyToActivity(HomePageDeals.this, "Error", getString(R.string.error_server), LandingPage.class);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("ubnaccountsfail", t.toString());
                ResponseDialogs.warningDialogLovelyToActivity(HomePageDeals.this, "Error", getString(R.string.error_server), LandingPage.class);
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }
}

