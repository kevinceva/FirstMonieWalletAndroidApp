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
import com.ceva.ubmobile.models.ConfirmModel;

import java.util.List;


public class ConfirmAdapter extends RecyclerView.Adapter<ConfirmAdapter.MyViewHolder> {

    private List<ConfirmModel> ConfirmModelList;
    private Context context;

    public ConfirmAdapter(List<ConfirmModel> ConfirmModelList, Context context) {
        this.ConfirmModelList = ConfirmModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.confirm_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ConfirmModel tx = ConfirmModelList.get(position);

        String name = tx.getLabel();
        //String line2 = "TRANSFER TO ACCT "+tx.getTransactionAccount();
        String savings = tx.getValue();

        holder.label.setText(name);
        // holder.txLine2.setText(line2);
        holder.value.setText(savings);

        //holder.year.setText(card.getYear());
    }

    @Override
    public int getItemCount() {
        return ConfirmModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView label, value;
        ImageView pic;
        ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            label = view.findViewById(R.id.label);
            value = view.findViewById(R.id.value);

        }
    }
}