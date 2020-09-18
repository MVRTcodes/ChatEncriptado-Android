package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


/**
 * Client en Androdi que funciona com a client d'un Xat.
 * Si es fa servir l'emulador cal fer
 * posar primer en marxa el emulador i després
 * telnet localhost 5554
 * auth
 * redir add tcp:5050:8189
 * <p>
 * telnet localhost 8189
 *
 * @author sergi.grau@fje.edu
 * @version 1.0 6.4.2020
 * @format UTF-8
 * <p>
 * NOTES
 * ORIGEN
 * Exercici demostratiu de comunicacions amb socols i fils
 */
public class ClientXatActivity extends AppCompatActivity {

    private Button boto;
    private TextView missatges;
    private EditText entrada;
    private PrintWriter sortida;
    private Socket socol = null;
    private String enctipe;
    private TextView TVtipoenc;

    private Hash H;
    private CifradorAES cAES;
    private CifradorRSA cRSA;
    private FirmaDigital FD;
    private byte[] Firma;

    private SecretKey secretKey;
    private KeyGenerator kG;

    private PublicKey publicKey;
    private KeyPairGenerator kpg;
    private KeyPair kp;
    private PrivateKey privateKey;
    private LecturaFil lectura;

    private String text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boto = findViewById(R.id.boto);
        missatges = findViewById(R.id.missatges);
        entrada = findViewById(R.id.entrada);

        Intent intentxat = getIntent();
        enctipe = intentxat.getStringExtra("tipenc");
        TVtipoenc = findViewById(R.id.tVtipEnc);

        if(enctipe.equals("h")){
            TVtipoenc.setText("Hash");
            H = new Hash();

        }
        if(enctipe.equals("as")){
            try {
                TVtipoenc.setText("Asimetric");
                kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(2048);

                kp = kpg.generateKeyPair();
                publicKey = kp.getPublic();
                privateKey = kp.getPrivate();
                cRSA = new CifradorRSA();
            }catch(Exception ex){
                System.out.println("RSA no funciona: tema Keys");
                ex.printStackTrace();
            }
        }

        if(enctipe.equals("s")){
            TVtipoenc.setText("Simetric");
            try {
                kG = KeyGenerator.getInstance("AES");
                kG.init(126);
                secretKey = kG.generateKey();
                cAES = new CifradorAES();
            }catch(Exception ex){
                System.out.println("No funciona");
            }
        }

        if(enctipe.equals("fd")){
            TVtipoenc.setText("Firma Digital");
            try {
                kpg = KeyPairGenerator.getInstance("DSA");
                kpg.initialize(2048);
                kp = kpg.genKeyPair();
                privateKey = (PrivateKey) kp.getPrivate();
                publicKey = (PublicKey) kp.getPublic();
                FD = new FirmaDigital();

            }catch(Exception ex){
                System.out.println("No funciona");
            }
        }

        byte[] addr = new byte[4];
        addr[0] = (byte) 192;
        addr[1] = (byte) 168;
        addr[2] = (byte) 1;
        addr[3] = (byte) 39;
        try {
            InetAddress adreca = InetAddress.getByAddress(addr);
            socol = new Socket();
            try {
                socol.connect(new InetSocketAddress(adreca.getHostAddress(),
                        8189), 2000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            lectura = new LecturaFil(socol);
            lectura.start();


            boto.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    try {

                        OutputStream outStream = socol.getOutputStream();
                        PrintWriter sortida = new PrintWriter(outStream, true);

                        if(enctipe.equals("h")){
                            text = entrada.getText().toString();
                            String MD5HashText = H.FRHash(entrada.getText().toString(),true);
                            sortida.println(" MD5: "+MD5HashText);
                            String SHA1HashText = H.FRHash(entrada.getText().toString(),false);
                            sortida.println(" SHA: "+MD5HashText);
                            Log.i("FuncioMD5",MD5HashText);
                            Log.i("FuncioSHA1",MD5HashText);

                        }

                        if(enctipe.equals("as")){
                            Log.i("cifradoRSA",entrada.getText().toString());
                            String textocifrado = cRSA.encriptar(entrada.getText().toString(),publicKey);
                            Log.i("cifradoRSA",textocifrado);
                            sortida.println(textocifrado);
                        }

                        if(enctipe.equals("s")){
                            Log.i("cifradoAES",entrada.getText().toString());
                            String textocifrado = cAES.encriptar(entrada.getText().toString(),secretKey);
                            Log.i("cifradoAES",textocifrado);
                            sortida.println(textocifrado);
                        }
                        if(enctipe.equals("fd")){
                            Firma = FD.CreateFD(entrada.getText().toString(),privateKey);
                            sortida.println(entrada.getText().toString());
                        }

                    } catch (UnknownHostException e) {
                        System.out.println("host desconegut");
                        e.printStackTrace();
                    } catch (IOException e) {
                        System.out.println("problemes E/S");
                    }catch (Exception e) {
                        System.out.println("encriptacion problemes");
                        e.printStackTrace();
                    }
                }
            });
        } catch (UnknownHostException ex) {
            System.out.println("problemes amb el servidor");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            System.out.println("adios");
            socol.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class LecturaFil extends Thread {
        private Socket socol = null;

        public LecturaFil(Socket s) {
            socol = s;

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run() {

            try {

                InputStream inStream = socol.getInputStream();
                Scanner entrada = new Scanner(inStream);
                while (true) {
                    //Log.i("mensaje","llego aqui");
                    //entrada.nextLine()
                    String resposta = entrada.nextLine();
                    String cabezal = resposta.substring(0,9);

                    String missatge = resposta.substring(9, resposta.length());
                    Log.i("mensajerecibido",missatge);
                    if(enctipe.equals("h")){
                        try {
                            String m2 = missatge.substring(6, missatge.length());
                            if (H.verificador(text, m2))
                                missatges.setText(missatges.getText() + "\n" + cabezal + missatge);
                            else
                                missatges.setText(missatges.getText() + "\n" + cabezal + "hash modificat(no valid)");
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    if(enctipe.equals("as")){
                        try {
                            String desencriptadotexto = cRSA.desencriptar(missatge, privateKey);
                            Log.i("mensajerecibido", desencriptadotexto);
                            missatges.setText(missatges.getText() + "\n" + cabezal + desencriptadotexto);
                        } catch (Exception ex) {
                            System.out.println("no funciona");
                        }
                    }
                    if(enctipe.equals("s")) {
                        try {
                            String desencriptadotexto = cAES.desencriptar(missatge, secretKey);
                            Log.i("mensajerecibido", desencriptadotexto);
                            missatges.setText(missatges.getText() + "\n" + cabezal + desencriptadotexto);
                        } catch (Exception ex) {
                            System.out.println("no funciona");
                        }
                    }
                    if(enctipe.equals("fd")){
                        if(FD.ValidarFD(missatge.getBytes(),Firma,publicKey)) missatges.setText(missatges.getText() + "\n" + cabezal + missatge+ "(firma valida)");
                        else missatges.setText(missatges.getText() + "\n" + resposta + "(firma no valida)");
                    }

                   Log.v("DAM2","\nSERVER> " + resposta);

                }

            } catch (UnknownHostException e) {
                System.out.println("host desconegut");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("problemes E/S");
            } catch (Exception e) {

            }

        }

    }
}


