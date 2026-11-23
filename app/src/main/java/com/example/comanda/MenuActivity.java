
package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comanda.Data.MenuRepository;
import com.example.comanda.Data.UsuarioRepository;
import com.example.comanda.adapters.CategoriaAdapter;
import com.example.comanda.adapters.ProductoHorizontalAdapter;
import com.example.comanda.adapters.ProductoVerticalAdapter;
import com.example.comanda.models.Categoria;
import com.example.comanda.models.Producto;
import com.example.comanda.models.Usuario;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private NestedScrollView nestedScroll;

    // RecyclerViews
    private RecyclerView rvCategorias;
    private RecyclerView rvRecomendados;
    private RecyclerView rvTodosProductos;

    // Estados de carga y catálogo vacío
    private ProgressBar pbCargando;
    private LinearLayout layoutEmptyState;

    // Navegación inferior
    private View btnNavInicio;
    private View btnNavBuscar;
    private View btnNavPedidos;
    private View btnNavPerfil;
    private View btnNotificaciones;

    // Panel de administración
    private Button btnPanelAdmin;

    // Saludo
    private TextView tvGreetingTitle;

    // Repositorios
    private MenuRepository repository;
    private UsuarioRepository usuarioRepository;

    // Listas de datos
    private final List<Producto> listaProductosOriginal =
            new ArrayList<>();

    private final List<Producto> listaProductosFiltrada =
            new ArrayList<>();

    private final List<Categoria> listaCategorias =
            new ArrayList<>();

    // Adaptadores
    private CategoriaAdapter categoriaAdapter;
    private ProductoHorizontalAdapter recomendadosAdapter;
    private ProductoVerticalAdapter todosProductosAdapter;

    // Categoría seleccionada
    private String categoriaSeleccionadaId = "todos";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.enableEdgeToEdge(getWindow());

        setContentView(R.layout.activity_menu);

        usuarioRepository = new UsuarioRepository();
        repository = new MenuRepository();

        setupWindowInsets();
        initViews();
        setupRecyclerViews();
        setupListeners();

        cargarDatosDesdeFirestore();
    }


    // --------------------------------------------------
    // AJUSTES DE VENTANA
    // --------------------------------------------------

    private void setupWindowInsets() {

        View root = findViewById(R.id.menuRoot);
        View bottomNav = findViewById(R.id.bottomNavigationContainer);

        if (root == null || bottomNav == null) {
            return;
        }

        final int alturaBase = getResources()
                .getDimensionPixelSize(R.dimen.bottom_nav_height);

        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, windowInsets) -> {

                    Insets barrasSistema = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            barrasSistema.left,
                            0,
                            barrasSistema.right,
                            0
                    );

                    ViewGroup.LayoutParams params =
                            bottomNav.getLayoutParams();

                    params.height = alturaBase + barrasSistema.bottom;

                    bottomNav.setLayoutParams(params);

                    bottomNav.setPadding(
                            bottomNav.getPaddingLeft(),
                            bottomNav.getPaddingTop(),
                            bottomNav.getPaddingRight(),
                            barrasSistema.bottom
                    );

                    return windowInsets;
                }
        );
    }


    // --------------------------------------------------
    // INICIALIZACIÓN DE VISTAS
    // --------------------------------------------------

    private void initViews() {

        nestedScroll = findViewById(R.id.nestedScrollMenu);

        rvCategorias = findViewById(R.id.rvCategorias);
        rvRecomendados = findViewById(R.id.rvRecomendados);
        rvTodosProductos = findViewById(R.id.rvTodosProductos);

        pbCargando = findViewById(R.id.pbCargandoMenu);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        btnNavInicio = findViewById(R.id.btnNavInicio);
        btnNavBuscar = findViewById(R.id.btnNavBuscar);
        btnNavPedidos = findViewById(R.id.btnNavPedidos);
        btnNavPerfil = findViewById(R.id.btnNavPerfil);

        btnNotificaciones = findViewById(R.id.ivNotificaciones);

        tvGreetingTitle = findViewById(R.id.tvGreetingTitle);

        btnPanelAdmin = findViewById(R.id.btnPanelAdmin);
    }


    // --------------------------------------------------
    // CONFIGURACIÓN DE RECYCLERVIEWS
    // --------------------------------------------------

    private void setupRecyclerViews() {

        // Categorías
        categoriaAdapter = new CategoriaAdapter(
                listaCategorias,
                (categoria, posicion) -> {

                    categoriaSeleccionadaId = categoria.getId();

                    aplicarFiltrosYBusqueda();
                }
        );

        rvCategorias.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        rvCategorias.setAdapter(categoriaAdapter);


        // Productos recomendados
        recomendadosAdapter = new ProductoHorizontalAdapter(
                new ArrayList<>(),
                this::mostrarDetalleProducto
        );

        rvRecomendados.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        rvRecomendados.setAdapter(recomendadosAdapter);


        // Todos los productos
        todosProductosAdapter = new ProductoVerticalAdapter(
                listaProductosFiltrada,
                this::mostrarDetalleProducto
        );

        rvTodosProductos.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvTodosProductos.setAdapter(todosProductosAdapter);
    }


    // --------------------------------------------------
    // EVENTOS Y NAVEGACIÓN
    // --------------------------------------------------

    private void setupListeners() {

        // Inicio
        btnNavInicio.setOnClickListener(v -> {

            if (nestedScroll != null) {
                nestedScroll.smoothScrollTo(0, 0);
            }
        });


        // Búsqueda
        btnNavBuscar.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            MenuActivity.this,
                            BusquedaActivity.class
                    )
            );
        });


        // Pedidos
        btnNavPedidos.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            MenuActivity.this,
                            RecibosActivity.class
                    )
            );
        });


        // Perfil
        btnNavPerfil.setOnClickListener(v -> {

            Intent intent;

            if (com.google.firebase.auth.FirebaseAuth
                    .getInstance()
                    .getCurrentUser() != null) {

                intent = new Intent(
                        MenuActivity.this,
                        CuentaActivity.class
                );

            } else {

                intent = new Intent(
                        MenuActivity.this,
                        LoginActivity.class
                );
            }

            startActivity(intent);
        });


        // Notificaciones
        btnNotificaciones.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "No tienes notificaciones pendientes",
                    Toast.LENGTH_SHORT
            ).show();
        });


        // Panel de administración
        btnPanelAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MenuActivity.this,
                    AdminActivity.class
            );

            startActivity(intent);
        });
    }


    // --------------------------------------------------
    // CICLO DE VIDA
    // --------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();

        if (usuarioRepository != null
                && tvGreetingTitle != null) {

            cargarSaludo();
            verificarAccesoAdmin();

            // Actualizar categorías al regresar de administración
            cargarCategoriasDesdeFirestore();
        }
    }


    // --------------------------------------------------
    // SALUDO DEL USUARIO
    // --------------------------------------------------

    private void cargarSaludo() {

        tvGreetingTitle.setText("Hola");

        if (!usuarioRepository.haySesionActiva()) {
            return;
        }

        usuarioRepository.obtenerPerfil(
                new UsuarioRepository.PerfilCallback() {

                    @Override
                    public void onSuccess(Usuario usuario) {

                        if (usuario.getNombre() != null
                                && !usuario.getNombre()
                                .trim().isEmpty()) {

                            tvGreetingTitle.setText(
                                    "Hola, " +
                                            usuario.getNombre().trim()
                            );
                        }
                    }

                    @Override
                    public void onError(String mensaje) {

                        tvGreetingTitle.setText("Hola");
                    }
                }
        );
    }


    // --------------------------------------------------
    // CARGA DE DATOS
    // --------------------------------------------------

    private void cargarDatosDesdeFirestore() {

        pbCargando.setVisibility(View.VISIBLE);

        layoutEmptyState.setVisibility(View.GONE);
        rvTodosProductos.setVisibility(View.GONE);

        repository.obtenerProductos(
                new MenuRepository.OnProductosCallback() {

                    @Override
                    public void onSuccess(List<Producto> productos) {

                        pbCargando.setVisibility(View.GONE);

                        listaProductosOriginal.clear();

                        if (productos != null
                                && !productos.isEmpty()) {

                            listaProductosOriginal.addAll(productos);

                        } else {

                            listaProductosOriginal.addAll(
                                    repository.obtenerProductosLocalesRespaldo()
                            );
                        }

                        aplicarFiltrosYBusqueda();
                        actualizarRecomendados();
                    }

                    @Override
                    public void onError(Exception e) {

                        pbCargando.setVisibility(View.GONE);

                        Toast.makeText(
                                MenuActivity.this,
                                R.string.error_loading_products,
                                Toast.LENGTH_LONG
                        ).show();

                        listaProductosOriginal.clear();

                        listaProductosOriginal.addAll(
                                repository.obtenerProductosLocalesRespaldo()
                        );

                        aplicarFiltrosYBusqueda();
                        actualizarRecomendados();
                    }
                }
        );
    }


    // --------------------------------------------------
    // CARGAR CATEGORÍAS DESDE FIRESTORE
    // --------------------------------------------------

    private void cargarCategoriasDesdeFirestore() {

        repository.obtenerCategorias(
                new MenuRepository.OnCategoriasCallback() {

                    @Override
                    public void onSuccess(List<Categoria> categorias) {

                        poblarCategorias(categorias);
                    }

                    @Override
                    public void onError(Exception e) {

                        Toast.makeText(
                                MenuActivity.this,
                                "No se pudieron cargar las categorías",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }


    // --------------------------------------------------
    // MOSTRAR CATEGORÍAS
    // --------------------------------------------------

    private void poblarCategorias(List<Categoria> categoriasRemotas) {

        listaCategorias.clear();

        // Primera opción: Todos
        listaCategorias.add(
                new Categoria(
                        "todos",
                        "Todos",
                        "Todos los productos"
                )
        );

        // Agregar categorías de Firestore
        if (categoriasRemotas != null) {

            for (Categoria categoria : categoriasRemotas) {

                if (categoria != null
                        && categoria.getId() != null
                        && !categoria.getId().trim().isEmpty()
                        && categoria.getNombre() != null
                        && !categoria.getNombre().trim().isEmpty()) {

                    listaCategorias.add(categoria);
                }
            }
        }

        // Comprobar si la categoría seleccionada todavía existe
        int posicionSeleccionada = 0;
        boolean encontrada = false;

        for (int i = 0; i < listaCategorias.size(); i++) {

            Categoria categoria = listaCategorias.get(i);

            if (categoria.getId().equals(categoriaSeleccionadaId)) {

                posicionSeleccionada = i;
                encontrada = true;
                break;
            }
        }

        // Si fue eliminada, regresar a Todos
        if (!encontrada) {

            categoriaSeleccionadaId = "todos";
            posicionSeleccionada = 0;
        }

        categoriaAdapter.setPosicionSeleccionada(posicionSeleccionada);
        categoriaAdapter.notifyDataSetChanged();

        aplicarFiltrosYBusqueda();
    }


    // --------------------------------------------------
    // PRODUCTOS RECOMENDADOS
    // --------------------------------------------------

    private void actualizarRecomendados() {

        List<Producto> recomendados = new ArrayList<>();

        for (Producto producto : listaProductosOriginal) {

            if (producto != null) {
                recomendados.add(producto);
            }
        }

        recomendadosAdapter.setProductos(recomendados);
    }


    // --------------------------------------------------
    // FILTRAR PRODUCTOS POR CATEGORÍA
    // --------------------------------------------------

    private void aplicarFiltrosYBusqueda() {

        listaProductosFiltrada.clear();

        for (Producto producto : listaProductosOriginal) {

            if (producto == null) {
                continue;
            }

            // Si se seleccionó Todos, mostrar todos los productos
            if (!"todos".equals(categoriaSeleccionadaId)) {

                String categoriaProducto = producto.getCategoriaId();

                // Comparar el ID de categoría del producto
                // con el ID de la categoría seleccionada
                if (categoriaProducto == null
                        || !categoriaProducto.equals(
                        categoriaSeleccionadaId
                )) {

                    continue;
                }
            }

            listaProductosFiltrada.add(producto);
        }

        todosProductosAdapter.notifyDataSetChanged();

        // Mostrar estado vacío si no hay coincidencias
        if (listaProductosFiltrada.isEmpty()) {

            layoutEmptyState.setVisibility(View.VISIBLE);
            rvTodosProductos.setVisibility(View.GONE);

        } else {

            layoutEmptyState.setVisibility(View.GONE);
            rvTodosProductos.setVisibility(View.VISIBLE);
        }
    }


    // --------------------------------------------------
    // DETALLE DEL PRODUCTO
    // --------------------------------------------------

    private void mostrarDetalleProducto(Producto producto) {

        if (producto == null) {
            return;
        }

        Intent intent = new Intent(
                this,
                DetalleProductoActivity.class
        );

        intent.putExtra(
                DetalleProductoActivity.EXTRA_PRODUCTO,
                producto
        );

        startActivity(intent);
    }


    // --------------------------------------------------
    // VERIFICAR ACCESO DE ADMINISTRADOR
    // --------------------------------------------------

    private void verificarAccesoAdmin() {

        btnPanelAdmin.setVisibility(View.GONE);

        if (!usuarioRepository.haySesionActiva()) {
            return;
        }

        usuarioRepository.comprobarAdministrador(
                new UsuarioRepository.RolCallback() {

                    @Override
                    public void onSuccess(boolean esAdministrador) {

                        if (esAdministrador) {

                            btnPanelAdmin.setVisibility(View.VISIBLE);

                        } else {

                            btnPanelAdmin.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(String mensaje) {

                        btnPanelAdmin.setVisibility(View.GONE);
                    }
                }
        );
    }
}
