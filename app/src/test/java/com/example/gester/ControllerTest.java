package com.example.gester;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.gester.dao.models.*;
import com.example.gester.controller.Controller;

import org.junit.Test;

public class ControllerTest {

    @Test
    public void conectarbbdd(){
        Controller c = Controller.getInstancia();
        assertTrue(c.conectarBBDD("gester","admin_gester","IasenCopysen67"));
        System.out.println(c.getCitas());
    }

    @Test
    public void crearNotificacion() {
        Controller c = Controller.getInstancia();
        c.conectarBBDD("gester","admin_gester","IasenCopysen67");
        assertTrue(c.registrarNotificacion("Hola", "Patata"));
        for (Notificacion n : c.obtenerNotificaciones()) {
            System.out.println(n);
        }
    }
}
