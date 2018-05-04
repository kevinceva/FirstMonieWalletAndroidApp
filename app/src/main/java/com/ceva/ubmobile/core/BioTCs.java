package com.ceva.ubmobile.core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;

import org.json.JSONObject;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BioTCs extends BaseActivity {
    public static final String KEY_TERMS_SHOWN = "hastermsbeenshown";
    @BindView(R.id.btnDecline)
    Button btnDecline;
    String terms = "Users can also log in to the Union Bank UnionMobile app by using the fingerprint authentication in the app. To use the fingerprint authentication feature, users must agree to and comply with these Terms and Conditions supplementing the Terms and Conditions of the Union Bank Online Banking and Mobile Banking Services.\n" +
            "\n" +
            "When you activate the fingerprint authentication feature, logging into the UnionMobile app with your fingerprint instead of a code is possible. You will also be able to select one of either of the password or fingerprint authentication method to log in and transact on your Union Bank accounts. Some of the services, such as transfers to other accounts and payment of bills still require the use of another authentication method (PIN Code, Token Code etc.). There will also be times when you will be required to use more than one authentication method to complete specific services.\n" +
            "\n" +
            "The fingerprint authentication is based on technology used by the manufacturer of the device and Union Bank cannot process fingerprint data or control them. All actions made by using the fingerprint authentication binds you as Union Bank’s Customer.\n" +
            "\n" +
            "You agree that you shall not allow any fingerprints belonging to anyone besides yourself from being registered in your device and to allow such fingerprints to be used to access the app.\n" +
            "\n" +
            "You are responsible for keeping your password and your devices safe, and you must inform us immediately if they are lost or stolen, or you think someone may have accessed or tried to access your account(s). Please remember that we will never ask you to tell us your password and transaction PIN, so you should not share it with anyone, even if you think they work for Union Bank.\n" +
            "\n" +
            "You are also responsible for taking reasonable steps to maintain the security of your device – we recommend you set up your device access security and install anti-virus software, where relevant.\n" +
            "\n" +
            "You can deactivate the fingerprint authentication at any time by using the left navigation menu of the app. \n" +
            "\n" +
            "You acknowledge and agree that we make no guarantee, representation, warranty or undertaking of any kind, whether express or implied, statutory or otherwise, relating to or arising from the use of the fingerprint authentication or the Union Mobile app, including but not limited to:\n" +
            "whether the fingerprint authentication method or the app will meet your requirements; or\n" +
            "whether the fingerprint authentication method or the App will always be available, accessible or function with any network infrastructure, system or such other services as we may offer from time to time.\n" +
            "\n" +
            "\n" +
            "You cannot hold us liable for any Loss you may suffer in connection with the fingerprint authentication howsoever arising (whether reasonably foreseeable or not), even if we have been advised of the possibility of the Loss, including Loss from:\n" +
            "the provision by us of and/or your use of the fingerprint authentication method or the app;\n" +
            "any unauthorised access and/or use of your Mobile Device;\n" +
            "the use in any manner and/or for any purpose by any person at any time of any information or data:\n" +
            "relating to you;\n" +
            "transmitted through your use of fingerprint authentication method or the app; and/or\n" +
            "obtained through your use of fingerprint authentication method or the App;\n" +
            "access to the App by way of the fingerprint authentication method by anyone other than yourself;\n" +
            "any event the occurrence of which we are not able to control or avoid by the use of reasonable diligence; and/or\n" +
            "the suspension, termination or discontinuance of the fingerprint authentication method or the app.\n" +
            "\n" +
            "You agree to indemnify us and keep us indemnified against any Loss suffered by us in connection with:\n" +
            "your access and use of the Fingerprint authentication method  and the App;\n" +
            "any improper or unauthorised use of the fingerprint authentication method or the App by you, or any improper use of your Mobile Device (whether authorised by you or otherwise);\n" +
            "any act or omission by any third party (including a relevant mobile or internet service provider);\n" +
            "any delay or failure in any transmission, despatch or communication facilities; or\n" +
            "any breach by you of these Terms.\n" +
            "\n" +
            "If any of these Terms is held to be invalid, unlawful or unenforceable under the laws of any country, it shall not affect or impair the validity, legality or enforceability of the rest of these Terms and/or these Terms under the laws of any other country.\n" +
            "\n" +
            "Any failure or delay by us in exercising or enforcing any right we have under these Terms does not operate as a waiver of and does not prejudice or affect our right subsequently to act strictly in accordance with our rights.\n" +
            "\n" +
            "In the event we are unable to observe or perform any of these Terms due to or caused by events beyond our control or events which we cannot reasonably be expected to prevent or avoid, we shall be excused from performing these Terms for the duration of the disabling event. We shall not be liable for any delay, loss, damage or inconvenience caused or arising from or in connection with the disabling events. Examples of such events include but are not limited to equipment, system or transmission link malfunction or failure, fire, flood, explosion, acts of elements, acts of God, acts of terrorism, wars, accidents, epidemics, strikes, lockouts, power blackouts or failures, labour disputes or acts, demands or requirements of governments.\n" +
            "\n" +
            "These Terms shall be governed by and construed in accordance with the laws of Nigeria. You agree to irrevocably submit to the exclusive jurisdiction of the courts of Nigeria.  \n" +
            "\n" +
            "We may, at any time, and at our sole discretion, modify these Terms and Conditions, with or without notice to the User. Any such modification will be effective immediately upon public posting. Your continued use of the fingerprint authentication and the app following any such modification constitutes your acceptance of these modified Terms.\n";
    @BindView(R.id.textViewTerms)
    TextView textViewTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_tcs);
        ButterKnife.bind(this);
        setToolbarTitle("Terms & Conditions");
        textViewTerms.setText(terms);
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void onContinueClick(View v) {
        if (NetworkUtils.isConnected(this)) {
            showFingerPrintDialog(true);
            //enableOrDisableBio(b, session.getUserName());
            /*FingerprintDialog.initialize(this)
                    .title("Identification")
                    .message("Place your finger on the sensor")
                    .callback(new FingerprintDialogCallback() {
                        @Override
                        public void onAuthenticationSucceeded() {
                            enableOrDisableBio(true, session.getUserName());
                        }

                        @Override
                        public void onAuthenticationCancel() {
                        }
                    })
                    .show();*/
        } else {
            noInternetDialog();
        }
    }

    private void enableOrDisableBio(final boolean state, String username) {
        //@Path("/registerBio/{userid}/{biostatus}")
        showLoadingProgress();
        String bioStatus = "NOBIO";
        if (state) {
            bioStatus = "BIO";
        } else {
            bioStatus = "NOBIO";
        }

        String params = username + "/" + bioStatus;
        String session_id = UUID.randomUUID().toString();
        String url = SecurityLayer.genURLCBC("registerBio", params, this);
        // String urlparam = "accountnovallidatin/" + SecurityLayer.generalEncrypt(accountNumber);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, BioTCs.this);

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        session.setString(KEY_TERMS_SHOWN, "yes");
                        if (state) {
                            session.enableorDisableBio(true);
                        } else {
                            session.enableorDisableBio(false);
                        }
                        ResponseDialogs.successToActivity("Success", obj.optString(Constants.KEY_MSG), BioTCs.this, DashBoard/**/.class, new Bundle());
                    } else {
                        ResponseDialogs.warningDialogLovely(BioTCs.this, "Error", obj.optString(Constants.KEY_MSG));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    warningDialog(getString(R.string.error_server));
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                // Log error here since request failed
                dismissProgress();
                Log.debug("ubnaccountsfail", t.toString());
                warningDialog(getString(R.string.error_server));
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

}
