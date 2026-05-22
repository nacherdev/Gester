package com.example.gester.controller;

import com.example.gester.dao.Dao;
import com.example.gester.dao.models.Cita;
import com.example.gester.dao.models.Notificacion;
import com.example.gester.dao.models.Servicio;
import com.example.gester.dao.models.Usuario;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class Controller {

    private static Controller instancia;
    private volatile Dao d = null;

    private Controller() { }

    public static synchronized Controller getInstancia() {
        if (instancia == null) {
            instancia = new Controller();
        }
        return instancia;
    }

    public boolean conectarBBDD(String nombrebd, String usuario, String contrasena){
        d = new Dao(nombrebd, usuario, contrasena);
        return d.conectar() != null;
    }

    public ArrayList<Cita> getCitas(){
        if (d == null) return new ArrayList<>();
        return d.todasLasCitas();
    }

    public ArrayList<Servicio> getServicios(){
        if (d == null) return new ArrayList<>();
        return d.todosLosServicios();
    }

    public ArrayList<Usuario> getUsusarios(){
        if (d == null) return new ArrayList<>();
        return d.todosLosUsuarios();
    }

    public boolean crearCitaYUsuario(String nombre, String apellidos, String DNI,
                                     String fechaNacimiento, int idServicio, String fechaCita,
                                     String horaCita, String fechaCreacion) {
        if (d == null) return false;

        try {
            DNI = DNI.trim();
            DNI = DNI.toUpperCase();
            int idUsuario = d.buscarIdUsuarioPorDni(DNI);
            System.out.println(idUsuario);
            if (idUsuario == -1) {
                boolean usuarioCreado = d.crearUsuario(nombre, apellidos, DNI, fechaNacimiento);

                if (!usuarioCreado) {
                    return false;
                }

                idUsuario = d.buscarIdUsuarioPorDni(DNI);

                if (idUsuario == -1) return false;
            }

            int idCitaExistente = d.buscarIdCita(idUsuario, fechaCita, horaCita);

            if (idCitaExistente != -1) {
                return false;
            }

            boolean citaCreada = d.crearCita(idUsuario, idServicio, fechaCita, horaCita, fechaCreacion);

            return citaCreada;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Cita> obtenerCitasPorFecha(String fecha) {
        if (d == null) {
            System.out.println("Controller: Error, la instancia del DAO es nula.");
            return new ArrayList<>();
        }

        if (fecha != null) {
            fecha = fecha.trim();
        }

        try {
            ArrayList<Cita> listaCitas = d.obtenerCitasPorFecha(fecha);

            if (listaCitas == null) {
                return new ArrayList<>();
            }

            return listaCitas;

        } catch (Exception e) {
            System.out.println("Controller: Error imprevisto al obtener las citas por fecha.");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<String> obtenerHorasOcupadasPorFecha(String fecha) {
        if (d == null) return new ArrayList<>();
        return d.obtenerHorasOcupadasPorFecha(fecha);
    }

    public boolean registrarNotificacion(String titulo, String mensaje) {

        if (d == null) return false;
        return d.registrarNotificacion(titulo, mensaje);
    }

    public ArrayList<Notificacion> obtenerNotificaciones() {
        if (d == null) return new ArrayList<>();
        return d.obtenerNotificaciones();
    }

    public boolean eliminarNotificacion(int id) {
        if (d == null) return false;
        return d.eliminarNotificacion(id);
    }

    public boolean eliminarTodasLasNotificacion() {
        if (d == null) return false;
        return d.eliminarTodasLasNotificacion();
    }

    public void verificarCitasProximas() {
        if (d != null) {
            d.verificarCitasProximas();
        }
    }

    public Cita obtenerCitaPorId(int idCita) {
        if (d == null) return null;
        return d.obtenerCitaPorId(idCita);
    }

    public boolean actualizarCita(int idCita, int idServicio, String fecha, String hora) {
        if (d == null) return false;
        return d.actualizarCita(idCita, idServicio, fecha, hora);
    }

    public boolean eliminarCita(int idCita) {
        if (d == null) return false;
        return d.eliminarCita(idCita);
    }

    public ArrayList<String> obtenerHorasDisponibles(String fecha) {
        ArrayList<String> ocupadas = obtenerHorasOcupadasPorFecha(fecha);
        ArrayList<String> limpiasOcupadas = new ArrayList<>();
        ArrayList<String> disponibles = new ArrayList<>();

        for (String h : ocupadas) {
            if (h != null && h.length() >= 5) {
                limpiasOcupadas.add(h.substring(0, 5));
            }
        }

        String[] jornada = {"09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30"};
        for (String h : jornada) {
            if (!limpiasOcupadas.contains(h)) {
                disponibles.add(h);
            }
        }
        return disponibles;
    }

    public Usuario obtenerUsuarioPorDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return null;
        }
        return d.buscarPorDni(dni);
    }
}