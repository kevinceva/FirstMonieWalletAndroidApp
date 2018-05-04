package com.ceva.ubmobile.utils;

import android.text.TextUtils;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by brian on 26/02/2018.
 */
public class StringUtilities {
    public static boolean checkFilledEditText(EditText editText, String errorMessage) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError(errorMessage);
            return false;
        } else {
            return true;
        }
    }

    public static String getEncodedString(String string) {
        try {
            return URLEncoder.encode(string, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
