package com.ceva.ubmobile.adapter;

/**
 * Created by brian on 20/09/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.models.ShopItem;
import com.squareup.picasso.Picasso;

import java.util.List;


public class HotDealsAdapter extends RecyclerView.Adapter<HotDealsAdapter.MyViewHolder> {

    Context context;
    private List<ShopItem> shopItemList;

    public HotDealsAdapter(List<ShopItem> shopItemList, Context context) {
        this.shopItemList = shopItemList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hot_deals_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        ShopItem item = shopItemList.get(position);
        Picasso.with(context)
                .load(item.getImageUrl())
                .error(context.getResources().getDrawable(R.drawable.placeholder))
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
        String price = Constants.KEY_NAIRA + item.getPrice();
        viewHolder.price.setText(price);
        viewHolder.title.setText(item.getTitle());

        //holder.year.setText(card.getYear());
    }

    @Override
    public int getItemCount() {
        return shopItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView price;
        TextView title;
        ImageView pic;
        ProgressBar progressBar;


        public MyViewHolder(View convertView) {
            super(convertView);
            price = (TextView) convertView.findViewById(R.id.shop_price);
            title = (TextView) convertView.findViewById(R.id.shop_title);
            pic = (ImageView) convertView.findViewById(R.id.shop_image);
            progressBar = (ProgressBar) convertView.findViewById(R.id.shop_progress);
        }
    }
}