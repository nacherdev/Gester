package com.example.gester.controller;

import com.example.gester.dao.Dao;
import com.example.gester.dao.models.Cita;

import java.util.ArrayList;

public class Controller {
    private Dao d = null;
        public boolean conectarBBDD(String nombrebd, String usuario, String contrasena){
            d = new Dao(nombrebd,usuario,contrasena);
            if(d.conectar() == false){
                return false;
            }else {
                return true;
            }
        }

        public ArrayList<Cita> getCitas(){
            return d.todasLasCitas();
        }


}
