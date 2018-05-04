package com.ceva.ubmobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.models.DealItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by brian on 23/03/2017.
 */

public class HomePageDealsAdapter extends RecyclerView.Adapter<HomePageDealsAdapter.MyViewHolder> implements Filterable {

    private List<DealItem> DealItemList;
    private Context context;
    private List<DealItem> orig;

    public HomePageDealsAdapter(List<DealItem> DealItemList, Context context) {
        this.DealItemList = DealItemList;
        this.context = context;
    }

    @Override
    public HomePageDealsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_page_deals_row, parent, false);

        return new HomePageDealsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HomePageDealsAdapter.MyViewHolder holder, int position) {
        DealItem tx = DealItemList.get(position);

        String name = tx.getBusinessName();
        //String line2 = "TRANSFER TO ACCT "+tx.getTransactionAccount();
        String savings = tx.getSavingsTag();
        String pic = tx.getImageUrl();

        holder.businessName.setText(name);
        holder.price.setText(tx.getDiscountPrice());
        holder.price.setVisibility(View.GONE);
        holder.productName.setText(tx.getProductName());
        holder.savingsTag.setText(savings);
        holder.pic.setVisibility(View.GONE);
        Picasso.with(context)
                .load(pic)
                .error(context.getResources().getDrawable(R.drawable.placeholder))
                .into(holder.pic, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //do smth when picture is loaded successfully
                        holder.progressBar.setVisibility(View.GONE);
                        holder.pic.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        //do smth when there is picture loading error
                        holder.progressBar.setVisibility(View.GONE);
                        holder.pic.setVisibility(View.VISIBLE);
                    }
                });

        //holder.year.setText(card.getYear());
    }

    @Override
    public int getItemCount() {
        return DealItemList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<DealItem> results = new ArrayList<DealItem>();
                if (orig == null)
                    orig = DealItemList;
                if (constraint != null) {
                    if (orig != null & orig.size() > 0) {
                        for (final DealItem g : orig) {
                            if (g.getBusinessName().toLowerCase().contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                DealItemList = (ArrayList<DealItem>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView businessName, savingsTag;
        ImageView pic;
        ProgressBar progressBar;
        @BindView(R.id.productName)
        TextView productName;
        @BindView(R.id.price)
        TextView price;


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            businessName = view.findViewById(R.id.businessName);
            savingsTag = view.findViewById(R.id.savingsTag);
            pic = view.findViewById(R.id.dealPic);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }
}