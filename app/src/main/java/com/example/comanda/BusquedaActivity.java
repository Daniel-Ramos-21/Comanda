package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

public class BusquedaActivity extends AppCompatActivity {

    private EditText etBuscar;
    private ImageView btnBorrar;
    private LinearLayout layoutInicial;
    private LinearLayout layoutResultados;
    private LinearLayout layoutVacio;
    private TextView tvContador;
    private RecyclerView rvResultados;
    private MaterialButton btnExplorarMenu;
    private ChipGroup chipGroupRecientes;
    private TextView btnBorrarTodo;

    private ProductoVerticalAdapter adapter;
    private List<Producto> listaCompleta = new ArrayList<>();
    private List<Producto> listaFiltrada = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_busqueda);

        setupWindowInsets();
        initViews();
        setupRecyclerView();
        cargarProductos();
        setupListeners();
        setupBottomNav();
    }

    private void setupWindowInsets() {
        View root = findViewById(R.id.busquedaRoot);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, 0, systemBars.right, 0);
                return insets;
            });
        }
    }

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
    }

    private void setupRecyclerView() {
        adapter = new ProductoVerticalAdapter(listaFiltrada, producto -> {
            Intent intent = new Intent(this, DetalleProductoActivity.class);
            intent.putExtra(DetalleProductoActivity.EXTRA_PRODUCTO, producto);
            startActivity(intent);
        });
        rvResultados.setAdapter(adapter);
    }

    private void cargarProductos() {
        MenuRepository repository = new MenuRepository();
        repository.obtenerProductos(new MenuRepository.OnProductosCallback() {
            @Override
            public void onSuccess(List<Producto> productos) {
                listaCompleta = productos != null && !productos.isEmpty() ? productos : repository.obtenerProductosLocalesRespaldo();
            }

            @Override
            public void onError(Exception e) {
                listaCompleta = repository.obtenerProductosLocalesRespaldo();
            }
        });
    }

    private void setupListeners() {
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s != null ? s.toString().trim() : "";
                btnBorrar.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                filtrar(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnBorrar.setOnClickListener(v -> etBuscar.setText(""));

        btnExplorarMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnBorrarTodo.setOnClickListener(v -> chipGroupRecientes.removeAllViews());

        for (int i = 0; i < chipGroupRecientes.getChildCount(); i++) {
            View child = chipGroupRecientes.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setOnCloseIconClickListener(v -> chipGroupRecientes.removeView(chip));
                chip.setOnClickListener(v -> etBuscar.setText(chip.getText()));
            }
        }
    }

    private void filtrar(String query) {
        if (query.isEmpty()) {
            layoutInicial.setVisibility(View.VISIBLE);
            layoutResultados.setVisibility(View.GONE);
            layoutVacio.setVisibility(View.GONE);
            return;
        }

        layoutInicial.setVisibility(View.GONE);
        listaFiltrada.clear();
        String qNorm = query.toLowerCase();

        for (Producto p : listaCompleta) {
            String nombre = p.getNombre() != null ? p.getNombre().toLowerCase() : "";
            String desc = p.getDescripcion() != null ? p.getDescripcion().toLowerCase() : "";
            if (nombre.contains(qNorm) || desc.contains(qNorm)) {
                listaFiltrada.add(p);
            }
        }

        if (listaFiltrada.isEmpty()) {
            layoutResultados.setVisibility(View.GONE);
            layoutVacio.setVisibility(View.VISIBLE);
        } else {
            layoutVacio.setVisibility(View.GONE);
            layoutResultados.setVisibility(View.VISIBLE);
            tvContador.setText(listaFiltrada.size() + " platillos encontrados");
            adapter.setProductos(listaFiltrada);
        }
    }

    private void setupBottomNav() {
        findViewById(R.id.btnNavInicio).setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
