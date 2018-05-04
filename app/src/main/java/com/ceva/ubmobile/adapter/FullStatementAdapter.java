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
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.models.FullStatementModel;

import java.util.List;


public class FullStatementAdapter extends RecyclerView.Adapter<FullStatementAdapter.MyViewHolder> {

    private List<FullStatementModel> transactionList;
    private Context context;

    public FullStatementAdapter(List<FullStatementModel> transactionList, Context context) {
        this.transactionList = transactionList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.full_statement_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FullStatementModel state = transactionList.get(position);

        String line1 = state.getDate();
        String line2 = state.getDesc();
        String line3 = state.getDeposit();
        String line4 = state.getWithdrawal();
        String amount = state.getBalance();
        String currency = state.getCurrency();

        if (currency.equals("NGN")) {
            currency = Constants.KEY_NAIRA;
        } else if (currency.equals("USD")) {
            currency = "$";
        }

        if (position % 2 == 0) {
            holder.row.setBackgroundColor(context.getResources().getColor(R.color.clouds));
        } else {
            holder.row.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        amount = currency + amount;
        holder.transactionDate.setText(line1);
        holder.stateDesc.setText(line2);
        holder.stateDeposit.setText(currency + line3);
        holder.stateDeposit.setTextColor(context.getResources().getColor(R.color.union_green));
        holder.stateWithdraw.setText(currency + line4);
        holder.stateWithdraw.setTextColor(context.getResources().getColor(R.color.union_red));
        holder.stateBalance.setText(amount);
        holder.stateBalance.setTextColor(context.getResources().getColor(R.color.colorPrimary));

        //holder.year.setText(card.getYear());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView transactionDate, stateDesc, stateDeposit, stateWithdraw, stateBalance;
        LinearLayout row;


        public MyViewHolder(View view) {
            super(view);
            transactionDate = (TextView) view.findViewById(R.id.date);
            stateDesc = (TextView) view.findViewById(R.id.narration);
            stateDeposit = (TextView) view.findViewById(R.id.deposit);
            stateWithdraw = (TextView) view.findViewById(R.id.withdrawal);
            stateBalance = (TextView) view.findViewById(R.id.balance);
            row = (LinearLayout) view.findViewById(R.id.row);
        }
    }
}