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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gester.R;
import com.example.gester.controller.Controller;
import com.example.gester.dao.models.Cita;
import com.example.gester.dao.models.Servicio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditAppointmentFragment extends Fragment {

    private int idCita = -1;
    private TextView tvFecha, tvCliente;
    private Spinner spHora, spServicio;
    private Button btnGuardar, btnEliminar;
    private Controller controller;
    private ArrayList<Servicio> listaServicios;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_appointment, container, false);

        controller = Controller.getInstancia();

        if (getArguments() != null) {
            idCita = getArguments().getInt("id_cita", -1);
        }

        tvFecha = root.findViewById(R.id.tvEditFecha);
        tvCliente = root.findViewById(R.id.tvEditCliente);
        spHora = root.findViewById(R.id.spEditHora);
        spServicio = root.findViewById(R.id.spEditServicio);
        btnGuardar = root.findViewById(R.id.btnGuardarCambios);
        btnEliminar = root.findViewById(R.id.btnEliminarCita);

        tvFecha.setOnClickListener(v -> mostrarCalendario());
        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnEliminar.setOnClickListener(v -> eliminarCita());

        if (idCita != -1) {
            inicializarDatos();
        } else {
            Toast.makeText(getContext(), "Error al cargar la cita", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }

        return root;
    }

    private void inicializarDatos() {
        executorService.execute(() -> {
            listaServicios = controller.getServicios();
            Cita cita = controller.obtenerCitaPorId(idCita);

            mainHandler.post(() -> {
                if (isAdded() && cita != null) {
                    tvCliente.setText(cita.getUsuario().getNombre() + " " + cita.getUsuario().getApellidos());
                    tvFecha.setText(cita.getFecha());

                    ArrayAdapter<Servicio> adapterServicios = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaServicios);
                    adapterServicios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spServicio.setAdapter(adapterServicios);

                    for (int i = 0; i < listaServicios.size(); i++) {
                        if (listaServicios.get(i).getId() == cita.getServicio().getId()) {
                            spServicio.setSelection(i);
                            break;
                        }
                    }

                    cargarHorasDisponibles(cita.getFecha(), cita.getHora());
                }
            });
        });
    }

    private void cargarHorasDisponibles(String fecha, String horaActualCita) {
        executorService.execute(() -> {
            ArrayList<String> horas = controller.obtenerHorasDisponibles(fecha);
            if (!horaActualCita.isEmpty() && !horas.contains(horaActualCita)) {
                horas.add(horaActualCita);
            }
            horas.sort(String::compareTo);

            mainHandler.post(() -> {
                if (isAdded()) {
                    ArrayAdapter<String> adapterHoras = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, horas);
                    adapterHoras.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spHora.setAdapter(adapterHoras);

                    for (int i = 0; i < horas.size(); i++) {
                        if (horas.get(i).equals(horaActualCita)) {
                            spHora.setSelection(i);
                            break;
                        }
                    }
                }
            });
        });
    }

    private void mostrarCalendario() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String fechaSeleccionada = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            tvFecha.setText(fechaSeleccionada);
            cargarHorasDisponibles(fechaSeleccionada, "");
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void guardarCambios() {
        if (spServicio.getSelectedItem() == null || spHora.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Faltan datos por seleccionar", Toast.LENGTH_SHORT).show();
            return;
        }

        Servicio s = (Servicio) spServicio.getSelectedItem();
        String fecha = tvFecha.getText().toString();
        String hora = spHora.getSelectedItem().toString();

        executorService.execute(() -> {
            boolean ok = controller.actualizarCita(idCita, s.getId(), fecha, hora);
            if (ok) {
                controller.registrarNotificacion("Cita Modificada", "Se ha actualizado la cita de " + tvCliente.getText().toString() + " para el " + fecha);
            }

            mainHandler.post(() -> {
                if (isAdded()) {
                    if (ok) {
                        Toast.makeText(getContext(), "Cita actualizada", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private void eliminarCita() {
        executorService.execute(() -> {
            boolean ok = controller.eliminarCita(idCita);
            if (ok) {
                controller.registrarNotificacion("Cita Cancelada", "Se ha eliminado la cita de " + tvCliente.getText().toString());
            }

            mainHandler.post(() -> {
                if (isAdded()) {
                    if (ok) {
                        Toast.makeText(getContext(), "Cita eliminada", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}