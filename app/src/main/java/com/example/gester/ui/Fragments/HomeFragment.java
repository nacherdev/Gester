package com.example.gester.ui.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class HomeFragment extends Fragment {

    private TextView tvBienvenidaHome;
    private LinearLayout containerProximasCitas;
    private Controller controller;
    private ArrayList<Cita> listaCitasHome;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        tvBienvenidaHome = root.findViewById(R.id.tvBienvenidaHome);
        containerProximasCitas = root.findViewById(R.id.containerProximasCitas);

        controller = Controller.getInstancia();
        listaCitasHome = new ArrayList<>();

        if (getArguments() != null) {
            String nombre = getArguments().getString("nombre", "Usuario");
            tvBienvenidaHome.setText("¡Bienvenido, " + nombre.toUpperCase().replace("_", " ") + "!");
        }

        cargarDatosHome();

        return root;
    }

    private void cargarDatosHome() {
        if (containerProximasCitas != null) {
            containerProximasCitas.removeAllViews();
            TextView tvCargando = new TextView(getContext());
            tvCargando.setText("Buscando citas...");
            tvCargando.setTextSize(15);
            tvCargando.setTextColor(android.graphics.Color.parseColor("#6B7280"));
            tvCargando.setPadding(32, 32, 32, 32);
            tvCargando.setGravity(android.view.Gravity.CENTER);
            containerProximasCitas.addView(tvCargando);
        }

        executorService.execute(() -> {
            ArrayList<Cita> proximas = controller.getCitasActivas();

            mainHandler.post(() -> {
                if (!isAdded()) {
                    return;
                }
                if (proximas != null && !proximas.isEmpty()) {
                    listaCitasHome.clear();
                    listaCitasHome.addAll(proximas);
                    Collections.sort(listaCitasHome);
                    pintarTablaHome(listaCitasHome);
                } else {
                    if (containerProximasCitas != null) {
                        containerProximasCitas.removeAllViews();
                    }
                }
            });
        });
    }

    private void pintarTablaHome(ArrayList<Cita> citas) {
        if (containerProximasCitas == null) return;

        containerProximasCitas.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (Cita cita : citas) {
            if (cita.isEstado()) {
                View cardCita = inflater.inflate(R.layout.item_cita_home, containerProximasCitas, false);

                TextView tvCardFecha = cardCita.findViewById(R.id.tvCardFecha);
                TextView tvCardHora = cardCita.findViewById(R.id.tvCardHora);
                TextView tvCardCliente = cardCita.findViewById(R.id.tvCardCliente);
                TextView tvCardServicio = cardCita.findViewById(R.id.tvCardServicio);

                String fechaOriginal = cita.getFecha();
                String fechaCorta = fechaOriginal.length() > 5 ? fechaOriginal.substring(5) : fechaOriginal;

                tvCardFecha.setText(fechaCorta);
                tvCardHora.setText(cita.getHora());
                tvCardCliente.setText(cita.getUsuario().getNombre());
                tvCardServicio.setText(cita.getServicio().getNombreServicio());

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

                containerProximasCitas.addView(cardCita);
            }
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