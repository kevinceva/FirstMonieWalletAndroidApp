package com.ceva.ubmobile.core.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.DebitCardValidation;
import com.ceva.ubmobile.utils.ImageUtils;
import com.ceva.ubmobile.utils.ScalingUtilities;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by brian on 06/10/2016.
 */
public class ResponseDialogs {
    static SweetAlertDialog prgDialog;
    static int smile_unicode = 0x1F60A;
    static int sad_unicode = 0x1F614;
    static String smile = new String(Character.toChars(smile_unicode));
    static String sad = new String(Character.toChars(sad_unicode));

    public static void success(String title, String content, final Context context) {
        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_check_circle_white, context.getResources());
        new LovelyStandardDialog(context)
                .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE)))
                .setButtonsColorRes(R.color.midnight_blue)
                .setIcon(icon)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("Close", null)
                .setOnButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, DashBoard.class);

                        // Staring Login Activity
                        context.startActivity(i);
                    }
                })
                .show();
    }

    public static void successStatic(String title, String content, final Context context) {
        if (TextUtils.isEmpty(content)) {
            content = context.getResources().getString(R.string.error_generic);
        }
        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_check_circle_white, context.getResources());
        new LovelyStandardDialog(context)
                .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                .setButtonsColorRes(R.color.midnight_blue)
                .setIcon(icon)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("Close", null)
                .show();
    }

    public static void fail(String title, String content, final Context context) {
        if (TextUtils.isEmpty(content)) {
            content = context.getResources().getString(R.string.error_generic);
        }
        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_warning_white_48, context.getResources());
        new LovelyStandardDialog(context)
                .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                .setButtonsColorRes(R.color.midnight_blue)
                .setIcon(icon)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("Close", null)
                .setOnButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, DashBoard.class);

                        // Staring Login Activity
                        context.startActivity(i);
                    }
                })
                .show();

    }

    public static void failStatic(String title, String content, final Context context) {
        if (TextUtils.isEmpty(content)) {
            content = context.getResources().getString(R.string.error_generic);
        }
        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_warning_white_48, context.getResources());
        new LovelyStandardDialog(context)
                .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark, Color.BLUE))
                .setButtonsColorRes(R.color.midnight_blue)
                .setIcon(icon)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("Close", null)
                .show();
    }

    public static void infoYesNo(String title, String content, final Context context) {
        prgDialog = new SweetAlertDialog(context);
        prgDialog.setTitleText(title)
                .setContentText(content)
                .setConfirmText("YES")
                .setCancelText("NO")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                    }
                }).changeAlertType(SweetAlertDialog.NORMAL_TYPE);
        prgDialog.show();
    }

    public static void info(String title, String content, final Context context) {

        if (context != null) {
            Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_bulb_white, context.getResources());
            new LovelyStandardDialog(context)
                    .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                    .setButtonsColorRes(R.color.midnight_blue)
                    .setIcon(icon)
                    .setTitle(title)
                    .setMessage(content)
                    .setPositiveButton("Close", null)
                    .show();
        }

    }

    public static void infoToMain(String title, String content, final Context context) {
        prgDialog = new SweetAlertDialog(context);
        prgDialog.setTitleText(title)
                .setContentText(content)
                .setConfirmText("Close")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        Intent i = new Intent(context, DashBoard.class);

                        // Staring Login Activity
                        context.startActivity(i);

                    }
                }).changeAlertType(SweetAlertDialog.NORMAL_TYPE);
        prgDialog.show();
    }

    public static void warning(String title, String content, final Context context) {
        prgDialog = new SweetAlertDialog(context);
        prgDialog.setTitleText(title)
                .setContentText(content)
                .setConfirmText("Close")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        Intent i = new Intent(context, DashBoard.class);

                        // Staring Login Activity
                        context.startActivity(i);
                        //((Activity)context).finish();
                    }
                }).changeAlertType(SweetAlertDialog.WARNING_TYPE);
        prgDialog.show();
    }

    public static void warningStatic(String title, String content, final Context context) {
        if (context != null) {
            if (TextUtils.isEmpty(content)) {
                content = context.getResources().getString(R.string.error_generic);
            }
            Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_warning_white_48, context.getResources());
            new LovelyStandardDialog(context)
                    .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                    .setButtonsColorRes(R.color.midnight_blue)
                    .setIcon(icon)
                    .setTitle(title)
                    .setMessage(content)
                    .setPositiveButton("Close", null)
                    .show();
        }
    }

    public static void noInternet(final Context context) {
        if (context != null) {
            Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_gsm_signal_white, context.getResources());
            new LovelyStandardDialog(context)
                    .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                    .setButtonsColorRes(R.color.midnight_blue)
                    .setIcon(icon)
                    .setTitle("Network Error")
                    .setMessage(context.getResources().getString(R.string.error_no_internet_connection))
                    .setPositiveButton("Close", null)
                    .show();
        }
    }

    public static void successToActivity(String title, final String content, final Context context, final Class activity, final Bundle bundle) {
        if (context != null) {
            Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_check_circle_white, context.getResources());
            new LovelyStandardDialog(context)
                    .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                    .setButtonsColorRes(R.color.midnight_blue)
                    .setIcon(icon)
                    .setTitle(title)
                    .setMessage(content)
                    .setPositiveButton("Close", null)
                    .setOnButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, activity);
                            i.putExtras(bundle);
                            // Staring Login Activity
                            context.startActivity(i);

                        }
                    })
                    .show();
        }
    }

    public static void failToActivity(String title, String content, final Context context, final Class activity, final Bundle bundle) {
        if (context != null) {
            if (TextUtils.isEmpty(content)) {
                content = context.getResources().getString(R.string.error_generic);
            }
            Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_warning_white_48, context.getResources());
            new LovelyStandardDialog(context)
                    .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                    .setButtonsColorRes(R.color.midnight_blue)
                    .setIcon(icon)
                    .setTitle(title)
                    .setMessage(content)
                    .setPositiveButton("Close", null)
                    .setOnButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, activity);
                            i.putExtras(bundle);
                            // Staring Login Activity
                            context.startActivity(i);
                        }
                    })
                    .show();
        }
    }

    public static void warningDialogLovely(Context context, String title, String message) {
        if (context != null) {
            if (TextUtils.isEmpty(message)) {
                message = context.getResources().getString(R.string.error_generic);
            }
            Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_warning_shield_white, context.getResources());
            new LovelyStandardDialog(context)
                    .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                    .setButtonsColorRes(R.color.midnight_blue)
                    .setIcon(icon)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Close", null)
                    .show();
        }
    }

    public static void warningDialogLovelyToActivity(final Context context, String title, String message, final Class activity) {
        if (context != null) {
            if (TextUtils.isEmpty(message)) {
                message = context.getResources().getString(R.string.error_generic);
            }
            Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_warning_shield_white, context.getResources());
            new LovelyStandardDialog(context)
                    .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                    .setButtonsColorRes(R.color.midnight_blue)
                    .setIcon(icon)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Close", null)
                    .setOnButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context, activity));
                        }
                    })
                    .show();
        }
    }

    public static void warningDialogLovelyToFragment(final Context context, String title, String message, final FragmentTransaction transaction) {
        if (context != null) {
            if (TextUtils.isEmpty(message)) {
                message = context.getResources().getString(R.string.error_generic);
            }
            Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_warning_shield_white, context.getResources());
            new LovelyStandardDialog(context)
                    .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                    .setButtonsColorRes(R.color.midnight_blue)
                    .setIcon(icon)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Close", null)
                    .setOnButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            transaction.commitAllowingStateLoss();
                        }
                    })
                    .show();
        }
    }

    public static void limitError(final Context context, String title, String message, final Class activity) {
        if (TextUtils.isEmpty(message)) {
            message = context.getResources().getString(R.string.error_generic);
        }
        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_warning_shield_white, context.getResources());
        new LovelyStandardDialog(context)
                .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                .setButtonsColorRes(R.color.midnight_blue)
                .setIcon(icon)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, activity));
                    }
                })
                .setPositiveButton("Increase Limit", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, DebitCardValidation.class));
                    }
                })
                .show();
    }

    public static void sucessDialogLovelyToActivity(final Context context, String title, String message, final Class activity) {
        Bitmap icon = ScalingUtilities.iconBitmap(R.drawable.ic_check_circle_white, context.getResources());
        new LovelyStandardDialog(context)
                .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                .setButtonsColorRes(R.color.midnight_blue)
                .setIcon(icon)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Close", null)
                .setOnButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, activity));
                    }
                })
                .show();
    }

    public static void infoToActivity(String title, String content, final Context context, final Class activity, final Bundle bundle) {
        new LovelyStandardDialog(context)
                .setTopColorRes(ImageUtils.getColorByThemeAttr(context,R.attr._ubnColorPrimaryDark,Color.BLUE))
                .setButtonsColorRes(R.color.midnight_blue)
                .setIcon(R.drawable.ic_bulb_white)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("Close", null)
                .setOnButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, activity);
                        i.putExtras(bundle);
                        // Staring Login Activity
                        context.startActivity(i);
                    }
                })
                .show();
    }
}
