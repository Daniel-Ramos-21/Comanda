
package com.example.comanda;

import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comanda.Data.CategoriaRepository;
import com.example.comanda.Data.ProductoRepository;
import com.example.comanda.models.Categoria;
import com.example.comanda.models.Producto;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminProductosActivity extends AppCompatActivity {

    private static final String CLOUD_NAME = "r3i1df5o";
    private static final String UPLOAD_PRESET = "munchly";

    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private ProductoRepository productoRepository;
    private CategoriaRepository categoriaRepository;

    private LinearLayout contenedorProductos;
    private TextView tvVacio;
    private ProgressBar progressBar;
    private Button btnAgregarProducto;

    private List<Categoria> categorias = new ArrayList<>();

    private ActivityResultLauncher<String> selectorImagen;

    private Uri imagenSeleccionada;
    private TextView tvEstadoImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_productos);

        inicializarSelectorImagen();
        inicializarComponentes();

        productoRepository = new ProductoRepository();
        categoriaRepository = new CategoriaRepository();

        btnAgregarProducto.setOnClickListener(v ->
                mostrarDialogoProducto(null)
        );

        cargarCategorias();
    }

    private void inicializarSelectorImagen() {

        selectorImagen = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imagenSeleccionada = uri;

                        if (tvEstadoImagen != null) {
                            tvEstadoImagen.setText(
                                    "Imagen seleccionada. Se subirá al guardar."
                            );
                        }
                    }
                }
        );
    }

    private void inicializarComponentes() {
        contenedorProductos = findViewById(R.id.contenedorProductos);
        tvVacio = findViewById(R.id.tvVacioProductos);
        progressBar = findViewById(R.id.progressBarProductos);
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto);
    }

    private void cargarCategorias() {

        btnAgregarProducto.setEnabled(false);

        categoriaRepository.obtenerCategorias(
                new CategoriaRepository.ListaCategoriasCallback() {

                    @Override
                    public void onSuccess(List<Categoria> resultado) {
                        categorias = resultado;
                        btnAgregarProducto.setEnabled(true);
                        cargarProductos();
                    }

                    @Override
                    public void onError(String mensaje) {
                        Toast.makeText(
                                AdminProductosActivity.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void cargarProductos() {

        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);
        contenedorProductos.removeAllViews();

        productoRepository.obtenerProductos(
                new ProductoRepository.ListaProductosCallback() {

                    @Override
                    public void onSuccess(List<Producto> productos) {

                        progressBar.setVisibility(View.GONE);
                        contenedorProductos.removeAllViews();

                        if (productos.isEmpty()) {
                            tvVacio.setVisibility(View.VISIBLE);
                            return;
                        }

                        tvVacio.setVisibility(View.GONE);

                        for (Producto producto : productos) {
                            agregarFilaProducto(producto);
                        }
                    }

                    @Override
                    public void onError(String mensaje) {

                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                AdminProductosActivity.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void agregarFilaProducto(Producto producto) {

        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setPadding(dp(12), dp(12), dp(12), dp(12));

        LinearLayout.LayoutParams parametrosFila =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosFila.setMargins(0, 0, 0, dp(8));
        fila.setLayoutParams(parametrosFila);

        LinearLayout informacion = new LinearLayout(this);
        informacion.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams parametrosInformacion =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        informacion.setLayoutParams(parametrosInformacion);

        TextView tvNombre = new TextView(this);
        tvNombre.setText(producto.getNombre());
        tvNombre.setTextSize(17);
        tvNombre.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvPrecio = new TextView(this);
        tvPrecio.setText(String.format(
                Locale.getDefault(),
                "$%.2f",
                producto.getPrecioCentavos() / 100.0
        ));
        tvPrecio.setTextSize(15);

        TextView tvCategoria = new TextView(this);
        tvCategoria.setText(
                "Categoría: " + obtenerNombreCategoria(
                        producto.getCategoriaId()
                )
        );
        tvCategoria.setTextSize(13);

        TextView tvDisponibilidad = new TextView(this);
        tvDisponibilidad.setText(
                producto.isDisponible() ? "Disponible" : "No disponible"
        );
        tvDisponibilidad.setTextSize(13);

        informacion.addView(tvNombre);
        informacion.addView(tvPrecio);
        informacion.addView(tvCategoria);
        informacion.addView(tvDisponibilidad);

        Button btnEditar = new Button(this);
        btnEditar.setText("Editar");
        btnEditar.setOnClickListener(v ->
                mostrarDialogoProducto(producto)
        );

        Button btnEliminar = new Button(this);
        btnEliminar.setText("Eliminar");
        btnEliminar.setOnClickListener(v ->
                confirmarEliminar(producto)
        );

        fila.addView(informacion);
        fila.addView(btnEditar);
        fila.addView(btnEliminar);

        contenedorProductos.addView(fila);
    }

    private String obtenerNombreCategoria(String categoriaId) {

        for (Categoria categoria : categorias) {
            if (categoria.getId().equals(categoriaId)) {
                return categoria.getNombre();
            }
        }

        return "Categoría no encontrada";
    }

    private void mostrarDialogoProducto(Producto producto) {

        if (categorias.isEmpty()) {
            Toast.makeText(
                    this,
                    "Primero debes registrar una categoría.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        imagenSeleccionada = null;
        tvEstadoImagen = null;

        boolean esEdicion = producto != null;

        LinearLayout formulario = new LinearLayout(this);
        formulario.setOrientation(LinearLayout.VERTICAL);
        formulario.setPadding(dp(20), dp(8), dp(20), dp(8));

        EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre del producto");
        etNombre.setSingleLine(true);

        EditText etDescripcion = new EditText(this);
        etDescripcion.setHint("Descripción");
        etDescripcion.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );
        etDescripcion.setMinLines(2);

        EditText etPrecio = new EditText(this);
        etPrecio.setHint("Precio (ejemplo: 5.99)");
        etPrecio.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        EditText etImagenUrl = new EditText(this);
        etImagenUrl.setHint("URL de imagen (opcional)");
        etImagenUrl.setInputType(InputType.TYPE_TEXT_VARIATION_URI);

        Button btnSeleccionarImagen = new Button(this);
        btnSeleccionarImagen.setText("Seleccionar imagen del teléfono");

        tvEstadoImagen = new TextView(this);
        tvEstadoImagen.setText("No has seleccionado una imagen.");
        tvEstadoImagen.setTextSize(13);

        btnSeleccionarImagen.setOnClickListener(v ->
                selectorImagen.launch("image/*")
        );

        TextView tvCategoria = new TextView(this);
        tvCategoria.setText("Selecciona una categoría");
        tvCategoria.setTextSize(15);
        tvCategoria.setPadding(0, dp(12), 0, dp(4));

        Spinner spinnerCategorias = new Spinner(this);

        List<String> nombresCategorias = new ArrayList<>();

        for (Categoria categoria : categorias) {
            nombresCategorias.add(categoria.getNombre());
        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombresCategorias
        );

        adaptador.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCategorias.setAdapter(adaptador);

        CheckBox cbDisponible = new CheckBox(this);
        cbDisponible.setText("Producto disponible");
        cbDisponible.setChecked(true);

        formulario.addView(etNombre);
        formulario.addView(etDescripcion);
        formulario.addView(etPrecio);
        formulario.addView(tvCategoria);
        formulario.addView(spinnerCategorias);
        formulario.addView(btnSeleccionarImagen);
        formulario.addView(tvEstadoImagen);
        formulario.addView(etImagenUrl);
        formulario.addView(cbDisponible);

        if (esEdicion) {

            etNombre.setText(producto.getNombre());
            etDescripcion.setText(producto.getDescripcion());

            etPrecio.setText(String.format(
                    Locale.US,
                    "%.2f",
                    producto.getPrecioCentavos() / 100.0
            ));

            etImagenUrl.setText(producto.getImagenUrl());
            cbDisponible.setChecked(producto.isDisponible());

            for (int i = 0; i < categorias.size(); i++) {

                if (categorias.get(i).getId()
                        .equals(producto.getCategoriaId())) {

                    spinnerCategorias.setSelection(i);
                    break;
                }
            }
        }

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle(esEdicion ? "Editar producto" : "Nuevo producto")
                .setView(formulario)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton(esEdicion ? "Guardar" : "Crear", null)
                .create();

        dialogo.setOnShowListener(dialog -> {

            Button btnGuardar = dialogo.getButton(
                    AlertDialog.BUTTON_POSITIVE
            );

            btnGuardar.setOnClickListener(v -> {

                String nombre = etNombre.getText().toString().trim();
                String descripcion = etDescripcion.getText().toString().trim();
                String precioTexto = etPrecio.getText().toString().trim();
                String imagenUrl = etImagenUrl.getText().toString().trim();

                if (nombre.isEmpty()) {
                    etNombre.setError("El nombre es obligatorio.");
                    return;
                }

                if (precioTexto.isEmpty()) {
                    etPrecio.setError("El precio es obligatorio.");
                    return;
                }

                int precioCentavos;

                try {

                    BigDecimal precio = new BigDecimal(precioTexto);

                    if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                        etPrecio.setError(
                                "El precio debe ser mayor que cero."
                        );
                        return;
                    }

                    if (precio.scale() > 2) {
                        etPrecio.setError(
                                "Utiliza máximo dos decimales."
                        );
                        return;
                    }

                    precioCentavos = precio
                            .movePointRight(2)
                            .setScale(0, RoundingMode.UNNECESSARY)
                            .intValueExact();

                } catch (NumberFormatException |
                         ArithmeticException error) {

                    etPrecio.setError("Ingresa un precio válido.");
                    return;
                }

                int posicion = spinnerCategorias.getSelectedItemPosition();

                if (posicion < 0 || posicion >= categorias.size()) {
                    Toast.makeText(
                            this,
                            "Selecciona una categoría válida.",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                String categoriaId = categorias.get(posicion).getId();

                Producto nuevoProducto;

                if (esEdicion) {

                    nuevoProducto = producto;
                    nuevoProducto.setNombre(nombre);
                    nuevoProducto.setDescripcion(descripcion);
                    nuevoProducto.setPrecioCentavos(precioCentavos);
                    nuevoProducto.setCategoriaId(categoriaId);
                    nuevoProducto.setDisponible(cbDisponible.isChecked());

                } else {

                    nuevoProducto = new Producto(
                            null,
                            nombre,
                            precioCentavos,
                            descripcion,
                            categoriaId,
                            imagenUrl,
                            cbDisponible.isChecked()
                    );
                }

                // Si se eligió una imagen nueva, su URL reemplaza la anterior.
                // Si no, se conserva la URL escrita o la que ya tenía el producto.
                nuevoProducto.setImagenUrl(imagenUrl);

                guardarProducto(
                        nuevoProducto,
                        esEdicion,
                        dialogo,
                        btnGuardar
                );
            });
        });

        dialogo.show();
    }

    private void guardarProducto(
            Producto producto,
            boolean esEdicion,
            AlertDialog dialogo,
            Button btnGuardar
    ) {

        btnGuardar.setEnabled(false);

        if (imagenSeleccionada != null) {

            btnGuardar.setText("Subiendo imagen...");

            subirImagenCloudinary(imagenSeleccionada,
                    new SubidaImagenCallback() {

                        @Override
                        public void onSuccess(String urlImagen) {

                            producto.setImagenUrl(urlImagen);

                            guardarEnFirestore(
                                    producto,
                                    esEdicion,
                                    dialogo,
                                    btnGuardar
                            );
                        }

                        @Override
                        public void onError(String mensaje) {

                            btnGuardar.setEnabled(true);
                            btnGuardar.setText(
                                    esEdicion ? "Guardar" : "Crear"
                            );

                            Toast.makeText(
                                    AdminProductosActivity.this,
                                    mensaje,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });

        } else {

            guardarEnFirestore(
                    producto,
                    esEdicion,
                    dialogo,
                    btnGuardar
            );
        }
    }

    private void guardarEnFirestore(
            Producto producto,
            boolean esEdicion,
            AlertDialog dialogo,
            Button btnGuardar
    ) {

        btnGuardar.setText("Guardando...");

        ProductoRepository.ProductoCallback callback =
                new ProductoRepository.ProductoCallback() {

                    @Override
                    public void onSuccess(Producto resultado) {

                        Toast.makeText(
                                AdminProductosActivity.this,
                                esEdicion
                                        ? "Producto actualizado correctamente."
                                        : "Producto creado correctamente.",
                                Toast.LENGTH_SHORT
                        ).show();

                        dialogo.dismiss();
                        cargarProductos();
                    }

                    @Override
                    public void onError(String mensaje) {

                        btnGuardar.setEnabled(true);
                        btnGuardar.setText(
                                esEdicion ? "Guardar" : "Crear"
                        );

                        Toast.makeText(
                                AdminProductosActivity.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                };

        if (esEdicion) {
            productoRepository.actualizarProducto(producto, callback);
        } else {
            productoRepository.crearProducto(producto, callback);
        }
    }

    private interface SubidaImagenCallback {
        void onSuccess(String urlImagen);
        void onError(String mensaje);
    }

    private void subirImagenCloudinary(
            Uri imagenUri,
            SubidaImagenCallback callback
    ) {

        executor.execute(() -> {

            HttpURLConnection conexion = null;

            try {

                String boundary = "----MunchlyBoundary" +
                        System.currentTimeMillis();

                URL url = new URL(
                        "https://api.cloudinary.com/v1_1/" +
                                CLOUD_NAME +
                                "/image/upload"
                );

                conexion = (HttpURLConnection) url.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                conexion.setConnectTimeout(15000);
                conexion.setReadTimeout(30000);
                conexion.setRequestProperty(
                        "Content-Type",
                        "multipart/form-data; boundary=" + boundary
                );

                conexion.setChunkedStreamingMode(8192);

                try (DataOutputStream salida =
                             new DataOutputStream(conexion.getOutputStream())) {

                    escribirCampo(
                            salida,
                            boundary,
                            "upload_preset",
                            UPLOAD_PRESET
                    );

                    String tipoMime = getContentResolver()
                            .getType(imagenUri);

                    if (tipoMime == null ||
                            !tipoMime.startsWith("image/")) {
                        tipoMime = "image/jpeg";
                    }

                    salida.writeBytes("--" + boundary + "\r\n");
                    salida.writeBytes(
                            "Content-Disposition: form-data; " +
                                    "name=\"file\"; filename=\"producto\"\r\n"
                    );
                    salida.writeBytes(
                            "Content-Type: " + tipoMime + "\r\n\r\n"
                    );

                    try (InputStream entrada =
                                 getContentResolver()
                                         .openInputStream(imagenUri)) {

                        if (entrada == null) {
                            throw new Exception(
                                    "No se pudo leer la imagen seleccionada."
                            );
                        }

                        byte[] buffer = new byte[8192];
                        int cantidad;

                        while ((cantidad = entrada.read(buffer)) != -1) {
                            salida.write(buffer, 0, cantidad);
                        }
                    }

                    salida.writeBytes("\r\n");
                    salida.writeBytes("--" + boundary + "--\r\n");
                    salida.flush();
                }

                int codigo = conexion.getResponseCode();

                InputStream respuestaStream =
                        codigo >= 200 && codigo < 300
                                ? conexion.getInputStream()
                                : conexion.getErrorStream();

                StringBuilder respuesta = new StringBuilder();

                try (BufferedReader lector = new BufferedReader(
                        new InputStreamReader(
                                respuestaStream,
                                StandardCharsets.UTF_8
                        ))) {

                    String linea;

                    while ((linea = lector.readLine()) != null) {
                        respuesta.append(linea);
                    }
                }

                JSONObject json = new JSONObject(respuesta.toString());

                if (codigo >= 200 && codigo < 300) {

                    String secureUrl = json.optString("secure_url", "");

                    if (secureUrl.isEmpty()) {
                        throw new Exception(
                                "Cloudinary no devolvió la URL de la imagen."
                        );
                    }

                    runOnUiThread(() ->
                            callback.onSuccess(secureUrl)
                    );

                } else {

                    String mensaje = json.optString(
                            "error",
                            "No se pudo subir la imagen."
                    );

                    runOnUiThread(() ->
                            callback.onError(mensaje)
                    );
                }

            } catch (Exception error) {

                String mensaje = error.getMessage() != null
                        ? error.getMessage()
                        : "Error al subir la imagen.";

                runOnUiThread(() ->
                        callback.onError(mensaje)
                );

            } finally {

                if (conexion != null) {
                    conexion.disconnect();
                }
            }
        });
    }

    private void escribirCampo(
            DataOutputStream salida,
            String boundary,
            String nombre,
            String valor
    ) throws Exception {

        salida.writeBytes("--" + boundary + "\r\n");
        salida.writeBytes(
                "Content-Disposition: form-data; name=\"" +
                        nombre + "\"\r\n\r\n"
        );
        salida.write(valor.getBytes(StandardCharsets.UTF_8));
        salida.writeBytes("\r\n");
    }

    private void confirmarEliminar(Producto producto) {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage(
                        "¿Deseas eliminar " + producto.getNombre() + "?"
                )
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialog, which) ->
                        eliminarProducto(producto)
                )
                .show();
    }

    private void eliminarProducto(Producto producto) {

        productoRepository.eliminarProducto(
                producto.getId(),
                new ProductoRepository.ProductoCallback() {

                    @Override
                    public void onSuccess(Producto resultado) {

                        Toast.makeText(
                                AdminProductosActivity.this,
                                "Producto eliminado correctamente.",
                                Toast.LENGTH_SHORT
                        ).show();

                        cargarProductos();
                    }

                    @Override
                    public void onError(String mensaje) {

                        Toast.makeText(
                                AdminProductosActivity.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private int dp(int valor) {
        return (int) (valor * getResources()
                .getDisplayMetrics().density);
    }
}