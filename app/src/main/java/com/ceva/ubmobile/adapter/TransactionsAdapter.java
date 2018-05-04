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
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.TransactionsModel;

import java.util.List;


public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.MyViewHolder> {

    private List<TransactionsModel> transactionList;
    private Context context;

    public TransactionsAdapter(List<TransactionsModel> transactionList, Context context) {
        this.transactionList = transactionList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TransactionsModel tx = transactionList.get(position);

        String line1 = tx.getTransactionDate() + " " + tx.getTransactionType();
        //String line2 = "TRANSFER TO ACCT "+tx.getTransactionAccount();
        String line3 = tx.getNarrative();
        String line4 = tx.getTransactionRef();
        String amount = "";

        String currency = Constants.KEY_NAIRA;

        if (tx.getTransactionRef().equals("USD")) {
            currency = "$";
        }

        if (tx.getTransactionAmount() == null || tx.getTransactionAmount().equals("null")) {
            if (tx.getTransactionAmountWithdraw() == null || tx.getTransactionAmountWithdraw().equals("null")) {
                amount = tx.getTransactionAmountLodgement();
            } else {
                amount = tx.getTransactionAmountWithdraw();
            }

        } else {
            amount = tx.getTransactionAmount();
        }

        amount = currency + amount;

        Log.debug("amount in adapter ", amount);

        holder.transactionDate.setText(line1);
        // holder.txLine2.setText(line2);
        holder.txLine2.setVisibility(View.GONE);
        holder.txLine3.setText(line3);
        holder.txLine4.setText(line4);

        holder.transactionAmount.setText(amount);
        if (tx.getTransactionType().equals("C")) {
            holder.transactionAmount.setTextColor(context.getResources().getColor(R.color.union_green));
        } else {
            holder.transactionAmount.setTextColor(context.getResources().getColor(R.color.red));
        }

        //holder.year.setText(card.getYear());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView transactionDate, txLine2, txLine3, txLine4, transactionAmount;


        public MyViewHolder(View view) {
            super(view);
            transactionDate = (TextView) view.findViewById(R.id.transactionDate);
            txLine2 = (TextView) view.findViewById(R.id.txLine2);
            txLine3 = (TextView) view.findViewById(R.id.txLine3);
            txLine4 = (TextView) view.findViewById(R.id.txLine4);
            transactionAmount = (TextView) view.findViewById(R.id.transactionAmount);
        }
    }
}