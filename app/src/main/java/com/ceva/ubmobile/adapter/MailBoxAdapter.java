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
import com.ceva.ubmobile.models.MailBoxModel;

import java.util.List;


public class MailBoxAdapter extends RecyclerView.Adapter<MailBoxAdapter.MyViewHolder> {

    Context context;
    private List<MailBoxModel> mailList;

    public MailBoxAdapter(List<MailBoxModel> mailList, Context context) {
        this.mailList = mailList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mailbox_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MailBoxModel tx = mailList.get(position);
        holder.mailboxtitle.setText(tx.getTitle());
        holder.mailboxdate.setText(tx.getDate());
        //holder.year.setText(card.getYear());
    }

    @Override
    public int getItemCount() {
        return mailList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mailboxtitle, mailboxdate;


        public MyViewHolder(View view) {
            super(view);
            mailboxtitle = (TextView) view.findViewById(R.id.mailbox_title);
            mailboxdate = (TextView) view.findViewById(R.id.mailbox_date);
            view.setOnClickListener(this);

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