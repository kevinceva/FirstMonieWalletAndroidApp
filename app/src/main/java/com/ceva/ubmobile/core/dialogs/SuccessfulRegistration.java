package com.ceva.ubmobile.core.dialogs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ceva.ubmobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessfulRegistration extends Fragment {


    public SuccessfulRegistration() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_successful_registration, container, false);
    }

}
