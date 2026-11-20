package com.example.comanda.Data;

import com.example.comanda.models.Pedido;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PedidosRepository {

    private final FirebaseFirestore db;

    public interface OnPedidoRegistradoCallback{
        void onSuccess(String pedidoId);
        void onError(Exception e);
    }

    public interface OnPedidosCallback{
        void onSuccess(List<Pedido> pedidos);
        void onError(Exception e);
    }

    public interface OnOperacionCallback{
        void onSuccess();
        void onError(Exception e);
    }

    public PedidosRepository(){
        this.db = FirebaseManager.getDb();
    }

    public void registrarPedido(Pedido pedido,
                               OnPedidoRegistradoCallback callback){
        if (pedido == null){
            callback.onError(
                    new IllegalArgumentException("El pedido no puede ser nulo")
            );
            return;
        }

        String pedidoId = db.collection("pedidos").document().getId();

        pedido.setId(pedidoId);

        if(pedido.getEstado() == null ||
            pedido.getEstado().trim().isEmpty()){
            pedido.setEstado("Pendiente");
        }

        if (pedido.getFechaCreacion() == null){
            pedido.setFechaCreacion(new Date());
        }

        db.collection("pedidos")
                .document(pedidoId)
                .set(pedido)
                .addOnSuccessListener(unused ->
                        callback.onSuccess(pedidoId)
                )
                .addOnFailureListener(callback::onError);
    }

    public void obtenerPedidos(OnPedidosCallback callback){
        db.collection("pedidos")
                .orderBy("fechaCreacion",
                        Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Pedido> pedidos = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        Pedido pedido = doc.toObject(Pedido.class);

                        if(pedido != null){
                            if (pedido.getId()==null ||
                                pedido.getId().isEmpty()){
                                pedido.setId(doc.getId());
                            }
                            pedidos.add(pedido);
                        }
                    }
                    callback.onSuccess(pedidos);
                })
                .addOnFailureListener(callback::onError);
    }

    public void actualizarEstado(String pedidoId,
                                 String nuevoEstado,
                                 OnOperacionCallback callback){
        if (pedidoId == null || pedidoId.trim().isEmpty()){
            callback.onError(
                    new IllegalArgumentException("El ID del pedido es obligatorio.")
            );
            return;
        }
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()){
            callback.onError(
                    new IllegalArgumentException("El estado es obligatorio.")
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
