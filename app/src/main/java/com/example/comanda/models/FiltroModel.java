package com.example.comanda.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para transportar y evaluar los criterios de filtrado seleccionados por el usuario.
 */
public class FiltroModel implements Serializable {

    private String tipoComida;
    private List<String> ingredientes;
    private Double precioMin;
    private Double precioMax;
    private String tiempoPrep;

    public FiltroModel() {
        this.ingredientes = new ArrayList<>();
    }

    public String getTipoComida() {
        return tipoComida;
    }

    public void setTipoComida(String tipoComida) {
        this.tipoComida = tipoComida;
    }

    public List<String> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<String> ingredientes) {
        this.ingredientes = ingredientes != null ? ingredientes : new ArrayList<>();
    }

    public Double getPrecioMin() {
        return precioMin;
    }

    public void setPrecioMin(Double precioMin) {
        this.precioMin = precioMin;
    }

    public Double getPrecioMax() {
        return precioMax;
    }

    public void setPrecioMax(Double precioMax) {
        this.precioMax = precioMax;
    }

    public String getTiempoPrep() {
        return tiempoPrep;
    }

    public void setTiempoPrep(String tiempoPrep) {
        this.tiempoPrep = tiempoPrep;
    }

    public boolean isActivo() {
        boolean hasTipo = tipoComida != null && !tipoComida.trim().isEmpty();
        boolean hasIng = ingredientes != null && !ingredientes.isEmpty();
        boolean hasMin = precioMin != null && precioMin > 0;
        boolean hasMax = precioMax != null && precioMax > 0;
        boolean hasTiempo = tiempoPrep != null && !tiempoPrep.trim().isEmpty();
        return hasTipo || hasIng || hasMin || hasMax || hasTiempo;
    }

    /**
     * Evalúa si un producto cumple con los criterios de filtrado establecidos.
     */
    public boolean coincideConProducto(Producto p) {
        if (p == null) return false;

        // 1. Rango de precio
        int precioCentavos = p.getPrecioCentavos();
        if (precioMin != null && precioMin > 0) {
            int minCentavos = (int) Math.round(precioMin * 100);
            if (precioCentavos < minCentavos) return false;
        }
        if (precioMax != null && precioMax > 0) {
            int maxCentavos = (int) Math.round(precioMax * 100);
            if (precioCentavos > maxCentavos) return false;
        }

        String textoBusqueda = ((p.getNombre() != null ? p.getNombre() : "") + " " +
                (p.getDescripcion() != null ? p.getDescripcion() : "")).toLowerCase();

        // 2. Tipo de comida
        if (tipoComida != null && !tipoComida.trim().isEmpty()) {
            String tipoNorm = tipoComida.trim().toLowerCase();
            if (!textoBusqueda.contains(tipoNorm)) {
                // Validación de categoría coincidente
                String catId = p.getCategoriaId() != null ? p.getCategoriaId().toLowerCase() : "";
                if (!catId.contains(tipoNorm)) {
                    return false;
                }
            }
        }

        // 3. Ingredientes (todos los seleccionados deben estar presentes en el nombre o descripción)
        if (ingredientes != null && !ingredientes.isEmpty()) {
            for (String ing : ingredientes) {
                if (ing != null && !ing.trim().isEmpty()) {
                    String ingNorm = ing.trim().toLowerCase();
                    if (!textoBusqueda.contains(ingNorm)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void limpiar() {
        this.tipoComida = null;
        if (this.ingredientes != null) {
            this.ingredientes.clear();
        }
        this.precioMin = null;
        this.precioMax = null;
        this.tiempoPrep = null;
    }
}
