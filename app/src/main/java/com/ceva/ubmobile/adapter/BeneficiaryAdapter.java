package com.ceva.ubmobile.adapter;

/**
 * Created by brian on 20/09/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.models.Beneficiary;
import com.ceva.ubmobile.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BeneficiaryAdapter extends RecyclerView.Adapter<BeneficiaryAdapter.MyViewHolder> implements Filterable {

    public MyAdapterListener onClickListener;
    private List<Beneficiary> beneficiaryList;
    private Context context;
    private List<Beneficiary> orig;
    private Beneficiary currentItem;
    // private List<Beneficiary> filteredBeneficiaryList = new ArrayList<>();


    public BeneficiaryAdapter(List<Beneficiary> beneficiaryList, Context context, MyAdapterListener listener) {
        this.beneficiaryList = beneficiaryList;
        this.context = context;
        this.onClickListener = listener;
        // this.filteredBeneficiaryList = beneficiaryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beneficiary_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Beneficiary benef = beneficiaryList.get(position);
        holder.bind(benef);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<Beneficiary> results = new ArrayList<Beneficiary>();
                if (orig == null)
                    orig = beneficiaryList;
                if (constraint != null) {
                    if (orig != null & orig.size() > 0) {
                        for (final Beneficiary g : orig) {
                            if (g.getName().toLowerCase().contains(constraint.toString().toLowerCase()) || g.getBank().toLowerCase().contains(constraint.toString().toLowerCase()) || g.getAccountNumber().toLowerCase().contains(constraint.toString().toLowerCase()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                } else {
                    oReturn.values = orig;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //beneficiaryList.clear();
                setBeneficiaryList((List<Beneficiary>) results.values);
                notifyDataSetChanged();

            }
        };
    }

    private void setBeneficiaryList(List<Beneficiary> list) {
        this.beneficiaryList = list;
        //notifyDataSetChanged();
    }

    public List<Beneficiary> getFilteredBeneficiaryList() {
        return beneficiaryList;
    }

    @Override
    public int getItemCount() {
        return beneficiaryList.size();
    }

    public interface MyAdapterListener {

        void iconDeleteOnClick(View v, int position, Beneficiary beneficiary);

        void iconTransferOnClick(View v, int position, Beneficiary beneficiary);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView benefName, benefLine2, benefbank, benefaccountNumber, transactionAmount;
        ImageView fancy;
        @BindView(R.id.imageDelete)
        ImageButton delete;
        @BindView(R.id.imageTransfer)
        ImageButton transfer;


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setTag(getAdapterPosition());
            benefName = view.findViewById(R.id.beneficiary_name);
            benefbank = view.findViewById(R.id.beneficiary_bank);
            benefaccountNumber = view.findViewById(R.id.beneficiary_account);
            fancy = view.findViewById(R.id.image_view);

        }

        void bind(final Beneficiary benef) {  //<--bind method allows the ViewHolder to bind to the data it is displaying
            final String name = benef.getName();
            //String line2 = "TRANSFER TO ACCT "+benef.getTransactionAccount();
            String bank = benef.getBank();
            String accountNumber = benef.getAccountNumber();

            benefName.setText(name);
            // holder.benefLine2.setText(line2);
            benefbank.setText(bank);
            benefaccountNumber.setText(accountNumber);

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            //int color1 = generator.getRandomColor();
            int color = ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark);
            // declare the builder object once.
            TextDrawable.IBuilder builder = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();

            TextDrawable ic1 = builder.build(String.valueOf(name.charAt(0)), color);
            benefbank.setTextColor(color);
            fancy.setImageDrawable(ic1);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setTag(name);
                    onClickListener.iconDeleteOnClick(view, getAdapterPosition(), benef);
                }
            });

            transfer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.iconTransferOnClick(view, getAdapterPosition(), benef);
                }
            });
            currentItem = benef; //<-- keep a reference to the current item

        }
    }
}