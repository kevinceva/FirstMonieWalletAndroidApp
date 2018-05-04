package com.zercomsystems.android.unionatmlocator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.security.UBNSession;
import com.zercomsystems.android.unionatmlocator.helpers.LocationType;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivityATM extends ATMBaseActivity {
    UBNSession session;

    /**
     * @return Terminoxx MainActivity layout
     */
    @Override
    public int getLayoutId() {
        return R.layout.tx_activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        ButterKnife.bind(this);
        session = new UBNSession(this);

    }


    @OnClick({
            R.id.loadin_atms,
            R.id.loadin_branches,
            R.id.loadin_smart_branches,
            R.id.btnHome
    })
    public void onMainItemClicked(View view) {
        Log.d(TAG, "onMainItemClicked");

        LocationType locationType = LocationType.BRANCH;

        switch (view.getId()) {

            case R.id.loadin_atms: {
                locationType = LocationType.ATM;
                Intent intent = new Intent(MainActivityATM.this, MapActivityATM.class);
                intent.putExtra("location_type", locationType);
                startActivity(intent);
                break;
            }

            case R.id.loadin_branches: {
                locationType = LocationType.BRANCH;
                Intent intent = new Intent(MainActivityATM.this, MapActivityATM.class);
                intent.putExtra("location_type", locationType);
                startActivity(intent);
                break;
            }

            case R.id.loadin_smart_branches: {
                locationType = LocationType.SMART_BRANCH;
                Intent intent = new Intent(MainActivityATM.this, MapActivityATM.class);
                intent.putExtra("location_type", locationType);
                startActivity(intent);
                break;
            }
            case R.id.btnHome: {
                onBackPressed();

                break;
            }

        }

    }

}
