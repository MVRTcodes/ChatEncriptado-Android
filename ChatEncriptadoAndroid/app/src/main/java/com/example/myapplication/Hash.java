package com.example.myapplication;

import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    public static String FRHash(String str,boolean HT) {
        try {
            String THash = HT ? "MD5" : "SHA-1";
            MessageDigest md = MessageDigest.getInstance(THash);
            md.update(str.getBytes());
            byte[] bDigest = md.digest();
            StringBuffer sb = new StringBuffer();

            for(byte b : bDigest){
                sb.append(Integer.toHexString(b & 0xff));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException(e);

        }
    }

    public boolean verificador(String str, String hash)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes());
        byte[] bDigest = md.digest();
        StringBuffer sb = new StringBuffer();

        for(byte b : bDigest){
            sb.append(Integer.toHexString(b & 0xff));
        }
        Log.i("CifratHash",sb.toString());
        Log.i("CifratHash",hash);
        if(sb.toString().equals(hash)) return true;
        else return false;
    }
}
