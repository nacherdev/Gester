package com.example.gester.controller;

import com.example.gester.dao.Dao;
import com.example.gester.dao.models.Cita;
import com.example.gester.dao.models.Servicio;
import com.example.gester.dao.models.Usuario;

import java.util.ArrayList;

public class Controller {

    private static Controller instancia;
    private Dao d = null;

    private Controller() { }


    public static synchronized Controller getInstancia() {
        if (instancia == null) {
            instancia = new Controller();
        }
        return instancia;
    }

    public boolean conectarBBDD(String nombrebd, String usuario, String contrasena){
        d = new Dao(nombrebd, usuario, contrasena);
        return d.conectar();
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

    public Cita getSingleCita(int id) {
        if (d==null) return null;
        ArrayList<Cita> citas = getCitas();
        if (citas != null) {
            for (Cita c : citas) {
                if (c.getId() == id) {
                    return c;
                }
            }
        }
        return null;
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

    public ArrayList<Cita> obtenerCitasProximosDias() {
        if (d == null) return new ArrayList<>();
        return d.obtenerCitasProximosDias();
    }

    public ArrayList<String> obtenerHorasOcupadasPorFecha(String fecha) {
        if (d == null) return new ArrayList<>();
        return d.obtenerHorasOcupadasPorFecha(fecha);
    }
}