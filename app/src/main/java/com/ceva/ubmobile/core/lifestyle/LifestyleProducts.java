package com.ceva.ubmobile.core.lifestyle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.DealAdapter;
import com.ceva.ubmobile.adapter.RecyclerItemClickListener;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.DealItem;
import com.ceva.ubmobile.models.MarketsModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LifestyleProducts extends BaseActivity {
    //AsymmetricGridView listView;
    DealAdapter adapter;
    List<DealItem> items = new ArrayList<>();
    String category_id = null, subcategory_id = null;
    UBNSession session;
    private RecyclerView recyclerView;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifestyle_products);
        setToolbarTitle("Products");
        session = new UBNSession(this);
        searchView = findViewById(R.id.searchView);
        category_id = getIntent().getStringExtra(MarketsModel.KEY_CAT_ID);
        subcategory_id = getIntent().getStringExtra(MarketsModel.KEY_SUBCAT_ID);
        recyclerView = findViewById(R.id.deals);
        adapter = new DealAdapter(items, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        fetchProducts(session.getUserName(), session.getPhoneNumber(), category_id, subcategory_id);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapter.getFilter().filter("");
                } else {
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        DealItem item = items.get(position);
                        Intent intent = new Intent(LifestyleProducts.this, ProductDetails.class);
                        ArrayList<String> extras = new ArrayList<String>();
                        extras.add(item.getBusinessName());
                        extras.add(item.getDescription());
                        extras.add(item.getSavingsTag());
                        extras.add(item.getImageUrl());
                        extras.add(item.getSecondImage());
                        extras.add(item.getThirdImage());

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
    }

    private void dummyDeals() {
        items.clear();
        DealItem item = new DealItem("http://order.chicken-republic.com/skin/frontend/food/chicken/images/logo.png",
                "Chicken Republic",
                "Save " + Constants.KEY_NAIRA + "400");
        items.add(item);

        item = new DealItem("https://s3-eu-west-1.amazonaws.com/punchng/wp-content/uploads/2016/11/15213026/SPAR-Nigeria.jpg",
                "Spar Supermarket",
                "Save " + Constants.KEY_NAIRA + "75");
        items.add(item);

        item = new DealItem("https://res.cloudinary.com/hello-world/image/upload/jw5pd3l6eucr6hhyrc78",
                "Hard Rock Cafe",
                "Save " + Constants.KEY_NAIRA + "1000");
        items.add(item);

        item = new DealItem("https://pbs.twimg.com/profile_images/458941762858274817/lbBNE4p__400x400.png",
                "Shoprite",
                "Save " + Constants.KEY_NAIRA + "999");
        items.add(item);

        item = new DealItem("http://isen.northwestern.edu/sites/default/files/Total_1.png",
                "Total",
                "Save " + Constants.KEY_NAIRA + "10");
        items.add(item);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Markets.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent i = new Intent(getApplicationContext(), Markets.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchProducts(String username, String msisdn, String category_id, String subcategoryid) {
        showLoadingProgress();
        String urlparam = username + "/" + category_id + "/" + subcategoryid;
        String url = SecurityLayer.genURLCBC("onlineproductlist", urlparam, getApplicationContext());

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

                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());

                    JSONArray array = new JSONArray(obj.optString("PRODUCT_LIST"));

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject un = array.getJSONObject(i);
                            String imageURL = Constants.NET_URL + Constants.IMAGE_DOWNLOAD_PATH + un.optString("PRODUCT_ID") + "/" + un.optString("IMG1");
                            // String imageURL2 = Constants.NET_URL + Constants.IMAGE_DOWNLOAD_PATH + un.optString("PRODUCT_ID") + "/" + un.optString("IMG3") + ".jpg";
                            //String imageURL3 = Constants.NET_URL + Constants.IMAGE_DOWNLOAD_PATH + un.optString("PRODUCT_ID") + "/" + un.optString("IMG4") + ".jpg";
                            Log.debug("productimage", imageURL);

                            DealItem item = new DealItem(imageURL,
                                    un.optString("PRODUCT_NAME"), un.optString("PRODUCT_PRICE"), null, null, un.optString("PRODUCT_DESC"));

                            items.add(item);

                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        ResponseDialogs.warningDialogLovely(LifestyleProducts.this, "Error", obj.optString(Constants.KEY_MSG));
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
}
