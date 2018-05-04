package com.ceva.ubmobile.security;

import android.util.Base64;

import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.Log;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESCBCEncryption implements SecurityConstants {
    public static byte[] pkey = base64Decode("amIY7bP5pfeG5wmh3tnBl2ki86S+dxDZu991NxdONnw=");
    public static byte[] piv = base64Decode("cE9XEq+2I+NVp6YblJGOHw==");
    public static byte[] skey = base64Decode("EHxxOa9FpK256bvlaICg2bYVsxKodO4XekhsJEdNzaE=");
    public static byte[] siv = base64Decode("7TLKFfqtScBUm0eP+QIitg==");
    public static byte[] key = base64Decode("mYCvSane74ZV2rS5BXiWi0beWxFQ2037I00wLipnFhU=");    //generateSessionKey(); // 256 bit key
    public static byte[] initVector = base64Decode("Eq/cxCxt6YPHUdy65/FMuA==");
    public static byte[] dummy_pkey = base64Decode("sjhE1SXzA5VddkhS9NC8Sws7iC5BP/yuUigXliQGRr0=");
    public static byte[] dummy_piv = base64Decode("V3SwyU7/TjPJt3Zkye9klA==");
    public static byte[] dummy_skey = base64Decode("EHxxOa9FpK256bvlaICg2bYVsxKodO4XekhsJEdNzaE=");
    public static byte[] dummy_siv = base64Decode("l6Zy71TDy2BMiyPNuNCjcA==");

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static String encrypt(byte[] key, byte[] initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance(SYMETRICKEY_ALG);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception ex) {
            // ex.printStackTrace();
            Log.debug(ex.toString());
        }

        return null;
    }

    public static String decrypt(byte[] key, byte[] initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance(SYMETRICKEY_ALG);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP));

            return new String(original);
        } catch (Exception ex) {
            // ex.printStackTrace();
            Log.debug(ex.toString());
        }

        return null;
    }

    public static byte[] generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator kgen = KeyGenerator.getInstance(KEYGEN_ALG, PKI_PROVIDER);
        kgen.init(SYMETRIC_KEY_SIZE);
        SecretKey key = kgen.generateKey();
        byte[] symmKey = key.getEncoded();
        return symmKey;
    }

    public static byte[] generateIV() throws NoSuchAlgorithmException, NoSuchProviderException {
        return Arrays.copyOfRange(generateSessionKey(), 0, 16);
    }

    public static String base64Encode(byte[] binaryData) {
        return Base64.encodeToString(binaryData, Base64.NO_WRAP);
    }


    public static byte[] base64Decode(String base64String) {
        return Base64.decode(base64String, Base64.NO_WRAP);
    }

    public static String firstLoginDecrypt(String param) throws UnsupportedEncodingException {
        String plaintext = decrypt(key, initVector, param);
        return plaintext;
    }

    public static String firstLogin(String imei, String params, String session_id, String endpoint) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        String vers = "2.0.0";
        String year = "2016";

        return sb.append(Constants.NET_URL + Constants.NET_PATH + endpoint)
                .append(toHex(encrypt(key, initVector, params)))
                .append("/" + Utility.generateHashString(params))
                .append("/" + session_id)
                .append(SecurityConstants.CH_KEY)
                .append("/" + Constants.APP_ID)
                .append("/1212")
                .append("/" + toHex(encrypt(key, initVector, imei)))
                .append("/" + toHex(encrypt(key, initVector, session_id)))
                .append("/" + vers)
                .append("/" + year)
                .toString();
    }

    public static String generalLogin(String imei, String params, String session_id, String endpoint) throws Exception {
        StringBuffer sb = new StringBuffer();

        byte[] randomKey = dummy_skey;
        byte[] randomSIV = dummy_siv;
        // String encryptedUrl = LoginAESProcess.getEncryptedUrlByPropKey(params, randkey);
        String encryptedUrl = encrypt(dummy_skey, dummy_siv, params);
        String encryptedpkey = toHex(encrypt(dummy_pkey, dummy_piv, base64Encode(randomKey)));//LoginAESProcess.getEncryptedUrlByPropKey(randkey, pkey);
        String encryptedRandomIV = toHex(encrypt(dummy_pkey, dummy_piv, base64Encode(randomSIV)));

        String encappid = toHex(encrypt(key, initVector, Constants.APP_ID));

        String vers = "2.0.0";
        String year = "2016";

        //Log.d("1", "=============START KEYS===========");
        //Log.d("encryptedpkey", encryptedpkey);
        //Log.d("encryptedpiv", encryptedRandomIV);
        //Log.d("2", "=============END KEYS===========");

        return sb.append(Constants.NET_URL + Constants.NET_PATH + endpoint + "/")
                .append(toHex(encryptedUrl))
                .append("/" + Utility.generateHashString(params))
                .append("/" + encryptedpkey)
                .append(SecurityConstants.CH_KEY)
                .append("/" + encappid)
                .append("/1212")
                .append("/" + toHex(encrypt(dummy_pkey, dummy_piv, imei)))
                .append("/" + toHex(encrypt(dummy_pkey, dummy_piv, session_id)))
                .append("/" + encryptedRandomIV)
                .append("/" + year)
                .toString();
    }


    public static String reference(String imei, String params, String session_id, String endpoint) throws Exception {
        StringBuffer sb = new StringBuffer();

        byte[] randomKey = generateSessionKey();
        byte[] randomSIV = generateIV();
        // String encryptedUrl = LoginAESProcess.getEncryptedUrlByPropKey(params, randkey);
        String encryptedUrl = encrypt(randomKey, randomSIV, params);
        String encryptedpkey = toHex(encrypt(pkey, piv, base64Encode(randomKey)));//LoginAESProcess.getEncryptedUrlByPropKey(randkey, pkey);
        String encryptedRandomIV = toHex(encrypt(pkey, piv, base64Encode(randomSIV)));

        String encappid = toHex(encrypt(key, initVector, "7233839676048371"));

        String vers = "2.0.0";
        String year = "2016";

        return sb.append(Constants.NET_URL + Constants.NET_PATH + endpoint + "/")
                .append(toHex(encryptedUrl))
                .append("/" + Utility.generateHashString(params))
                .append("/" + encryptedpkey)
                .append(SecurityConstants.CH_KEY)
                .append("/" + encappid)
                .append("/1212")
                .append("/" + toHex(encrypt(pkey, piv, imei)))
                .append("/" + toHex(encrypt(pkey, piv, session_id)))
                .append("/" + encryptedRandomIV)
                .append("/" + year)
                .toString();
    }

    public static JSONObject decryptFirstTimeLogin(JSONObject jsonobj) throws Exception {

        String status = jsonobj.getString("status");
        String svoke = jsonobj.getString("svoke");
        String input = jsonobj.getString("inp");
        System.out.println("svoke [" + svoke + "]");
        String pkey = jsonobj.getString("pkey");
        String pvoke = jsonobj.getString("pvoke");
        String skey = jsonobj.getString("skey");
        String dhash = jsonobj.getString("DHASH");

        System.out.println("pkey [" + pkey + "]\n pvoke [" + pvoke + "]");

        JSONObject decjsonobj = new JSONObject();

        String finalresp = "";

        if ("S".equals(status)) {

            String pkey_dec = decrypt(key, initVector, toString(pkey));
            String pvoke_dec = decrypt(key, initVector, toString(pvoke));
            String sessionkey = decrypt(base64Decode(pkey_dec), base64Decode(pvoke_dec), toString(skey));
            String sessioniv = decrypt(base64Decode(pkey_dec), base64Decode(pvoke_dec), toString(svoke));
            finalresp = decrypt(base64Decode(pkey_dec), base64Decode(pvoke_dec), toString(input));

            System.out.println("pkey_dec [" + pkey_dec + "]");
            System.out.println("pvoke_dec [" + pvoke_dec + "");
            System.out.println("session key [" + sessionkey + "]");
            System.out.println("sessioniv [" + sessioniv + "]");
            System.out.println("finalresp [" + finalresp + "]");

            String gen = Utility.generateHashString(finalresp);
            System.out.println("Hashing Status [" + gen.equals(dhash) + "]");

            decjsonobj.put("pkey_dec", pkey_dec);
            decjsonobj.put("pvoke_dec", pvoke_dec);
            decjsonobj.put("sessionkey", sessionkey);
            decjsonobj.put("sessioniv", sessioniv);
            decjsonobj.put("finalresp", finalresp);
            decjsonobj.put("hashstatus", gen.equals(dhash));

        }

        return new JSONObject(finalresp);
        //System.out.println(decjsonobj);

    }

    public static void decryptGeneralLogin(JSONObject jsonobj) throws Exception {

        String status = jsonobj.getString("status");
        String svoke = jsonobj.getString("svoke");
        String input = jsonobj.getString("inp");
        System.out.println("svoke [" + svoke + "]");
        //String pkey = jsonobj.getString("pkey");
        //String pvoke = jsonobj.getString("pvoke");
        String skey = jsonobj.getString("skey");
        String dhash = jsonobj.getString("DHASH");

        // System.out.println("pkey ["+pkey+"]\n pvoke ["+pvoke+"]");

        JSONObject decjsonobj = new JSONObject();

        byte[] key = base64Decode("mYCvSane74ZV2rS5BXiWi0beWxFQ2037I00wLipnFhU=");    //generateSessionKey(); // 256 bit key
        byte[] initVector = base64Decode("Eq/cxCxt6YPHUdy65/FMuA==");

        byte[] pkey = base64Decode("sjhE1SXzA5VddkhS9NC8Sws7iC5BP/yuUigXliQGRr0=");    //generateSessionKey(); // 256 bit key
        byte[] pinitVector = base64Decode("V3SwyU7/TjPJt3Zkye9klA==");

        if ("S".equals(status)) {

            //String pkey_dec = decrypt(key, initVector, toString(pkey));
            //String pvoke_dec =  decrypt(key, initVector, toString(pvoke));
            String sessionkey = decrypt(pkey, pinitVector, toString(skey));
            String sessioniv = decrypt(pkey, pinitVector, toString(svoke));
            String finalresp = decrypt(pkey, pinitVector, toString(input));

            ///System.out.println("pkey_dec ["+pkey_dec+"]");
            //System.out.println("pvoke_dec ["+pvoke_dec+"");
            System.out.println("session key [" + sessionkey + "]");
            System.out.println("sessioniv [" + sessioniv + "]");
            System.out.println("finalresp [" + finalresp + "]");

            String gen = Utility.generateHashString(finalresp);
            System.out.println("Hashing Status [" + gen.equals(dhash) + "]");

            decjsonobj.put("pkey_dec", "");
            decjsonobj.put("pvoke_dec", "");
            decjsonobj.put("sessionkey", sessionkey);
            decjsonobj.put("sessioniv", sessioniv);
            decjsonobj.put("finalresp", finalresp);
            decjsonobj.put("hashstatus", gen.equals(dhash));

        }

        System.out.println(decjsonobj);

    }

    public static void decryptTransaction(JSONObject jsonobj) throws Exception {

        String status = jsonobj.getString("status");
        String svoke = jsonobj.getString("svoke");
        String input = jsonobj.getString("inp");
        System.out.println("svoke [" + svoke + "]");
        String dhash = jsonobj.getString("DHASH");

        // System.out.println("pkey ["+pkey+"]\n pvoke ["+pvoke+"]");

        JSONObject decjsonobj = new JSONObject();

        byte[] key = base64Decode("mYCvSane74ZV2rS5BXiWi0beWxFQ2037I00wLipnFhU=");    //generateSessionKey(); // 256 bit key
        byte[] initVector = base64Decode("Eq/cxCxt6YPHUdy65/FMuA==");

        byte[] pkey = base64Decode("sjhE1SXzA5VddkhS9NC8Sws7iC5BP/yuUigXliQGRr0=");    //generateSessionKey(); // 256 bit key
        byte[] pinitVector = base64Decode("V3SwyU7/TjPJt3Zkye9klA==");

        byte[] skey = base64Decode("EHxxOa9FpK256bvlaICg2bYVsxKodO4XekhsJEdNzaE=");

        if ("S".equals(status)) {

            //String pkey_dec = decrypt(key, initVector, toString(pkey));
            //String pvoke_dec =  decrypt(key, initVector, toString(pvoke));
            String sessioniv = decrypt(pkey, pinitVector, toString(svoke));
            String finalresp = decrypt(skey, base64Decode(sessioniv), toString(input));

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

    }

    public static String toHex(String arg) throws UnsupportedEncodingException {
        return String.format("%040x", new BigInteger(1, arg.getBytes("UTF-8")));
    }

    public static String toString(String hex) throws UnsupportedEncodingException, DecoderException {
        return new String(Hex.decodeHex(hex.toCharArray()), "UTF-8");
    }

    public static String b64_sha256(String inputString) {
        String outputString = "";
        if (inputString != null) {
            outputString = base64Encode(DigestUtils.sha256(inputString)).trim();
            Log.debug("b64_sha256 outputString::" + outputString);
        } else {
            Log.debug("Input String Missing for b64_sha256");
        }
        outputString = outputString.substring(0, outputString.length() - 1);
        return outputString;

    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException {
       /* String key = "Bar12345Bar12345"; // 128 bit key
        String initVector = "RandomInitVector"; // 16 bytes IV
*/

        //generateIV();

        //System.out.println("length : "+EnDesEncyp.base64Encode(initVector));

        //System.out.println(decrypt(key, initVector,
        //encrypt(key, initVector, "Hello World")));

        //System.out.println(firstLogin());
    }
}