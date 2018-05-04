package com.zercomsystems.android.unionatmlocator.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.ceva.ubmobile.R;


/**
 * Created by oreofe on 5/10/2016.
 */
public abstract class ATMBaseActivity extends AppCompatActivity {

    public static String TAG;

    public int mLayoutResID;

    public abstract
    @LayoutRes
    int getLayoutId();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayoutResID = getLayoutId();

        setContentView(mLayoutResID);
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {

        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarTranslucentLollipop(makeTranslucent);
        } else if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            setStatusBarTranslucentKitkat(makeTranslucent);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setStatusBarTranslucentLollipop(boolean makeTranslucent) {
        getWindow().setStatusBarColor(
                getWindow().getContext()
                        .getResources()
                        .getColor(R.color.toolbarTransparent));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void setStatusBarTranslucentKitkat(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}