package com.example.comanda;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comanda.models.Producto;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class DetalleProductoActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCTO = "extra_producto";

    private ImageView btnAtras;
    private ImageView btnFavorito;
    private ImageView ivHero;
    private TextView tvNombre;
    private TextView tvPrecio;
    private TextView tvDescripcion;
    private TextView tvCantidad;
    private TextView btnMenos;
    private TextView btnMas;
    private EditText etNotas;
    private MaterialButton btnAgregar;

    private int cantidad = 1;
    private double precioUnitario = 4.50;
    private boolean esFavorito = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_detalle_producto);

        setupWindowInsets();
        initViews();
        recuperarDatosProducto();
        setupListeners();
        actualizarBotonTotal();
    }

    private void setupWindowInsets() {
        View root = findViewById(R.id.detalleRoot);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void initViews() {
        btnAtras = findViewById(R.id.btnDetalleAtras);
        btnFavorito = findViewById(R.id.btnDetalleFavorito);
        ivHero = findViewById(R.id.ivDetalleHero);
        tvNombre = findViewById(R.id.tvDetalleNombre);
        tvPrecio = findViewById(R.id.tvDetallePrecio);
        tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        tvCantidad = findViewById(R.id.tvCantidad);
        btnMenos = findViewById(R.id.btnMenosCantidad);
        btnMas = findViewById(R.id.btnMasCantidad);
        etNotas = findViewById(R.id.etDetalleNotas);
        btnAgregar = findViewById(R.id.btnAgregarAlPedido);
    }

    private void recuperarDatosProducto() {
        if (getIntent() != null && getIntent().hasExtra(EXTRA_PRODUCTO)) {
            Producto p = (Producto) getIntent().getSerializableExtra(EXTRA_PRODUCTO);
            if (p != null) {
                if (p.getNombre() != null) tvNombre.setText(p.getNombre());
                if (p.getDescripcion() != null) tvDescripcion.setText(p.getDescripcion());
                precioUnitario = p.getPrecioCentavos() > 0 ? p.getPrecioCentavos() / 100.0 : 4.50;
                tvPrecio.setText(String.format(Locale.US, "$%.2f", precioUnitario));
            }
        }
    }

    private void setupListeners() {
        btnAtras.setOnClickListener(v -> finish());

        btnFavorito.setOnClickListener(v -> {
            esFavorito = !esFavorito;
            Toast.makeText(this, esFavorito ? "Añadido a favoritos" : "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
        });

        btnMenos.setOnClickListener(v -> {
            if (cantidad > 1) {
                cantidad--;
                tvCantidad.setText(String.valueOf(cantidad));
                actualizarBotonTotal();
            }
        });

        btnMas.setOnClickListener(v -> {
            cantidad++;
            tvCantidad.setText(String.valueOf(cantidad));
            actualizarBotonTotal();
        });

        btnAgregar.setOnClickListener(v -> {
            double total = cantidad * precioUnitario;
            String mensaje = String.format(Locale.US, "Agregado %d platillo(s) al pedido ($%.2f)", cantidad, total);
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void actualizarBotonTotal() {
        double total = cantidad * precioUnitario;
        btnAgregar.setText(String.format(Locale.US, "Agregar al pedido • $%.2f", total));
    }
}
