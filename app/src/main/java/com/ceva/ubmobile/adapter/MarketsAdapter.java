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
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.models.MarketsModel;

import java.util.ArrayList;
import java.util.List;


public class MarketsAdapter extends RecyclerView.Adapter<MarketsAdapter.MyViewHolder> implements Filterable {

    private List<MarketsModel> BillerList;
    private Context context;
    private List<MarketsModel> orig;

    public MarketsAdapter(List<MarketsModel> BillerList, Context context) {
        this.BillerList = BillerList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.markets_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MarketsModel tx = BillerList.get(position);

        String name = tx.getCategory();
        //String line2 = "TRANSFER TO ACCT "+tx.getTransactionAccount();

        holder.txName.setText(name);

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color = generator.getRandomColor();
        //int color = context.getResources().getColor(R.color.colorPrimary);
        // declare the builder object once.
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .endConfig()
                .rect();
        TextDrawable ic1 = builder.build(String.valueOf(name.charAt(0)), color);
        holder.fancy.setImageDrawable(ic1);

        //holder.year.setText(card.getYear());
    }

    @Override
    public int getItemCount() {
        return BillerList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<MarketsModel> results = new ArrayList<MarketsModel>();
                if (orig == null)
                    orig = BillerList;
                if (constraint != null) {
                    if (orig != null & orig.size() > 0) {
                        for (final MarketsModel g : orig) {
                            if (g.getCategory().toLowerCase().contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                BillerList = (ArrayList<MarketsModel>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txName;
        ImageView fancy;


        public MyViewHolder(View view) {
            super(view);
            txName = view.findViewById(R.id.biller_name);
            fancy = view.findViewById(R.id.image_view);
        }
    }
}