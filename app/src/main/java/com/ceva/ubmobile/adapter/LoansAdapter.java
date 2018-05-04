package com.ceva.ubmobile.adapter;

/**
 * Created by brian on 14/09/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.dashboard.LoanDetails;
import com.ceva.ubmobile.core.ui.dashboard.TermDepositDetails;
import com.ceva.ubmobile.models.AccountsModel;

import java.util.List;


public class LoansAdapter extends RecyclerView.Adapter<LoansAdapter.MyViewHolder> {

    Context context;
    private List<AccountsModel> cardsList;

    public LoansAdapter(List<AccountsModel> cardsList, Context context) {
        this.cardsList = cardsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.accounts_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AccountsModel tx = cardsList.get(position);
        holder.accountType.setText(tx.getAccountType());
        String accno = tx.getAccountNumber();
        holder.accountNumber.setText(accno);

        String currency = "";
        if (tx.getAccountCurrency() != null) {
            if (tx.getAccountCurrency().equals("USD")) {
                currency = "$";
            } else if (tx.getAccountCurrency().equals("GBP")) {
                currency = "£";
            } else if (tx.getAccountCurrency().equals("JPY")) {
                currency = "¥";
            } else if (tx.getAccountCurrency().equals("EUR")) {
                currency = "€";
            } else if (tx.getAccountCurrency().equals("CHF")) {
                currency = "CHF";
            } else if (tx.getAccountCurrency().equals("CNY")) {
                currency = "¥";
            } else {
                currency = Constants.KEY_NAIRA;
            }
            //19100
        }

        String amount = currency + tx.getAccountBalance();
        holder.accountBalance.setText(amount);
        if (tx.getAccountType().toLowerCase().contains("dom")) {
            //holder.accountBalance.setText("\u0024" + amount);
        } else {
            // holder.accountBalance.setText(Html.fromHtml("&#8358;") + amount);
        }
        //holder.year.setText(card.getYear());
    }

    @Override
    public int getItemCount() {
        return cardsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView accountType, accountNumber, accountBalance;


        public MyViewHolder(View view) {
            super(view);
            accountType = view.findViewById(R.id.accountType);
            accountNumber = view.findViewById(R.id.accountNumber);
            accountBalance = view.findViewById(R.id.accountBalance);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            // Toast.makeText(view.getContext(), "position = " + getPosition(), Toast.LENGTH_SHORT).show();
            int position = getAdapterPosition();
            Intent i = null;
            AccountsModel loan = cardsList.get(position);

            if (loan.isLoans()) {
                i = new Intent(context, LoanDetails.class);
            } else {
                i = new Intent(context, TermDepositDetails.class);
            }
            i.putExtra("payload", loan.getAccountCurrency());
            context.startActivity(i);

        }
    }
}