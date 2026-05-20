package com.example.gester.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.gester.R;
import com.example.gester.controller.Controller;

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

                            if (linea != null && !linea.trim().isEmpty()) {
                                credenciales = linea.trim().split(";");
                            }

                            br.close();
                            is.close();
                        } catch (IOException e) {
                            Toast.makeText(AccesoActivity.this, "Error al leer BBDD.txt", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (credenciales == null || credenciales.length < 3) {
                            Toast.makeText(AccesoActivity.this, "El archivo no tiene los 3 datos separados por ';'", Toast.LENGTH_LONG).show();
                            return;
                        }

                        final String dbNombre = credenciales[0];
                        final String dbUsuario = credenciales[1];
                        final String dbPassword = credenciales[2];

                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());

                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                boolean conectado = false;
                                try {
                                    Controller c = Controller.getInstancia();
                                    conectado = c.conectarBBDD(dbNombre, dbUsuario, dbPassword);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                boolean finalConectado = conectado;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isFinishing() || isDestroyed()) {
                                            return;
                                        }
                                        if (finalConectado) {
                                            Intent intent = new Intent(AccesoActivity.this, HomeActivity.class);
                                            intent.putExtra("nombre", dbNombre);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(AccesoActivity.this, "No se pudo conectar a nacherdev.es. Revisa el Driver o la red.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
        );
    }
}