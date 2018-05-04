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

import butterknife.BindView;
import butterknife.ButterKnife;


public class MiniStatementAdapter extends RecyclerView.Adapter<MiniStatementAdapter.MyViewHolder> {

    private List<TransactionsModel> transactionList;
    private Context context;

    public MiniStatementAdapter(List<TransactionsModel> transactionList, Context context) {
        this.transactionList = transactionList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ministatement_row_revised, parent, false);

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

        String[] datemonth = tx.getTransactionDate().split("-");
        String date = datemonth[0];
        String month = datemonth[1];
        String[] titleNarr = tx.getNarrative().split("/");
        String title = titleNarr[0];
        String narration = "";

        for (int i = 0; i < titleNarr.length; i++) {
            if (i == 0) {

            } else {
                narration += titleNarr[i];
            }

        }
        narration = tx.getNarrative();

        String currency = Constants.KEY_NAIRA;

        /*1.    USD - $
        2.    Euro - €
        3.    Pound sterling - £
        4.    Swiss franc – CHF
        5.    China Yuan – CNY
        6.    Japanese Yen - ¥*/

        if (tx.getTransactionRef().equals("USD")) {
            currency = "$";
        } else if (tx.getTransactionRef().equals("GBP")) {
            currency = "£";
        } else if (tx.getTransactionRef().equals("JPY")) {
            currency = "¥";
        } else if (tx.getTransactionRef().equals("EUR")) {
            currency = "€";
        } else if (tx.getTransactionRef().equals("CHF")) {
            currency = "CHF";
        } else if (tx.getTransactionRef().equals("CNY")) {
            currency = "¥";
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

        if (tx.getTransactionType().equals("D")) {
            amount = amount.replace("-", "");
            amount = "-" + amount;
        } else {
            amount = "+" + amount;
        }

        Log.debug("amount in adapter ", amount);

        holder.txDate.setText(date);
        // holder.txLine2.setText(line2);
        holder.txMonth.setText(month);
        holder.txTitle.setText(title);
        holder.txTitle.setVisibility(View.GONE);
        holder.txNarration.setText(narration);

        holder.txAmount.setText(amount);
        if (tx.getTransactionType().equals("C")) {
            holder.txAmount.setTextColor(context.getResources().getColor(R.color.union_green));
        } else {
            holder.txAmount.setTextColor(context.getResources().getColor(R.color.red));
        }

        //holder.year.setText(card.getYear());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.date)
        TextView txDate;
        @BindView(R.id.month)
        TextView txMonth;
        @BindView(R.id.title)
        TextView txTitle;
        @BindView(R.id.narration)
        TextView txNarration;
        @BindView(R.id.amount)
        TextView txAmount;


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}