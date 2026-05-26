package com.example.gester.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.gester.R;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class RegistrarActivity extends AppCompatActivity {

    private EditText etDb, etPass;
    private Button btnSubmit, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar);

        hideSystemUI();

        etDb = findViewById(R.id.et_register_db_name);
        etPass = findViewById(R.id.et_register_password);
        btnSubmit = findViewById(R.id.btn_register_submit);
        btnVolver = findViewById(R.id.btn_volver);

        btnSubmit.setOnClickListener(v -> {
            String db = etDb.getText().toString().trim().replace(" ", "_").toLowerCase();
            String user = "admin_"+db.toLowerCase();
            String pass = etPass.getText().toString().trim();

            if (db.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            } else {
                guardarCredenciales(db, user, pass);
            }
        });

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrarActivity.this, AccesoActivity.class);
            startActivity(intent);
        });
    }

    private void guardarCredenciales(String nameDB, String userDB, String passDB) {
        String contenido = nameDB + ";" + userDB + ";" + passDB;

        try (FileOutputStream fos = openFileOutput("BBDD.txt", MODE_PRIVATE);
             OutputStreamWriter osw = new OutputStreamWriter(fos);
             BufferedWriter writer = new BufferedWriter(osw)) {

            writer.write(contenido);
            writer.close();

            Toast.makeText(this, "Credenciales registradas", Toast.LENGTH_SHORT).show();
            finish();

        } catch (IOException e) {
            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideSystemUI() {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }
}