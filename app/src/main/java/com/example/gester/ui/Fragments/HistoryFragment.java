package com.example.gester.ui.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HistoryFragment extends Fragment {

    private TableLayout tableHistorial;
    private Controller controller;
    private ArrayList<Cita> listaHistorial;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        tableHistorial = root.findViewById(R.id.tableHistorial);

        controller = Controller.getInstancia();
        listaHistorial = new ArrayList<>();

        cargarDatosDelHistorial();

        return root;
    }

    private void cargarDatosDelHistorial() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ArrayList<Cita> todas = controller.getCitas();

            handler.post(() -> {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                if (todas != null && !todas.isEmpty()) {
                    listaHistorial.clear();
                    listaHistorial.addAll(todas);

                    Collections.sort(listaHistorial, (c1, c2) ->
                            c2.getFechaDeCreacion().compareTo(c1.getFechaDeCreacion())
                    );

                    pintarTabla(listaHistorial);
                } else {
                    Toast.makeText(getContext(), "Historial vacío", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void pintarTabla(ArrayList<Cita> citas) {
        int filasActuales = tableHistorial.getChildCount();
        if (filasActuales > 1) {
            tableHistorial.removeViews(1, filasActuales - 1);
        }

        for (Cita cita : citas) {
            TableRow fila = new TableRow(getContext());
            fila.setPadding(0, 20, 0, 20);

            TextView tvCreada = new TextView(getContext());
            tvCreada.setText(cita.getFechaDeCreacion());
            tvCreada.setTextColor(Color.BLACK);
            tvCreada.setTextSize(14);

            TextView tvCliente = new TextView(getContext());
            String nombreFull = cita.getUsuario().getNombre() + " " + cita.getUsuario().getApellidos();
            tvCliente.setText(nombreFull);
            tvCliente.setTextColor(Color.BLACK);
            tvCliente.setTextSize(14);

            TextView tvInfoCita = new TextView(getContext());
            String info = cita.getFecha() + "\n" + cita.getHora();
            tvInfoCita.setText(info);
            tvInfoCita.setTextColor(Color.GRAY);
            tvInfoCita.setTextSize(12);

            fila.addView(tvCreada);
            fila.addView(tvCliente);
            fila.addView(tvInfoCita);

            tableHistorial.addView(fila);
        }
    }
}