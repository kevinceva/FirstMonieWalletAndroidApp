package com.ceva.ubmobile.core.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.BioTCs;
import com.ceva.ubmobile.core.UBNApplication;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.omniview.InviteFriend;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.utils.ImageUtils;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Profile extends BaseActivity implements ImageUtils.ImageAttachmentListener {
    UBNSession session;
    @BindView(R.id.profile_image)
    CircularImageView profile_image;

    @BindView(R.id.btnBack)
    ImageView btnBack;

    @BindView(R.id.bioFields)
    LinearLayout bioFields;

    ImageUtils imageutils;
    @BindView(R.id.bioSwitch)
    SwitchCompat bioSwitch;
    private Bitmap bitmap;
    private String file_name;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_alt);
        ButterKnife.bind(this);
        setToolbarTitle("");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        session = new UBNSession(this);
        imageutils = new ImageUtils(this);

        if (UBNApplication.isBioPresent()) {
            bioFields.setVisibility(View.VISIBLE);
            //bioFields.setVisibility(View.GONE);
            if (session.isBioEnabled()) {
                bioSwitch.setChecked(true);
            } else {
                bioSwitch.setChecked(false);
            }
        } else {
            bioFields.setVisibility(View.GONE);
        }

        if (session.getString(ImageUtils.KEY_PROFILE_IMAGE) == null) {
            // pulsator.start();
        } else {
            File imgFile = new File(session.getString(ImageUtils.KEY_PROFILE_IMAGE));

            if (imgFile.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                profile_image.setImageBitmap(myBitmap);

            }
        }

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageutils.imagepicker(0);
            }
        });

        TextView accountName = findViewById(R.id.accountName);
        TextView userName = findViewById(R.id.username);
        TextView email = findViewById(R.id.email);
        TextView phoneNumber = findViewById(R.id.phoneNumber);

        accountName.setText(session.getAccountName());
        userName.setText(session.getUserName());
        phoneNumber.setText(session.getPhoneNumber());

        bioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {

                if (NetworkUtils.isConnected(Profile.this)) {
                    //enableOrDisableBio(b, session.getUserName());
                    if (b) {
                        startActivity(new Intent(Profile.this, BioTCs.class));
                        finish();
                    } else {
                        showFingerPrintDialog(b);
                        /*FingerprintDialog.initialize(Profile.this)
                                .title("Identification")
                                .message("Place your finger on the sensor")
                                .callback(new FingerprintDialogCallback() {
                                    @Override
                                    public void onAuthenticationSucceeded() {
                                        enableOrDisableBio(b, session.getUserName());
                                    }

                                    @Override
                                    public void onAuthenticationCancel() {

                                    }
                                })
                                .show();*/
                    }
                    } else {
                        noInternetDialog();
                    }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageutils.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        this.bitmap = file;
        this.file_name = filename;
        profile_image.setImageBitmap(file);

        String path = Constants.KEY_IMAGE_PATH;

        session.setString(ImageUtils.KEY_PROFILE_IMAGE, path + filename);
        imageutils.createImage(file, filename, path, false);
        Log.debug("image_path", path + filename);
        //pulsator.stop();

    }


    public void onChangePinClick(View v) {
        startActivity(new Intent(this, PINChange.class));
    }

    public void onChangeTransactionClick(View v) {
        startActivity(new Intent(this, TransactionPinChange.class));
    }

    public void onInviteClick(View v) {
        startActivity(new Intent(this, InviteFriend.class));
    }

    public void onDebitCardClick(View v) {
        startActivity(new Intent(this, DebitCardValidation.class));
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
                    obj = SecurityLayer.decryptTransaction(obj, Profile.this);

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        if (state) {
                            session.enableorDisableBio(true);
                        } else {
                            session.enableorDisableBio(false);
                        }
                        //ResponseDialogs.successToActivity("Success", obj.optString(Constants.KEY_MSG), Profile.this, Sign_In.class, new Bundle());
                    } else {
                        ResponseDialogs.warningDialogLovely(Profile.this, "Error", obj.optString(Constants.KEY_MSG));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    warningDialog(getString(R.string.error_server));
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
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
