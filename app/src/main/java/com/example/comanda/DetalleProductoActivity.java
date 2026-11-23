
package com.example.comanda;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.Dialog;
import android.view.Gravity;
import android.view.Window;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
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
    private Producto productoActual;

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
        android.view.View root = findViewById(R.id.detalleRoot);

        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                );

                v.setPadding(
                        systemBars.left,
                        0,
                        systemBars.right,
                        systemBars.bottom
                );

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
        if (getIntent() == null ||
                !getIntent().hasExtra(EXTRA_PRODUCTO)) {
            Toast.makeText(
                    this,
                    "No se recibió información del producto",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        try {
            if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.TIRAMISU) {
                productoActual = getIntent().getSerializableExtra(
                        EXTRA_PRODUCTO,
                        Producto.class
                );
            } else {
                productoActual = (Producto) getIntent()
                        .getSerializableExtra(EXTRA_PRODUCTO);
            }
        } catch (Exception e) {
            productoActual = null;
        }

        if (productoActual == null) {
            Toast.makeText(
                    this,
                    "No se pudo cargar el producto",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        tvNombre.setText(productoActual.getNombre());

        tvDescripcion.setText(
                productoActual.getDescripcion() != null
                        ? productoActual.getDescripcion()
                        : "Sin descripción disponible."
        );

        precioUnitario = productoActual.getPrecioCentavos() > 0
                ? productoActual.getPrecioCentavos() / 100.0
                : 4.50;

        tvPrecio.setText(
                String.format(Locale.US, "$%.2f", precioUnitario)
        );

        cargarImagenProducto(productoActual.getImagenUrl());
    }

    private void cargarImagenProducto(String imagenUrl) {
        ivHero.setOnClickListener(v -> mostrarImagenAmpliada());

        if (imagenUrl == null ||
                imagenUrl.trim().isEmpty() ||
                !imagenUrl.startsWith("http")) {

            ivHero.setImageResource(R.drawable.ic_food_pupusas);
            return;
        }

        Glide.with(this)
                .load(imagenUrl)
                .placeholder(R.drawable.ic_food_pupusas)
                .error(R.drawable.ic_food_pupusas)
                .centerCrop()
                .into(ivHero);
    }

    private void setupListeners() {
        btnAtras.setOnClickListener(v -> finish());
        btnFavorito.setOnClickListener(v -> {
            esFavorito = !esFavorito;

            Toast.makeText(
                    this,
                    esFavorito
                            ? "Añadido a favoritos"
                            : "Eliminado de favoritos",
                    Toast.LENGTH_SHORT
            ).show();
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

            String mensaje = String.format(
                    Locale.US,
                    "Agregado %d platillo(s) al pedido ($%.2f)",
                    cantidad,
                    total
            );

            Toast.makeText(
                    this,
                    mensaje,
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        });
    }

    private void actualizarBotonTotal() {
        double total = cantidad * precioUnitario;

        btnAgregar.setText(
                String.format(
                        Locale.US,
                        "Agregar al pedido • $%.2f",
                        total
                )
        );
    }

    private void mostrarImagenAmpliada() {
        if (productoActual == null ||
                productoActual.getImagenUrl() == null ||
                !productoActual.getImagenUrl().startsWith("http")) {
            return;
        }

        Dialog dialog = new Dialog(this);
        ImageView imagen = new ImageView(this);

        imagen.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imagen.setBackgroundColor(android.graphics.Color.BLACK);

        Glide.with(this)
                .load(productoActual.getImagenUrl())
                .into(imagen);

        imagen.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(imagen);
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.black);
            window.setLayout(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            window.setGravity(Gravity.CENTER);
        }
    }
}
