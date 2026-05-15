package com.example.gester;

import com.example.gester.dao.models.Cita;
import com.example.gester.dao.Dao;
import com.example.gester.dao.models.Servicio;
import com.example.gester.dao.models.Usuario;

import org.junit.Test;
import java.util.ArrayList;

public class DaoTest {
    @Test
    public void probarConexion() {
        Dao dao = new Dao("dbName", "dbUser", "dbPass");
        ArrayList<Cita> lista = dao.todasLasCitas();

        System.out.println(lista);
    }

    @Test
    public void probarGetUsuarios() {
        Dao dao = new Dao("gester", "admin_gester", "IasenCopysen67");
        ArrayList<Usuario> lista = dao.todosLosUsuarios();

        System.out.println(lista);
    }

    @Test
    public void probarGetServicios() {
        Dao dao = new Dao("gester", "admin_gester", "IasenCopysen67");
        ArrayList<Servicio> lista = dao.todosLosServicios();

        System.out.println(lista);
    }
}