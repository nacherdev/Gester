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

public class HomeFragment extends Fragment {

    private TextView tvBienvenidaHome;
    private TableLayout tableProximasCitas;
    private Controller controller;
    private ArrayList<Cita> listaCitasHome;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        tvBienvenidaHome = root.findViewById(R.id.tvBienvenidaHome);
        tableProximasCitas = root.findViewById(R.id.tableProximasCitas);

        controller = Controller.getInstancia();
        listaCitasHome = new ArrayList<>();

        if (getArguments() != null) {
            String nombreUsuario = getArguments().getString("nombre");
            if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                tvBienvenidaHome.setText("¡Bienvenido, " + nombreUsuario + "!");
            }
        }

        cargarCitasCincoDias();

        return root;
    }

    private void cargarCitasCincoDias() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ArrayList<Cita> proximas = controller.obtenerCitasProximosDias();

            handler.post(() -> {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                if (proximas != null && !proximas.isEmpty()) {
                    listaCitasHome.clear();
                    listaCitasHome.addAll(proximas);

                    Collections.sort(listaCitasHome);

                    pintarTablaHome(listaCitasHome);
                }
            });
        });
    }

    private void pintarTablaHome(ArrayList<Cita> citas) {
        int filasActuales = tableProximasCitas.getChildCount();
        if (filasActuales > 1) {
            tableProximasCitas.removeViews(1, filasActuales - 1);
        }

        for (Cita cita : citas) {
            TableRow fila = new TableRow(getContext());
            fila.setPadding(8, 16, 8, 16);

            TextView tvFechaHora = new TextView(getContext());
            String fechaCorta = cita.getFecha().substring(5);
            tvFechaHora.setText(fechaCorta + " (" + cita.getHora() + ")");
            tvFechaHora.setTextColor(Color.BLACK);

            TextView tvCliente = new TextView(getContext());
            tvCliente.setText(cita.getUsuario().getNombre());
            tvCliente.setTextColor(Color.BLACK);

            TextView tvServicio = new TextView(getContext());
            tvServicio.setText(cita.getServicio().getNombreServicio());
            tvServicio.setTextColor(Color.DKGRAY);

            fila.addView(tvFechaHora);
            fila.addView(tvCliente);
            fila.addView(tvServicio);

            tableProximasCitas.addView(fila);
        }
    }
}