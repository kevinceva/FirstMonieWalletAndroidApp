package com.ceva.ubmobile.adapter;

/**
 * Created by brian on 20/09/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.models.BottomMenuItem;

import java.util.List;


public class BottomMenuAdapter extends RecyclerView.Adapter<BottomMenuAdapter.MyViewHolder> {

    Context context;
    private List<BottomMenuItem> BottomMenuItemList;

    public BottomMenuAdapter(List<BottomMenuItem> BottomMenuItemList, Context context) {
        this.BottomMenuItemList = BottomMenuItemList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottom_menu_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        BottomMenuItem item = BottomMenuItemList.get(position);
        viewHolder.pic.setImageResource(item.getIcon());
        viewHolder.title.setText(item.getTitle());

    }

    @Override
    public int getItemCount() {
        return BottomMenuItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // TextView price;
        TextView title;
        ImageView pic;


        public MyViewHolder(View convertView) {
            super(convertView);
            //price = (TextView) convertView.findViewById(R.id.shop_price);
            title = (TextView) convertView.findViewById(R.id.menuText);
            pic = (ImageView) convertView.findViewById(R.id.menuIcon);

        }
    }
}