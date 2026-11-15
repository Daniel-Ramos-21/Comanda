package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CuentaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_cuenta);

        setupWindowInsets();
        setupOptions();
        setupBottomNav();
    }

    private void setupWindowInsets() {
        View root = findViewById(R.id.cuentaRoot);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, 0, systemBars.right, 0);
                return insets;
            });
        }
    }

    private void setupOptions() {
        findViewById(R.id.cardOpcionDatos).setOnClickListener(v ->
            Toast.makeText(this, "Datos personales: Daniel Ramos (danielramos20451@gmail.com)", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.cardOpcionDirecciones).setOnClickListener(v ->
            Toast.makeText(this, "Direcciones: 2 ubicaciones guardadas", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.cardOpcionPagos).setOnClickListener(v ->
            Toast.makeText(this, "Método de pago: Tarjeta Visa terminada en •••• 4242", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.cardOpcionNotificaciones).setOnClickListener(v ->
            Toast.makeText(this, "Notificaciones activadas", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.cardOpcionAyuda).setOnClickListener(v ->
            Toast.makeText(this, "Munchly Soporte: contacto@munchly.app", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.cardOpcionLogout).setOnClickListener(v -> mostrarModalCerrarSesion());
    }

    private void mostrarModalCerrarSesion() {
        // Modal de confirmación según Figma Screen 12_Cuenta_Modal_Logout
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.account_logout_confirm_title)
                .setMessage(R.string.account_logout_confirm_desc)
                .setNegativeButton(R.string.account_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.account_logout, (dialog, which) -> {
                    Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
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
    }
}
