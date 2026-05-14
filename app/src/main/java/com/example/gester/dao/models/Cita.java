package com.example.gester.dao.models;

public class Cita {

    private int id;
    private Usuario usuario;
    private Servicio servicio;
    private String fecha;
    private String hora;
    private boolean estado;
    private String fechaDeCreacion;

    public Cita(int id, Usuario usuario, Servicio servicio, String fecha, String hora, boolean estado, String fechaDeCreacion) {
        this.id = id;
        this.usuario = usuario;
        this.servicio = servicio;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.fechaDeCreacion = fechaDeCreacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getFechaDeCreacion() {
        return fechaDeCreacion;
    }

    public void setFechaDeCreacion(String fechaDeCreacion) {
        this.fechaDeCreacion = fechaDeCreacion;
    }

    @Override
    public String toString() {
        return "Cita{" +
                "id=" + id +
                ", usuario=" + usuario +
                ", servicio=" + servicio +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                ", estado=" + estado +
                ", fechaDeCreacion='" + fechaDeCreacion + '\'' +
                '}';
    }
}
