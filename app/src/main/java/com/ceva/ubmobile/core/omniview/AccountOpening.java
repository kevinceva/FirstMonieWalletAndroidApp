package com.ceva.ubmobile.core.omniview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.AccountProducts;

import java.util.ArrayList;
import java.util.List;

public class AccountOpening extends BaseActivity implements View.OnClickListener {
    List<String> checked = new ArrayList<>();
    List<AccountProducts> productsList = new ArrayList<AccountProducts>();
    // AccountProductsAdapter mAdapter;
    // ListView mListView;
    Button btnContinue;
    RadioGroup radioGroup;
    String[] productNames;
    String[] productCodes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountopening);
        setToolbarTitle(getString(R.string.page_account_open));
        //  mListView = (ListView) findViewById(R.id.accountsList);
        btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);
        productNames = getResources().getStringArray(R.array.account_products);
        productCodes = getResources().getStringArray(R.array.account_products_codes);
       /* mAdapter = new AccountProductsAdapter(this, R.id.product, productsList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                AccountProducts country = (AccountProducts) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + country.getProductName(),
                        Toast.LENGTH_LONG).show();
            }
        });*/
        radioGroup = findViewById(R.id.optionRadioGroup);
        feedProducts();

    }

    private void feedProducts() {

        int k = productNames.length;
        RadioButton[] rb = new RadioButton[k];
        for (int i = 0; i < k; i++) {
            Log.debug("product: " + productNames[i]);
            rb[i] = new RadioButton(this);
            rb[i].setText(productNames[i]);
            rb[i].setId(i);
            radioGroup.addView(rb[i]);
        }

    }
    /*public void onCheckBoxClick(View view) {
        CheckBox checkBox = (CheckBox) view;
        String tagName = "";
        if (checkBox.isChecked()) {
            tagName = checkBox.getTag().toString();

            checked.add(tagName);
        } else {
            tagName = checkBox.getTag().toString();
            checked.remove(tagName);

        }
    }*/

    public void goNext(View v) {
        startActivity(new Intent(this, OpenAccountValidation.class));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnContinue) {
            setChecked();
        }
    }

    private void setChecked() {
        Bundle bundle = new Bundle();
        StringBuffer responseText = new StringBuffer();

        int k = radioGroup.getCheckedRadioButtonId();

        Intent i = new Intent(this, OpenAccountValidation.class);
        bundle.putString(Constants.KEY_PRODUCT_BUNDLE, productNames[k]);
        bundle.putString(Constants.KEY_PRODUCT_NAMES, productNames[k]);
        bundle.putString(Constants.KEY_PRODUCT_CODES, productCodes[k]);

        i.putExtras(bundle);
        startActivity(i);

    }
}

