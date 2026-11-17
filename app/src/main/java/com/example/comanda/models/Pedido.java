package com.example.comanda.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pedido {
    private String id;
    private  String usuarioId;
    private String cliente;
    private  String mesa;

    private String tipoEntrega;
    private  String direccionEntrega;
    private String notas;
    private List<DetallePedido> detalles;
    private int totalCentavos;
    private  String estado;
    private String metodoPago;
    private String estadoPago;
    private Date fechaCreacion;

    public Pedido() {
        detalles = new ArrayList<>();
    }

    public Pedido(String id, String usuarioId, String cliente,
                  String mesa, String tipoEntrega,
                  String direccionEntrega, String notas,
                  List<DetallePedido> detalles, int totalCentavos,
                  String estado, String metodoPago,
                  String estadoPago, Date fechaCreacion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.cliente = cliente;
        this.mesa = mesa;
        this.tipoEntrega = tipoEntrega;
        this.direccionEntrega = direccionEntrega;
        this.notas = notas;
        this.detalles = detalles;
        this.totalCentavos = totalCentavos;
        this.estado = estado;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
        this.fechaCreacion = fechaCreacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
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

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
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

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
