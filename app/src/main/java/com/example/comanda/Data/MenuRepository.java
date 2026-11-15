package com.example.comanda.Data;

import com.example.comanda.models.Categoria;
import com.example.comanda.models.Producto;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio encargado de gestionar la consulta de productos y categorías desde Firebase Firestore.
 */
public class MenuRepository {

    private final FirebaseFirestore db;

    public interface OnProductosCallback {
        void onSuccess(List<Producto> productos);
        void onError(Exception e);
    }

    public interface OnCategoriasCallback {
        void onSuccess(List<Categoria> categorias);
        void onError(Exception e);
    }

    public MenuRepository() {
        this.db = FirebaseManager.getDb();
    }

    /**
     * Consulta los productos registrados en la colección 'productos' de Firestore.
     * Solo retorna productos donde disponible == true.
     */
    public void obtenerProductos(OnProductosCallback callback) {
        db.collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Producto> productos = new ArrayList<>();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Producto p = doc.toObject(Producto.class);
                            if (p != null) {
                                if (p.getId() == null || p.getId().isEmpty()) {
                                    p.setId(doc.getId());
                                }
                                if (p.isDisponible()) {
                                    productos.add(p);
                                }
                            }
                        }
                    }
                    callback.onSuccess(productos);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Consulta las categorías registradas en la colección 'categorias' de Firestore.
     */
    public void obtenerCategorias(OnCategoriasCallback callback) {
        db.collection("categorias")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Categoria> categorias = new ArrayList<>();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Categoria c = doc.toObject(Categoria.class);
                            if (c != null) {
                                if (c.getId() == null || c.getId().isEmpty()) {
                                    c.setId(doc.getId());
                                }
                                categorias.add(c);
                            }
                        }
                    }
                    callback.onSuccess(categorias);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Proporciona productos locales curados como respaldo temporal si la base de datos
     * de Firestore aún no tiene registros en la colección 'productos'.
     */
    public List<Producto> obtenerProductosLocalesRespaldo() {
        List<Producto> lista = new ArrayList<>();

        Producto p1 = new Producto(
                "prod_1",
                "Pupusas de Queso y Loroco",
                350,
                "Tradicionales pupusas hechas a mano con masa de maíz artesanal, queso derretido y flor de loroco fresca. Incluye curtido y salsa.",
                "cat_desayunos",
                "local:pupusas",
                true
        );

        Producto p2 = new Producto(
                "prod_2",
                "Horchata Artesanal de Morro",
                200,
                "Bebida tradicional fría preparada con semillas de morro tostadas, cacao y canela dulce en vaso artesanal.",
                "cat_bebidas",
                "local:horchata",
                true
        );

        Producto p3 = new Producto(
                "prod_3",
                "Tacos de Autor con Aguacate",
                450,
                "Tortillas de maíz recién comaleadas, carne marinada al fuego, guacamole cremoso y cilantro fresco.",
                "cat_almuerzos",
                "local:tacos",
                true
        );

        Producto p4 = new Producto(
                "prod_4",
                "Bowl de Brotes y Salmón Marinado",
                600,
                "Arroz integral, láminas de salmón curado con semillas de sésamo, huevo poché y aguacate en láminas.",
                "cat_almuerzos",
                "local:bowl",
                true
        );

        lista.add(p1);
        lista.add(p2);
        lista.add(p3);
        lista.add(p4);
        return lista;
    }
}
