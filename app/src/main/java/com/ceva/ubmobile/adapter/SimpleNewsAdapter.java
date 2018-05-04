package com.ceva.ubmobile.adapter;

/**
 * Created by brian on 20/09/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.Log;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.toptas.rssconverter.RssItem;


public class SimpleNewsAdapter extends RecyclerView.Adapter<SimpleNewsAdapter.MyViewHolder> implements Filterable {

    int defaultImage;
    private List<RssItem> RSSItemList;
    private Context context;
    private List<RssItem> orig;

    public SimpleNewsAdapter(List<RssItem> RSSItemList, Context context, int defaultImage) {
        this.RSSItemList = RSSItemList;
        this.context = context;
        this.defaultImage = defaultImage;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_news_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        RssItem item = RSSItemList.get(position);

        String name = item.getTitle();
        String pic = item.getImage();
        String description = item.getDescription();

        holder.title.setText(name);
        // holder.itemLine2.setText(line2);
        try {
            holder.description.setText(Html.fromHtml(description));
        } catch (Exception e) {
            Log.Error(e);
        }
        holder.pic.setVisibility(View.GONE);
        Picasso.with(context)
                .load(pic)
                .error(context.getResources().getDrawable(defaultImage))
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
        if (name.toLowerCase().contains("union bank") || description.toLowerCase().contains("union bank")) {
            holder.progressBar.setVisibility(View.GONE);
            holder.pic.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(defaultImage)
                    .error(context.getResources().getDrawable(defaultImage)).into(holder.pic);
        }

        //holder.year.setText(card.getYear());
    }

    @Override
    public int getItemCount() {
        return RSSItemList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<RssItem> results = new ArrayList<>();
                if (orig == null)
                    orig = RSSItemList;
                if (constraint != null) {
                    if (orig != null & orig.size() > 0) {
                        for (final RssItem g : orig) {
                            if (g.getTitle().toLowerCase().contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                RSSItemList = (ArrayList<RssItem>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description;
        ImageView pic;
        ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            pic = view.findViewById(R.id.main_icon);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }
}