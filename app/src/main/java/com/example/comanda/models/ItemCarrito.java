
package com.example.comanda.models;

public class ItemCarrito {

    private String productoId;
    private String nombre;
    private String imagenUrl;
    private int cantidad;
    private int precioUnitarioCentavos;
    private String notas;

    public ItemCarrito(
            String productoId,
            String nombre,
            String imagenUrl,
            int cantidad,
            int precioUnitarioCentavos,
            String notas
    ) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
        this.cantidad = cantidad;
        this.precioUnitarioCentavos = precioUnitarioCentavos;
        this.notas = notas;
    }

    public String getProductoId() {
        return productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getPrecioUnitarioCentavos() {
        return precioUnitarioCentavos;
    }

    public String getNotas() {
        return notas;
    }

    public long getSubtotalCentavos() {
        return (long) precioUnitarioCentavos * cantidad;
    }

    public ItemCarrito copiar() {
        return new ItemCarrito(
                productoId,
                nombre,
                imagenUrl,
                cantidad,
                precioUnitarioCentavos,
                notas
        );
    }
}
