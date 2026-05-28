package com.example.gester.ui.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.gester.R;

public class ConfirmacionDialog extends DialogFragment {

    public interface ConfirmacionListener {
        void onResultado(boolean aceptado);
    }

    private ConfirmacionListener listener;

    public void setListener(ConfirmacionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.RoundedDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirmacion, container, false);

        Button btnSi = view.findViewById(R.id.btnSi);
        Button btnNo = view.findViewById(R.id.btnNo);

        btnSi.setOnClickListener(v -> {
            if (listener != null) listener.onResultado(true);
            dismiss();
        });

        btnNo.setOnClickListener(v -> {
            if (listener != null) listener.onResultado(false);
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            getDialog().getWindow().setDimAmount(0.5f);
            getDialog().getWindow().setElevation(0);
        }
    }
}