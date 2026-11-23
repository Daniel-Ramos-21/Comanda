
package com.example.comanda.Data;

import com.example.comanda.models.Producto;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductoRepository {

    private FirebaseFirestore db;

    // Callback para un producto
    public interface ProductoCallback {
        void onSuccess(Producto producto);
        void onError(String mensaje);
    }

    // Callback para una lista de productos
    public interface ListaProductosCallback {
        void onSuccess(List<Producto> productos);
        void onError(String mensaje);
    }

    // Constructor
    public ProductoRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Crear producto
    public void crearProducto(Producto producto,
                              ProductoCallback callback) {

        if (producto.getCategoriaId() == null ||
                producto.getCategoriaId().trim().isEmpty()) {

            callback.onError("Debes seleccionar una categoría.");
            return;
        }

        // Comprobar que categoría exista
        db.collection("categorias")
                .document(producto.getCategoriaId())
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        callback.onError(
                                "La categoría seleccionada no existe."
                        );
                        return;
                    }

                    String id = db.collection("productos")
                            .document()
                            .getId();

                    producto.setId(id);

                    db.collection("productos")
                            .document(id)
                            .set(producto)
                            .addOnSuccessListener(unused ->
                                    callback.onSuccess(producto)
                            )
                            .addOnFailureListener(error ->
                                    callback.onError(
                                            "No se pudo crear el producto: "
                                                    + error.getMessage()
                                    ));
                })
                .addOnFailureListener(error ->
                        callback.onError(
                                "No se pudo verificar la categoría: "
                                        + error.getMessage()
                        ));
    }

    // Obtener todos los productos
    public void obtenerProductos(ListaProductosCallback callback) {

        db.collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<Producto> productos = new ArrayList<>();

                    for (QueryDocumentSnapshot document :
                            queryDocumentSnapshots) {

                        Producto producto =
                                document.toObject(Producto.class);

                        producto.setId(document.getId());

                        productos.add(producto);
                    }

                    callback.onSuccess(productos);
                })
                .addOnFailureListener(error ->
                        callback.onError(
                                "No se pudieron obtener los productos: "
                                        + error.getMessage()
                        ));
    }

    // Actualizar producto
    public void actualizarProducto(Producto producto,
                                   ProductoCallback callback) {

        if (producto.getId() == null ||
                producto.getId().trim().isEmpty()) {

            callback.onError("El producto no tiene un ID válido.");
            return;
        }

        if (producto.getCategoriaId() == null ||
                producto.getCategoriaId().trim().isEmpty()) {

            callback.onError("Debes seleccionar una categoría.");
            return;
        }

        // Comprobar que la categoría exista
        db.collection("categorias")
                .document(producto.getCategoriaId())
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        callback.onError(
                                "La categoría seleccionada no existe."
                        );
                        return;
                    }

                    db.collection("productos")
                            .document(producto.getId())
                            .update(
                                    "nombre", producto.getNombre(),
                                    "descripcion", producto.getDescripcion(),
                                    "precioCentavos", producto.getPrecioCentavos(),
                                    "categoriaId", producto.getCategoriaId(),
                                    "imagenUrl", producto.getImagenUrl(),
                                    "disponible", producto.isDisponible()
                            )
                            .addOnSuccessListener(unused ->
                                    callback.onSuccess(producto)
                            )
                            .addOnFailureListener(error ->
                                    callback.onError(
                                            "No se pudo actualizar el producto: "
                                                    + error.getMessage()
                                    ));
                })
                .addOnFailureListener(error ->
                        callback.onError(
                                "No se pudo verificar la categoría: "
                                        + error.getMessage()
                        ));
    }

    // Eliminar producto
    public void eliminarProducto(String id,
                                 ProductoCallback callback) {

        if (id == null || id.trim().isEmpty()) {
            callback.onError("El ID del producto no es válido.");
            return;
        }

        db.collection("productos")
                .document(id)
                .delete()
                .addOnSuccessListener(unused -> {

                    Producto producto = new Producto();
                    producto.setId(id);

                    callback.onSuccess(producto);
                })
                .addOnFailureListener(error ->
                        callback.onError(
                                "No se pudo eliminar el producto: "
                                        + error.getMessage()
                        ));
    }
}
