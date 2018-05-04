package com.ceva.ubmobile.security;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ceva.ubmobile.core.constants.Constants;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class which has Utility methods
 */
public class Utility {
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String NUMBER_PATTERN = "[0-9]+";
    private static final String LWCASE_PATTERN = "[a-z]+";
    private static final String SPEC_CHARPATTERN = "[a-zA-Z0-9]+";
    private static Pattern pattern;
    private static Matcher matcher;

    /**
     * Validate Email with regular expression
     *
     * @param email
     * @return true for Valid Email and false for Invalid Email
     */
    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public static String getrandkey() {
        byte[] genkey = null;
        try {
            genkey = AESURL.generateSessionKey();
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
        } catch (NoSuchProviderException e) {
            //e.printStackTrace();
        }
        String sessid = android.util.Base64.encodeToString(genkey, android.util.Base64.NO_WRAP);
        return sessid;

    }

    public static boolean isValidWord(String str) {
//  return str.matches(".*[a-zA-Z]+.*"); // match a number with optional
        // '-' and decimal.
        return str.matches("^[a-zA-Z0-9]*$"); // match a number with optional
    }

    public static boolean isValidWordHyphen(String str) {
//  return str.matches(".*[a-zA-Z]+.*"); // match a number with optional
        // '-' and decimal.
        return str.matches("^[a-zA-Z0-9_]*$"); // match a number with optional
    }

    public static boolean isValidWordSpace(String str) {
//  return str.matches(".*[a-zA-Z]+.*"); // match a number with optional
        // '-' and decimal.
        return str.matches("^[a-zA-Z0-9 ]*$"); // match a number with optional
    }

    public static boolean isValidWordStudId(String str) {
//  return str.matches(".*[a-zA-Z]+.*"); // match a number with optional
        // '-' and decimal.

        return str.matches("^[a-zA-Z0-9-|/\\\\_]*$"); // match a number with optional
    }

    public static boolean checknum(String num) {
        pattern = Pattern.compile(NUMBER_PATTERN);
        matcher = pattern.matcher(num);
        return matcher.matches();

    }

    public static boolean checklwcase(String tstcase) {
        pattern = Pattern.compile(LWCASE_PATTERN);
        matcher = pattern.matcher(tstcase);
        return matcher.matches();

    }

    public static boolean checkemail(String tstcase) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(tstcase);
        return matcher.matches();

    }

    /**
     * Checks for Null String object
     *
     * @return true for not null and false for null String object
     */

    public static String genOutsideURL(String params, String endpoint) {
        String mas = Constants.PRIVATE_KEY;
        //Log.v("Mas::", mas);
        String encryptedUrl = LoginAESProcess.getEncryptedUrlByPropKey(params, mas);
        //Log.v("encrypted Str Prop Key", encryptedUrl);
        try {
            encryptedUrl = toHex(encryptedUrl);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        //Log.v("encrypted Str Prop Key", encryptedUrl);
        String hash = null;
        try {
            hash = generateHashString(params);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        String token = "8900";

        String hexkey = Utility.getrandkey();
        try {
            hexkey = toHex(hexkey);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        String imei = "923494";

        String encryptedimei = LoginAESProcess.getEncryptedUrlByPropKey(imei, mas);
        //Log.v("encrypted Str Imei", encryptedimei);
        try {
            encryptedimei = toHex(encryptedimei);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        String sessid = "923494";

        String encryptedsessid = LoginAESProcess.getEncryptedUrlByPropKey(sessid, mas);
        //Log.v("encrypted Str Sess", encryptedsessid);
        try {
            encryptedsessid = toHex(encryptedsessid);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        String vers = "2.0.0";
        String year = "2016";
        try {
            year = toHex(year);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        String url = Constants.NBK_URL + Constants.NBK_ENDPOINT + endpoint + "/" + encryptedUrl + "/" + hash + "/" + hexkey + Constants.KIFUNGUO_CHA_BARABARA + "/" + Constants.APP_OUTSIDEID + "/" + token + "/" + encryptedimei + "/" + encryptedsessid + "/" + vers + "/" + year;
        return url;
    }

    public static boolean isNotNull(String txt) {
        return txt != null && txt.trim().length() > 0;
    }

    public static String toHex(String arg) throws UnsupportedEncodingException {
        return String.format("%040x", new BigInteger(1, arg.getBytes("UTF-8")));
    }


    public static String generateHashString(String data) throws UnsupportedEncodingException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dataInBytes = data.getBytes("UTF-8");
            md.update(dataInBytes);

            byte[] mdbytes = md.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < mdbytes.length; i++) {
                hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
        }

        return null;
    }

    public static String genURL(String endpoint, String params, Context c) {

        SessionManagement session = new SessionManagement(c);

        HashMap<String, String> tkt = session.getToken();
        String fnltkt = tkt.get(SessionManagement.KEY_TOKEN);
        fnltkt = nextToken(fnltkt);
        if (endpoint.equals("getcheq") || endpoint.equals("loadschools")) {
            fnltkt = nextToken(fnltkt);
        }
        HashMap<String, String> sapp = session.getAppID();
        String fnlappid = sapp.get(SessionManagement.KEY_APPID);

        //Log.v("Plain Appid", fnlappid);
        String encryptedpkey = Utility.getrandkey();
        //Log.v("Encrypted Key", encryptedpkey);
        HashMap<String, String> sesikey = session.getSessKey();
        String skey = sesikey.get(SessionManagement.SESSKEY);
        //Log.v("Session Key", skey);

        String encryptedUrl = LoginAESProcess.getEncryptedUrlByPropKey(params, skey);
        //Log.v("encrypted URL Key", encryptedUrl);

        try {
            encryptedUrl = Utility.toHex(encryptedUrl);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        try {
            encryptedpkey = Utility.toHex(encryptedpkey);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        String mas = Constants.PRIVATE_KEY;
        String encappid = LoginAESProcess.getEncryptedUrlByPropKey(fnlappid, mas);
        //Log.v("encrypted App Id", encappid);
        try {
            encappid = Utility.toHex(encappid);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        String hash = null;
        try {
            hash = Utility.generateHashString(params);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        String imei = "";

        TelephonyManager telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();

        String encryptedimei = LoginAESProcess.getEncryptedUrlByPropKey(imei, skey);

        try {
            encryptedimei = toHex(encryptedimei);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        HashMap<String, String> gtsessid = session.getSessId();
        String fsess = gtsessid.get(SessionManagement.KEY_SESS);

        fsess = LoginAESProcess.getEncryptedUrlByPropKey(fsess, skey);
        try {
            fsess = toHex(fsess);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        String vers = "2.0.0";
        String year = "2016";
        try {
            year = toHex(year);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        String url = Constants.NBK_URL + Constants.NBK_ENDPOINT + endpoint + "/" + encryptedUrl + "/" + hash + "/" + encryptedpkey + Constants.KIFUNGUO_CHA_BARABARA + "/" + encappid + "/" + fnltkt + "/" + encryptedimei + "/" + fsess + "/" + vers + "/" + year;
        return url;
    }

    public static String genURLCBC(String endpoint, String params, Context c) {

        SessionManagement session = new SessionManagement(c);

        HashMap<String, String> tkt = session.getToken();
        String fnltkt = tkt.get(SessionManagement.KEY_TOKEN);
        fnltkt = nextToken(fnltkt);

        HashMap<String, String> sapp = session.getAppID();
        //String fnlappid = sapp.get(SessionManagement.KEY_APPID);
        String fnlappid = "7233839676048371";

        String encryptedpkey = "";
        String encryptedrandomIV = "";
        String encryptedUrl = "";
        String encappid = "";
        String hash = null;
        String imei = "";
        TelephonyManager telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        String encryptedimei = "";
        imei = telephonyManager.getDeviceId();
        HashMap<String, String> gtsessid = session.getSessId();
        String fsess = gtsessid.get(SessionManagement.KEY_SESS);

        Log.d("cbcatgen", fsess);
        String year = "2016";
        try {
           /* encryptedpkey = toHex(AESCBCEncryption.encrypt(AESCBCEncryption.pkey,AESCBCEncryption.piv,AESCBCEncryption.base64Encode(AESCBCEncryption.generateSessionKey())));
            encryptedrandomIV = toHex(AESCBCEncryption.encrypt(AESCBCEncryption.pkey,AESCBCEncryption.piv,AESCBCEncryption.base64Encode(AESCBCEncryption.generateIV())));
            encryptedUrl = toHex(AESCBCEncryption.encrypt(AESCBCEncryption.skey,AESCBCEncryption.siv,params));
            encappid = toHex(AESCBCEncryption.encrypt(AESCBCEncryption.key,AESCBCEncryption.initVector,fnlappid));
            hash = Utility.generateHashString(params);
            encryptedimei = toHex(AESCBCEncryption.encrypt(AESCBCEncryption.skey,AESCBCEncryption.siv,imei));
            fsess = toHex(AESCBCEncryption.encrypt(AESCBCEncryption.skey,AESCBCEncryption.siv,fsess));
            year = toHex(year);*/
        } catch (Exception e) {

        }

        String vers = encryptedrandomIV;

        String url = Constants.NBK_URL + Constants.NBK_ENDPOINT + endpoint + "/" + encryptedUrl + "/" + hash + "/" + encryptedpkey + Constants.KIFUNGUO_CHA_BARABARA + "/" + encappid + "/" + fnltkt + "/" + encryptedimei + "/" + fsess + "/" + vers + "/" + year;
        return url;
    }

    public static String toString(String hex) throws UnsupportedEncodingException, DecoderException {
        return new String(Hex.decodeHex(hex.toCharArray()), "UTF-8");
    }

    public static JSONObject onOutsideresp(JSONObject obj, Context c) {
        SessionManagement session = new SessionManagement(c);

        String mas = Constants.PRIVATE_KEY;
        //Log.v("Mas::", mas);
        String out = obj.optString("inp");
        try {
            out = toString(out);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        } catch (DecoderException e) {
            //e.printStackTrace();
        }

        String decryptVal = LoginAESProcess.getDecryptedUrlByDBKey(out, mas);
        //Log.d(natmobile.natbank.com.natmobile.Constant.LOG_TAG,"Decrypted Str Params::" + decryptVal);
        try {
            obj = new JSONObject(decryptVal);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        String token = obj.optString("token");

        //Log.v("Final Token", token);
        if (!(token == null || token.equals(""))) {
            session.putToken(token);
        }
        return obj;
    }


    public static JSONObject onresp(JSONObject obj, Context c) {
        SessionManagement session = new SessionManagement(c);

        HashMap<String, String> sesikey = session.getSessKey();
        String skey = sesikey.get(SessionManagement.SESSKEY);
        //Log.v("Session Key", skey);
        String out = obj.optString("inp");
        try {
            out = toString(out);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        } catch (DecoderException e) {
            //e.printStackTrace();
        }

        String decryptVal = LoginAESProcess.getDecryptedUrlByDBKey(out, skey);
        //Log.d(natmobile.natbank.com.natmobile.Constant.LOG_TAG,"Decrypted Str Params::" + decryptVal);
        try {
            obj = new JSONObject(decryptVal);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        String token = obj.optString("token");

        //Log.v("Final Token", token);
        if (!(token == null || token.equals(""))) {
            session.putToken(token);
        }
        return obj;
    }

    public static String nextToken(String token) {
        String s = "000000";
        try {
            Integer tokenNo = Integer.parseInt(token);
            tokenNo = (tokenNo < 9999) ? tokenNo + 1 : 1;
            return s.substring(0, s.length() - tokenNo.toString().length()) + tokenNo.toString();
        } catch (Exception e) {
            //e.printStackTrace();
            com.ceva.ubmobile.core.ui.Log.Error(e);
            return null;
        }
    }

    public static int containerHeight(Context ba) {
        DisplayMetrics dm = new DisplayMetrics();
        // ba.getWindowManager().getDefaultDisplay().getMetrics(dm);
        //Toast.makeText(ba.getApplicationContext(), "Screen Height of " + Build.MANUFACTURER + "  is "+Integer.toString(dm.heightPixels) , Toast.LENGTH_LONG).show();

        //get predefined value
        double ratio = 15;

        //check if there is cached value; using SnappyDB

        return (int) (dm.heightPixels / ratio);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String firstLetterUppercase(String word) {
        String response = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
        return response;
    }

    public static String camelCase(String sentence) {
        String[] words = sentence.split(" ");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString().toLowerCase());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString().toLowerCase());
            }
        }
        String titleCaseValue = sb.toString();
        return titleCaseValue;
    }

    public static String stringNumberToDecimal(String a) {

        return String.format("%.2f", Float.parseFloat(a));
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
