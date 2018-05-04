package com.ceva.ubmobile.core.ui.cards;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.Log;

public class CardFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    public static final CardFragment newInstance(String message) {

        CardFragment f = new CardFragment();

        Bundle bdl = new Bundle(1);

        bdl.putString(EXTRA_MESSAGE, message);

        f.setArguments(bdl);

        return f;

    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        String message = getArguments().getString(EXTRA_MESSAGE);
        Log.debug("fragment met");
        View v = inflater.inflate(R.layout.fragment_card_local, container, false);
        LinearLayout newcard = (LinearLayout) v.findViewById(R.id.newcard);
        newcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CardTopUp.class));
            }
        });
        return v;

    }

}

