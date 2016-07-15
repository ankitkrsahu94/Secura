package com.secura.ankit.secura.utils;

import android.util.Base64;
import android.util.Log;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import javax.crypto.Cipher;


public class Utils{
    static final String TAG = "SymmetricAlgorithmAES";
    // Original text
    //String theTestText = "This is just a simple test";
    // Set up secret key spec for 128-bit AES encryption and decryption
    SecretKeySpec sks = null;


    public String encrypt(String data){

        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any data used as random seed".getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Log.e(TAG, "AES secret key spec error");
        }
        // Encode the original data with AES
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(data.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "AES encryption error");
        }

         return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
    }


    public String decrypt(String data){

        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, sks);
            decodedBytes = c.doFinal(Base64.decode(data, Base64.DEFAULT));
        } catch (Exception e) {
            Log.e(TAG, "AES decryption error");
        }
        return new String(decodedBytes);
    }
}