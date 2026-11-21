package com.example.comanda;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comanda.Data.UsuarioRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends  AppCompatActivity {

    private TextInputLayout tilCorreo, tilContrasena;
    private TextInputEditText etCorreo, etContrasena;
    private MaterialButton btnIniciarSesion;

    private TextView tvIrRegistro;
    private TextView btnRegresar;

    private UsuarioRepository usuarioRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuarioRepository = new UsuarioRepository();

        initViews();
        setupListener();
    }

    private void initViews(){
        tilCorreo = findViewById(R.id.tilLoginCorreo);
        tilContrasena = findViewById(R.id.tilLoginContrasena);

        etCorreo = findViewById(R.id.etLoginCorreo);
        etContrasena = findViewById(R.id.etLoginContrasena);

        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        tvIrRegistro = findViewById(R.id.tvIrRegistro);

        btnRegresar = findViewById(R.id.btnRegresar);

    }

    private void setupListener(){
        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
        tvIrRegistro.setOnClickListener(v -> abrirRegistrar());
        btnRegresar.setOnClickListener(v -> salir());
    }

    private void iniciarSesion(){

        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        tilCorreo.setError(null);
        tilContrasena.setError(null);

        if (correo.isEmpty()){
            tilCorreo.setError("Ingresa tu contraseña");
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            tilCorreo.setError("Ingresa un correo válido");
            return;
        }

        if (contrasena.isEmpty()){
            tilContrasena.setError("Ingresa tu contraseña");
            return;
        }

        btnIniciarSesion.setEnabled(false);

        usuarioRepository.iniciarSesion(correo, contrasena,
                new UsuarioRepository.AuthCallback(){
                    @Override
                    public void onSuccess(FirebaseUser user){
                        Toast.makeText(LoginActivity.this,
                                "Bienvenido",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(
                                LoginActivity.this,
                                MenuActivity.class
                        );

                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String mensaje){
                        btnIniciarSesion.setEnabled(true);
                        Toast.makeText(LoginActivity.this,
                                mensaje,
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private  void abrirRegistrar(){
        Intent intent = new Intent(
                LoginActivity.this,
                RegistroActivity.class
        );
        startActivity(intent);
        finish();
    }

    private void salir(){
        Intent intent = new Intent(
                LoginActivity.this,
                MenuActivity.class
        );
        startActivity(intent);
        finish();
    }
}
