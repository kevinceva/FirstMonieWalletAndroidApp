package com.ceva.ubmobile.adapter;

/**
 * Created by brian on 20/04/2017.
 */

/*
public class BeneficiaryListViewAdapter extends BaseAdapter {

    public class ViewHolder {
        public TextView benefName, benefLine2, benefbank, benefaccountNumber, transactionAmount;
        ImageView fancy;
        @BindView(R.id.imageDelete)
        ImageButton delete;
        @BindView(R.id.imageTransfer)
        ImageButton transfer;


    }


    public List<Post> parkingList;

    public Context context;
    ArrayList<Post> arraylist;

    private BeneficiaryListViewAdapter (List<Post> apps, Context context) {
        this.parkingList = apps;
        this.context = context;
        arraylist = new ArrayList<Post>();
        arraylist.addAll(parkingList);

    }

    @Override
    public int getCount() {
        return parkingList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ViewHolder viewHolder;

        if (rowView == null) {
            LayoutInflater inflater = getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_post, null);
            // configure view holder
            viewHolder = new ViewHolder();
            viewHolder.txtTitle = (TextView) rowView.findViewById(R.id.title);
            viewHolder.txtSubTitle = (TextView) rowView.findViewById(R.id.subtitle);
            rowView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtTitle.setText(parkingList.get(position).getPostTitle() + "");
        viewHolder.txtSubTitle.setText(parkingList.get(position).getPostSubTitle() + "");
        return rowView;


    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        parkingList.clear();
        if (charText.length() == 0) {
            parkingList.addAll(arraylist);

        } else {
            for (Post postDetail : arraylist) {
                if (charText.length() != 0 && postDetail.getPostTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    parkingList.add(postDetail);
                } else if (charText.length() != 0 && postDetail.getPostSubTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    parkingList.add(postDetail);
                }
            }
        }
        notifyDataSetChanged();
    }
}*/
