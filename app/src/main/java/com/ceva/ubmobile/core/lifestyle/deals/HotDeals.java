package com.ceva.ubmobile.core.lifestyle.deals;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.HotDealsAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.ShopItem;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotDeals extends BaseActivity {
    LinearLayout hot_deals;
    List<String> CATEGORY_IDs = new ArrayList<>();
    List<ShopItem> shopItemList = new ArrayList<>();
    String[] category_names = {"Phones and Accessories", "Electronics and Appliances", "Vehicles", "Agriculture and Food", "Home and Furniture"};
    int CAT_NO = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_deals);
        setToolbarTitle("Hot Deals");
        hot_deals = findViewById(R.id.hot_deals);
        //fetchCategories("Collins");

        int m = category_names.length;

        for (int i = 0; i < m; i++) {
            //if(i == 0){

            List<ShopItem> shopItemList = new ArrayList<>();

            shopItemList.add(new ShopItem("Smart Phone", "http://smart.com.ph/Postpaid/images/default-source/default-album/smart-postpaid-s7-phonethumbnail-01.png?sfvrsn=0", "5,400.00", "This is a smart phone"));
            shopItemList.add(new ShopItem("Beats by Dre", "http://img.bbystatic.com/BestBuy_US/en_US/images/abn/2015/global/buyingguides/RE_headphones/dj.png", "18,400.00", "This is a headphone"));
            shopItemList.add(new ShopItem("Seinheisser", "http://static.digit.in/default/b246bc043be079343e1d8c29b08f9636edb2a6ce.jpeg", "17,400.00", "This is a good pair"));
            shopItemList.add(new ShopItem("Panasonic TV", "https://www.thegoodguys.com.au/cs/groups/public/documents/graphic/50-inch-tv.png", "5,400.00", "This is a smart phone"));
            shopItemList.add(new ShopItem("Tomatoes", "http://www.grow-it-organically.com/images/tomato-variety-stupice-h2-l.jpg", "200.00", "This is a smart phone"));
            shopItemList.add(new ShopItem("California Couch", "https://tctechcrunch2011.files.wordpress.com/2016/08/burrow.png?w=1024&h=676", "205,400.00", "This is a smart phone"));

            // }
            dummyUI(category_names[i], shopItemList);
        }

    }

    private void dummyUI(String category, List<ShopItem> shopItems) {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        LLParams.gravity = Gravity.CENTER;
        LLParams.setMargins(2, 4, 2, 4);
        bar.setLayoutParams(LLParams);

        TextView categoryName = new TextView(this);
        categoryName.setText(category);
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f);
        params.gravity = Gravity.CENTER | Gravity.START;
        categoryName.setLayoutParams(params);
        categoryName.setPadding(4, 4, 4, 4);
        categoryName.setTextColor(getResources().getColor(R.color.colorPrimary));

        bar.addView(categoryName);

        TextView more = new TextView(this);
        params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        params.gravity = Gravity.CENTER | Gravity.END;
        more.setLayoutParams(params);

        more.setText("MORE");
        more.setTextColor(getResources().getColor(R.color.colorPrimary));

        bar.addView(more);
        hot_deals.addView(bar);

        /*List<ShopItem> shopItemList = new ArrayList<>();

        shopItemList.add(new ShopItem("Smart Phone","http://smart.com.ph/Postpaid/images/default-source/default-album/smart-postpaid-s7-phonethumbnail-01.png?sfvrsn=0", "5,400.00","This is a smart phone"));
        shopItemList.add(new ShopItem("Beats by Dre","http://img.bbystatic.com/BestBuy_US/en_US/images/abn/2015/global/buyingguides/RE_headphones/dj.png", "18,400.00","This is a headphone"));
        shopItemList.add(new ShopItem("Smart Phone","http://smart.com.ph/Postpaid/images/default-source/default-album/smart-postpaid-s7-phonethumbnail-01.png?sfvrsn=0", "5,400.00","This is a smart phone"));
        shopItemList.add(new ShopItem("Smart Phone","http://smart.com.ph/Postpaid/images/default-source/default-album/smart-postpaid-s7-phonethumbnail-01.png?sfvrsn=0", "5,400.00","This is a smart phone"));
        shopItemList.add(new ShopItem("Smart Phone","http://smart.com.ph/Postpaid/images/default-source/default-album/smart-postpaid-s7-phonethumbnail-01.png?sfvrsn=0", "5,400.00","This is a smart phone"));
        shopItemList.add(new ShopItem("Smart Phone","http://smart.com.ph/Postpaid/images/default-source/default-album/smart-postpaid-s7-phonethumbnail-01.png?sfvrsn=0", "5,400.00","This is a smart phone"));*/

        HotDealsAdapter hotDealsAdapter = new HotDealsAdapter(shopItems, this);
        RecyclerView recyclerView = new RecyclerView(this);
        LinearLayoutManager mLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        ViewGroup.LayoutParams recyclerparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400);

        mLayoutManager.generateLayoutParams(recyclerparams);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(hotDealsAdapter);

        hot_deals.addView(recyclerView);
    }

    private void fetchCategories(final String username) {

        CATEGORY_IDs.clear();

        String urlparam = "onlineCategoryList/" + username;
        String data = null;

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparam);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("onlineCategoryList", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    JSONArray array = new JSONArray(obj.optString("CATEGORY_LIST"));

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        for (int i = 0; i < array.length(); i++) {
                            shopItemList.clear();
                            final JSONObject un = array.getJSONObject(i);
                            CATEGORY_IDs.add(un.optString("CATEGORY_ID"));
                            String urlparam = "onlineSubCategoryList/" + username + "/" + un.optString("CATEGORY_ID");
                            String data = null;

                            ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
                            Call<String> call1 = apiService.setGenericRequestRaw(urlparam);

                            call1.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    Log.debug("onlineSubCategoryList", response.body());
                                    dismissProgress();

                                    try {
                                        JSONObject obj = new JSONObject(response.body());
                                        JSONArray array = new JSONArray(obj.optString("SUB_CATEGORY_LIST"));

                                        if (obj.optString(Constants.KEY_CODE).equals("00")) {
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject un1 = array.getJSONObject(i);
                                                //fetchProducts(username,"na",category_id,un.optString("SUB_CATEGORY_ID"));
                                                String urlparam = "onlineproductlist/" + username + "/na" + "/" + un.optString("CATEGORY_ID") + "/" + un1.optString("SUB_CATEGORY_ID");
                                                String data = null;

                                                ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
                                                Call<String> call2 = apiService.setGenericRequestRaw(urlparam);

                                                call2.enqueue(new Callback<String>() {
                                                    @Override
                                                    public void onResponse(Call<String> call, Response<String> response) {
                                                        Log.debug("onlineSubCategoryList", response.body());
                                                        dismissProgress();

                                                        try {
                                                            JSONObject obj = new JSONObject(response.body());
                                                            JSONArray array = new JSONArray(obj.optString("PRODUCT_LIST"));

                                                            if (obj.optString(Constants.KEY_CODE).equals("00")) {
                                                                for (int i = 0; i < array.length(); i++) {

                                                                    JSONObject un = array.getJSONObject(i);
                                                                    ShopItem item = new ShopItem(un.optString("PRODUCT_NAME"),
                                                                            Constants.NET_URL + Constants.IMAGE_DOWNLOAD_PATH + un.optString("IMAGE1"),
                                                                            un.optString("PRODUCT_PRICE"), un.optString("PRODUCT_DESC"));

                                                                    shopItemList.add(item);

                                                                }

                                                            } else {
                                                                ResponseDialogs.warningDialogLovely(HotDeals.this, "Error", obj.optString(Constants.KEY_MSG));
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

                                        } else {
                                            ResponseDialogs.warningDialogLovely(HotDeals.this, "Error", obj.optString(Constants.KEY_MSG));
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

                            dummyUI(un.optString("CATEGORY_DESC"), shopItemList);

                        }

                    } else {
                        ResponseDialogs.warningDialogLovely(HotDeals.this, "Error", obj.optString(Constants.KEY_MSG));
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

    /*private void fetchCategories(final String username){

        CATEGORY_IDs.clear();

        String urlparam = "onlineCategoryList/"+username;
        String data = null;

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparam);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("onlineCategoryList",response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    JSONArray array = new JSONArray(obj.optString("CATEGORY_LIST"));



                    if(obj.optString(Constants.KEY_CODE).equals("00")) {
                        for (int i = 0; i < array.length(); i++) {
                            //shopItemList.clear();
                            JSONObject un = array.getJSONObject(i);
                            CATEGORY_IDs.add(un.optString("CATEGORY_ID"));
                            fetchSubcategories(username,un.optString("CATEGORY_ID"));

                        }

                    }else{
                        ResponseDialogs.warningDialogLovely(HotDeals.this,"Error",obj.optString(Constants.KEY_MSG));
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

    private void fetchSubcategories(final String username, final String category_id){
        String urlparam = "onlineSubCategoryList/"+username+"/"+category_id;
        String data = null;

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparam);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("onlineSubCategoryList",response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    JSONArray array = new JSONArray(obj.optString("SUB_CATEGORY_LIST"));

                    if(obj.optString(Constants.KEY_CODE).equals("00")) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject un = array.getJSONObject(i);
                             fetchProducts(username,"na",category_id,un.optString("SUB_CATEGORY_ID"));

                        }

                    }else{
                        ResponseDialogs.warningDialogLovely(HotDeals.this,"Error",obj.optString(Constants.KEY_MSG));
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

    private void fetchProducts(String username,String msisdn,String category_id, String subcategoryid){
        String urlparam = "onlineproductlist/"+username+"/"+msisdn+"/"+category_id+"/"+subcategoryid;
        String data = null;

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparam);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("onlineSubCategoryList",response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    JSONArray array = new JSONArray(obj.optString("PRODUCT_LIST"));

                    if(obj.optString(Constants.KEY_CODE).equals("00")) {
                        for (int i = 0; i < array.length(); i++) {


                            JSONObject un = array.getJSONObject(i);
                            ShopItem item = new ShopItem(un.optString("PRODUCT_NAME"),
                                    Constants.NET_URL+Constants.IMAGE_DOWNLOAD_PATH+un.optString("IMAGE1"),
                                    un.optString("PRODUCT_PRICE"),un.optString("PRODUCT_DESC"));

                            shopItemList.add(item);

                        }

                    }else{
                        ResponseDialogs.warningDialogLovely(HotDeals.this,"Error",obj.optString(Constants.KEY_MSG));
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
    }*/

}
