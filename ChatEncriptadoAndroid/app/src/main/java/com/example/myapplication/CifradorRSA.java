package com.example.myapplication;

import android.util.Base64;
import android.util.Log;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class CifradorRSA {

    private String descryptedString;
    private byte[] encrytedByte;
    private byte[] descryptedByte;
    private Cipher cipher;

    private final static String OPCION_RSA= "RSA/ECB/OAEPWithSHA1AndMGF1Padding";

    public String encriptar(String mensajeAEncriptar,PublicKey publicKey) throws Exception{
        cipher = Cipher.getInstance(OPCION_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        encrytedByte = cipher.doFinal(mensajeAEncriptar.getBytes());
        return Base64.encodeToString(encrytedByte, Base64.NO_WRAP);
    }

    public String desencriptar(String result,PrivateKey privateKey) throws Exception{
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        descryptedByte = cipher.doFinal(Base64.decode(result, Base64.DEFAULT));
        descryptedString = new String(descryptedByte);
        return descryptedString;
    }
}
