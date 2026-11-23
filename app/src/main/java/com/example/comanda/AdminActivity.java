package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends  AppCompatActivity{

    private Button btnCategorias;
    private Button btnProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnCategorias = findViewById(R.id.btnCategorias);
        btnProductos = findViewById(R.id.btnProductos);

        btnCategorias.setOnClickListener(v -> {
            Intent intent = new Intent(
              AdminActivity.this,
              AdminCategoriasActivity.class
            );
            startActivity(intent);
        });

        btnProductos.setOnClickListener(v -> {
            Intent intent = new Intent(
                    AdminActivity.this,
                    AdminProductosActivity.class
            );
            startActivity(intent);
        });
    }
}
