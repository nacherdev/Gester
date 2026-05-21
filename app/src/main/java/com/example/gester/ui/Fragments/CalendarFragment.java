package com.example.gester.ui.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
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
    private LinearLayout containerCitasCalendario;
    private TextView tvTituloCitas;

    private Controller controller;
    private ArrayList<Cita> listaCitasDelDia;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = root.findViewById(R.id.calendarView);
        containerCitasCalendario = root.findViewById(R.id.containerCitasCalendario);
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
        if (containerCitasCalendario != null) {
            containerCitasCalendario.removeAllViews();
            TextView tvCargando = new TextView(getContext());
            tvCargando.setText("Buscando...");
            tvCargando.setTextSize(15);
            tvCargando.setTextColor(android.graphics.Color.parseColor("#6B7280"));
            tvCargando.setPadding(32, 32, 32, 32);
            tvCargando.setGravity(android.view.Gravity.CENTER);
            containerCitasCalendario.addView(tvCargando);
        }

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
                    llenarListaTarjetas(listaCitasDelDia);
                } else {
                    limpiarListaDeDatos();
                    Toast.makeText(getContext(), "No hay citas programadas para este día", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void llenarListaTarjetas(ArrayList<Cita> citas) {
        limpiarListaDeDatos();
        if (containerCitasCalendario == null) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (Cita cita : citas) {
            View cardCita = inflater.inflate(R.layout.item_cita_calendario, containerCitasCalendario, false);

            TextView tvHora = cardCita.findViewById(R.id.tvCalCardHora);
            TextView tvCliente = cardCita.findViewById(R.id.tvCalCardCliente);
            TextView tvServicio = cardCita.findViewById(R.id.tvCalCardServicio);

            tvHora.setText(cita.getHora());

            String nombreCompleto = cita.getUsuario().getNombre() + " " + cita.getUsuario().getApellidos();
            tvCliente.setText(nombreCompleto);

            tvServicio.setText(cita.getServicio().getNombreServicio());

            cardCita.setOnClickListener(v -> {
                EditAppointmentFragment editFrag = new EditAppointmentFragment();
                Bundle mochila = new Bundle();
                mochila.putInt("id_cita", cita.getId());
                editFrag.setArguments(mochila);

                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, editFrag)
                            .addToBackStack(null)
                            .commit();
                }
            });

            containerCitasCalendario.addView(cardCita);
        }
    }

    private void limpiarListaDeDatos() {
        if (containerCitasCalendario != null) {
            containerCitasCalendario.removeAllViews();
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