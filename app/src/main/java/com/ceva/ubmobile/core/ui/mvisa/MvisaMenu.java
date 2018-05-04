package com.ceva.ubmobile.core.ui.mvisa;

import android.os.Bundle;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.BaseActivity;

public class MvisaMenu extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvisa_menu);
        setToolbarTitle(getString(R.string.mvisa));
    }
}
