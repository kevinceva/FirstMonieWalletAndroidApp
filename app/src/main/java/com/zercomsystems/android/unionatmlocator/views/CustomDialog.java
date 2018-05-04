package com.zercomsystems.android.unionatmlocator.views;

import android.app.Dialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ceva.ubmobile.R;


/**
 * Created by oreofe on 6/9/2016.
 */
public class CustomDialog {
    public static final int SUCCESS = 1;
    public static final int ENQUIRY = 2;
    public static final int ERROR = 0;

    private int mAlertType = SUCCESS;
    private String mMessage;
    private AppCompatActivity mActivity;
    private View.OnClickListener mListener;
    private final Dialog mDialog;

    public CustomDialog(AppCompatActivity activity) {
        mActivity = activity;
        mDialog = new Dialog(activity);

        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.dialog);

    }


    public void showDialog() {

        int color, drawableId;

        //get settings
        if (mAlertType == ERROR) {
            drawableId = R.drawable.ic_error_info;
        } else if (mAlertType == ENQUIRY) {
            drawableId = R.drawable.ic_success_info;
        } else {
            drawableId = R.drawable.ic_done_white;
        }

        ImageView imageView = (ImageView) mDialog.findViewById(R.id.image_dialog);
        imageView.setImageDrawable(ContextCompat.getDrawable(mActivity, drawableId));

        TextView text = (TextView) mDialog.findViewById(R.id.text_dialog);
        text.setText(mMessage);

        mDialog.show();

    }

    public void setAlertType(int alertType) {
        mAlertType = alertType;
    }

    public void setMessage(String message) {
        mMessage = message;

    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public void setNeutralButton(String title, View.OnClickListener onClickListener) {
        Button neutralButton = (Button) mDialog.findViewById(R.id.btn_neutral);
        neutralButton.setText(title);
        neutralButton.setOnClickListener(mListener);
        neutralButton.setVisibility(View.VISIBLE);

        neutralButton.setOnClickListener(onClickListener);
    }

    public void setActionButton(String title, View.OnClickListener onClickListener) {
        Button positiveButton = (Button) mDialog.findViewById(R.id.btn_action);
        positiveButton.setText(title);
        positiveButton.setOnClickListener(mListener);
        positiveButton.setVisibility(View.VISIBLE);

        positiveButton.setOnClickListener(onClickListener);
    }

    public boolean isOnScreen() {
        try {
            return mDialog.isShowing();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
