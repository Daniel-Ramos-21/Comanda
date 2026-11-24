
package com.example.comanda.Data;

import com.example.comanda.models.Pedido;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PedidosRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public interface OnPedidoRegistradoCallback {
        void onSuccess(String pedidoId);
        void onError(Exception e);
    }

    public interface OnPedidosCallback {
        void onSuccess(List<Pedido> pedidos);
        void onError(Exception e);
    }

    public interface OnOperacionCallback {
        void onSuccess();
        void onError(Exception e);
    }

    public PedidosRepository() {
        db = FirebaseManager.getDb();
        auth = FirebaseAuth.getInstance();
    }

    public void registrarPedido(
            Pedido pedido,
            OnPedidoRegistradoCallback callback
    ) {

        if (pedido == null) {
            callback.onError(
                    new IllegalArgumentException(
                            "El pedido no puede ser nulo."
                    )
            );
            return;
        }

        FirebaseUser usuario = auth.getCurrentUser();

        if (usuario == null) {
            callback.onError(
                    new IllegalStateException(
                            "Debes iniciar sesión para realizar un pedido."
                    )
            );
            return;
        }

        if (pedido.getDetalles() == null
                || pedido.getDetalles().isEmpty()) {
            callback.onError(
                    new IllegalArgumentException(
                            "El pedido no contiene productos."
                    )
            );
            return;
        }

        String uid = usuario.getUid();

        // El pedido siempre pertenece al usuario autenticado.
        pedido.setUsuarioId(uid);

        if (pedido.getEstado() == null
                || pedido.getEstado().trim().isEmpty()) {
            pedido.setEstado("Pendiente");
        }

        if (pedido.getFechaCreacion() == null) {
            pedido.setFechaCreacion(new Date());
        }

        if (pedido.getMetodoPago() == null
                || pedido.getMetodoPago().trim().isEmpty()) {
            pedido.setMetodoPago("Efectivo");
        }

        if (pedido.getEstadoPago() == null
                || pedido.getEstadoPago().trim().isEmpty()) {
            pedido.setEstadoPago("Pendiente");
        }

        String pedidoId = db.collection("pedidos")
                .document()
                .getId();

        pedido.setId(pedidoId);

        db.collection("pedidos")
                .document(pedidoId)
                .set(pedido)
                .addOnSuccessListener(unused ->
                        callback.onSuccess(pedidoId)
                )
                .addOnFailureListener(callback::onError);
    }


    public void obtenerPedidos(OnPedidosCallback callback) {

        FirebaseUser usuario = auth.getCurrentUser();

        if (usuario == null) {
            callback.onError(
                    new IllegalStateException("No hay una sesión activa.")
            );
            return;
        }

        String uid = usuario.getUid();

        db.collection("pedidos")
                .whereEqualTo("usuarioId", uid)
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Pedido> pedidos = new ArrayList<>();

                    android.util.Log.d(
                            "MUNCHLY_RECIBOS",
                            "Documentos encontrados: " + snapshot.size()
                    );

                    for (QueryDocumentSnapshot doc : snapshot) {

                        try {
                            Pedido pedido = doc.toObject(Pedido.class);

                            if (pedido.getId() == null ||
                                    pedido.getId().isEmpty()) {
                                pedido.setId(doc.getId());
                            }

                            pedidos.add(pedido);

                        } catch (Exception e) {
                            android.util.Log.e(
                                    "MUNCHLY_RECIBOS",
                                    "Error convirtiendo documento: " + doc.getId(),
                                    e
                            );
                        }
                    }

                    // Ordenar por fecha, del más reciente al más antiguo.
                    pedidos.sort((p1, p2) -> {

                        Date fecha1 = p1.getFechaCreacion();
                        Date fecha2 = p2.getFechaCreacion();

                        if (fecha1 == null && fecha2 == null) return 0;
                        if (fecha1 == null) return 1;
                        if (fecha2 == null) return -1;

                        return fecha2.compareTo(fecha1);
                    });

                    android.util.Log.d(
                            "MUNCHLY_RECIBOS",
                            "Pedidos cargados: " + pedidos.size()
                    );

                    callback.onSuccess(pedidos);

                })
                .addOnFailureListener(e -> {

                    android.util.Log.e(
                            "MUNCHLY_RECIBOS",
                            "Error consultando Firestore",
                            e
                    );

                    callback.onError(e);
                });
    }


    public void actualizarEstado(
            String pedidoId,
            String nuevoEstado,
            OnOperacionCallback callback
    ) {

        if (pedidoId == null || pedidoId.trim().isEmpty()) {
            callback.onError(
                    new IllegalArgumentException(
                            "El ID del pedido es obligatorio."
                    )
            );
            return;
        }

        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            callback.onError(
                    new IllegalArgumentException(
                            "El estado es obligatorio."
                    )
            );
            return;
        }

        db.collection("pedidos")
                .document(pedidoId)
                .update("estado", nuevoEstado)
                .addOnSuccessListener(unused ->
                        callback.onSuccess()
                )
                .addOnFailureListener(callback::onError);
    }
}
