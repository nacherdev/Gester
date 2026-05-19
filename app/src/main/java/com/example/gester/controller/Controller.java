package com.example.gester.controller;

import com.example.gester.dao.Dao;
import com.example.gester.dao.models.Cita;
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
}