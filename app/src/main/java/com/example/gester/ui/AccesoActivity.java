package com.example.gester.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.example.gester.R;

public class AccesoActivity extends AppCompatActivity {

    private Button btnIniciarSesion, btnRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.acceso);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_acceso), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnIniciarSesion = findViewById(R.id.btn_login);
        btnRegistrarse = findViewById(R.id.btn_signup);

        btnIniciarSesion.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] credenciales = null;
                        String linea = "";
                        InputStream is = null;
                        BufferedReader br = null;
                        try {
                            is = getAssets().open("BBDD.txt");
                            br = new BufferedReader(new InputStreamReader(is));
                            linea = br.readLine();
                            credenciales = linea.split(";");

                            br.close();
                            is.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        Intent intent = new Intent(AccesoActivity.this, HomeActivity.class);
                        intent.putExtra("nombre", credenciales[0]);
                        startActivity(intent);
                        finish();
                    }
                }
        );

    }
}
