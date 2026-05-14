package com.example.gester;

import com.example.gester.dao.models.Cita;
import com.example.gester.dao.Dao;
import org.junit.Test;
import java.util.List;

public class DaoTest {
    @Test
    public void probarConexion() {        Dao dao = new Dao("gester", "admin_gester", "IasenCopysen67");
        List<Cita> lista = dao.historialDeCitas();

        System.out.println(lista);
    }
}