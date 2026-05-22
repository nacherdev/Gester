package com.example.gester.ui.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gester.R;
import com.example.gester.controller.Controller;
import com.example.gester.dao.models.Servicio;
import com.example.gester.dao.models.Usuario;
import com.example.gester.utils.NotificationCreator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddFragment extends Fragment {

    private EditText etNombre;
    private EditText etApellidos;
    private EditText etDni;
    private EditText etFechaNacimiento;
    private EditText etDniBusqueda;
    private Button btnSeleccionarFecha;
    private Button btnSeleccionarNacimiento;
    private Button btnRecuperarUsuario;
    private Button btnGuardarCita;
    private TextView tvFechaSeleccionada;
    private Spinner spinnerServicios;
    private Spinner spinnerHoras;

    private Controller controller;
    private String fechaFinalMsql = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add, container, false);

        etNombre = root.findViewById(R.id.etNombre);
        etApellidos = root.findViewById(R.id.etApellidos);
        etDni = root.findViewById(R.id.etDni);
        etFechaNacimiento = root.findViewById(R.id.etFechaNacimiento);
        etDniBusqueda = root.findViewById(R.id.etDniBusqueda);
        btnSeleccionarFecha = root.findViewById(R.id.btnSeleccionarFecha);
        btnSeleccionarNacimiento = root.findViewById(R.id.btnSeleccionarNacimiento);
        btnRecuperarUsuario = root.findViewById(R.id.btnRecuperarUsuario);
        btnGuardarCita = root.findViewById(R.id.btnGuardarCita);
        tvFechaSeleccionada = root.findViewById(R.id.tvFechaSeleccionada);
        spinnerServicios = root.findViewById(R.id.spinnerServicios);
        spinnerHoras = root.findViewById(R.id.spinnerHoras);

        controller = Controller.getInstancia();
        cargarServicios();

        btnSeleccionarFecha.setOnClickListener(v -> mostrarMinicalendario());
        btnSeleccionarNacimiento.setOnClickListener(v -> mostrarCalendarioNacimiento());
        btnGuardarCita.setOnClickListener(v -> procesarGuardadoCita());

        btnRecuperarUsuario.setOnClickListener(v -> {
            if (etDniBusqueda.getVisibility() == View.GONE) {
                etDniBusqueda.setVisibility(View.VISIBLE);
                etDniBusqueda.requestFocus();
            } else {
                etDniBusqueda.setVisibility(View.GONE);
                etDniBusqueda.setText("");
            }
        });

        etDniBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String dniFiltrado = s.toString().trim();
                if (dniFiltrado.length() >= 4) {
                    buscarClientePorDni(dniFiltrado);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return root;
    }

    private void buscarClientePorDni(String dni) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Usuario usuarioEncontrado = controller.obtenerUsuarioPorDni(dni);

            handler.post(() -> {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                if (usuarioEncontrado != null) {
                    etNombre.setText(usuarioEncontrado.getNombre());
                    etApellidos.setText(usuarioEncontrado.getApellidos());
                    etDni.setText(usuarioEncontrado.getDNI());
                    etFechaNacimiento.setText(usuarioEncontrado.getFechaNacimiento());
                }
            });
        });
    }

    private void cargarServicios() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ArrayList<Servicio> servicios = controller.getServicios();

            handler.post(() -> {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                if (servicios.isEmpty()) {
                    servicios.add(new Servicio());
                }

                ArrayAdapter<Servicio> adapter = new ArrayAdapter<>(getContext(),
                        R.layout.spinner_item_custom, servicios);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerServicios.setAdapter(adapter);
            });
        });
    }

    private void mostrarCalendarioNacimiento() {
        Calendar calendar = Calendar.getInstance();
        int anyo = calendar.get(Calendar.YEAR) - 20;
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            int mesReal = month + 1;
            String fechaVisual = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, mesReal, year);
            etFechaNacimiento.setText(fechaVisual);
        }, anyo, mes, dia);

        datePickerDialog.show();
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
        if (spinnerHoras != null) {
            ArrayList<String> cargando = new ArrayList<>();
            cargando.add("Buscando horas...");
            ArrayAdapter<String> adapterCargando = new ArrayAdapter<>(getContext(), R.layout.spinner_item_custom, cargando);
            spinnerHoras.setAdapter(adapterCargando);
        }

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
                        R.layout.spinner_item_custom, todasLasHoras);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerHoras.setAdapter(adapter);
            });
        });
    }

    private void procesarGuardadoCita() {
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String fechaNac = etFechaNacimiento.getText().toString().trim();

        if (nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty() || fechaNac.isEmpty() || fechaFinalMsql.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, rellena todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerServicios.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Debes seleccionar un servicio", Toast.LENGTH_SHORT).show();
            return;
        }
        Servicio servicioSeleccionado = (Servicio) spinnerServicios.getSelectedItem();
        int idServicio = servicioSeleccionado.getId();

        if (spinnerHoras.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Debes seleccionar una hora", Toast.LENGTH_SHORT).show();
            return;
        }
        String horaSeleccionada = spinnerHoras.getSelectedItem().toString();

        if (horaSeleccionada.contains("Día completo") || horaSeleccionada.contains("Buscando horas...")) {
            Toast.makeText(getContext(), "Selecciona una hora válida disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String fechaCreacion = sdf.format(new Date());

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean exito = controller.crearCitaYUsuario(
                    nombre, apellidos, dni, fechaNac, idServicio, fechaFinalMsql, horaSeleccionada, fechaCreacion
            );

            handler.post(() -> {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                if (exito) {
                    Toast.makeText(getContext(), "Cita registrada con éxito", Toast.LENGTH_SHORT).show();
                    String titulo = "Agendado con éxito";
                    String mensaje = "Se ha creado la cita de "+nombre+" para el dia "+fechaFinalMsql +" a las "+horaSeleccionada;
                    NotificationCreator.enviar(getContext(), titulo, mensaje);
                    executor.execute(() -> {
                        controller.registrarNotificacion(titulo, mensaje);
                    });
                    limpiarFormulario();
                } else {
                    Toast.makeText(getContext(), "Error: La cita ya existe o no se pudo registrar", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void limpiarFormulario() {
        etNombre.setText("");
        etApellidos.setText("");
        etDni.setText("");
        etFechaNacimiento.setText("");
        etDniBusqueda.setText("");
        etDniBusqueda.setVisibility(View.GONE);
        tvFechaSeleccionada.setText("Fecha no seleccionada");
        fechaFinalMsql = "";
        spinnerHoras.setAdapter(null);
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