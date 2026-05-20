package com.example.gester.ui.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gester.R;
import com.example.gester.controller.Controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddFragment extends Fragment {

    private Button btnSeleccionarFecha;
    private TextView tvFechaSeleccionada;
    private Spinner spinnerHoras;

    private Controller controller;
    private String fechaFinalMsql = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add, container, false);

        btnSeleccionarFecha = root.findViewById(R.id.btnSeleccionarFecha);
        tvFechaSeleccionada = root.findViewById(R.id.tvFechaSeleccionada);
        spinnerHoras = root.findViewById(R.id.spinnerHoras);

        controller = Controller.getInstancia();

        btnSeleccionarFecha.setOnClickListener(v -> mostrarMinicalendario());

        return root;
    }

    private void mostrarMinicalendario() {
        Calendar calendar = Calendar.getInstance();
        int anyo = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            int mesReal = month + 1;

            fechaFinalMsql = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, mesReal, dayOfMonth);
            tvFechaSeleccionada.setText(dayOfMonth + "/" + mesReal + "/" + year);

            actualizarSpinnerHorasLibres(fechaFinalMsql);

        }, anyo, mes, dia);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void actualizarSpinnerHorasLibres(String fecha) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ArrayList<String> ocupadas = controller.obtenerHorasOcupadasPorFecha(fecha);
            ArrayList<String> todasLasHoras = generarHorario();

            todasLasHoras.removeAll(ocupadas);

            handler.post(() -> {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                if (todasLasHoras.isEmpty()) {
                    todasLasHoras.add("Día completo (Sin horas libres)");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, todasLasHoras);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerHoras.setAdapter(adapter);
            });
        });
    }

    private ArrayList<String> generarHorario() {
        ArrayList<String> horario = new ArrayList<>();
        horario.add("09:00"); horario.add("09:30");
        horario.add("10:00"); horario.add("10:30");
        horario.add("11:00"); horario.add("11:30");
        horario.add("12:00"); horario.add("12:30");
        horario.add("13:00"); horario.add("13:30");
        horario.add("16:00"); horario.add("16:30");
        horario.add("17:00"); horario.add("17:30");
        horario.add("18:00"); horario.add("18:30");
        horario.add("19:00"); horario.add("19:30");
        horario.add("20:00");
        return horario;
    }
}