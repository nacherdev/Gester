package com.example.gester.ui.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryFragment extends Fragment {

    private LinearLayout containerHistorial;
    private Controller controller;
    private ArrayList<Cita> listaHistorial;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        containerHistorial = root.findViewById(R.id.containerHistorial);

        controller = Controller.getInstancia();
        listaHistorial = new ArrayList<>();

        cargarDatosDelHistorial();

        return root;
    }

    private void cargarDatosDelHistorial() {
        if (containerHistorial != null) {
            containerHistorial.removeAllViews();
            TextView tvCargando = new TextView(getContext());
            tvCargando.setText("Buscando registros...");
            tvCargando.setTextSize(15);
            tvCargando.setTextColor(android.graphics.Color.parseColor("#6B7280"));
            tvCargando.setPadding(32, 32, 32, 32);
            tvCargando.setGravity(android.view.Gravity.CENTER);
            containerHistorial.addView(tvCargando);
        }

        executorService.execute(() -> {
            ArrayList<Cita> todas = controller.getCitas();

            mainHandler.post(() -> {
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
                    if (containerHistorial != null) containerHistorial.removeAllViews();
                    Toast.makeText(getContext(), "Historial vacío", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void pintarTabla(ArrayList<Cita> citas) {
        if (containerHistorial == null) return;

        containerHistorial.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (Cita cita : citas) {
            View cardItem = inflater.inflate(R.layout.item_historial, containerHistorial, false);

            TextView tvCitaInfo = cardItem.findViewById(R.id.tvHistorialCita);
            TextView tvCliente = cardItem.findViewById(R.id.tvHistorialCliente);
            TextView tvCreada = cardItem.findViewById(R.id.tvHistorialCreada);

            String infoCita = cita.getFecha() + "\n" + cita.getHora();
            tvCitaInfo.setText(infoCita);

            String nombreFull = cita.getUsuario().getNombre() + " " + cita.getUsuario().getApellidos();
            tvCliente.setText(nombreFull);

            tvCreada.setText("Creado el: " + cita.getFechaDeCreacion());

            cardItem.setOnClickListener(v -> {
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

            containerHistorial.addView(cardItem);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}