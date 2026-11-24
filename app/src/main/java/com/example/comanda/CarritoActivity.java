package com.example.comanda;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comanda.Data.CarritoManager;
import com.example.comanda.adapters.CarritoAdapter;
import com.example.comanda.models.ItemCarrito;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarritoActivity extends AppCompatActivity {

    private TextView btnAtras;
    private RecyclerView rvCarrito;
    private TextView tvCantidadProductos;
    private TextView tvSubtotal;
    private TextView tvTotal;
    private View tvCarritoVacio;
    private MaterialButton btnContinuar;

    private CarritoAdapter carritoAdapter;
    private CarritoManager carritoManager;

    private final List<ItemCarrito> listaItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        carritoManager = CarritoManager.getInstance();

        initViews();
        configurarRecyclerView();
        configurarListeners();
    }

    private void initViews() {
        btnAtras = findViewById(R.id.btnCarritoAtras);
        rvCarrito = findViewById(R.id.rvCarrito);
        tvCantidadProductos = findViewById(R.id.tvCantidadProductos);
        tvSubtotal = findViewById(R.id.tvSubtotalCarrito);
        tvTotal = findViewById(R.id.tvTotalCarrito);
        tvCarritoVacio = findViewById(R.id.tvCarritoVacio);
        btnContinuar = findViewById(R.id.btnContinuarCompra);
    }

    private void configurarRecyclerView() {
        carritoAdapter = new CarritoAdapter(
                listaItems,
                new CarritoAdapter.OnCarritoChangeListener() {

                    @Override
                    public void onCantidadCambiada(
                            int posicion,
                            int nuevaCantidad
                    ) {
                        carritoManager.actualizarCantidad(
                                posicion,
                                nuevaCantidad
                        );

                        cargarCarrito();
                    }

                    @Override
                    public void onEliminar(int posicion) {
                        carritoManager.eliminarItem(posicion);
                        cargarCarrito();

                        Toast.makeText(
                                CarritoActivity.this,
                                "Producto eliminado del carrito",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        rvCarrito.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvCarrito.setAdapter(carritoAdapter);
    }

    private void configurarListeners() {
        btnAtras.setOnClickListener(v -> finish());

        btnContinuar.setOnClickListener(v -> {

            if (carritoManager.estaVacio()) {
                Toast.makeText(
                        this,
                        "Tu carrito está vacío",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            Intent intent = new Intent(
                    CarritoActivity.this,
                    ConfirmarPedidoActivity.class
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCarrito();
    }

    private void cargarCarrito() {
        listaItems.clear();
        listaItems.addAll(carritoManager.obtenerItems());

        carritoAdapter.notifyDataSetChanged();

        actualizarResumen();
        actualizarEstadoCarrito();
    }

    private void actualizarResumen() {
        int cantidad = carritoManager.obtenerCantidadTotal();
        long totalCentavos = carritoManager.obtenerTotalCentavos();

        tvCantidadProductos.setText(
                String.format(
                        Locale.US,
                        "%d producto(s)",
                        cantidad
                )
        );

        String totalFormateado = String.format(
                Locale.US,
                "$%.2f",
                totalCentavos / 100.0
        );

        tvSubtotal.setText(totalFormateado);
        tvTotal.setText(totalFormateado);
    }

    private void actualizarEstadoCarrito() {
        boolean vacio = listaItems.isEmpty();

        if (vacio) {
            rvCarrito.setVisibility(View.GONE);
            tvCarritoVacio.setVisibility(View.VISIBLE);
            btnContinuar.setEnabled(false);
            btnContinuar.setAlpha(0.5f);
        } else {
            rvCarrito.setVisibility(View.VISIBLE);
            tvCarritoVacio.setVisibility(View.GONE);
            btnContinuar.setEnabled(true);
            btnContinuar.setAlpha(1f);
        }
    }
}