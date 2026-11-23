
package com.example.comanda.Data;

import com.example.comanda.models.Categoria;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoriaRepository {

    private FirebaseFirestore db;

    // Callback para una categoría
    public interface CategoriaCallback {
        void onSuccess(Categoria categoria);
        void onError(String mensaje);
    }

    // Callback para una lista de categorías
    public interface ListaCategoriasCallback {
        void onSuccess(List<Categoria> categorias);
        void onError(String mensaje);
    }

    // Constructor
    public CategoriaRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Crear categoría
    public void crearCategoria(Categoria categoria,
                               CategoriaCallback callback) {

        String id = db.collection("categorias").document().getId();

        categoria.setId(id);

        db.collection("categorias")
                .document(id)
                .set(categoria)
                .addOnSuccessListener(unused ->
                        callback.onSuccess(categoria)
                )
                .addOnFailureListener(error ->
                        callback.onError(
                                "No se pudo crear la categoría: "
                                        + error.getMessage()
                        ));
    }

    // Obtener todas las categorías
    public void obtenerCategorias(ListaCategoriasCallback callback) {

        db.collection("categorias")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<Categoria> categorias = new ArrayList<>();

                    for (QueryDocumentSnapshot document :
                            queryDocumentSnapshots) {

                        Categoria categoria =
                                document.toObject(Categoria.class);

                        categoria.setId(document.getId());
                        categorias.add(categoria);
                    }

                    callback.onSuccess(categorias);
                })
                .addOnFailureListener(error ->
                        callback.onError(
                                "No se pudieron obtener las categorías: "
                                        + error.getMessage()
                        ));
    }

    // Actualizar categoría
    public void actualizarCategoria(Categoria categoria,
                                    CategoriaCallback callback) {

        if (categoria.getId() == null ||
                categoria.getId().trim().isEmpty()) {

            callback.onError("La categoría no tiene un ID válido.");
            return;
        }

        db.collection("categorias")
                .document(categoria.getId())
                .update(
                        "nombre", categoria.getNombre(),
                        "descripcion", categoria.getDescripcion()
                )
                .addOnSuccessListener(unused ->
                        callback.onSuccess(categoria)
                )
                .addOnFailureListener(error ->
                        callback.onError(
                                "No se pudo actualizar la categoría: "
                                        + error.getMessage()
                        ));
    }

    // Eliminar categoría
    public void eliminarCategoria(String id,
                                  CategoriaCallback callback) {

        if (id == null || id.trim().isEmpty()) {
            callback.onError("El ID de la categoría no es válido.");
            return;
        }

        // Comprobar si existen productos asociados
        db.collection("productos")
                .whereEqualTo("categoriaId", id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        callback.onError(
                                "No se puede eliminar esta categoría porque "
                                        + "tiene productos asociados."
                        );
                        return;
                    }

                    // Si no tiene productos, eliminar categoría
                    db.collection("categorias")
                            .document(id)
                            .delete()
                            .addOnSuccessListener(unused -> {

                                Categoria categoria = new Categoria();
                                categoria.setId(id);

                                callback.onSuccess(categoria);
                            })
                            .addOnFailureListener(error ->
                                    callback.onError(
                                            "No se pudo eliminar la categoría: "
                                                    + error.getMessage()
                                    ));
                })
                .addOnFailureListener(error ->
                        callback.onError(
                                "No se pudieron comprobar los productos: "
                                        + error.getMessage()
                        ));
    }
}
