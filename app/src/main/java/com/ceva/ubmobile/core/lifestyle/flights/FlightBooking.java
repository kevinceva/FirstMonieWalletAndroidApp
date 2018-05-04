package com.ceva.ubmobile.core.lifestyle.flights;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.BaseActivity;

public class FlightBooking extends BaseActivity {
    RadioGroup radioGroup;
    LinearLayout flights, history, return_fields;
    EditText fromDate, toDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_booking);
        setToolbarTitle("Flights");
        flights = findViewById(R.id.flight_fields);
        history = findViewById(R.id.history_fields);
        return_fields = findViewById(R.id.return_fields);

        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);
        getDateIntoEditText(fromDate, this);
        getDateIntoEditText(toDate, this);

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.check(R.id.rb_oneway);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_oneway) {
                    showOneWay();
                } else if (i == R.id.rb_roundtrip) {
                    showRound();
                } else {
                    showHistory();
                }
            }
        });
    }

    private void showOneWay() {
        flights.setVisibility(View.VISIBLE);
        history.setVisibility(View.GONE);
        return_fields.setVisibility(View.GONE);
    }

    private void showRound() {
        flights.setVisibility(View.VISIBLE);
        history.setVisibility(View.GONE);
        return_fields.setVisibility(View.VISIBLE);
    }

    private void showHistory() {
        flights.setVisibility(View.GONE);
        history.setVisibility(View.VISIBLE);

    }

    public void onClickNext(View v) {
        startActivity(new Intent(this, FlightConfirm.class));
    }
}
