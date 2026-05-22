package com.example.gester.dao.models;

public class Notificacion {
    private int id;
    private String titulo;
    private String mensaje;
    private String fechaEnvio;

    public Notificacion(int id, String titulo, String mensaje, String fechaEnvio) {
        this.id = id;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", fechaEnvio='" + fechaEnvio + '\'' +
                '}';
    }
}