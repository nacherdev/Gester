package com.example.gester.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarioController {

    private CitaDao citadDAO;
    private SimpleDateFormat sdfMes = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    private SimpleDateFormat sdfDia = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public CalendarioController() {
        this.citadDAO = citadDAO;
    }

    //DIAS CON CITA EN UN MES
    public Map<String, Integer> obtenerDiasConCitaDelMes(int anio, int mes, int idUsuario){
        String prefixMes = String.format(Locale.getDefault(), "%04d-%02d",anio, mes);

        List<Cita> citas;
        if(idUsuario ==0){
            citas = citadDAO.obtenerPorMes(prefixMes);
        }else{
            citas = citadDAO.obtenerPorMesYUsuario(prefixMes, idUsuario);
        }

        Map<String, Integer> diasConCitas = new HashMap<>();
        for(Cita cita :citas){
            if("cancelada".equalsIgnoreCase(cita.getEstado())) continue;
            String dia = cita.getFecha();
            diasConCitas.put(dia, diasConCitas.getOrDefault(dia,0)+1);
        }
        return diasConCitas;
    }

    //DIAS DE UN DIA CONCRETO

    public List<Cita> obtenerCitasDelDia(String fecha, int idUsuario){

    }
}
