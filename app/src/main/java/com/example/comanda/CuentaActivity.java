package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comanda.Data.UsuarioRepository;
import com.example.comanda.models.Usuario;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CuentaActivity extends AppCompatActivity {

    private UsuarioRepository usuarioRepository;
    private TextView tvCuentaNombre;
    private TextView tvCuentaCorreo;
    private TextView tvAvataarInitials;
    private View btnNavCarrito;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_cuenta);

        usuarioRepository = new UsuarioRepository();

        initViews();
        setupWindowInsets();
        setupOptions();
        setupBottomNav();
        cargarPerfil();
    }

    private void initViews(){
        tvCuentaNombre = findViewById(R.id.tvCuentaNombre);
        tvCuentaCorreo = findViewById(R.id.tvCuentaCorreo);
        tvAvataarInitials = findViewById(R.id.tvAvatarInitials);
        btnNavCarrito = findViewById(R.id.btnNavCarrito);
    }

    private void setupWindowInsets() {
        View root = findViewById(R.id.cuentaRoot);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void cargarPerfil(){
        if (!usuarioRepository.haySesionActiva()){
            tvCuentaNombre.setText("Invitado");
            tvCuentaCorreo.setText("Inicia sesión para ver tu perfil");
            tvAvataarInitials.setText("M");

            return;
        }
        usuarioRepository.obtenerPerfil(
                new UsuarioRepository.PerfilCallback() {
                    @Override
                    public void onSuccess(Usuario usuario) {
                        tvCuentaNombre.setText(usuario.getNombre());
                        tvCuentaCorreo.setText(usuario.getCorreo());

                        tvAvataarInitials.setText(
                                obtenerIniciales(usuario.getNombre())
                        );

                    }

                    @Override
                    public void onError(String mensaje) {
                        Toast.makeText(
                                CuentaActivity.this,
                                "No se pudo cargar tu perfil" + mensaje,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private String obtenerIniciales(String nombre){
        if (nombre == null || nombre.trim().isEmpty()){
            return "M";
        }

        String[] palabras = nombre.trim().split("\\s+");


        if (palabras.length == 1){
            return palabras[0].substring(0,1).toUpperCase();
        }

        String primera = palabras[0].substring(0,1);
        String segunda = palabras[1].substring(0,1);

        return (primera + segunda).toUpperCase();
    }


    private void setupOptions() {
        findViewById(R.id.cardOpcionLogout).setOnClickListener(v -> mostrarModalCerrarSesion());
    }

    private void mostrarModalCerrarSesion() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.account_logout_confirm_title)
                .setMessage(R.string.account_logout_confirm_desc)
                .setNegativeButton(R.string.account_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.account_logout, (dialog, which) -> {
                   usuarioRepository.cerrarSesion();

                   Intent intent = new Intent(this, MenuActivity.class);
                   intent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                   );
                   startActivity(intent);
                })
                .show();
    }

    private void setupBottomNav() {
        findViewById(R.id.btnNavInicio).setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btnNavBuscar).setOnClickListener(v -> {
            startActivity(new Intent(this, BusquedaActivity.class));
            finish();
        });

        findViewById(R.id.btnNavRecibos).setOnClickListener(v -> {
            startActivity(new Intent(this, RecibosActivity.class));
            finish();
        });
        btnNavCarrito.setOnClickListener(v-> {
            Intent intent = new Intent(
                    CuentaActivity.this,
                    CarritoActivity.class
            );
            startActivity(intent);
        });
    }


}
