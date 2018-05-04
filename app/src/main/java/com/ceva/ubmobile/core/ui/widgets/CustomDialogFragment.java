package com.ceva.ubmobile.core.ui.widgets;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.Log;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by brian on 08/08/2017.
 */

public class CustomDialogFragment extends DialogFragment {
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View view = inflater.inflate(R.layout.avloadingindicator, container, false);
        ButterKnife.bind(this, view);
        startAnim();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        } catch (Exception e) {
            Log.Error(e);
        }

        return dialog;
    }

    public void startAnim() {
        //avi.show();
        avi.smoothToShow();
        //avi.hide();
        // avi.setVisibility(View.GONE);
    }

    public void stopAnim() {
        avi.hide();
        // or avi.smoothToHide();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }


    public void onBackPressed() {
        //Do nothing
    }
}