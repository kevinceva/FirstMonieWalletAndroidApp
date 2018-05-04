package com.ceva.ubmobile.adapter;

/**
 * Created by brian on 20/09/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceva.ubmobile.R;

import java.util.List;


public class SimpleListAdapter extends RecyclerView.Adapter<SimpleListAdapter.MyViewHolder> {

    Context context;
    private List<String> simpleList;

    public SimpleListAdapter(List<String> simpleList, Context context) {
        this.simpleList = simpleList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.branch_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String string = simpleList.get(position);
        holder.title.setText(string);
        //holder.year.setText(card.getYear());
    }

    @Override
    public int getItemCount() {
        return simpleList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.branchName);
            //view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            // Toast.makeText(view.getContext(), "position = " + getPosition(), Toast.LENGTH_SHORT).show();
            int position = getAdapterPosition();
            //Intent i = new Intent(context, MiniStatement.class);

            //context.startActivity(i);
        }

    }
}