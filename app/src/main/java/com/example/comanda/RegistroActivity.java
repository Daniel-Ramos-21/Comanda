package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comanda.Data.UsuarioRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
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
    private MaterialCardView cardRegresar;

    private UsuarioRepository usuarioRepository;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        usuarioRepository = new UsuarioRepository();

        initViews();
        setupListener();
    }

    private  void initViews(){
        tilNombre = findViewById(R.id.tilRegistroNombre);
        tilCorreo = findViewById(R.id.tilRegistroCorreo);
        tilContrasena = findViewById(R.id.tilLoginContrasena);
        tilConfirmarContrasena = findViewById(R.id.tilRegistroConfirmarContrasena);

        etNombre = findViewById(R.id.etRegistroNombre);
        etCorreo = findViewById(R.id.etRegistroCorreo);
        etContrasena = findViewById(R.id.etRegistroContrasena);
        etConfirmarContrasena = findViewById(R.id.etRegistroConfirmarContrasena);

        cbAceptarTerminos = findViewById(R.id.cbAceptarTerminos);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        tvIrLogin = findViewById(R.id.tvIrLogin);
    }

    private void setupListener(){
        btnCrearCuenta.setOnClickListener(v -> crearCuenta());
        tvIrLogin.setOnClickListener(v -> abrirLogin() );
    }

    private void crearCuenta(){
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String confirmarContrasena = etConfirmarContrasena.getText().toString().trim();


        if (correo.isEmpty()){
            tilCorreo.setError("Ingresa tu correo electronico");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            tilCorreo.setError("Ingresa un correo electrónico válido");
            return;
        }

        if (contrasena.isEmpty()){
            tilContrasena.setError("Ingresa una contraseña");
            return;
        }

        if (contrasena.length() < 8){
            tilContrasena.setError(
                    "La contraseña debe tener al menos 8 caracteres"
            );
            return;
        }

        if (!contrasena.equals(confirmarContrasena)){
            tilContrasena.setError(
                    "Las contraseñas no coinciden"
            );
            return;
        }

        if (!cbAceptarTerminos.isChecked()){
            Toast.makeText(
                    this,
                    "Debes aceptar los términos de servicio y privicidad",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        btnCrearCuenta.setEnabled(false);

        usuarioRepository.registrarUsuario(
                nombre,
                correo,
                contrasena,
                "",
                new UsuarioRepository.AuthCallback(){
                    @Override
                    public  void onSuccess(FirebaseUser user){
                        Toast.makeText(
                                RegistroActivity.this,
                                "!Tu cuenta fue creada correctamente¡",
                                Toast.LENGTH_LONG
                        ).show();
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
                    public void onError(String mensaje)
                    {
                        btnCrearCuenta.setEnabled(true);

                        Toast.makeText(
                                RegistroActivity.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void limpiarErrores(){
        tilNombre.setError(null);
        tilCorreo.setError(null);
        tilContrasena.setError(null);
        tilConfirmarContrasena.setError(null);

    }

    private void abrirLogin(){
        Intent intent = new Intent(
                RegistroActivity.this,
                LoginActivity.class
        );
        startActivity(intent);
        finish();
    }
}
