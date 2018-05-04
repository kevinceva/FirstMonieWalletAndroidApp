package com.ceva.ubmobile.adapter;

/**
 * Created by brian on 20/09/2016.
 */

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
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.models.DealItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class DealAdapter extends RecyclerView.Adapter<DealAdapter.MyViewHolder> implements Filterable {

    private List<DealItem> DealItemList;
    private Context context;
    private List<DealItem> orig;

    public DealAdapter(List<DealItem> DealItemList, Context context) {
        this.DealItemList = DealItemList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deals_row_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        DealItem tx = DealItemList.get(position);

        String name = tx.getBusinessName();
        //String line2 = "TRANSFER TO ACCT "+tx.getTransactionAccount();
        String savings = tx.getSavingsTag();
        String pic = tx.getImageUrl();

        holder.businessName.setText(name);
        // holder.txLine2.setText(line2);
        holder.savingsTag.setText(Constants.KEY_NAIRA + savings);
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

        public MyViewHolder(View view) {
            super(view);
            businessName = view.findViewById(R.id.businessName);
            savingsTag = view.findViewById(R.id.savingsTag);
            pic = view.findViewById(R.id.dealPic);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }
}