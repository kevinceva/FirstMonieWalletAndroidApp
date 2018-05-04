package com.ceva.ubmobile.core.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.RecyclerItemClickListener;
import com.ceva.ubmobile.adapter.TransferMenuAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.transfers.TransfersControllerActivity;
import com.ceva.ubmobile.security.UBNSession;

import butterknife.BindArray;
import butterknife.ButterKnife;

public class Transfers extends BaseActivity {
    public static final int OPEN_NEW_ACTIVITY = 123456;
    UBNSession session;
    @BindArray(R.array.tx_types)
    String[] transfers;
    private RecyclerView recyclerView;
    private TransferMenuAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfers);
        ButterKnife.bind(this);
        setToolbarTitle("Send Money");

        recyclerView = findViewById(R.id.transactions);
        mAdapter = new TransferMenuAdapter(transfers, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Bundle b = new Bundle();
                        /*if (position == 4) {
                            Toast.makeText(Transfers.this, "Coming soon...", Toast.LENGTH_LONG).show();
                        } else {
                            b.putString(Constants.KEY_FRAG_POSITION, "" + position);
                            Intent n = new Intent(Transfers.this, TransfersControllerActivity.class);
                            n.putExtras(b);
                            startActivity(n);
                            finish();
                        }*/
                        b.putString(Constants.KEY_FRAG_POSITION, "" + position);
                        Intent n = new Intent(Transfers.this, TransfersControllerActivity.class);
                        n.putExtras(b);
                        startActivity(n);
                        finish();
                        /*switch (position) {
                            default:
                                startActivity(new Intent(Transfers.this, WithinBankActivity.class));
                                break;
                        }*/

                    }
                }));

        session = new UBNSession(this);
        if (session.getBeneficiaries() == null) {
            //fetchBeneficiaries(session.getUserName());
        }

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashBoard.class));
        /*if (getFragmentManager().getBackStackEntryCount() == 1 ) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }*/
    }

}
