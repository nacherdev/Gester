package com.example.gester.ui.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gester.R;

public class HomeFragment extends Fragment {

    private CalendarView calendarView;
    private TextView txtBienvenida;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        txtBienvenida = root.findViewById(R.id.textView2);
        calendarView = root.findViewById(R.id.calendarView);

        String nombreFinal = "Usuario";
        if (getArguments() != null) {
            nombreFinal = getArguments().getString("nombre");
        }

        txtBienvenida.setText("¡" + nombreFinal + ", bienvenidos!");
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                Toast.makeText(getContext(), "Citas para el: " + fecha, Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}