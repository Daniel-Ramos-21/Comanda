package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comanda.Data.MenuRepository;
import com.example.comanda.adapters.ProductoVerticalAdapter;
import com.example.comanda.models.Producto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class BusquedaActivity extends AppCompatActivity {

    private EditText etBuscar;
    private ImageView btnBorrar;
    private LinearLayout layoutInicial;
    private LinearLayout layoutResultados;
    private LinearLayout layoutVacio;
    private TextView tvContador;
    private TextView tvSinRecientes;
    private RecyclerView rvResultados;
    private MaterialButton btnExplorarMenu;
    private ChipGroup chipGroupRecientes;
    private TextView btnBorrarTodo;

    private ProductoVerticalAdapter adapter;

    private List<Producto> listaCompleta = new ArrayList<>();
    private List<Producto> listaFiltrada = new ArrayList<>();
    private List<String> busquedasRecientes = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String uid;

    private static final int MAX_BUSQUEDAS = 10;

    private DocumentReference historialRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_busqueda);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            uid = auth.getCurrentUser().getUid();

            historialRef = db.collection("usuarios")
                    .document(uid)
                    .collection("busquedas")
                    .document("historial");
        }

        setupWindowInsets();
        initViews();
        setupRecyclerView();
        cargarProductos();
        setupListeners();
        setupBottomNav();

        cargarBusquedasRecientes();
    }

    // --------------------------------------------------
    // INSETS
    // --------------------------------------------------

    private void setupWindowInsets() {

        View root = findViewById(R.id.busquedaRoot);

        if (root != null) {

            final int initialLeft = root.getPaddingLeft();
            final int initialTop = root.getPaddingTop();
            final int initialRight = root.getPaddingRight();
            final int initialBottom = root.getPaddingBottom();

            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {

                Insets systemBars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                );

                v.setPadding(
                        initialLeft + systemBars.left,
                        initialTop + systemBars.top,
                        initialRight + systemBars.right,
                        initialBottom + systemBars.bottom
                );

                return insets;
            });
        }
    }

    // --------------------------------------------------
    // VIEWS
    // --------------------------------------------------

    private void initViews() {

        etBuscar = findViewById(R.id.etBuscarTexto);
        btnBorrar = findViewById(R.id.btnBorrarBusqueda);

        layoutInicial = findViewById(R.id.layoutEstadoInicial);
        layoutResultados = findViewById(R.id.layoutEstadoResultados);
        layoutVacio = findViewById(R.id.layoutEstadoVacio);

        tvContador = findViewById(R.id.tvContadorResultados);
        rvResultados = findViewById(R.id.rvResultadosBusqueda);

        btnExplorarMenu = findViewById(R.id.btnExplorarMenuDesdeBusqueda);

        chipGroupRecientes = findViewById(R.id.chipGroupRecientes);
        btnBorrarTodo = findViewById(R.id.btnBorrarTodoRecientes);

        tvSinRecientes = findViewById(R.id.tvSinRecientes);
    }

    // --------------------------------------------------
    // RECYCLER VIEW
    // --------------------------------------------------

    private void setupRecyclerView() {

        adapter = new ProductoVerticalAdapter(listaFiltrada, producto -> {

            Intent intent = new Intent(this, DetalleProductoActivity.class);

            intent.putExtra(
                    DetalleProductoActivity.EXTRA_PRODUCTO,
                    producto
            );

            startActivity(intent);
        });

        rvResultados.setAdapter(adapter);
    }

    // --------------------------------------------------
    // LOAD PRODUCTS
    // --------------------------------------------------

    private void cargarProductos() {

        MenuRepository repository = new MenuRepository();

        repository.obtenerProductos(new MenuRepository.OnProductosCallback() {

            @Override
            public void onSuccess(List<Producto> productos) {

                listaCompleta = productos != null && !productos.isEmpty()
                        ? productos
                        : repository.obtenerProductosLocalesRespaldo();

                // Si ya hay texto escrito, aplicar el filtro nuevamente
                String query = etBuscar.getText().toString().trim();

                if (!query.isEmpty()) {
                    filtrar(query);
                }
            }

            @Override
            public void onError(Exception e) {

                listaCompleta = repository.obtenerProductosLocalesRespaldo();

                String query = etBuscar.getText().toString().trim();

                if (!query.isEmpty()) {
                    filtrar(query);
                }
            }
        });
    }

    // --------------------------------------------------
    // LISTENERS
    // --------------------------------------------------

    private void setupListeners() {

        etBuscar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {}

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {

                String query = s != null ? s.toString().trim() : "";

                btnBorrar.setVisibility(
                        query.isEmpty() ? View.GONE : View.VISIBLE
                );

                filtrar(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Limpiar campo de búsqueda
        btnBorrar.setOnClickListener(v -> etBuscar.setText(""));

        // Botón explorar menú
        btnExplorarMenu.setOnClickListener(v -> {

            Intent intent = new Intent(this, MenuActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();
        });

        // Guardar búsqueda al presionar buscar en el teclado
        etBuscar.setOnEditorActionListener((v, actionId, event) -> {

            boolean accionBuscar =
                    actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE;

            boolean teclaEnter =
                    event != null
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            && event.getAction() == KeyEvent.ACTION_DOWN;

            if (accionBuscar || teclaEnter) {

                String termino = etBuscar.getText().toString().trim();

                if (!termino.isEmpty()) {
                    guardarBusquedaReciente(termino);
                }

                return true;
            }

            return false;
        });

        // Borrar todo el historial
        btnBorrarTodo.setOnClickListener(v -> borrarTodoElHistorial());
    }

    // --------------------------------------------------
    // SEARCH
    // --------------------------------------------------

    private void filtrar(String query) {

        if (query.isEmpty()) {

            layoutInicial.setVisibility(View.VISIBLE);
            layoutResultados.setVisibility(View.GONE);
            layoutVacio.setVisibility(View.GONE);

            listaFiltrada.clear();
            adapter.notifyDataSetChanged();

            return;
        }

        layoutInicial.setVisibility(View.GONE);

        listaFiltrada.clear();

        String qNorm = query.toLowerCase();

        for (Producto p : listaCompleta) {

            String nombre = p.getNombre() != null
                    ? p.getNombre().toLowerCase()
                    : "";

            String desc = p.getDescripcion() != null
                    ? p.getDescripcion().toLowerCase()
                    : "";

            if (nombre.contains(qNorm) || desc.contains(qNorm)) {
                listaFiltrada.add(p);
            }
        }

        if (listaFiltrada.isEmpty()) {

            layoutResultados.setVisibility(View.GONE);
            layoutVacio.setVisibility(View.VISIBLE);

            adapter.setProductos(listaFiltrada);

        } else {

            layoutVacio.setVisibility(View.GONE);
            layoutResultados.setVisibility(View.VISIBLE);

            tvContador.setText(
                    listaFiltrada.size() + " platillos encontrados"
            );

            adapter.setProductos(listaFiltrada);
        }
    }

    // --------------------------------------------------
    // FIRESTORE: LOAD RECENT SEARCHES
    // --------------------------------------------------

    private void cargarBusquedasRecientes() {

        if (historialRef == null) {
            busquedasRecientes.clear();
            actualizarHistorial();
            return;
        }

        historialRef.get().addOnSuccessListener(document -> {

            busquedasRecientes.clear();

            if (document.exists()) {

                Object data = document.get("terminos");

                if (data instanceof List<?>) {

                    List<?> historial = (List<?>) data;

                    for (Object elemento : historial) {

                        if (elemento instanceof String) {
                            busquedasRecientes.add((String) elemento);
                        }
                    }
                }
            }

            actualizarHistorial();

        }).addOnFailureListener(error -> {

            Toast.makeText(
                    this,
                    "Error al cargar búsquedas recientes.",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    // --------------------------------------------------
    // FIRESTORE: SAVE SEARCH
    // --------------------------------------------------

    private void guardarBusquedaReciente(String termino) {

        if (historialRef == null || termino.isEmpty()) {
            return;
        }

        db.runTransaction(transaction -> {

            com.google.firebase.firestore.DocumentSnapshot document =
                    transaction.get(historialRef);

            List<String> historial = new ArrayList<>();

            if (document.exists()) {

                Object data = document.get("terminos");

                if (data instanceof List<?>) {

                    for (Object elemento : (List<?>) data) {

                        if (elemento instanceof String) {
                            historial.add((String) elemento);
                        }
                    }
                }
            }

            // Evitar duplicados
            historial.remove(termino);

            // La búsqueda más reciente se coloca al inicio
            historial.add(0, termino);

            // Limitar a 10 búsquedas
            while (historial.size() > MAX_BUSQUEDAS) {
                historial.remove(historial.size() - 1);
            }

            transaction.set(
                    historialRef,
                    java.util.Collections.singletonMap("terminos", historial)
            );

            return null;

        }).addOnSuccessListener(unused -> {

            cargarBusquedasRecientes();

        }).addOnFailureListener(error -> {

            Toast.makeText(
                    this,
                    "No se pudo guardar la búsqueda.",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    // --------------------------------------------------
    // FIRESTORE: DELETE ONE SEARCH
    // --------------------------------------------------

    private void eliminarBusquedaReciente(String termino) {

        if (historialRef == null) {
            return;
        }

        db.runTransaction(transaction -> {

            com.google.firebase.firestore.DocumentSnapshot document =
                    transaction.get(historialRef);

            List<String> historial = new ArrayList<>();

            if (document.exists()) {

                Object data = document.get("terminos");

                if (data instanceof List<?>) {

                    for (Object elemento : (List<?>) data) {

                        if (elemento instanceof String) {
                            historial.add((String) elemento);
                        }
                    }
                }
            }

            historial.remove(termino);

            transaction.set(
                    historialRef,
                    java.util.Collections.singletonMap("terminos", historial)
            );

            return null;

        }).addOnSuccessListener(unused -> {

            cargarBusquedasRecientes();

        }).addOnFailureListener(error -> {

            Toast.makeText(
                    this,
                    "No se pudo eliminar la búsqueda.",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    // --------------------------------------------------
    // FIRESTORE: DELETE ALL SEARCHES
    // --------------------------------------------------

    private void borrarTodoElHistorial() {

        if (historialRef == null) {
            busquedasRecientes.clear();
            actualizarHistorial();
            return;
        }

        historialRef.set(
                java.util.Collections.singletonMap(
                        "terminos",
                        new ArrayList<String>()
                )
        ).addOnSuccessListener(unused -> {

            busquedasRecientes.clear();
            actualizarHistorial();

            Toast.makeText(
                    this,
                    "Historial eliminado.",
                    Toast.LENGTH_SHORT
            ).show();

        }).addOnFailureListener(error -> {

            Toast.makeText(
                    this,
                    "No se pudo borrar el historial.",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    // --------------------------------------------------
    // UPDATE RECENT SEARCH CHIPS
    // --------------------------------------------------

    private void actualizarHistorial() {

        chipGroupRecientes.removeAllViews();

        boolean hayBusquedas = !busquedasRecientes.isEmpty();

        chipGroupRecientes.setVisibility(
                hayBusquedas ? View.VISIBLE : View.GONE
        );

        tvSinRecientes.setVisibility(
                hayBusquedas ? View.GONE : View.VISIBLE
        );

        btnBorrarTodo.setVisibility(
                hayBusquedas ? View.VISIBLE : View.GONE
        );

        for (String termino : busquedasRecientes) {

            Chip chip = new Chip(this);

            chip.setText(termino);
            chip.setClickable(true);
            chip.setCheckable(false);
            chip.setFocusable(true);

            // Tocar el chip repite la búsqueda
            chip.setOnClickListener(v -> {

                etBuscar.setText(termino);
                etBuscar.setSelection(etBuscar.getText().length());

                filtrar(termino);
            });

            // Mantener presionado permite eliminar un término
            chip.setOnLongClickListener(v -> {

                eliminarBusquedaReciente(termino);

                Toast.makeText(
                        this,
                        "Eliminando: " + termino,
                        Toast.LENGTH_SHORT
                ).show();

                return true;
            });

            chipGroupRecientes.addView(chip);
        }
    }

    // --------------------------------------------------
    // BOTTOM NAVIGATION
    // --------------------------------------------------

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

        findViewById(R.id.btnNavCuenta).setOnClickListener(v -> {

            startActivity(new Intent(this, CuentaActivity.class));
            finish();
        });

        findViewById(R.id.btnNavRecibos).setOnClickListener(v -> {

            startActivity(new Intent(this, RecibosActivity.class));
            finish();
        });
    }
}