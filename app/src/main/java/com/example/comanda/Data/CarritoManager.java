package com.example.comanda.Data;

import androidx.credentials.webauthn.Cbor;

import com.example.comanda.models.ItemCarrito;
import com.example.comanda.models.Producto;

import java.util.ArrayList;
import java.util.List;

public class CarritoManager {

    private static CarritoManager instancia;

    private final List<ItemCarrito> items;

    private CarritoManager(){
        items = new ArrayList<>();
    }

    public static synchronized CarritoManager getInstance(){
        if (instancia == null){
            instancia = new CarritoManager();
        }
        return  instancia;
    }
    public synchronized boolean agregarProducto(
            Producto producto,
            int cantidad,
            String notas
    ){
        if (producto == null
        || producto.getId() == null
        || producto.getId().trim().isEmpty()
        ||cantidad <= 0
        || producto.getPrecioCentavos() <= 0){
            return false;
        }
        String notasLimpias = notas != null ? notas.trim() : "";

        for (ItemCarrito item : items){
            boolean mismoProducto = item.getProductoId().equals(producto.getId());

            boolean mismasNotas = item.getNotas().equals(notasLimpias);

            boolean mismoPrecio = item.getPrecioUnitarioCentavos() == producto.getPrecioCentavos();

            if (mismoProducto && mismasNotas && mismoPrecio){
                item.setCantidad(item.getCantidad() + cantidad);
                return true;
            }
        }
        items.add(new ItemCarrito(
                producto.getId(),
                producto.getNombre(),
                producto.getImagenUrl(),
                cantidad,
                producto.getPrecioCentavos(),
                notasLimpias
        ));
        return true;
    }

    public synchronized List<ItemCarrito> obtenerItems(){
        List<ItemCarrito> copia = new ArrayList<>();

        for (ItemCarrito item : items){
            copia.add(item.copiar());
        }
        return copia;
    }

    public synchronized int obtenerCatidadTotal(){
        int cantidadTotal = 0;

        for (ItemCarrito item : items){
            cantidadTotal += item.getCantidad();
        }
        return cantidadTotal;
    }

    public synchronized long obtenerTotalCentavos(){
        long total = 0;
        for (ItemCarrito item : items){
            total += item.getSubtotalCentavos();
        }
        return total;
    }

    public synchronized  void eliminarProducto(String productoId){
        items.removeIf(item -> item.getProductoId().equals(productoId)
        );
    }

    public synchronized  void limpiarCarrito(){
        items.clear();
    }

    public synchronized boolean estaVacio(){
        return items.isEmpty();
    }

}
