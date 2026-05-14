package com.example.gester.dao.models;

public class Servicio {
    private int id;
    private String nombreServicio;
    private int duracion;
    private double precio;

    public Servicio(int id, String nombreServicio, int duracion, double precio) {
        this.id = id;
        this.nombreServicio = nombreServicio;
        this.duracion = duracion;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Servicio{" +
                "id=" + id +
                ", nombreServicio='" + nombreServicio + '\'' +
                ", duracion=" + duracion +
                ", precio=" + precio +
                '}';
    }
}
