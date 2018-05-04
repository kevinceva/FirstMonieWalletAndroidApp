package com.ceva.ubmobile.adapter;

/**
 * Created by brian on 20/09/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.models.InvestmentModel;

import java.util.List;


public class InvestmentAdapter extends RecyclerView.Adapter<InvestmentAdapter.MyViewHolder> {

    private List<InvestmentModel> investmentList;
    private Context context;

    public InvestmentAdapter(List<InvestmentModel> investmentList, Context context) {
        this.investmentList = investmentList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.investment_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        InvestmentModel tx = investmentList.get(position);

        String account = tx.getAccount();
        String amount = tx.getAmount();
        String details = tx.getDetails();
        String description = tx.getDescription();

        if (position % 2 == 0) {
            holder.row.setBackgroundColor(context.getResources().getColor(R.color.clouds));
        } else {
            holder.row.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        holder.txtAccount.setText(account);
        holder.txtDesc.setText(description);
        holder.txtDetails.setText(details);
        holder.txtAmount.setText(amount);

        //holder.year.setText(card.getYear());
    }

    @Override
    public int getItemCount() {
        return investmentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtAccount, txtDesc, txtAmount, txtDetails;
        LinearLayout row;


        public MyViewHolder(View view) {
            super(view);
            txtAccount = view.findViewById(R.id.txtAccount);
            txtDesc = view.findViewById(R.id.txtDesc);
            txtAmount = view.findViewById(R.id.txtAmount);
            txtDetails = view.findViewById(R.id.txtDetails);
            row = view.findViewById(R.id.investment_row);
        }
    }
}