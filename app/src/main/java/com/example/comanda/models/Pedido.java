package com.example.comanda.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pedido {
    private String id;
    private String cliente;
    private  String mesa;
    private String notas;
    private List<DetallePedido> detalles;
    private int totalCentavos;
    private  String estado;
    private Date fechaCreacion;

    public Pedido() {
        detalles = new ArrayList<>();
    }

    public Pedido(String id, String cliente, String mesa,
                  String notas, List<DetallePedido> detalles,
                  int totalCentavos, String estado, Date fechaCreacion) {
        this.id = id;
        this.cliente = cliente;
        this.mesa = mesa;
        this.notas = notas;
        this.detalles = detalles;
        this.totalCentavos = totalCentavos;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    public int getTotalCentavos() {
        return totalCentavos;
    }

    public void setTotalCentavos(int totalCentavos) {
        this.totalCentavos = totalCentavos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
