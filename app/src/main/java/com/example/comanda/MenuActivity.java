package com.example.comanda;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comanda.Data.MenuRepository;
import com.example.comanda.adapters.CategoriaAdapter;
import com.example.comanda.adapters.ProductoHorizontalAdapter;
import com.example.comanda.adapters.ProductoVerticalAdapter;
import com.example.comanda.models.Categoria;
import com.example.comanda.models.FiltroModel;
import com.example.comanda.models.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pantalla principal de la carta gastronómica de Munchly.
 * Integra búsqueda interactiva, filtrado por categorías, favoritos de la casa,
 * productos recomendados, catálogo general conectado a Firestore y navegación inferior.
 */
public class MenuActivity extends AppCompatActivity {

    private NestedScrollView nestedScroll;
    private EditText etBuscar;
    private ImageView btnFiltros;
    private LinearLayout layoutIndicadorFiltro;
    private TextView tvFiltroActivoTexto;
    private TextView btnLimpiarFiltroRapido;

    private RecyclerView rvCategorias;
    private RecyclerView rvRecomendados;
    private RecyclerView rvTodosProductos;

    private View cardFavoritoCasa;
    private TextView tvFavoritoNombre;
    private TextView tvFavoritoDescripcion;
    private TextView tvFavoritoPrecio;
    private ImageView ivFavoritoImagen;

    private ProgressBar pbCargando;
    private LinearLayout layoutEmptyState;

    // Navegación inferior
    private View btnNavInicio;
    private View btnNavBuscar;
    private View btnNavPedidos;
    private View btnNavPerfil;
    private View btnNotificaciones;

    // Datos y adaptadores
    private MenuRepository repository;
    private final List<Producto> listaProductosOriginal = new ArrayList<>();
    private final List<Producto> listaProductosFiltrada = new ArrayList<>();
    private final List<Categoria> listaCategorias = new ArrayList<>();

    private CategoriaAdapter categoriaAdapter;
    private ProductoHorizontalAdapter recomendadosAdapter;
    private ProductoVerticalAdapter todosProductosAdapter;

    private String categoriaSeleccionadaId = "todos";
    private FiltroModel filtroModelActual = new FiltroModel();

    private final ActivityResultLauncher<Intent> launcherFiltros = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    FiltroModel resultado = obtenerFiltroDesdeIntent(data);
                    if (resultado != null) {
                        this.filtroModelActual = resultado;
                        aplicarFiltrosYBusqueda();
                        actualizarIndicadorFiltrosActivos();
                    }
                }
            }
    );

    @SuppressWarnings("deprecation")
    private FiltroModel obtenerFiltroDesdeIntent(Intent intent) {
        if (intent == null) return new FiltroModel();
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                return intent.getSerializableExtra(FiltrosActivity.EXTRA_RESULT_FILTROS, FiltroModel.class);
            } else {
                return (FiltroModel) intent.getSerializableExtra(FiltrosActivity.EXTRA_RESULT_FILTROS);
            }
        } catch (Exception e) {
            return new FiltroModel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_menu);

        repository = new MenuRepository();

        setupWindowInsets();
        initViews();
        setupRecyclerViews();
        setupListeners();
        cargarDatosDesdeFirestore();
    }


    private void setupWindowInsets() {
        View root = findViewById(R.id.menuRoot);
        View bottomNav = findViewById(R.id.bottomNavigationContainer);

        if (root != null && bottomNav != null) {

            final int alturaBase = getResources()
                    .getDimensionPixelSize(R.dimen.bottom_nav_height);

            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {

                Insets systemBars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                );
                // Mantener el espacio lateral del sistema
                v.setPadding(
                        systemBars.left,
                        0,
                        systemBars.right,
                        0
                );
                // Sumar el espacio inferior a la altura de la barra
                ViewGroup.LayoutParams params = bottomNav.getLayoutParams();
                params.height = alturaBase + systemBars.bottom;
                bottomNav.setLayoutParams(params);
                // Reservar el espacio inferior sin reducir el área de los elementos
                bottomNav.setPadding(
                        bottomNav.getPaddingLeft(),
                        bottomNav.getPaddingTop(),
                        bottomNav.getPaddingRight(),
                        systemBars.bottom
                );
                return insets;
            });
        }
    }


    private void initViews() {
        nestedScroll = findViewById(R.id.nestedScrollMenu);
        etBuscar = findViewById(R.id.etBuscarMenu);
        btnFiltros = findViewById(R.id.btnAbrirFiltros);
        layoutIndicadorFiltro = findViewById(R.id.layoutIndicadorFiltroActivo);
        tvFiltroActivoTexto = findViewById(R.id.tvFiltroActivoTexto);
        btnLimpiarFiltroRapido = findViewById(R.id.btnLimpiarFiltroRapido);

        rvCategorias = findViewById(R.id.rvCategorias);
        rvRecomendados = findViewById(R.id.rvRecomendados);
        rvTodosProductos = findViewById(R.id.rvTodosProductos);

        cardFavoritoCasa = findViewById(R.id.cardFavoritoCasa);
        tvFavoritoNombre = findViewById(R.id.tvFavoritoNombre);
        tvFavoritoDescripcion = findViewById(R.id.tvFavoritoDescripcion);
        tvFavoritoPrecio = findViewById(R.id.tvFavoritoPrecio);
        ivFavoritoImagen = findViewById(R.id.ivFavoritoImagen);

        pbCargando = findViewById(R.id.pbCargandoMenu);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        btnNavInicio = findViewById(R.id.btnNavInicio);
        btnNavBuscar = findViewById(R.id.btnNavBuscar);
        btnNavPedidos = findViewById(R.id.btnNavPedidos);
        btnNavPerfil = findViewById(R.id.btnNavPerfil);
        btnNotificaciones = findViewById(R.id.ivNotificaciones);
    }

    private void setupRecyclerViews() {
        // Categorías
        categoriaAdapter = new CategoriaAdapter(listaCategorias, (cat, pos) -> {
            categoriaSeleccionadaId = cat.getId();
            aplicarFiltrosYBusqueda();
        });
        rvCategorias.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategorias.setAdapter(categoriaAdapter);

        // Recomendados ("También te puede gustar")
        recomendadosAdapter = new ProductoHorizontalAdapter(new ArrayList<>(), this::mostrarDetalleProducto);
        rvRecomendados.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRecomendados.setAdapter(recomendadosAdapter);

        // Todos los productos
        todosProductosAdapter = new ProductoVerticalAdapter(listaProductosFiltrada, this::mostrarDetalleProducto);
        rvTodosProductos.setLayoutManager(new LinearLayoutManager(this));
        rvTodosProductos.setAdapter(todosProductosAdapter);
    }

    private void setupListeners() {
        // Buscador reactivo
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltrosYBusqueda();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Botón de filtros
        btnFiltros.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, FiltrosActivity.class);
            intent.putExtra(FiltrosActivity.EXTRA_FILTROS_ACTUALES, filtroModelActual);
            launcherFiltros.launch(intent);
        });

        // Limpiar filtros activo rápido
        btnLimpiarFiltroRapido.setOnClickListener(v -> {
            filtroModelActual.limpiar();
            actualizarIndicadorFiltrosActivos();
            aplicarFiltrosYBusqueda();
        });

        // Clic en la tarjeta de Favorito de la casa
        cardFavoritoCasa.setOnClickListener(v -> {
            Producto favorito = new Producto(
                    "fav_pupusa",
                    "Pupusas de Queso y Loroco",
                    "Tradicionales pupusas hechas a mano con masa de maíz artesanal, queso fundido y flor de loroco fresca. Acompañadas de curtido y salsa.",
                    350,
                    "cat_desayunos",
                    true
            );
            mostrarDetalleProducto(favorito);
        });

        // Navegación inferior persistente de 4 secciones según Figma
        btnNavInicio.setOnClickListener(v -> {
            if (nestedScroll != null) {
                nestedScroll.smoothScrollTo(0, 0);
            }
        });

        btnNavBuscar.setOnClickListener(v -> startActivity(new Intent(this, BusquedaActivity.class)));
        btnNavPedidos.setOnClickListener(v -> startActivity(new Intent(this, RecibosActivity.class)));
        btnNavPerfil.setOnClickListener(v -> startActivity(new Intent(this, CuentaActivity.class)));
        btnNotificaciones.setOnClickListener(v -> Toast.makeText(this, "No tienes notificaciones pendientes", Toast.LENGTH_SHORT).show());
    }

    private void cargarDatosDesdeFirestore() {
        pbCargando.setVisibility(View.VISIBLE);

        // 1. Cargar Categorías
        repository.obtenerCategorias(new MenuRepository.OnCategoriasCallback() {
            @Override
            public void onSuccess(List<Categoria> categorias) {
                poblarCategorias(categorias);
            }

            @Override
            public void onError(Exception e) {
                // Si falla o la colección no existe aún, se usan las categorías base de identidad
                poblarCategorias(new ArrayList<>());
            }
        });

        // 2. Cargar Productos
        repository.obtenerProductos(new MenuRepository.OnProductosCallback() {
            @Override
            public void onSuccess(List<Producto> productos) {
                pbCargando.setVisibility(View.GONE);
                listaProductosOriginal.clear();
                if (productos != null && !productos.isEmpty()) {
                    listaProductosOriginal.addAll(productos);
                } else {
                    // Si Firestore aún no tiene documentos en 'productos', cargamos la carta local curada
                    listaProductosOriginal.addAll(repository.obtenerProductosLocalesRespaldo());
                }
                aplicarFiltrosYBusqueda();
                actualizarFavoritoDeLaCasa();
                actualizarRecomendados();
            }

            @Override
            public void onError(Exception e) {
                pbCargando.setVisibility(View.GONE);
                Toast.makeText(MenuActivity.this, R.string.error_loading_products, Toast.LENGTH_LONG).show();
                listaProductosOriginal.clear();
                listaProductosOriginal.addAll(repository.obtenerProductosLocalesRespaldo());
                aplicarFiltrosYBusqueda();
                actualizarFavoritoDeLaCasa();
                actualizarRecomendados();
            }
        });
    }

    private void poblarCategorias(List<Categoria> categoriasRemotas) {
        listaCategorias.clear();
        listaCategorias.add(new Categoria("todos", getString(R.string.category_all), "Todos los platillos"));

        if (categoriasRemotas != null && !categoriasRemotas.isEmpty()) {
            listaCategorias.addAll(categoriasRemotas);
        } else {
            listaCategorias.add(new Categoria("cat_desayunos", getString(R.string.category_breakfast), "Desayunos"));
            listaCategorias.add(new Categoria("cat_almuerzos", getString(R.string.category_lunch), "Almuerzos"));
            listaCategorias.add(new Categoria("cat_bebidas", getString(R.string.category_drinks), "Bebidas"));
        }

        categoriaAdapter.notifyDataSetChanged();
    }

    private void actualizarFavoritoDeLaCasa() {
        Producto favorito = null;
        for (Producto p : listaProductosOriginal) {
            if (p.getNombre() != null && p.getNombre().toLowerCase().contains("pupusa")) {
                favorito = p;
                break;
            }
        }
        if (favorito == null && !listaProductosOriginal.isEmpty()) {
            favorito = listaProductosOriginal.get(0);
        }

        if (favorito != null) {
            tvFavoritoNombre.setText(favorito.getNombre());
            tvFavoritoDescripcion.setText(favorito.getDescripcion());
            double precio = favorito.getPrecioCentavos() / 100.0;
            tvFavoritoPrecio.setText(String.format(Locale.US, "$%.2f", precio));
            ivFavoritoImagen.setImageResource(R.drawable.ic_food_pupusas);
        }
    }

    private void actualizarRecomendados() {
        List<Producto> recomendados = new ArrayList<>();
        for (Producto p : listaProductosOriginal) {
            if (p.getNombre() != null && !p.getNombre().toLowerCase().contains("pupusa")) {
                recomendados.add(p);
            }
        }
        recomendadosAdapter.setProductos(recomendados);
    }

    private void aplicarFiltrosYBusqueda() {
        String query = etBuscar.getText() != null ? etBuscar.getText().toString().trim().toLowerCase() : "";

        listaProductosFiltrada.clear();

        for (Producto p : listaProductosOriginal) {
            // 1. Filtro por categoría seleccionada
            if (!"todos".equalsIgnoreCase(categoriaSeleccionadaId)) {
                String catId = p.getCategoriaId() != null ? p.getCategoriaId().toLowerCase() : "";
                if (!catId.contains(categoriaSeleccionadaId.toLowerCase())) {
                    continue;
                }
            }

            // 2. Filtro por búsqueda de texto (nombre o descripción/ingredientes)
            if (!query.isEmpty()) {
                String nombre = p.getNombre() != null ? p.getNombre().toLowerCase() : "";
                String desc = p.getDescripcion() != null ? p.getDescripcion().toLowerCase() : "";
                if (!nombre.contains(query) && !desc.contains(query)) {
                    continue;
                }
            }

            // 3. Filtros avanzados (precio, tipo comida, ingredientes)
            if (filtroModelActual != null && filtroModelActual.isActivo()) {
                if (!filtroModelActual.coincideConProducto(p)) {
                    continue;
                }
            }

            listaProductosFiltrada.add(p);
        }

        todosProductosAdapter.notifyDataSetChanged();

        if (listaProductosFiltrada.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvTodosProductos.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvTodosProductos.setVisibility(View.VISIBLE);
        }
    }

    private void actualizarIndicadorFiltrosActivos() {
        if (filtroModelActual != null && filtroModelActual.isActivo()) {
            StringBuilder sb = new StringBuilder("Filtros: ");
            if (filtroModelActual.getTipoComida() != null) {
                sb.append(filtroModelActual.getTipoComida()).append(" • ");
            }
            if (filtroModelActual.getIngredientes() != null && !filtroModelActual.getIngredientes().isEmpty()) {
                sb.append(String.join(", ", filtroModelActual.getIngredientes())).append(" • ");
            }
            if (filtroModelActual.getPrecioMin() != null || filtroModelActual.getPrecioMax() != null) {
                sb.append("$").append(filtroModelActual.getPrecioMin() != null ? filtroModelActual.getPrecioMin() : "0")
                        .append(" - $").append(filtroModelActual.getPrecioMax() != null ? filtroModelActual.getPrecioMax() : "∞")
                        .append(" • ");
            }
            String texto = sb.toString();
            if (texto.endsWith(" • ")) {
                texto = texto.substring(0, texto.length() - 3);
            }
            tvFiltroActivoTexto.setText(texto);
            layoutIndicadorFiltro.setVisibility(View.VISIBLE);
        } else {
            layoutIndicadorFiltro.setVisibility(View.GONE);
        }
    }

    private void mostrarDetalleProducto(Producto p) {
        if (p == null) return;
        Intent intent = new Intent(this, DetalleProductoActivity.class);
        intent.putExtra(DetalleProductoActivity.EXTRA_PRODUCTO, p);
        startActivity(intent);
    }
}
