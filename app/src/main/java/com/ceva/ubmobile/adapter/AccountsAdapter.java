package com.ceva.ubmobile.adapter;

/**
 * Created by brian on 20/09/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.MiniStatement;
import com.ceva.ubmobile.models.AccountsModel;

import java.util.List;


public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.MyViewHolder> {

    Context context;
    private List<AccountsModel> cardsList;

    public AccountsAdapter(List<AccountsModel> cardsList, Context context) {
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
        String accno = "******" + tx.getAccountNumber().substring(6);
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
            Intent i = new Intent(context, MiniStatement.class);
            String currency = "";
            if (cardsList.get(position).getAccountCurrency() != null) {
                if (cardsList.get(position).getAccountCurrency().equals("USD")) {
                    currency = "$";
                } else if (cardsList.get(position).getAccountCurrency().equals("GBP")) {
                    currency = "£";
                } else if (cardsList.get(position).getAccountCurrency().equals("JPY")) {
                    currency = "¥";
                } else if (cardsList.get(position).getAccountCurrency().equals("EUR")) {
                    currency = "€";
                } else if (cardsList.get(position).getAccountCurrency().equals("CHF")) {
                    currency = "CHF";
                } else if (cardsList.get(position).getAccountCurrency().equals("CNY")) {
                    currency = "¥";
                } else {
                    currency = Constants.KEY_NAIRA;
                }
                i.putExtra(Constants.KEY_ACCOUNTNUMBER, cardsList.get(position).getAccountNumber());
                i.putExtra(Constants.KEY_CLEAREDBALANCE, currency + cardsList.get(position).getAccountBalance());
                i.putExtra(Constants.KEY_ACCOUNTPRODUCT, cardsList.get(position).getAccountType());
                i.putExtra(Constants.KEY_ACCOUNTCURRENCY, cardsList.get(position).getAccountCurrency());
                AccountsModel acc = cardsList.get(position);
                i.putExtra("AccountsModel", acc);
                Log.d("ubnaccnumbadapter", cardsList.get(position).getAccountNumber());
                context.startActivity(i);
            }

        }
    }
}