package com.ceva.ubmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.AccountProducts;

import java.util.List;

/**
 * Created by brian on 17/11/2016.
 */

public class AccountProductsAdapter extends ArrayAdapter<AccountProducts> {

    Context context;
    private List<AccountProducts> productList;

    public AccountProductsAdapter(Context context, int textViewResourceId, List<AccountProducts> productList) {
        super(context, textViewResourceId, productList);
        this.productList = productList;
        //this.productList.addAll(productList);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.debug("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.account_products_row, null);

            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.product);

            convertView.setTag(holder);

            holder.name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    AccountProducts product = (AccountProducts) cb.getTag();

                    product.setSelected(cb.isChecked());

                }
            });

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (productList.size() > 0) {
            AccountProducts product = productList.get(position);
            holder.name.setText(product.getProductName());
            holder.name.setChecked(product.isSelected());
            holder.name.setTag(product);
        }

        return convertView;

    }

    private class ViewHolder {
        CheckBox name;

    }

}
