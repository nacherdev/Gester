package com.example.gester;

import static org.junit.Assert.assertTrue;

import com.example.gester.controller.Controller;

import org.junit.Test;

public class ControllerTest {

    @Test
    public void conectarbbdd(){
        Controller c = new Controller();
        assertTrue(c.conectarBBDD("gester","admin_gester","IasenCopysen67"));
        System.out.println(c.getCitas());
    }
}
