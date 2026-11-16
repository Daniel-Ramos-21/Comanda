package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class RecibosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_recibos);

        setupWindowInsets();
        setupOrderCards();
        setupBottomNav();
    }

    private void setupWindowInsets() {
        View root = findViewById(R.id.recibosRoot);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void setupOrderCards() {
        findViewById(R.id.cardOrden2401).setOnClickListener(v ->
            mostrarDetalleRecibo("Orden #MN-2401", "2x Pupusas Mixtas ($9.00)\n1x Horchata Artesanal ($2.25)\n1x Quesadilla Tradicional ($1.75)\n\nSubtotal: $13.00\nDescuento promo: -$1.50\nEnvío: $1.00\nTotal pagado: $12.50\n\nMétodo: Visa •••• 4242\nEntregado hoy 2:30 PM")
        );

        findViewById(R.id.cardOrden2398).setOnClickListener(v ->
            mostrarDetalleRecibo("Orden #MN-2398", "1x Tacos de Birria ($6.00)\n1x Bebida Fría ($1.25)\n\nTotal pagado: $7.25\nMétodo: Visa •••• 4242\nEntregado 12 Nov, 8:15 PM")
        );

        findViewById(R.id.cardOrden2350).setOnClickListener(v ->
            mostrarDetalleRecibo("Orden #MN-2350", "4x Pupusas Variadas ($12.25)\n2x Horchata ($4.50)\n\nTotal pagado: $16.75\nMétodo: Visa •••• 4242\nEntregado 5 Nov, 1:10 PM")
        );
    }

    private void mostrarDetalleRecibo(String orden, String desglose) {
        // Detalle de recibo según Figma Screen 14_Recibos_Detalle
        new MaterialAlertDialogBuilder(this)
                .setTitle(orden)
                .setMessage(desglose)
                .setPositiveButton("Volver a pedir esta orden", (dialog, which) -> {
                    Intent intent = new Intent(this, MenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cerrar", (dialog, which) -> dialog.dismiss())
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

        findViewById(R.id.btnNavCuenta).setOnClickListener(v -> {
            startActivity(new Intent(this, CuentaActivity.class));
            finish();
        });
    }
}
