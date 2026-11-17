package com.example.comanda.models;
import java.util.Date;

public class Usuario {
    private  String UID;
    private  String nombre;
    private  String correo;
    private String telefono;
    private Date fechaRegistro;

    public Usuario() {
    }

    public Usuario(String UID, String nombre, String correo,
                   String telefono, Date fechaRegistro) {
        this.UID = UID;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
