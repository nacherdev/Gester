package com.example.gester.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gester.R;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class RegistrarActivity extends AppCompatActivity {

    private EditText etDb, etUser, etPass;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar);

        etDb = findViewById(R.id.et_register_db_name);
        etUser = findViewById(R.id.et_register_user);
        etPass = findViewById(R.id.et_register_password);
        btnSubmit = findViewById(R.id.btn_register_submit);

        btnSubmit.setOnClickListener(v -> {
            String db = etDb.getText().toString().trim();
            String user = etUser.getText().toString().trim();
            String pass = etPass.getText().toString().trim();

            if (db.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            } else {
                guardarCredenciales(db, user, pass);
            }
        });
    }

    private void guardarCredenciales(String nameDB, String userDB, String passDB) {
        String contenido = nameDB + ";" + userDB + ";" + passDB;

        try (FileOutputStream fos = openFileOutput("BBDD.txt", MODE_PRIVATE);
             OutputStreamWriter osw = new OutputStreamWriter(fos);
             BufferedWriter writer = new BufferedWriter(osw)) {

            writer.write(contenido);
            writer.flush();

            Toast.makeText(this, "Credenciales registradas", Toast.LENGTH_SHORT).show();
            finish();

        } catch (IOException e) {
            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}