
package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comanda.Data.CarritoManager;
import com.example.comanda.Data.PedidosRepository;
import com.example.comanda.models.DetallePedido;
import com.example.comanda.models.ItemCarrito;
import com.example.comanda.models.Pedido;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConfirmarPedidoActivity extends AppCompatActivity {

    private TextView btnAtras;
    private EditText etNombreCliente;
    private RadioGroup rgTipoEntrega;
    private EditText etDireccion;
    private EditText etNotasPedido;
    private TextView tvTotalConfirmacion;
    private MaterialButton btnConfirmarPedido;

    private CarritoManager carritoManager;
    private PedidosRepository pedidosRepository;

    private boolean pedidoEnProceso = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_pedido);

        carritoManager = CarritoManager.getInstance();
        pedidosRepository = new PedidosRepository();

        inicializarViews();
        configurarListeners();
        actualizarTotal();
    }

    private void inicializarViews() {
        btnAtras = findViewById(R.id.btnConfirmacionAtras);
        etNombreCliente = findViewById(R.id.etNombreCliente);
        rgTipoEntrega = findViewById(R.id.rgTipoEntrega);
        etDireccion = findViewById(R.id.etDireccionEntrega);
        etNotasPedido = findViewById(R.id.etNotasPedido);
        tvTotalConfirmacion = findViewById(R.id.tvTotalConfirmacion);
        btnConfirmarPedido = findViewById(R.id.btnConfirmarPedido);
    }

    private void configurarListeners() {
        btnAtras.setOnClickListener(v -> finish());

        rgTipoEntrega.setOnCheckedChangeListener(
                (group, checkedId) -> actualizarDireccion()
        );

        btnConfirmarPedido.setOnClickListener(v -> confirmarPedido());
    }

    private void actualizarDireccion() {
        boolean esDomicilio =
                rgTipoEntrega.getCheckedRadioButtonId()
                        == R.id.rbDomicilio;

        etDireccion.setVisibility(
                esDomicilio ? View.VISIBLE : View.GONE
        );

        if (!esDomicilio) {
            etDireccion.setText("");
        }
    }

    private void actualizarTotal() {
        long total = carritoManager.obtenerTotalCentavos();

        tvTotalConfirmacion.setText(
                String.format(
                        Locale.US,
                        "$%.2f",
                        total / 100.0
                )
        );
    }

    private void confirmarPedido() {

        if (pedidoEnProceso) {
            return;
        }

        if (carritoManager.estaVacio()) {
            Toast.makeText(
                    this,
                    "Tu carrito está vacío.",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        String nombre = etNombreCliente.getText()
                .toString()
                .trim();

        if (TextUtils.isEmpty(nombre)) {
            etNombreCliente.setError("Ingresa tu nombre");
            etNombreCliente.requestFocus();
            return;
        }

        boolean esDomicilio =
                rgTipoEntrega.getCheckedRadioButtonId()
                        == R.id.rbDomicilio;

        String tipoEntrega = esDomicilio
                ? "Domicilio"
                : "Recoger";

        String direccion = "";

        if (esDomicilio) {
            direccion = etDireccion.getText()
                    .toString()
                    .trim();

            if (TextUtils.isEmpty(direccion)) {
                etDireccion.setError(
                        "Ingresa la dirección de entrega"
                );
                etDireccion.requestFocus();
                return;
            }
        }

        List<ItemCarrito> items = carritoManager.obtenerItems();
        List<DetallePedido> detalles = new ArrayList<>();

        long totalCentavos = 0;

        for (ItemCarrito item : items) {

            DetallePedido detalle = new DetallePedido(
                    item.getProductoId(),
                    item.getNombre(),
                    item.getImagenUrl(),
                    item.getCantidad(),
                    item.getPrecioUnitarioCentavos(),
                    item.getNotas()
            );

            detalles.add(detalle);
            totalCentavos += detalle.getSubtotalCentavos();
        }

        if (totalCentavos <= 0
                || totalCentavos > Integer.MAX_VALUE) {
            Toast.makeText(
                    this,
                    "El total del pedido no es válido.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        Pedido pedido = new Pedido();

        pedido.setUsuarioId("");
        pedido.setCliente(nombre);
        pedido.setMesa("");
        pedido.setTipoEntrega(tipoEntrega);
        pedido.setDireccionEntrega(direccion);
        pedido.setNotas(
                etNotasPedido.getText().toString().trim()
        );
        pedido.setDetalles(detalles);
        pedido.setTotalCentavos((int) totalCentavos);
        pedido.setEstado("Pendiente");
        pedido.setMetodoPago("Efectivo");
        pedido.setEstadoPago("Pendiente");
        pedido.setFechaCreacion(new Date());

        guardarPedido(pedido);
    }

    private void guardarPedido(Pedido pedido) {

        pedidoEnProceso = true;
        btnConfirmarPedido.setEnabled(false);
        btnConfirmarPedido.setText("Guardando pedido...");

        pedidosRepository.registrarPedido(
                pedido,
                new PedidosRepository.OnPedidoRegistradoCallback() {

                    @Override
                    public void onSuccess(String pedidoId) {

                        carritoManager.limpiarCarrito();

                        Toast.makeText(
                                ConfirmarPedidoActivity.this,
                                "Pedido confirmado correctamente",
                                Toast.LENGTH_LONG
                        ).show();

                        Intent intent = new Intent(
                                ConfirmarPedidoActivity.this,
                                RecibosActivity.class
                        );

                        intent.putExtra("pedidoId", pedidoId);
                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
                        );

                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {

                        pedidoEnProceso = false;
                        btnConfirmarPedido.setEnabled(true);
                        btnConfirmarPedido.setText(
                                "Confirmar pedido"
                        );

                        Toast.makeText(
                                ConfirmarPedidoActivity.this,
                                "No se pudo guardar el pedido. "
                                        + "Tu carrito se mantiene. "
                                        + (e != null ? e.getMessage() : ""),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}
