package com.example.gester.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.gester.R;
import com.example.gester.controller.Controller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccesoActivity extends AppCompatActivity {

    private Button btnIniciarSesion, btnRegistrarse;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.acceso);

        hideSystemUI();

        btnIniciarSesion = findViewById(R.id.btn_login);
        btnRegistrarse = findViewById(R.id.btn_signup);

        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(AccesoActivity.this, RegistrarActivity.class);
            startActivity(intent);
        });

        btnIniciarSesion.setOnClickListener(v -> intentarConexion());
    }

    private void intentarConexion() {
        String[] credenciales = null;
        try {
            File archivoInterno = new File(getFilesDir(), "BBDD.txt");
            InputStream is;

            if (archivoInterno.exists()) {
                is = new FileInputStream(archivoInterno);
            } else {
                is = getAssets().open("BBDD.txt");
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String linea = br.readLine();
            if (linea != null && !linea.trim().isEmpty()) {
                credenciales = linea.trim().split(";");
            }
            br.close();
            is.close();
        } catch (IOException e) {
            Toast.makeText(this, "Error leyendo configuración", Toast.LENGTH_SHORT).show();
            return;
        }

        if (credenciales == null || credenciales.length < 3) {
            Toast.makeText(this, "No hay credenciales registradas", Toast.LENGTH_LONG).show();
            return;
        }

        String dbNombre = credenciales[0];
        String dbUsuario = credenciales[1];
        String dbPassword = credenciales[2];

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean conectado = false;
            try {
                Controller c = Controller.getInstancia();
                conectado = c.conectarBBDD(dbNombre, dbUsuario, dbPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean finalConectado = conectado;
            handler.post(() -> {
                if (isFinishing() || isDestroyed()) return;
                if (finalConectado) {
                    Intent intent = new Intent(AccesoActivity.this, HomeActivity.class);
                    intent.putExtra("nombre", dbNombre);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AccesoActivity.this, "Error de conexión. Verifica los datos.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void hideSystemUI() {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }
}