package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.squareup.picasso.Picasso;

import java.util.*;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageView;
    private Spinner spinner;
    private Button bnext;
    private String url = "https://i.ibb.co/phFkHgX/fotoapp.png";
    private String enctipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        setViews();
        cargaImagendesdeURL();

        ArrayList<String> palabras = new ArrayList<String>();
        palabras.add("Hash");
        palabras.add("Asimetric");
        palabras.add("Simetric");
        palabras.add("Firma Digital");

        ArrayAdapter<String> myAdpt = new ArrayAdapter<String>(PrincipalActivity.this,
                android.R.layout.simple_list_item_1, palabras);
        myAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdpt);

        bnext.setOnClickListener(this);
    }

    private void setViews(){
        imageView = (ImageView)findViewById(R.id.iVlogo);
        spinner = (Spinner)findViewById(R.id.spinner);
        bnext = (Button)findViewById(R.id.btnnext);
    }

    private void cargaImagendesdeURL(){
        Picasso.get()
                .load(url)
                .into(imageView);
    }

    public void BnextOnClick(){
        if (spinner.getSelectedItem().toString()=="Hash") enctipe = "h";
        if (spinner.getSelectedItem().toString()=="Asimetric") enctipe = "as";
        if (spinner.getSelectedItem().toString()=="Simetric") enctipe = "s";
        if (spinner.getSelectedItem().toString()=="Firma Digital") enctipe = "fd";

        Intent intentChat = new Intent(this,ClientXatActivity.class);
        intentChat.putExtra("tipenc",enctipe);

        if (intentChat.resolveActivity(getPackageManager()) != null) {
            startActivity(intentChat);
        }
    }

    @Override
    public void onClick(View v) {
        BnextOnClick();
    }
}
