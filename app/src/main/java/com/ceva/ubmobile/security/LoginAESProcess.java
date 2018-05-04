package com.ceva.ubmobile.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class LoginAESProcess {

    public String url = null;
    public String encryptedUrl = null;


    /*
     * Code for Step1 : Encrypt url and sending request
     */
    public static String getEncryptedUrlByDBKey(String url, String dbKey) {
        String encryptedStr = null;

        AESURL AESURL = new AESURL();
        try {
            byte[] dbkeys = com.ceva.ubmobile.security.AESURL.base64Decode(dbKey);
            encryptedStr = com.ceva.ubmobile.security.AESURL.encryptRequestWithSessionKey(dbkeys, url.getBytes());
        } catch (InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException | NoSuchProviderException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        return encryptedStr;

    }

    public static String getEncryptedUrlByPropKey(String encryptedStrByDBKey, String propKey) {
        String encryptedStr = null;

        AESURL AESURL = new AESURL();
        try {
            byte[] dbkeys = com.ceva.ubmobile.security.AESURL.base64Decode(propKey);
            encryptedStr = com.ceva.ubmobile.security.AESURL.encryptRequestWithSessionKey(dbkeys, encryptedStrByDBKey.getBytes());
        } catch (InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException | NoSuchProviderException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        return encryptedStr;

    }


    public static String getDecryptedUrlByDBKey(String encryptedStr, String dbKey) {
        String decryptedStr = null;
        AESURL AESURL = new AESURL();
        try {
            decryptedStr = com.ceva.ubmobile.security.AESURL.decryptRequestWithSessionKey(com.ceva.ubmobile.security.AESURL.base64Decode(dbKey), encryptedStr);
        } catch (InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException | NoSuchProviderException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        return decryptedStr;

    }

    public static String getDecryptedUrlByPropKey(String decryptedStrByDbKey, String propKey) {
        String decryptedStr = null;
        AESURL AESURL = new AESURL();
        try {
            decryptedStr = com.ceva.ubmobile.security.AESURL.decryptRequestWithSessionKey(android.util.Base64.decode(propKey, android.util.Base64.NO_WRAP), decryptedStrByDbKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException | NoSuchProviderException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        return decryptedStr;

    }


    public static void main(String args[]) {

	/*	String url="mobile=12313123?email=adadd@gmail.com";
        //Log.d(natmobile.natbank.com.natmobile.Constant.LOG_TAG,"input url:::"+url);
		String encryptedStrByDBKey=getEncryptedUrlByDBKey(url,dbKey);
		//Log.d(natmobile.natbank.com.natmobile.Constant.LOG_TAG,"encrypted Str By DB Key::"+encryptedStrByDBKey);
		String encryptedUrl=getEncryptedUrlByPropKey(encryptedStrByDBKey,dbKey);
		//Log.d(natmobile.natbank.com.natmobile.Constant.LOG_TAG,"encrypted Str By Prop Key::"+encryptedUrl);
		String decryptValfromPropKey=getDecryptedUrlByPropKey(encryptedUrl,propKey);
		//Log.d(natmobile.natbank.com.natmobile.Constant.LOG_TAG,"Decrypted Str By Prop Key::"+decryptValfromPropKey);
		String decryptVal=getDecryptedUrlByDBKey(decryptValfromPropKey,dbKey);
		//Log.d(natmobile.natbank.com.natmobile.Constant.LOG_TAG,"Decrypted Str By DB Key::"+decryptVal);*/

    }

}
