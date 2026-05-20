package com.example.gester.ui.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gester.R;
import com.example.gester.dao.models.Cita;
import com.example.gester.controller.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private TableLayout tableCitas;
    private TextView tvTituloCitas;

    private Controller controller;
    private ArrayList<Cita> listaCitasDelDia;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = root.findViewById(R.id.calendarView);
        tableCitas = root.findViewById(R.id.tableCitas);
        tvTituloCitas = root.findViewById(R.id.tvTituloCitas);

        controller = Controller.getInstancia();
        listaCitasDelDia = new ArrayList<>();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                int mesReal = month + 1;
                String fechaSeleccionada = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, mesReal, dayOfMonth);

                tvTituloCitas.setText("Citas del día: " + dayOfMonth + "/" + mesReal + "/" + year);
                buscarCitasPorFecha(fechaSeleccionada);
            }
        });

        cargarDiaActualDeInicio();

        return root;
    }

    private void buscarCitasPorFecha(String fecha) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ArrayList<Cita> citasFiltradas = controller.obtenerCitasPorFecha(fecha);

            handler.post(() -> {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                listaCitasDelDia.clear();
                if (citasFiltradas != null && !citasFiltradas.isEmpty()) {
                    listaCitasDelDia.addAll(citasFiltradas);
                    Collections.sort(listaCitasDelDia);
                    llenarTabla(listaCitasDelDia);
                } else {
                    limpiarTablaDeDatos();
                    Toast.makeText(getContext(), "No hay citas programadas para este día", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void llenarTabla(ArrayList<Cita> citas) {
        limpiarTablaDeDatos();

        for (Cita cita : citas) {
            TableRow fila = new TableRow(getContext());
            fila.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            fila.setPadding(8, 16, 8, 16);

            TextView tvHora = new TextView(getContext());
            tvHora.setText(cita.getHora());
            tvHora.setTextColor(Color.BLACK);

            TextView tvCliente = new TextView(getContext());
            String nombreCompleto = cita.getUsuario().getNombre() + " " + cita.getUsuario().getApellidos();
            tvCliente.setText(nombreCompleto);
            tvCliente.setTextColor(Color.BLACK);

            TextView tvServicio = new TextView(getContext());
            tvServicio.setText(cita.getServicio().getNombreServicio());
            tvServicio.setTextColor(Color.BLACK);

            fila.addView(tvHora);
            fila.addView(tvCliente);
            fila.addView(tvServicio);

            tableCitas.addView(fila);
        }
    }

    private void limpiarTablaDeDatos() {
        int totalFilas = tableCitas.getChildCount();
        if (totalFilas > 1) {
            tableCitas.removeViews(1, totalFilas - 1);
        }
    }

    private void cargarDiaActualDeInicio() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String hoy = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                cal.get(java.util.Calendar.YEAR),
                (cal.get(java.util.Calendar.MONTH) + 1),
                cal.get(java.util.Calendar.DAY_OF_MONTH));

        tvTituloCitas.setText("Citas de hoy");
        buscarCitasPorFecha(hoy);
    }
}