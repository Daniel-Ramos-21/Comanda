package com.example.comanda.models;

public class Producto {
    private String id;
    private  String nombre;
    private  String descripcion;
    private int precioCentavos;
    private String categoriaId;
    private String imagenUrl;
    private boolean disponible;

    public Producto() {
    }

    public Producto(String id, String nombre, int precioCentavos,
                    String descripcion, String categoriaId,
                    String imagenUrl, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.precioCentavos = precioCentavos;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
        this.imagenUrl = imagenUrl;
        this.disponible = disponible;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecioCentavos() {
        return precioCentavos;
    }

    public void setPrecioCentavos(int precioCentavos) {
        this.precioCentavos = precioCentavos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(String categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
