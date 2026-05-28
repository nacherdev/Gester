package com.example.gester.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.gester.R;
import com.example.gester.controller.Controller;
import com.example.gester.ui.Fragments.ConfirmacionDialog;

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

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

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
        File archivoInterno = new File(getFilesDir(), "BBDD.txt");

        if (!archivoInterno.exists()) {
            Toast.makeText(this, "No hay credenciales registradas. Registralos!.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AccesoActivity.this, RegistrarActivity.class);
            startActivity(intent);
            return;
        }

        String[] credenciales = null;
        try (FileInputStream fis = new FileInputStream(archivoInterno);
             BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
            String linea = br.readLine();
            if (linea != null && !linea.trim().isEmpty()) {
                credenciales = linea.trim().split(";");
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error leyendo archivo de configuración", Toast.LENGTH_SHORT).show();
            return;
        }

        if (credenciales == null || credenciales.length < 3) {
            Toast.makeText(this, "Credenciales inválidas. Redirigiendo...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AccesoActivity.this, RegistrarActivity.class);
            startActivity(intent);
            return;
        }

        realizarConexionHilo(credenciales[0], credenciales[1], credenciales[2]);
    }

    private void realizarConexionHilo(String dbNombre, String dbUsuario, String dbPassword) {
        executorService.execute(() -> {
            int conectado = 0;
            try {
                Controller c = Controller.getInstancia();
                conectado = c.conectarBBDD(dbNombre, dbUsuario, dbPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int finalConectado = conectado;
            mainHandler.post(() -> {
                if (isFinishing() || isDestroyed()) return;

                switch (finalConectado) {
                    case 0:
                        Intent intent = new Intent(AccesoActivity.this, HomeActivity.class);
                        intent.putExtra("nombre", dbNombre);
                        startActivity(intent);
                        break;
                    case -1:
                        Toast.makeText(AccesoActivity.this, "Error: El servidor no responde", Toast.LENGTH_LONG).show();
                        break;
                    case -2:
                        Toast.makeText(AccesoActivity.this, "Error: Credenciales incorrectas", Toast.LENGTH_LONG).show();
                        break;
                    case -3:
                        ConfirmacionDialog dialogo = new ConfirmacionDialog();
                        dialogo.setListener(aceptado -> {
                            if (aceptado) {
                                Toast.makeText(AccesoActivity.this, "Creando base de datos...", Toast.LENGTH_LONG).show();

                                executorService.execute(() -> {
                                    Controller controller = Controller.getInstancia();
                                    boolean exito = controller.crearBaseDeDatos(dbNombre, dbUsuario, dbPassword);

                                    mainHandler.post(() -> {
                                        if (isFinishing() || isDestroyed()) return;
                                        if (exito) {
                                            Toast.makeText(AccesoActivity.this, "Base de datos creada. Accediendo...", Toast.LENGTH_SHORT).show();
                                            realizarConexionHilo(dbNombre, dbUsuario, dbPassword);

                                        } else {
                                            Toast.makeText(AccesoActivity.this, "Ha habido un problema al crear la base de datos", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                });
                            }
                        });
                        dialogo.show(getSupportFragmentManager(), "ConfirmacionDialog");
                        break;
                    case -4:
                        Toast.makeText(AccesoActivity.this, "Error: Contacta con administrador", Toast.LENGTH_LONG).show();
                        break;
                }
            });
        });
    }

    private void hideSystemUI() {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}