package com.ceva.ubmobile.core.omniview;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;
import com.ceva.ubmobile.utils.NumberUtilities;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteFriend extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    public static ArrayList<String> phoneValueArr = new ArrayList<String>();
    public static ArrayList<String> nameValueArr = new ArrayList<String>();
    private static int SPLASH_TIME_OUT = 2000;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    AutoCompleteTextView textView = null;
    String finalnam = null;
    String toNumberValue = "";
    EditText et;
    Button ok;
    UBNSession session;

    private ArrayAdapter<String> adapter5;

    public InviteFriend() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitefriend);
        setToolbarTitle("Invite Friend");
        session = new UBNSession(this);
        et = findViewById(R.id.phoneNumber);
        // fname = (EditText) findViewById(R.id.fname);
        ok = findViewById(R.id.btnSubmit);
        ok.setOnClickListener(this);

        textView = findViewById(R.id.name);
        adapter5 = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        textView.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        textView.setAdapter(adapter5);
        textView.setOnItemSelectedListener(this);
        textView.setOnItemClickListener(this);
        showLoadingProgress();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                readContactData();
            }
        }, SPLASH_TIME_OUT);

    }


    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }

    private void readContactData() {
        if (!(this == null)) {
            ContentResolver cr = this.getContentResolver();
            Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
            if (cursor != null) {
                try {
                    final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    String name, number;
                    while (cursor.moveToNext()) {
                        name = cursor.getString(nameIndex);
                        number = cursor.getString(numberIndex);
                        number = number.replaceAll("\\s", "");
                        adapter5.add(name);

                        // Add ArrayList names to adapter
                        phoneValueArr.add(number.toString());
                        nameValueArr.add(name.toString());
                    }
                } finally {
                    cursor.close();
                }
                dismissProgress();
                ResponseDialogs.info("Info!", "We have successfully imported your contacts. Please search using your contact's name", this);
                /*Toast.makeText(this,
                        "You can select phone number from your contact list",
                        Toast.LENGTH_LONG).show();*/
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

        // Get Array index value for selected name
        int i = nameValueArr.indexOf("" + arg0.getItemAtPosition(arg2));

        // If name exist in name ArrayList
        if (i >= 0) {

            // Get Phone Number
            toNumberValue = phoneValueArr.get(i);
            String names = nameValueArr.get(i);
            textView.setText(names);

            InputMethodManager imm = (InputMethodManager) this.getSystemService(
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            et.setText(toNumberValue);

            // Show Alert
        /*    Toast.makeText(getActivity(),
                    "Position:" + arg2 + " Name:" + arg0.getItemAtPosition(arg2) + " Number:" + toNumberValue,
                    Toast.LENGTH_LONG).show();*/

            //Log.d("AutocompleteContacts",
            //"Position:" + arg2 + " Name:" + arg0.getItemAtPosition(arg2) + " Number:" + toNumberValue);

        }

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSubmit) {
            String invitename = textView.getText().toString();
            String phone = et.getText().toString();
            if (Utility.isNotNull(invitename)) {
                if (Utility.isNotNull(phone)) {
                    if (NetworkUtils.isConnected(this)) {
                        invokeInviteFriend(session.getUserName(), invitename, phone);
                    } else {
                        ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.error_no_internet_connection), this);
                    }
                } else {
                    ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.prompt_phone_invite), this);
                }
            } else {
                ResponseDialogs.warningStatic(getString(R.string.error), getString(R.string.prompt_name_invite), this);
            }

        }

    }

    private void invokeInviteFriend(String userName, String invitename, String mobileNumber) {
        //@Path("/invitefriend/{username}/{invitename}/{mobile}")
        showLoadingProgress();
        mobileNumber = NumberUtilities.getNumbersOnly(mobileNumber);
        String params = userName + "/" + invitename + "/" + mobileNumber;
        String url = SecurityLayer.genURLCBC("invitefriend", params, this);
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
                        ResponseDialogs.success(getString(R.string.success), responsemessage, InviteFriend.this);
                    } else {
                        ResponseDialogs.fail(getString(R.string.error), responsemessage, InviteFriend.this);
                    }
                } catch (Exception e) {
                    com.ceva.ubmobile.core.ui.Log.Error(e);
                    SecurityLayer.generateToken(getApplicationContext());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                Log.e("ubnaccountsfail", t.toString());
                SecurityLayer.generateToken(getApplicationContext());
                dismissProgress();
            }
        });

    }
}
