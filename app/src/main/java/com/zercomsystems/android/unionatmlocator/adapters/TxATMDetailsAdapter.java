package com.zercomsystems.android.unionatmlocator.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.zercomsystems.android.unionatmlocator.helpers.LocationType;
import com.zercomsystems.android.unionatmlocator.models.ATM;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by android on 21/07/2017.
 */

public class TxATMDetailsAdapter extends RecyclerView.Adapter<TxATMDetailsAdapter.Holder> {

    private ArrayList<ATM> atms;
    private int selected;
    private int unselected;
    private TxATMPicker listener;
    private Context context;
    private LocationType type;


    public void setListener(TxATMPicker listener) {
        this.listener = listener;
    }

    public TxATMDetailsAdapter(ArrayList<ATM> atms, Context context, LocationType type) {
        if (atms == null)
            throw new RuntimeException("adapter can to contain null");
        selected = ContextCompat.getColor(context, R.color.on_item_clicked);
        unselected = ContextCompat.getColor(context, R.color.white);
        this.context = context;
        // make top the current selection
        atms.get(0).isSelected = true;
        this.type = type;
        this.atms = atms;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.xtxatmsdetails, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ATM atm = atms.get(position);

        holder.desc.setText(atm.getAddress());
        String name;

        if (type == LocationType.ATM) {
            name = "ATM Terminal";
        } else if (type == LocationType.BRANCH) {
            name = "Branch";
        } else if (type == LocationType.SMART_BRANCH) {
            name = "Smart Branch";
        } else {
            name = atm.getName();
        }

        holder.header.setText(String.format(Locale.ENGLISH, "%s %d", name, ++position));
        holder.atm = atm;
        if (atm.isSelected) {
            holder.backgroundImg.setBackgroundColor(selected);
        } else {
            holder.backgroundImg.setBackgroundColor(unselected);
        }

    }

    @Override
    public int getItemCount() {
        return atms.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.tx_list_background)
        View backgroundImg;
        @BindView(R.id.tx_terminal_name)
        TextView header;
        @BindView(R.id.tx_terminal_desc)
        TextView desc;
        private ATM atm;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.tx_list_background)
        public void onClick(View background) {
            background.setBackgroundColor(selected);
            // deselected other atms
            for (ATM m : atms) {
                m.isSelected = false;
            }
            // select yours
            atm.isSelected = true;
            listener.onATMClicked(atm);
            // notify the viewer
            notifyDataSetChanged();
        }

    }

    public interface TxATMPicker {
        void onATMClicked(ATM atm);
    }
}
