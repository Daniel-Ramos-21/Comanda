
package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comanda.Data.PedidosRepository;
import com.example.comanda.models.DetallePedido;
import com.example.comanda.models.Pedido;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecibosActivity extends AppCompatActivity {

    private LinearLayout layoutListaRecibos;
    private ProgressBar progressRecibos;
    private TextView tvEstadoRecibos;

    private PedidosRepository pedidosRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_recibos);

        pedidosRepository = new PedidosRepository();

        inicializarViews();
        setupWindowInsets();
        setupBottomNav();
        cargarRecibos();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        cargarRecibos();
    }

    private void inicializarViews() {
        layoutListaRecibos = findViewById(R.id.layoutListaRecibos);
        progressRecibos = findViewById(R.id.progressRecibos);
        tvEstadoRecibos = findViewById(R.id.tvEstadoRecibos);
    }

    private void cargarRecibos() {

        progressRecibos.setVisibility(View.VISIBLE);
        tvEstadoRecibos.setVisibility(View.GONE);
        layoutListaRecibos.removeAllViews();

        pedidosRepository.obtenerPedidos(
                new PedidosRepository.OnPedidosCallback() {

                    @Override
                    public void onSuccess(List<Pedido> pedidos) {

                        progressRecibos.setVisibility(View.GONE);
                        layoutListaRecibos.removeAllViews();

                        if (pedidos == null || pedidos.isEmpty()) {
                            mostrarEstado(
                                    "Todavía no tienes pedidos.\n"
                                            + "Cuando realices uno, aparecerá aquí."
                            );
                            return;
                        }

                        tvEstadoRecibos.setVisibility(View.GONE);

                        for (Pedido pedido : pedidos) {
                            agregarTarjetaRecibo(pedido);
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                        progressRecibos.setVisibility(View.GONE);

                        mostrarEstado(
                                "No pudimos cargar tus recibos.\n"
                                        + "Comprueba tu conexión e inténtalo de nuevo."
                        );

                        Toast.makeText(
                                RecibosActivity.this,
                                "Error al cargar pedidos: "
                                        + (e != null ? e.getMessage() : ""),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void mostrarEstado(String mensaje) {
        tvEstadoRecibos.setText(mensaje);
        tvEstadoRecibos.setVisibility(View.VISIBLE);
    }

    private void agregarTarjetaRecibo(Pedido pedido) {

        MaterialCardView tarjeta = new MaterialCardView(this);

        LinearLayout.LayoutParams parametrosTarjeta =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosTarjeta.bottomMargin = dp(14);

        tarjeta.setLayoutParams(parametrosTarjeta);
        tarjeta.setClickable(true);
        tarjeta.setFocusable(true);
        tarjeta.setCardBackgroundColor(
                getColor(R.color.munchly_surface)
        );
        tarjeta.setRadius(dp(20));
        tarjeta.setCardElevation(dp(2));
        tarjeta.setStrokeWidth(dp(1));
        tarjeta.setStrokeColor(
                getColor(R.color.munchly_border)
        );

        LinearLayout contenido = new LinearLayout(this);
        contenido.setOrientation(LinearLayout.VERTICAL);
        contenido.setPadding(dp(18), dp(18), dp(18), dp(18));

        // Encabezado: número de pedido y fecha.
        LinearLayout encabezado = new LinearLayout(this);
        encabezado.setOrientation(LinearLayout.HORIZONTAL);
        encabezado.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvNumero = crearTexto(
                "Pedido #" + obtenerIdCorto(pedido.getId()),
                16,
                R.color.munchly_primary,
                true
        );

        LinearLayout.LayoutParams parametrosNumero =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        encabezado.addView(tvNumero, parametrosNumero);

        TextView tvFecha = crearTexto(
                formatearFecha(pedido),
                12,
                R.color.munchly_text_secondary,
                false
        );

        encabezado.addView(tvFecha);
        contenido.addView(encabezado);

        // Estado del pedido.
        TextView tvEstado = crearTexto(
                "● " + textoSeguro(pedido.getEstado(), "Pendiente"),
                11,
                R.color.munchly_primary,
                true
        );

        tvEstado.setBackgroundResource(R.drawable.bg_chip_unselected);
        tvEstado.setPadding(dp(10), dp(5), dp(10), dp(5));

        LinearLayout.LayoutParams parametrosEstado =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosEstado.topMargin = dp(8);
        contenido.addView(tvEstado, parametrosEstado);

        // Resumen de productos.
        TextView tvProductos = crearTexto(
                obtenerResumenProductos(pedido),
                13,
                R.color.munchly_text_secondary,
                false
        );

        LinearLayout.LayoutParams parametrosProductos =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosProductos.topMargin = dp(10);
        contenido.addView(tvProductos, parametrosProductos);

        // Separador.
        View separador = new View(this);
        separador.setBackgroundColor(
                getColor(R.color.munchly_border)
        );

        LinearLayout.LayoutParams parametrosSeparador =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        parametrosSeparador.topMargin = dp(12);
        parametrosSeparador.bottomMargin = dp(12);
        contenido.addView(separador, parametrosSeparador);

        // Total y acción.
        LinearLayout pie = new LinearLayout(this);
        pie.setOrientation(LinearLayout.HORIZONTAL);
        pie.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvTotal = crearTexto(
                "Total: " + formatoDinero(pedido.getTotalCentavos()),
                16,
                R.color.munchly_primary,
                true
        );

        LinearLayout.LayoutParams parametrosTotal =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        pie.addView(tvTotal, parametrosTotal);

        TextView tvDetalle = crearTexto(
                "Ver detalle",
                13,
                R.color.munchly_accent,
                true
        );

        pie.addView(tvDetalle);
        contenido.addView(pie);

        tarjeta.addView(contenido);

        tarjeta.setOnClickListener(v -> mostrarDetalleRecibo(pedido));

        layoutListaRecibos.addView(tarjeta);
    }

    private void mostrarDetalleRecibo(Pedido pedido) {

        StringBuilder detalle = new StringBuilder();

        detalle.append("Cliente: ")
                .append(textoSeguro(pedido.getCliente(), "No especificado"))
                .append("\n\n");

        detalle.append("Productos:\n");

        List<DetallePedido> productos = pedido.getDetalles();

        if (productos != null) {
            for (DetallePedido producto : productos) {

                if (producto == null) {
                    continue;
                }

                detalle.append(producto.getCantidad())
                        .append(" x ")
                        .append(textoSeguro(producto.getNombre(), "Producto"))
                        .append("\n");

                detalle.append("   Precio unitario: ")
                        .append(formatoDinero(producto.getPrecioUnitario()))
                        .append("\n");

                detalle.append("   Subtotal: ")
                        .append(formatoDinero(producto.getSubtotalCentavos()))
                        .append("\n");

                if (producto.getNotas() != null
                        && !producto.getNotas().trim().isEmpty()) {

                    detalle.append("   Nota: ")
                            .append(producto.getNotas())
                            .append("\n");
                }

                detalle.append("\n");
            }
        }

        detalle.append("Tipo de entrega: ")
                .append(textoSeguro(pedido.getTipoEntrega(), "No especificado"))
                .append("\n");

        if ("Domicilio".equalsIgnoreCase(pedido.getTipoEntrega())) {
            detalle.append("Dirección: ")
                    .append(textoSeguro(
                            pedido.getDireccionEntrega(),
                            "No especificada"
                    ))
                    .append("\n");
        }

        detalle.append("Método de pago: ")
                .append(textoSeguro(pedido.getMetodoPago(), "No especificado"))
                .append("\n");

        detalle.append("Estado del pago: ")
                .append(textoSeguro(pedido.getEstadoPago(), "Pendiente"))
                .append("\n");

        detalle.append("Estado del pedido: ")
                .append(textoSeguro(pedido.getEstado(), "Pendiente"))
                .append("\n\n");

        detalle.append("Total: ")
                .append(formatoDinero(pedido.getTotalCentavos()));

        if (pedido.getNotas() != null
                && !pedido.getNotas().trim().isEmpty()) {

            detalle.append("\n\nNotas del pedido: ")
                    .append(pedido.getNotas());
        }

        MaterialAlertDialogBuilder builder =
                new MaterialAlertDialogBuilder(this);

        builder.setTitle("Recibo #" + obtenerIdCorto(pedido.getId()));

        builder.setMessage(detalle.toString());

        builder.setPositiveButton("Cerrar", (dialog, which) ->
                dialog.dismiss()
        );

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> {
            TextView mensaje = dialog.findViewById(android.R.id.message);

            if (mensaje != null) {
                mensaje.setTextColor(android.graphics.Color.DKGRAY);
            }
        });

        dialog.show();
    }

    private String obtenerResumenProductos(Pedido pedido) {

        StringBuilder resumen = new StringBuilder();

        List<DetallePedido> productos = pedido.getDetalles();

        if (productos == null || productos.isEmpty()) {
            return "Sin productos registrados";
        }

        for (int i = 0; i < productos.size(); i++) {

            DetallePedido producto = productos.get(i);

            if (producto == null) {
                continue;
            }

            if (resumen.length() > 0) {
                resumen.append(", ");
            }

            resumen.append(producto.getCantidad())
                    .append("x ")
                    .append(textoSeguro(producto.getNombre(), "Producto"));
        }

        return resumen.toString();
    }

    private String formatearFecha(Pedido pedido) {

        if (pedido.getFechaCreacion() == null) {
            return "Fecha no disponible";
        }

        SimpleDateFormat formato =
                new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());

        return formato.format(pedido.getFechaCreacion());
    }

    private String formatoDinero(long centavos) {
        return String.format(
                Locale.US,
                "$%.2f",
                centavos / 100.0
        );
    }

    private String obtenerIdCorto(String id) {

        if (id == null || id.trim().isEmpty()) {
            return "Sin ID";
        }

        return id.length() > 8
                ? id.substring(0, 8).toUpperCase(Locale.ROOT)
                : id.toUpperCase(Locale.ROOT);
    }

    private String textoSeguro(String valor, String valorAlternativo) {
        return valor == null || valor.trim().isEmpty()
                ? valorAlternativo
                : valor;
    }

    private TextView crearTexto(
            String texto,
            int tamano,
            int color,
            boolean negrita
    ) {

        TextView vista = new TextView(this);

        vista.setText(texto);
        vista.setTextSize(tamano);
        vista.setTextColor(getColor(color));
        vista.setFontFeatureSettings("kern");

        if (negrita) {
            vista.setTypeface(
                    vista.getTypeface(),
                    android.graphics.Typeface.BOLD
            );
        }

        return vista;
    }

    private int dp(int valor) {
        return (int) (valor * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private void setupWindowInsets() {

        View root = findViewById(R.id.recibosRoot);

        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {

                Insets systemBars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                );

                v.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                );

                return insets;
            });
        }
    }

    private void setupBottomNav() {

        findViewById(R.id.btnNavInicio).setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );
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

        findViewById(R.id.btnNavCarrito).setOnClickListener(v -> {
            startActivity(new Intent(this, CarritoActivity.class));
            finish();
        });
    }
}
