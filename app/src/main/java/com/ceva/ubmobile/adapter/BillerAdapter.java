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
import com.ceva.ubmobile.models.BillerModel;

import java.util.ArrayList;
import java.util.List;


public class BillerAdapter extends RecyclerView.Adapter<BillerAdapter.MyViewHolder> implements Filterable {

    private List<BillerModel> BillerList;
    private Context context;
    private List<BillerModel> orig;


    public BillerAdapter(List<BillerModel> BillerList, Context context) {
        this.BillerList = BillerList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.biller_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BillerModel tx = BillerList.get(position);
        holder.bind(tx);

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
                final List<BillerModel> results = new ArrayList<BillerModel>();
                if (orig == null)
                    orig = BillerList;
                if (constraint != null) {
                    if (orig != null & orig.size() > 0) {
                        for (final BillerModel g : orig) {
                            if (g.getBillerName().toLowerCase().contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                BillerList = (ArrayList<BillerModel>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txName;
        ImageView fancy;
        View view;


        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            txName = view.findViewById(R.id.biller_name);
            fancy = view.findViewById(R.id.image_view);
        }

        void bind(final BillerModel biller) {
            String name = biller.getBillerName();
            //String line2 = "TRANSFER TO ACCT "+tx.getTransactionAccount();
            txName.setText(name);
            view.setTag(biller);

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            //int color1 = generator.getRandomColor();
            int color = context.getResources().getColor(R.color.colorPrimary);
            // declare the builder object once.
            TextDrawable.IBuilder builder = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();
            TextDrawable ic1 = builder.build(String.valueOf(name.charAt(0)), color);
            fancy.setImageDrawable(ic1);
        }
    }
}