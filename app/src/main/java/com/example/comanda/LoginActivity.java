
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

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilCorreo, tilContrasena;
    private TextInputEditText etCorreo, etContrasena;
    private MaterialButton btnIniciarSesion;

    private TextView tvIrRegistro;
    private TextView btnRegresar;

    private UsuarioRepository usuarioRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuarioRepository = new UsuarioRepository();

        initViews();
        setupListener();
    }

    // Vincular elementos del XML
    private void initViews() {

        tilCorreo = findViewById(R.id.tilLoginCorreo);
        tilContrasena = findViewById(R.id.tilLoginContrasena);

        etCorreo = findViewById(R.id.etLoginCorreo);
        etContrasena = findViewById(R.id.etLoginContrasena);

        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        tvIrRegistro = findViewById(R.id.tvIrRegistro);
        btnRegresar = findViewById(R.id.btnRegresar);
    }

    // Eventos de los botones
    private void setupListener() {

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());

        tvIrRegistro.setOnClickListener(v -> abrirRegistrar());

        btnRegresar.setOnClickListener(v -> salir());
    }

    // Validar e iniciar sesión
    private void iniciarSesion() {

        String correo = etCorreo.getText().toString().trim();

        // No aplicar trim a la contraseña
        String contrasena = etContrasena.getText().toString();

        limpiarErrores();

        // Validar correo vacío
        if (correo.isEmpty()) {
            tilCorreo.setError("Ingresa tu correo electrónico");
            etCorreo.requestFocus();
            return;
        }

        // Validar formato del correo
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.setError("Ingresa un correo válido");
            etCorreo.requestFocus();
            return;
        }

        // Validar contraseña vacía
        if (contrasena.isEmpty()) {
            tilContrasena.setError("Ingresa tu contraseña");
            etContrasena.requestFocus();
            return;
        }

        // Evitar múltiples solicitudes
        btnIniciarSesion.setEnabled(false);
        btnIniciarSesion.setText("Verificando...");

        // Conectar con Firebase Authentication
        usuarioRepository.iniciarSesion(
                correo,
                contrasena,
                new UsuarioRepository.AuthCallback() {

                    @Override
                    public void onSuccess(FirebaseUser user) {

                        Toast.makeText(
                                LoginActivity.this,
                                "Bienvenido",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                LoginActivity.this,
                                MenuActivity.class
                        );

                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String mensaje) {

                        btnIniciarSesion.setEnabled(true);
                        btnIniciarSesion.setText("Iniciar sesión");

                        limpiarErrores();

                        Toast.makeText(
                                LoginActivity.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // Limpiar mensajes de validación
    private void limpiarErrores() {

        tilCorreo.setError(null);
        tilContrasena.setError(null);
    }

    // Abrir pantalla de registro
    private void abrirRegistrar() {

        Intent intent = new Intent(
                LoginActivity.this,
                RegistroActivity.class
        );

        startActivity(intent);
        finish();
    }

    // Regresar al menú
    private void salir() {

        Intent intent = new Intent(
                LoginActivity.this,
                MenuActivity.class
        );

        startActivity(intent);
        finish();
    }
}
