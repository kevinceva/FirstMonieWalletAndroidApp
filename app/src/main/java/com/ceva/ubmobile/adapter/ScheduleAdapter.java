package com.ceva.ubmobile.adapter;

/**
 * Created by brian on 20/09/2016.
 */

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.models.ScheduleModel;
import com.ceva.ubmobile.security.UBNSession;

import java.util.List;


public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {

    private List<ScheduleModel> transactionList;
    private Context context;

    public ScheduleAdapter(List<ScheduleModel> transactionList, Context context) {
        this.transactionList = transactionList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scheduled_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UBNSession session = new UBNSession(context);

        //List<Banks> banks = session.getBanks();
        ScheduleModel tx = transactionList.get(position);

        String toNametxt = tx.getBeneficiaryAccountName();
        String toAccounttxt = tx.getDestionBank() + " - " + tx.getBeneficiaryAccountNumber();
        String fromNametxt = session.getAccountName();
        String fromAccounttxt = tx.getMyAccountNumber();
        String amounttxt = tx.getAmount() + " - " + tx.getFrequency();
        String period = "From " + tx.getStartDate() + " To " + tx.getEndDate();

        holder.toName.setText(toNametxt);
        holder.toAccount.setText(toAccounttxt);
        holder.fromName.setText(fromNametxt);
        holder.fromAccount.setText(fromAccounttxt);
        holder.txAmount.setText(amounttxt);
        holder.txPeriod.setText(period);

        if (position % 2 == 0) {
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.clouds));
        }

        //holder.year.setText(card.getYear());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView toName, toAccount, fromName, fromAccount, txAmount, txPeriod;
        CardView card;


        public MyViewHolder(View view) {
            super(view);
            toName = view.findViewById(R.id.toName);
            toAccount = view.findViewById(R.id.toAccount);
            fromName = view.findViewById(R.id.fromName);
            fromAccount = view.findViewById(R.id.fromAccount);
            txAmount = view.findViewById(R.id.txAmount);
            txPeriod = view.findViewById(R.id.txPeriod);
            card = view.findViewById(R.id.schedule_card);

        }
    }
}