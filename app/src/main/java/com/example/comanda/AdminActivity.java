package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends  AppCompatActivity{

    private Button btnCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_categorias);

        btnCategorias = findViewById(R.id.btnAgregarCategoria);

        btnCategorias.setOnClickListener(v -> {
            Intent intent = new Intent(
              AdminActivity.this,
              AdminCategoriasActivity.class
            );
            startActivity(intent);
        });
    }
}
