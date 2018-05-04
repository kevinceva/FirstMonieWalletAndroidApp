package com.ceva.ubmobile.security;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.ceva.ubmobile.BuildConfig;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.network.NetworkUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;

import static com.ceva.ubmobile.security.AESCBCEncryption.base64Decode;
import static com.ceva.ubmobile.security.AESCBCEncryption.base64Encode;
import static com.ceva.ubmobile.security.AESCBCEncryption.decrypt;
import static com.ceva.ubmobile.security.AESCBCEncryption.encrypt;
import static com.ceva.ubmobile.security.AESCBCEncryption.initVector;
import static com.ceva.ubmobile.security.AESCBCEncryption.key;
import static com.ceva.ubmobile.security.AESCBCEncryption.toHex;


/**
 * Created by brian on 07/12/2016.
 */

public class SecurityLayer {

    //public static boolean isSecure = true;
    public final static String KEY_PIV = "pvoke";
    public final static String KEY_PKEY = "pkey";
    public static final String KEY_SIV = "svoke";
    public static final String KEY_SKEY = "skey";
    public static final String KEY_APP_ID = "appid";
    public static final String KEY_DHASH = "DHASH";
    public static final String KEY_INP = "inp";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_SESSION_ID = "sessionID";
    public static final String KEY_ACCOUNT_NUMBER = "agentAccountNumber";
    public static final String KEY_BEFORE_APP_ID = "255255255";
    public static final String KEY_FIRST_APP_ID = "155155155";
    public static final String KEY_VERSION = BuildConfig.VERSION_NAME;
    public static final String KEY_IMEI = "KEYIMEI";
    public static boolean isDebug = false;
    public static boolean isDemo = true;
    public static boolean isSecure = false;

    public static String beforeLogin(String params, String session_id, String endpoint, Context context) throws UnsupportedEncodingException {
        if (isSecure) {
            StringBuffer sb = new StringBuffer();
            String vers = KEY_VERSION;
            String year = getYear();
            String imei = NetworkUtils.getImei(context);

            Log.debug(endpoint + "params before encoding" + params);

            String[] paramarr = params.split("/");
            for (int k = 0; k < paramarr.length; k++) {
                paramarr[k] = URLEncoder.encode(paramarr[k], "utf-8");
            }

            params = TextUtils.join("/", paramarr);
            Log.debug(endpoint + "params after encoding" + params);
            Log.debug("unencrypted url " + Constants.NET_URL + Constants.NET_PATH + endpoint + "/" + params);

            return sb.append(endpoint + "/")
                    .append(toHex(encrypt(key, initVector, params)))
                    .append("/" + Utility.generateHashString(params))
                    .append("/" + session_id)
                    .append(SecurityConstants.CH_KEY)
                    .append("/" + KEY_BEFORE_APP_ID)
                    .append("/1212")
                    .append("/" + toHex(encrypt(key, initVector, imei)))
                    .append("/" + toHex(encrypt(key, initVector, session_id)))
                    .append("/" + vers)
                    .append("/" + KEY_VERSION)
                    .toString();
        }

        return endpoint + "/" + params;
    }

    public static JSONObject decryptBeforeLogin(JSONObject jsonobj, Context context) throws Exception {
        if (isSecure) {
            UBNSession session = new UBNSession(context);
            String status = jsonobj.optString("status");
            //String svoke = jsonobj.getString("svoke");
            String input = jsonobj.optString("inp");
            // System.out.println("svoke [" + svoke + "]");
            // String pkey = jsonobj.getString("pkey");
            //String pvoke = jsonobj.getString("pvoke");
            //String skey = jsonobj.getString("skey");
            String dhash = jsonobj.optString("DHASH");
            Log.debug(input);

            JSONObject decjsonobj = new JSONObject();

            String finalresp = "";

            if ("S".equals(status)) {

                // String pkey_dec = decrypt(key, initVector, AESCBCEncryption.toString(pkey));
                //String pvoke_dec = decrypt(key, initVector, AESCBCEncryption.toString(pvoke));
                //String sessionkey = decrypt(base64Decode(pkey_dec), base64Decode(pvoke_dec), AESCBCEncryption.toString(skey));
                //String sessioniv = decrypt(base64Decode(pkey_dec), base64Decode(pvoke_dec), AESCBCEncryption.toString(svoke));

                // session.setString(KEY_PKEY, pkey_dec);//set pkey
                //Log.debug("setpkey", session.getString(KEY_PKEY));
                //session.setString(KEY_PIV, pvoke_dec);//set piv
                //session.setString(KEY_SKEY, sessionkey);//set skey
                //session.setString(KEY_SIV, sessioniv);
                session.setString(KEY_DHASH, dhash);

                finalresp = decrypt(key, initVector, AESCBCEncryption.toString(input));

                // Log.debug("pkey_dec [" + pkey_dec + "]");
                //Log.debug("pvoke_dec [" + pvoke_dec + "");
                //Log.debug("session key [" + sessionkey + "]");
                //Log.debug("sessioniv [" + sessioniv + "]");
                Log.debug("finalresp [" + finalresp + "]");

                String gen = Utility.generateHashString(finalresp);
                Log.debug("Hashing Status [" + gen.equals(dhash) + "]");

                // decjsonobj.put("pkey_dec", pkey_dec);
                //decjsonobj.put("pvoke_dec", pvoke_dec);
                //decjsonobj.put("sessionkey", sessionkey);
                //decjsonobj.put("sessioniv", sessioniv);
                decjsonobj.put("finalresp", finalresp);
                decjsonobj.put("hashstatus", gen.equals(dhash));

            } else {
                finalresp = jsonobj.optString("msg");
            }
            return new JSONObject(finalresp);
            //return decjsonobj;
            //System.out.println(decjsonobj);
        } else {
            return jsonobj;
        }

    }

    public static String firstLogin(String params, String session_id, String endpoint, Context context) throws UnsupportedEncodingException {
        if (isSecure) {
            StringBuffer sb = new StringBuffer();
            String vers = KEY_VERSION;
            String year = getYear();
            String imei = NetworkUtils.getImei(context);

            UBNSession session = new UBNSession(context);

            session.setString(KEY_SESSION_ID, session_id);

            Log.debug(endpoint + "params before encoding" + params);

            String[] paramarr = params.split("/");
            for (int k = 0; k < paramarr.length; k++) {
                paramarr[k] = URLEncoder.encode(paramarr[k], "utf-8");
            }

            params = TextUtils.join("/", paramarr);
            Log.debug(endpoint + "params after encoding" + params);
            ////device ip | serial no | sw ver | device type | device os | mac address
            String deviceinfo = NetworkUtils.getLocalIpAddress() + "|";
            deviceinfo += Build.SERIAL + "|" + KEY_VERSION + "|" + Build.MANUFACTURER + " " + Build.MODEL + "|" + Build.VERSION.RELEASE + "|" + NetworkUtils.getMacAddress();
            Log.debug("deviceinfo before encoding " + deviceinfo);
            deviceinfo = URLEncoder.encode(deviceinfo, "utf-8");
            Log.debug("deviceinfo after encoding " + deviceinfo);

            return sb.append(endpoint + "/")
                    .append(toHex(encrypt(key, initVector, params)))
                    .append("/" + Utility.generateHashString(params))
                    .append("/" + session_id)
                    .append(SecurityConstants.CH_KEY)
                    .append("/" + KEY_FIRST_APP_ID)
                    .append("/1212")
                    .append("/" + toHex(encrypt(key, initVector, imei)))
                    .append("/" + toHex(encrypt(key, initVector, session_id)))
                    .append("/" + toHex(encrypt(key, initVector, deviceinfo)))
                    .append("/" + KEY_VERSION)
                    .toString();
        } else {
            return endpoint + "/" + params;
        }
    }

    public static JSONObject decryptFirstTimeLogin(JSONObject jsonobj, Context context) throws Exception {
        if (isSecure) {
            UBNSession session = new UBNSession(context);
            String status = jsonobj.getString("status");

            JSONObject decjsonobj = new JSONObject();

            String finalresp = "";

            if ("S".equals(status)) {

                String input = jsonobj.getString("inp");
                String svoke = jsonobj.getString("svoke");
                // System.out.println("svoke [" + svoke + "]");
                String pkey = jsonobj.getString("pkey");
                String pvoke = jsonobj.getString("pvoke");
                String skey = jsonobj.getString("skey");
                String dhash = jsonobj.getString("DHASH");
                Log.debug(input);

                String pkey_dec = decrypt(key, initVector, AESCBCEncryption.toString(pkey));
                String pvoke_dec = decrypt(key, initVector, AESCBCEncryption.toString(pvoke));
                String sessionkey = decrypt(base64Decode(pkey_dec), base64Decode(pvoke_dec), AESCBCEncryption.toString(skey));
                String sessioniv = decrypt(base64Decode(pkey_dec), base64Decode(pvoke_dec), AESCBCEncryption.toString(svoke));

                session.setString(KEY_PKEY, pkey_dec);//set pkey
                Log.debug("setpkey", session.getString(KEY_PKEY));
                session.setString(KEY_PIV, pvoke_dec);//set piv
                session.setString(KEY_SKEY, sessionkey);//set skey
                session.setString(KEY_SIV, sessioniv);
                session.setString(KEY_DHASH, dhash);

                finalresp = decrypt(base64Decode(pkey_dec), base64Decode(pvoke_dec), AESCBCEncryption.toString(input));

                Log.debug("pkey_dec [" + pkey_dec + "]");
                Log.debug("pvoke_dec [" + pvoke_dec + "");
                Log.debug("session key [" + sessionkey + "]");
                Log.debug("sessioniv [" + sessioniv + "]");
                Log.debug("finalresp [" + finalresp + "]");

                String gen = Utility.generateHashString(finalresp);
                Log.debug("Hashing Status [" + gen.equals(dhash) + "]");

                decjsonobj.put("pkey_dec", pkey_dec);
                decjsonobj.put("pvoke_dec", pvoke_dec);
                decjsonobj.put("sessionkey", sessionkey);
                decjsonobj.put("sessioniv", sessioniv);
                decjsonobj.put("finalresp", finalresp);
                decjsonobj.put("hashstatus", gen.equals(dhash));
                Log.debug(decjsonobj.toString());

                JSONObject object = new JSONObject(finalresp);
                String token = object.optString("token");
                String respcode = object.optString("respcode");

                if (respcode.equals("00")) {
                    session.setString(KEY_APP_ID, object.optString("appid"));
                    session.setString(SecurityLayer.KEY_TOKEN, token);
                    session.setString(KEY_IMEI, NetworkUtils.getImei(context));
                }

                return new JSONObject(finalresp);

            } else if (status.equals("U")) {
                JSONObject resp = new JSONObject();
                resp.put("respcode", "U"); //force update
                resp.put("respdesc", jsonobj.optString("respdesc"));
                return resp;
            } else {
                JSONObject resp = new JSONObject();
                resp.put("respcode", "custom");
                resp.put("respdesc", jsonobj.optString("msg"));
                return resp;
            }
        } else {
            return jsonobj;
        }

        //return decjsonobj;
        //System.out.println(decjsonobj);

    }

    public static String generalEncrypt(String param) {
        String cipher = AESCBCEncryption.encrypt(AESCBCEncryption.key, AESCBCEncryption.initVector, param);

        return cipher;

    }

    public static String generalLogin(String params, String session_id, Context context, String endpoint) throws Exception {
        if (isSecure) {
            UBNSession session = new UBNSession(context);
            String imei = "";
            if (session.getString(KEY_IMEI) != null) {
                imei = session.getString(KEY_IMEI);
            } else {
                imei = NetworkUtils.getImei(context);
                session.setString(KEY_IMEI, imei);
            }

            String skey = session.getString(SecurityLayer.KEY_SKEY);
            Log.debug("skey", skey);
            String siv = session.getString(SecurityLayer.KEY_SIV);
            Log.debug("siv", siv);
            String pkey = (session.getString(SecurityLayer.KEY_PKEY));
            Log.debug("pkey", pkey);
            String piv = (session.getString(SecurityLayer.KEY_PIV));
            Log.debug("piv", piv);
            String appid = session.getString(SecurityLayer.KEY_APP_ID);
            Log.debug("appid", appid);
            StringBuffer sb = new StringBuffer();

            Log.debug(endpoint + "params before encoding" + params);

            String[] paramarr = params.split("/");
            for (int k = 0; k < paramarr.length; k++) {
                paramarr[k] = URLEncoder.encode(paramarr[k], "utf-8");
            }

            params = TextUtils.join("/", paramarr);
            Log.debug(endpoint + "params after encoding" + params);

            String deviceinfo = NetworkUtils.getLocalIpAddress() + "|";
            deviceinfo += Build.SERIAL + "|" + BuildConfig.VERSION_NAME + "|" + Build.MANUFACTURER + " " + Build.MODEL + "|" + Build.VERSION.RELEASE + "|" + NetworkUtils.getMacAddress();
            Log.debug("deviceinfo before encoding " + deviceinfo);
            deviceinfo = URLEncoder.encode(deviceinfo, "UTF-8");
            Log.debug("deviceinfo after encoding " + deviceinfo);
            Log.debug("phone imei", imei);

            byte[] randomKey = base64Decode(skey);
            byte[] randomSIV = base64Decode(siv);
            byte[] dummy_pkey = base64Decode(pkey);
            byte[] dummy_piv = base64Decode(piv);
            // String encryptedUrl = LoginAESProcess.getEncryptedUrlByPropKey(params, randkey);
            String encryptedUrl = encrypt(base64Decode(skey), base64Decode(siv), params);
            String encryptedpkey = toHex(encrypt(base64Decode(pkey), base64Decode(piv), base64Encode(randomKey)));//LoginAESProcess.getEncryptedUrlByPropKey(randkey, pkey);
            String encryptedRandomIV = toHex(encrypt(base64Decode(pkey), base64Decode(piv), base64Encode(randomSIV)));

            String encappid = toHex(encrypt(key, initVector, appid));

            String vers = KEY_VERSION;
            String year = getYear();

            //Log.d("1", "=============START KEYS===========");
            //Log.d("encryptedpkey", encryptedpkey);
            //Log.d("encryptedpiv", encryptedRandomIV);
            //Log.d("2", "=============END KEYS===========");

            session.setString(KEY_SESSION_ID, session_id);

            return sb.append(endpoint + "/")
                    .append(toHex(encryptedUrl))
                    .append("/" + Utility.generateHashString(params))
                    .append("/" + encryptedpkey)
                    .append(SecurityConstants.CH_KEY)
                    .append("/" + encappid)
                    .append("/1212")
                    .append("/" + toHex(encrypt(dummy_pkey, dummy_piv, imei)))
                    .append("/" + toHex(encrypt(dummy_pkey, dummy_piv, session_id)))
                    .append("/" + encryptedRandomIV)
                    .append("/" + KEY_VERSION)
                    .toString();
        } else {
            return endpoint + "/" + params;
        }
    }

    public static JSONObject decryptGeneralLogin(JSONObject jsonobj, Context context) throws Exception {
        if (isSecure) {
            UBNSession session = new UBNSession(context);
            String status = jsonobj.getString("status");

            // System.out.println("pkey ["+pkey+"]\n pvoke ["+pvoke+"]");

            JSONObject decjsonobj = new JSONObject();

            byte[] pkey = base64Decode(session.getString(KEY_PKEY));    //generateSessionKey(); // 256 bit key
            byte[] pinitVector = base64Decode(session.getString(KEY_PIV));
            String finalresp = "";

            if ("S".equals(status)) {
                String svoke = jsonobj.getString("svoke");
                String input = jsonobj.getString("inp");
                Log("svoke [" + svoke + "]");
                //String pkey = jsonobj.getString("pkey");
                //String pvoke = jsonobj.getString("pvoke");
                String skey = jsonobj.getString("skey");
                String dhash = jsonobj.getString("DHASH");

                //String pkey_dec = decrypt(key, initVector, toString(pkey));
                //String pvoke_dec =  decrypt(key, initVector, toString(pvoke));
                String sessionkey = decrypt(pkey, pinitVector, AESCBCEncryption.toString(skey));
                String sessioniv = decrypt(pkey, pinitVector, AESCBCEncryption.toString(svoke));
                finalresp = decrypt(pkey, pinitVector, AESCBCEncryption.toString(input));

                session.setString(KEY_SKEY, sessionkey);
                session.setString(KEY_SIV, sessioniv);

                ///System.out.println("pkey_dec ["+pkey_dec+"]");
                //System.out.println("pvoke_dec ["+pvoke_dec+"");
                Log("session key [" + sessionkey + "]");
                Log("sessioniv [" + sessioniv + "]");
                Log("finalresp [" + finalresp + "]");

                String gen = Utility.generateHashString(finalresp);
                Log("Hashing Status [" + gen.equals(dhash) + "]");

                decjsonobj.put("sessionkey", sessionkey);
                decjsonobj.put("sessioniv", sessioniv);
                decjsonobj.put("finalresp", finalresp);
                decjsonobj.put("hashstatus", gen.equals(dhash));

                JSONObject objD = new JSONObject(finalresp);
                String token = objD.optString("token");
                String appid = objD.optString("appid");

                //session.setString(SecurityLayer.KEY_APP_ID,appid);
                session.setString(SecurityLayer.KEY_TOKEN, token);
                Log(decjsonobj.toString());
                return new JSONObject(finalresp);

            } else if (status.equals("U")) {
                JSONObject resp = new JSONObject();
                resp.put("respcode", "U");
                resp.put("respdesc", jsonobj.optString("respdesc"));
                return resp;
            } else {
                JSONObject resp = new JSONObject();
                resp.put("respcode", "custom");
                resp.put("respdesc", jsonobj.optString("msg"));
                return resp;
            }
        } else {
            return jsonobj;
        }

    }

    public static String genURLCBC(String endpoint, String params, Context context) {
        if (isSecure) {

            Log.debug(endpoint + "params before encoding" + params);

            try {
                String[] paramarr = params.split("/");
                for (int k = 0; k < paramarr.length; k++) {
                    paramarr[k] = URLEncoder.encode(paramarr[k], "utf-8").replace("/", "%2F");
                }
                params = TextUtils.join("/", paramarr);
                Log.debug("unencrypted: " + Constants.NET_URL + Constants.NET_PATH + endpoint + "/" + params);
            } catch (Exception e) {
                Log.Error(e);
            }

            Log.debug(endpoint + "params after encoding" + params);
            UBNSession session = new UBNSession(context);
            String token = session.getString(KEY_TOKEN);
            Log.debug("existing_token", token);
            String fnltkt = token;
            fnltkt = Utility.nextToken(fnltkt);
            Log.debug("next_token", token);

            String skey = session.getString(SecurityLayer.KEY_SKEY);
            Log.debug("skey", skey);
            String siv = session.getString(SecurityLayer.KEY_SIV);
            Log.debug("siv", siv);
            String pkey = (session.getString(SecurityLayer.KEY_PKEY));
            Log.debug("pkey", pkey);
            String piv = (session.getString(SecurityLayer.KEY_PIV));
            Log.debug("piv", piv);
            String appid = session.getString(SecurityLayer.KEY_APP_ID);
            Log.debug("appid", appid);

            String encryptedpkey = "";
            String encryptedrandomIV = "";
            String encryptedUrl = "";
            String encappid = "";
            String hash = null;

            String encryptedimei = "";
            String imei = NetworkUtils.getImei(context);

            String fsess = session.getString(KEY_SESSION_ID);

            //Log.d("cbcatgen", fsess);
            String year = getYear();
            try {
                encryptedpkey = toHex(encrypt(base64Decode(pkey), base64Decode(piv), base64Encode(AESCBCEncryption.generateSessionKey())));
                encryptedrandomIV = toHex(encrypt(base64Decode(pkey), base64Decode(piv), base64Encode(AESCBCEncryption.generateIV())));
                encryptedUrl = toHex(encrypt(base64Decode(skey), base64Decode(siv), params));
                encappid = toHex(encrypt(key, initVector, appid));
                hash = Utility.generateHashString(params);
                encryptedimei = toHex(encrypt(base64Decode(skey), base64Decode(siv), imei));
                fsess = toHex(encrypt(base64Decode(skey), base64Decode(siv), fsess));
                year = toHex(KEY_VERSION);
            } catch (Exception e) {

            }

            //String vers = encryptedrandomIV;

            return endpoint + "/" + encryptedUrl + "/" + hash + "/" + encryptedpkey + SecurityConstants.CH_KEY + "/" + encappid + "/" + fnltkt + "/" + encryptedimei + "/" + fsess + "/" + encryptedrandomIV + "/" + year;
        } else {
            return endpoint + "/" + params;
        }

    }

    public static JSONObject decryptTransaction(JSONObject jsonobj, Context context) throws Exception {
        if (isSecure) {
            UBNSession session = new UBNSession(context);
            String status = jsonobj.getString("status");
            String svoke = jsonobj.getString("svoke");
            String input = jsonobj.getString("inp");
            System.out.println("svoke [" + svoke + "]");
            String dhash = jsonobj.getString("DHASH");

            // System.out.println("pkey ["+pkey+"]\n pvoke ["+pvoke+"]");

            JSONObject decjsonobj = new JSONObject();

            byte[] pkey = base64Decode(session.getString(KEY_PKEY));    //generateSessionKey(); // 256 bit key
            byte[] pinitVector = base64Decode(session.getString(KEY_PIV));

            byte[] skey = base64Decode(session.getString(KEY_SKEY));

            String finalresp = "";
            JSONObject data = null;

            if ("S".equals(status)) {

                //String pkey_dec = decrypt(key, initVector, toString(pkey));
                //String pvoke_dec =  decrypt(key, initVector, toString(pvoke));
                String sessioniv = decrypt(pkey, pinitVector, AESCBCEncryption.toString(svoke));
                finalresp = decrypt(skey, base64Decode(sessioniv), AESCBCEncryption.toString(input));

                //finalresp = URLDecoder.decode(finalresp, "utf-8");

                session.setString(KEY_SIV, sessioniv);
                data = new JSONObject(finalresp);

                String token = data.optString("token");
                Log.debug("returned_token", token);
                session.setString(KEY_TOKEN, token);

                String appid = data.optString("appid");
                // session.setString(KEY_APP_ID,appid);

                System.out.println("finalresp [" + finalresp + "]");
                String gen = Utility.generateHashString(finalresp);
                System.out.println("Hashing Status [" + gen.equals(dhash) + "]");

                decjsonobj.put("pkey_dec", "");
                decjsonobj.put("pvoke_dec", "");
                decjsonobj.put("sessioniv", sessioniv);
                decjsonobj.put("finalresp", finalresp);
                decjsonobj.put("hashstatus", gen.equals(dhash));

            }
            Log.debug(decjsonobj.toString());
            // System.out.println(decjsonobj);
            return data;
        } else {
            return jsonobj;
        }

    }

    public static void Log(String tag, String message) {
        if (isDebug) {
            System.out.println(tag + ":" + message);
        }
    }

    public static void generateToken(Context context) {
        try {
            UBNSession session = new UBNSession(context);
            String current = session.getString(KEY_TOKEN);
            int next = Integer.parseInt(current) + 2;
            String nextst = String.format("%06d", next);
            Log.debug("generating next token", nextst);

            session.setString(KEY_TOKEN, nextst);
        } catch (Exception e) {
            Log.Error(e);
        }
    }

    public static void Log(String message) {
        if (BuildConfig.DEBUG) {
            System.out.println(message);
        }
    }

    public static String getYear() {
        // return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        return BuildConfig.VERSION_NAME;
    }

    public static String getSHA256(String data) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            byte byteData[] = md.digest();

            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
