
package com.example.comanda;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comanda.Data.CategoriaRepository;
import com.example.comanda.models.Categoria;

import java.util.List;

public class AdminCategoriasActivity extends AppCompatActivity {

    private CategoriaRepository categoriaRepository;

    private LinearLayout contenedorCategorias;
    private TextView tvVacio;
    private ProgressBar progressBar;
    private Button btnAgregarCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_categorias);

        inicializarComponentes();

        categoriaRepository = new CategoriaRepository();

        btnAgregarCategoria.setOnClickListener(v ->
                mostrarDialogoCategoria(null)
        );

        cargarCategorias();
    }

    // Conectar los componentes del XML
    private void inicializarComponentes() {

        contenedorCategorias = findViewById(R.id.contenedorCategorias);
        tvVacio = findViewById(R.id.tvVacio);
        progressBar = findViewById(R.id.progressBar);
        btnAgregarCategoria = findViewById(R.id.btnAgregarCategoria);
    }

    // Consultar categorías en Firestore
    private void cargarCategorias() {

        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);
        contenedorCategorias.removeAllViews();

        categoriaRepository.obtenerCategorias(
                new CategoriaRepository.ListaCategoriasCallback() {

                    @Override
                    public void onSuccess(List<Categoria> categorias) {

                        progressBar.setVisibility(View.GONE);
                        contenedorCategorias.removeAllViews();

                        if (categorias.isEmpty()) {
                            tvVacio.setVisibility(View.VISIBLE);
                            return;
                        }

                        tvVacio.setVisibility(View.GONE);

                        for (Categoria categoria : categorias) {
                            agregarFilaCategoria(categoria);
                        }
                    }

                    @Override
                    public void onError(String mensaje) {

                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                AdminCategoriasActivity.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // Crear una fila visual por cada categoría
    private void agregarFilaCategoria(Categoria categoria) {

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
        tvNombre.setText(categoria.getNombre());
        tvNombre.setTextSize(17);
        tvNombre.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvDescripcion = new TextView(this);
        String descripcion = categoria.getDescripcion();

        if (descripcion == null || descripcion.trim().isEmpty()) {
            descripcion = "Sin descripción";
        }

        tvDescripcion.setText(descripcion);
        tvDescripcion.setTextSize(14);

        informacion.addView(tvNombre);
        informacion.addView(tvDescripcion);

        Button btnEditar = new Button(this);
        btnEditar.setText("Editar");

        btnEditar.setOnClickListener(v ->
                mostrarDialogoCategoria(categoria)
        );

        Button btnEliminar = new Button(this);
        btnEliminar.setText("Eliminar");

        btnEliminar.setOnClickListener(v ->
                confirmarEliminar(categoria)
        );

        fila.addView(informacion);
        fila.addView(btnEditar);
        fila.addView(btnEliminar);

        contenedorCategorias.addView(fila);
    }

    // Mostrar formulario para crear o editar
    private void mostrarDialogoCategoria(Categoria categoria) {

        boolean esEdicion = categoria != null;

        LinearLayout formulario = new LinearLayout(this);
        formulario.setOrientation(LinearLayout.VERTICAL);
        formulario.setPadding(dp(20), dp(8), dp(20), dp(8));

        EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre de la categoría");
        etNombre.setSingleLine(true);

        EditText etDescripcion = new EditText(this);
        etDescripcion.setHint("Descripción");
        etDescripcion.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );
        etDescripcion.setMinLines(2);

        formulario.addView(etNombre);
        formulario.addView(etDescripcion);

        if (esEdicion) {
            etNombre.setText(categoria.getNombre());
            etDescripcion.setText(categoria.getDescripcion());
        }

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle(esEdicion ? "Editar categoría" : "Nueva categoría")
                .setView(formulario)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton(esEdicion ? "Guardar" : "Crear", null)
                .create();

        dialogo.setOnShowListener(dialog -> {

            dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String nombre = etNombre.getText()
                                .toString().trim();

                        String descripcion = etDescripcion.getText()
                                .toString().trim();

                        if (nombre.isEmpty()) {
                            etNombre.setError(
                                    "El nombre es obligatorio."
                            );
                            return;
                        }

                        Categoria nuevaCategoria;

                        if (esEdicion) {
                            nuevaCategoria = categoria;
                            nuevaCategoria.setNombre(nombre);
                            nuevaCategoria.setDescripcion(descripcion);
                        } else {
                            nuevaCategoria = new Categoria(
                                    null,
                                    nombre,
                                    descripcion
                            );
                        }

                        guardarCategoria(nuevaCategoria, esEdicion);
                        dialogo.dismiss();
                    });
        });

        dialogo.show();
    }

    // Guardar una categoría nueva o actualizar una existente
    private void guardarCategoria(Categoria categoria,
                                  boolean esEdicion) {

        if (esEdicion) {

            categoriaRepository.actualizarCategoria(
                    categoria,
                    new CategoriaRepository.CategoriaCallback() {

                        @Override
                        public void onSuccess(Categoria resultado) {
                            Toast.makeText(
                                    AdminCategoriasActivity.this,
                                    "Categoría actualizada correctamente.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            cargarCategorias();
                        }

                        @Override
                        public void onError(String mensaje) {
                            Toast.makeText(
                                    AdminCategoriasActivity.this,
                                    mensaje,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );

        } else {

            categoriaRepository.crearCategoria(
                    categoria,
                    new CategoriaRepository.CategoriaCallback() {

                        @Override
                        public void onSuccess(Categoria resultado) {
                            Toast.makeText(
                                    AdminCategoriasActivity.this,
                                    "Categoría creada correctamente.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            cargarCategorias();
                        }

                        @Override
                        public void onError(String mensaje) {
                            Toast.makeText(
                                    AdminCategoriasActivity.this,
                                    mensaje,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );
        }
    }

    // Confirmar antes de eliminar
    private void confirmarEliminar(Categoria categoria) {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar categoría")
                .setMessage(
                        "¿Estás seguro de que deseas eliminar la categoría "
                                + categoria.getNombre() + "?"
                )
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialog, which) ->
                        eliminarCategoria(categoria)
                )
                .show();
    }

    // Eliminar categoría mediante el repositorio
    private void eliminarCategoria(Categoria categoria) {

        categoriaRepository.eliminarCategoria(
                categoria.getId(),
                new CategoriaRepository.CategoriaCallback() {

                    @Override
                    public void onSuccess(Categoria resultado) {

                        Toast.makeText(
                                AdminCategoriasActivity.this,
                                "Categoría eliminada correctamente.",
                                Toast.LENGTH_SHORT
                        ).show();

                        cargarCategorias();
                    }

                    @Override
                    public void onError(String mensaje) {

                        Toast.makeText(
                                AdminCategoriasActivity.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // Convertir unidades dp a píxeles
    private int dp(int valor) {
        return (int) (valor * getResources()
                .getDisplayMetrics().density);
    }
}
