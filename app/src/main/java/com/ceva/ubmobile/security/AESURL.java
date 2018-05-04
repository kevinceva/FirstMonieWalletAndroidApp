package com.ceva.ubmobile.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class AESURL {


    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static void main(String[] args) {

        try {

		/*	//Log.d(natmobile.natbank.com.natmobile.Constant.LOG_TAG,"Welcome to program");

			*//* code for key generation *//*
            *//*byte[] symkey = generateSessionKey();
            String generatedKey = base64Encode(symkey);
			//Log.d(natmobile.natbank.com.natmobile.Constant.LOG_TAG,"Key:::"+generatedKey);*//*
            String generatedKey ="o+wSIWWhQo6KFcuWm+vRjQ==";
			*//*Code for Encryption *//*
			String reqData = "UEtg1VQpkNAViJv5ENB9ukP8+xFcTJiXqlnBcZIGfi75GxAdFMkLiNntYa6bwOH5";
			String encdata = encryptRequestWithSessionKey(base64Decode(generatedKey), reqData.getBytes());
			//Log.d(natmobile.natbank.com.natmobile.Constant.LOG_TAG,"Encripted Data ["+encdata+"]");


			*//*Code for Decryption *//*
			String plaindata = decryptRequestWithSessionKey(base64Decode(generatedKey),encdata);
			//Log.d(natmobile.natbank.com.natmobile.Constant.LOG_TAG,"after decrypt data"+plaindata);*/

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }


    public static byte[] generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES", "BC");
        kgen.init(256);
        SecretKey key = kgen.generateKey();
        byte[] symmKey = key.getEncoded();
        return symmKey;
    }


    public static String encryptRequestWithSessionKey(byte[] sessionKey, byte[] RequestData) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {

        String symetricKeyAlg = "AES/ECB/PKCS5Padding"; //AES/ECB/PKCS7Padding
        symetricKeyAlg = "AES/ECB/PKCS7Padding";
        String pkiProvider = "BC";

        SecretKeySpec symmKeySpec = new SecretKeySpec(sessionKey, symetricKeyAlg);
        Cipher symmCipher = Cipher.getInstance(symetricKeyAlg, pkiProvider);
        symmCipher.init(Cipher.ENCRYPT_MODE, symmKeySpec);
        byte[] encXMLPidData = symmCipher.doFinal(RequestData);
        return android.util.Base64.encodeToString(encXMLPidData, android.util.Base64.NO_WRAP);

    }


    public static String decryptRequestWithSessionKey(
            byte[] sessionKey, String encData) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {

        String symetricKeyAlg = "AES/ECB/PKCS5Padding"; //AES/ECB/PKCS7Padding

        symetricKeyAlg = "AES/ECB/PKCS7Padding";

        String pkiProvider = "BC";
        SecretKeySpec symmKeySpec = new SecretKeySpec(sessionKey, symetricKeyAlg);
        Cipher symmCipher = Cipher.getInstance(symetricKeyAlg, pkiProvider);
        symmCipher.init(Cipher.DECRYPT_MODE, symmKeySpec);
        byte[] xmlData = symmCipher.doFinal(android.util.Base64.decode(encData, android.util.Base64.NO_WRAP));
        return new String(xmlData);

    }


    public static byte[] base64Decode(String base64String) {
        return android.util.Base64.decode(base64String, android.util.Base64.NO_WRAP);
    }

}
