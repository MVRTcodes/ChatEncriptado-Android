package com.example.myapplication;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CifradorAES {

    private static final String AES = "AES";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encriptar(String x, SecretKey SK) throws Exception{
            Cipher cipher = Cipher.getInstance(AES);
            SecretKeySpec SKS = new SecretKeySpec(SK.getEncoded(), AES);
            cipher.init(Cipher.ENCRYPT_MODE, SKS);
            byte[] mensajeEnc = cipher.doFinal(x.getBytes("utf-8"));
            String Base64format = Base64.getEncoder().encodeToString(mensajeEnc);
            return Base64format;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String desencriptar(String x, SecretKey SK) throws Exception{
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            byte[] actualByte= Base64.getDecoder().decode(x);
            Cipher cipher = Cipher.getInstance(AES);
            SecretKeySpec SKS = new SecretKeySpec(SK.getEncoded(), AES);
            cipher.init(Cipher.DECRYPT_MODE, SKS);
            byte[] mensajedesEnc = cipher.doFinal(actualByte);
            return new String(mensajedesEnc);
        }
        return null;
    }
}
