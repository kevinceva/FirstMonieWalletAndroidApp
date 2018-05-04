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
import com.ceva.ubmobile.models.ShopItem;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ShopOnDashboard extends RecyclerView.Adapter<ShopOnDashboard.MyViewHolder> {

    Context context;
    private List<ShopItem> shopItemList;

    public ShopOnDashboard(List<ShopItem> shopItemList, Context context) {
        this.shopItemList = shopItemList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_row_2, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        ShopItem item = shopItemList.get(position);

        if (position == 0) {
            Picasso.with(context)
                    .load(R.drawable.gigm_logo)
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
        } else if (position == 1) {
            Picasso.with(context)
                    .load(R.drawable.lasg_revpay)
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
        } else {
            if (item.getTitle().equals("more")) {
                Picasso.with(context)
                        .load(R.drawable.ic_big_cart_blue)
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
            } else {
                Picasso.with(context)
                        .load(item.getImageUrl())
                        .error(R.drawable.placeholder)
                        .into(viewHolder.pic, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                //do smth when picture is loaded successfully
                                viewHolder.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                //do smth when there is picture loading error
                                viewHolder.progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        }
        viewHolder.price.setText(item.getPrice());
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
            price = convertView.findViewById(R.id.price);
            title = convertView.findViewById(R.id.shop_title);
            pic = convertView.findViewById(R.id.shop_image);
            progressBar = convertView.findViewById(R.id.shop_progress);
        }
    }
}