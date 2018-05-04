package com.ceva.ubmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.models.ShopItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by brian on 30/10/2016.
 */

public class ShopAdapter extends ArrayAdapter<ShopItem> {
    Context context;

    public ShopAdapter(Context context, List<ShopItem> items) {
        super(context, R.layout.shop_row, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ShopItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.shop_row, parent, false);
            viewHolder.price = (TextView) convertView.findViewById(R.id.shop_price);
            viewHolder.title = (TextView) convertView.findViewById(R.id.shop_title);
            viewHolder.pic = (ImageView) convertView.findViewById(R.id.shop_image);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.shop_progress);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        Picasso.with(context)
                .load(item.getImageUrl())
                .into(viewHolder.pic, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //do smth when picture is loaded successfully
                        viewHolder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        //do smth when there is picture loading error
                    }
                });
        viewHolder.price.setText(item.getPrice());
        viewHolder.title.setText(item.getTitle());

        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView price;
        TextView title;
        ImageView pic;
        ProgressBar progressBar;
    }
}