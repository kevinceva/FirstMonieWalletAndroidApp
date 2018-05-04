package com.ceva.ubmobile.core.ui.merchantpayments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NairabetRef extends BaseActivity {

    @BindView(R.id.reference)
    EditText reference;
    @BindView(R.id.btnStatus)
    Button btnStatus;
    UBNSession session;
    @BindView(R.id.identifier)
    Spinner identifier;
    String labelVal;
    @BindView(R.id.label)
    TextView textView;
    String[] spinnerStrings = {"Nairabet Username~CUST_USER_NAME", "Nairabet Customer ID~CUST_ID", "ID Number~CUST_ID_NO"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nairabet_ref);
        ButterKnife.bind(this);
        setToolbarTitle("Nairabet");

        session = new UBNSession(this);

        List<String> identifiers = new ArrayList<>();
        for (String spinnerString : spinnerStrings) {
            String[] strings;
            strings = spinnerString.split("~");
            identifiers.add(strings[0]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_row, identifiers);
        identifier.setAdapter(adapter);

        identifier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                labelVal = "Enter " + identifiers.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnStatus.setOnClickListener(view -> {
            if (NetworkUtils.isConnected(NairabetRef.this)) {
                if (TextUtils.isEmpty(reference.getText().toString())) {
                    reference.setError("Please " + labelVal);
                } else {
                    fetchData(session.getUserName(), getValidationMethod(spinnerStrings[identifier.getSelectedItemPosition()]), reference.getText().toString());
                }
            } else {
                noInternetDialog();
            }
        });

    }

    private String getValidationMethod(String string) {
        String[] strings = string.split("~");
        return strings[1];
    }


    private void fetchData(String username, String validation, final String refnumber) {
        //@Path("/customervalidation/{userid}/{typeofvaildation}/{nirabetvalue}
        showLoadingProgress();

        String params = username + "/" + validation + "/" + refnumber;
        String url = SecurityLayer.genURLCBC("customervalidation", params, this);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    String responsecode = obj.optString(Constants.KEY_CODE);
                    String responsemessage = obj.optString(Constants.KEY_MSG);

                    if (responsecode.equals("00")) {
                        String data = obj.optString("validateguidresp");
                        JSONObject val = new JSONObject(data);
                        //val.put("webref",refnumber);
                        Intent intent = new Intent(NairabetRef.this, RevPayConfirm.class);
                        intent.putExtra("data", data);
                        intent.putExtra("webref", refnumber);
                        startActivity(intent);
                    } else {
                        ResponseDialogs.warningStatic(getString(R.string.error), responsemessage, NairabetRef.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("Server Error", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });
    }

}
