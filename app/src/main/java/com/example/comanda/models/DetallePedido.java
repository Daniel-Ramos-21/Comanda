
package com.example.comanda.models;

public class DetallePedido {

    private String productoId;
    private String nombre;
    private String imageUrl;
    private int cantidad;
    private int precioUnitario;
    private String notas;

    public DetallePedido() {
    }

    public DetallePedido(
            String productoId,
            String nombre,
            String imageUrl,
            int cantidad,
            int precioUnitario
    ) {
        this(
                productoId,
                nombre,
                imageUrl,
                cantidad,
                precioUnitario,
                ""
        );
    }

    public DetallePedido(
            String productoId,
            String nombre,
            String imageUrl,
            int cantidad,
            int precioUnitario,
            String notas
    ) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.imageUrl = imageUrl;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.notas = notas;
    }

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(int precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public long getSubtotalCentavos() {
        return (long) precioUnitario * cantidad;
    }
}
