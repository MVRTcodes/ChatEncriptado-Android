package com.example.myapplication;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class FirmaDigital {

    public byte[] CreateFD(String str, PrivateKey priv){
        byte[] firma = null;
        try {
            Signature s = Signature.getInstance("SHA256withDSA");
            s.initSign(priv);
            byte[] bytestr = str.getBytes();
            s.update(bytestr);
            firma = s.sign();
            System.out.println("Firma creada correctamente");
        }catch (Exception ex){
            System.err.println("Error creando firma" + ex);
        }
        return firma;
    }

    public boolean ValidarFD(byte[] str, byte[] firma, PublicKey publ){
        boolean valido = false;
        try {
            Signature s = Signature.getInstance("SHA256withDSA");
            s.initVerify(publ);
            s.update(str);
            valido = s.verify(firma);
            System.out.println("Firma valida");
        }catch (Exception ex){
            System.err.println("Error validando firma" + ex);
        }
        return valido;
    }
}
