package com.ceva.ubmobile.core.ui.widgets;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ceva.ubmobile.BuildConfig;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.BioTCs;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BlankFragment;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.SecurityLayer;
import com.ceva.ubmobile.security.UBNSession;
import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;
import com.github.ajalt.reprint.core.Reprint;
import com.mattprecious.swirl.SwirlView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ceva.ubmobile.core.BioTCs.KEY_TERMS_SHOWN;

/**
 * A simple {@link Fragment} subclass.
 */
public class FingerPrintDialog extends DialogFragment {
    @BindView(R.id.swirl)
    SwirlView swirlView;
    @BindView(R.id.text)
    TextView textView;
    boolean running = false;
    @BindView(R.id.main)
    RelativeLayout main;
    UBNSession session;
    private static final String ARG_STATE = "param1";
    private String state;
    @BindView(R.id.btnCancel)
    TextView btnCancel;

    public FingerPrintDialog() {

    }

    public static FingerPrintDialog newInstance(String state) {
        FingerPrintDialog fragment = new FingerPrintDialog();
        Bundle args = new Bundle();
        args.putString(ARG_STATE, state);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View view = inflater.inflate(R.layout.fragment_finger_print_dialog, container, false);
        ButterKnife.bind(this, view);
        state = getArguments().getString(ARG_STATE);
        session = new UBNSession(getContext());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Log.debug("tumepata hii", state);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                dismissAllowingStateLoss();
            }
        });
        swirlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    cancel();
                }
                startTraditionalBio();
            }
        });
        startTraditionalBio();
        /*SquareProgressBar squareProgressBar = (SquareProgressBar) view.findViewById(R.id.sprogressbar);
        squareProgressBar.setImage(R.drawable.ubn_logo);
        squareProgressBar.setProgress(50.0);
        squareProgressBar.setColorRGB(R.color.colorAccent);
        squareProgressBar.setIndeterminate(true);*/
        setCancelable(false);
        return view;
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        try {
            Objects.requireNonNull(window).setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        } catch (Exception e) {
            Log.Error(e);
        }

        return dialog;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }


    public void onBackPressed() {
        //Do nothing
    }

    public void startTraditionalBio() {
        running = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            swirlView.setState(SwirlView.State.ON, true);
        }

        /*Snackbar mySnackbar = Snackbar.make(main,
                "Place your finger on the sensor", Snackbar.LENGTH_SHORT);
        mySnackbar.setAction("CANCEL", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });*/
        // mySnackbar.show();
        Reprint.authenticate(new AuthenticationListener() {
            @Override
            public void onSuccess(int moduleTag) {
                showSuccess();
            }

            @Override
            public void onFailure(AuthenticationFailureReason failureReason, boolean fatal,
                                  CharSequence errorMessage, int moduleTag, int errorCode) {
                showError(failureReason, fatal, errorMessage, errorCode);
            }
        });
    }

    private void showSuccess() {
        //result.setText("Success");

        running = false;

        swirlView.setState(SwirlView.State.ON, true);

        textView.setText("Authentication Successful!");
        //startDashBoard();
        if (NetworkUtils.isConnected(getContext())) {
            if (state.equalsIgnoreCase("true")) {
                enableOrDisableBio(true, session.getUserName());
            } else {
                enableOrDisableBio(false, session.getUserName());
            }
        }
    }

    private void showError(AuthenticationFailureReason failureReason, boolean fatal,
                           CharSequence errorMessage, int errorCode) {
        //result.setText(errorMessage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            swirlView.setState(SwirlView.State.ERROR, true);
        }
        /*Snackbar mySnackbar = Snackbar.make(main,
                errorMessage, Snackbar.LENGTH_SHORT);
        //mySnackbar.setAction(R.string.undo_string, new MyUndoListener());
        mySnackbar.show();*/
        textView.setText(errorMessage);
        if (fatal) {

            running = false;
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    swirlView.setState(SwirlView.State.ON, true);
                }
            }
        }, 2000);

    }

    private void cancel() {
        //result.setText("Cancelled");
        running = false;
        textView.setText("Cancelled");
        Reprint.cancelAuthentication();
    }

    private void enableOrDisableBio(final boolean state, String username) {
        //@Path("/registerBio/{userid}/{biostatus}")
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Loading...", "Please wait...", true);

        String bioStatus = "NOBIO";
        if (state) {
            bioStatus = "BIO";
        } else {
            bioStatus = "NOBIO";
        }

        String params = username + "/" + bioStatus;
        String session_id = UUID.randomUUID().toString();
        String url = SecurityLayer.genURLCBC("registerBio", params, getContext());
        // String urlparam = "accountnovallidatin/" + SecurityLayer.generalEncrypt(accountNumber);

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("nipservice", response.body());
                dialog.dismiss();

                try {
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getContext());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        session.setString(KEY_TERMS_SHOWN, "yes");
                        if (state) {
                            session.enableorDisableBio(true);
                        } else {
                            session.enableorDisableBio(false);
                        }
                        ResponseDialogs.successToActivity("Success", obj.optString(Constants.KEY_MSG), getContext(), DashBoard/**/.class, new Bundle());
                    } else {
                        ResponseDialogs.warningDialogLovely(getContext(), "Error", obj.optString(Constants.KEY_MSG));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ResponseDialogs.warningStatic("Error", getString(R.string.error_server), getContext());
                    //warningDialog(getString(R.string.error_server));
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                // Log error here since request failed
                dialog.dismiss();
                Log.debug("ubnaccountsfail", t.toString());
                ResponseDialogs.warningStatic("Error", getString(R.string.error_server), getContext());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }
}