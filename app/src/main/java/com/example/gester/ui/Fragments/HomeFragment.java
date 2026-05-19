package com.example.gester.ui.Fragments;

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
import com.example.gester.controller.Controller;
import com.example.gester.dao.models.Cita;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private CalendarView calendarView;
    private TextView txtBienvenida;
    private TableLayout tablaCitas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtBienvenida = view.findViewById(R.id.textView2);
        calendarView = view.findViewById(R.id.calendarView);
        tablaCitas = view.findViewById(R.id.tablaCitas);

        String nombreFinal = "Usuario";
        if (getArguments() != null) {
            nombreFinal = getArguments().getString("nombre");
        }
        txtBienvenida.setText("¡" + nombreFinal + ", bienvenido!");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                Toast.makeText(getContext(), "Citas para el: " + fecha, Toast.LENGTH_SHORT).show();
            }
        });

        cargarCitasDesdeSegundoPlano();
    }

    private void cargarCitasDesdeSegundoPlano() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<Cita> listaCitas = new ArrayList<>();
                try {
                    Controller c = Controller.getInstancia();
                    listaCitas = c.getCitas();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ArrayList<Cita> finalListaCitas = listaCitas;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isAdded()) return;

                        if (finalListaCitas != null && !finalListaCitas.isEmpty()) {
                            llenarTabla(finalListaCitas);
                        } else {
                            Toast.makeText(getContext(), "No se encontraron citas en la base de datos.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void llenarTabla(ArrayList<Cita> citas) {
        int filasExistentes = tablaCitas.getChildCount();
        if (filasExistentes > 1) {
            tablaCitas.removeViews(1, filasExistentes - 1);
        }

        for (Cita cita : citas) {
            TableRow fila = new TableRow(getContext());
            fila.setPadding(8, 8, 8, 8);

            TextView txtCliente = new TextView(getContext());
            txtCliente.setText(cita.getUsuario().getNombre());
            txtCliente.setTextSize(14);

            TextView txtServicio = new TextView(getContext());
            txtServicio.setText(cita.getServicio().getNombreServicio());
            txtServicio.setTextSize(14);

            TextView txtFechaHora = new TextView(getContext());
            txtFechaHora.setText(cita.getFecha() + " " + cita.getHora());
            txtFechaHora.setTextSize(14);

            fila.addView(txtCliente);
            fila.addView(txtServicio);
            fila.addView(txtFechaHora);

            tablaCitas.addView(fila);
        }
    }
}