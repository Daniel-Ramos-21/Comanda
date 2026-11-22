
package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comanda.Data.UsuarioRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class RegistroActivity extends AppCompatActivity {

    private TextInputLayout tilNombre;
    private TextInputLayout tilCorreo;
    private TextInputLayout tilContrasena;
    private TextInputLayout tilConfirmarContrasena;

    private TextInputEditText etNombre;
    private TextInputEditText etCorreo;
    private TextInputEditText etContrasena;
    private TextInputEditText etConfirmarContrasena;

    private MaterialCheckBox cbAceptarTerminos;
    private MaterialButton btnCrearCuenta;

    private TextView tvIrLogin;
    private TextView btnRegresarRegistro;

    private UsuarioRepository usuarioRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        usuarioRepository = new UsuarioRepository();

        initViews();
        setupListener();
    }

    // Vincular los elementos del XML
    private void initViews() {

        tilNombre = findViewById(R.id.tilRegistroNombre);
        tilCorreo = findViewById(R.id.tilRegistroCorreo);
        tilContrasena = findViewById(R.id.tilRegistroContrasena);
        tilConfirmarContrasena = findViewById(R.id.tilRegistroConfirmarContrasena);

        etNombre = findViewById(R.id.etRegistroNombre);
        etCorreo = findViewById(R.id.etRegistroCorreo);
        etContrasena = findViewById(R.id.etRegistroContrasena);
        etConfirmarContrasena = findViewById(R.id.etRegistroConfirmarContrasena);

        cbAceptarTerminos = findViewById(R.id.cbAceptarTerminos);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        tvIrLogin = findViewById(R.id.tvIrLogin);
        btnRegresarRegistro = findViewById(R.id.btnRegresarRegistro);
    }

    // Eventos de la pantalla
    private void setupListener() {

        btnCrearCuenta.setOnClickListener(v -> crearCuenta());

        tvIrLogin.setOnClickListener(v -> abrirLogin());

        btnRegresarRegistro.setOnClickListener(v -> regresarMenu());
    }

    // Registrar usuario
    private void crearCuenta() {

        limpiarErrores();

        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();

        // No recortar la contraseña, ya que los espacios podrían formar parte de ella
        String contrasena = etContrasena.getText().toString();
        String confirmarContrasena = etConfirmarContrasena.getText().toString();

        // Validar nombre
        if (nombre.isEmpty()) {
            tilNombre.setError("Ingresa tu nombre");
            etNombre.requestFocus();
            return;
        }

        if (nombre.length() < 2) {
            tilNombre.setError("El nombre debe tener al menos 2 caracteres");
            etNombre.requestFocus();
            return;
        }

        // Validar correo
        if (correo.isEmpty()) {
            tilCorreo.setError("Ingresa tu correo electrónico");
            etCorreo.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.setError("Ingresa un correo electrónico válido");
            etCorreo.requestFocus();
            return;
        }

        // Validar contraseña
        if (contrasena.isEmpty()) {
            tilContrasena.setError("Ingresa una contraseña");
            etContrasena.requestFocus();
            return;
        }

        if (contrasena.length() < 8) {
            tilContrasena.setError(
                    "La contraseña debe tener al menos 8 caracteres"
            );
            etContrasena.requestFocus();
            return;
        }

        // Validar confirmación
        if (confirmarContrasena.isEmpty()) {
            tilConfirmarContrasena.setError("Confirma tu contraseña");
            etConfirmarContrasena.requestFocus();
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            tilConfirmarContrasena.setError("Las contraseñas no coinciden");
            etConfirmarContrasena.requestFocus();
            return;
        }

        // Validar términos
        if (!cbAceptarTerminos.isChecked()) {
            Toast.makeText(
                    this,
                    "Debes aceptar los términos de servicio y privacidad",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        // Evitar múltiples solicitudes
        btnCrearCuenta.setEnabled(false);
        btnCrearCuenta.setText("Creando cuenta...");

        // Crear cuenta en Firebase y guardar perfil en Firestore
        usuarioRepository.registrarUsuario(
                nombre,
                correo,
                contrasena,
                "",
                new UsuarioRepository.AuthCallback() {

                    @Override
                    public void onSuccess(FirebaseUser user) {

                        Toast.makeText(
                                RegistroActivity.this,
                                "Cuenta creada correctamente",
                                Toast.LENGTH_SHORT
                        ).show();

                        // Ir al menú principal
                        Intent intent = new Intent(
                                RegistroActivity.this,
                                MenuActivity.class
                        );

                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );

                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String mensaje) {

                        btnCrearCuenta.setEnabled(true);
                        btnCrearCuenta.setText("Crear mi cuenta");

                        Toast.makeText(
                                RegistroActivity.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // Limpiar mensajes de validación
    private void limpiarErrores() {

        tilNombre.setError(null);
        tilCorreo.setError(null);
        tilContrasena.setError(null);
        tilConfirmarContrasena.setError(null);
    }

    // Ir al login
    private void abrirLogin() {

        Intent intent = new Intent(
                RegistroActivity.this,
                LoginActivity.class
        );

        startActivity(intent);
        finish();
    }

    // Regresar al menú
    private void regresarMenu() {

        Intent intent = new Intent(
                RegistroActivity.this,
                MenuActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
        finish();
    }
}
