package com.example.calendariostareas;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.calendariostareas.database.AppDatabase;
import com.example.calendariostareas.modelos.Materia;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Executors;

public class AgregarMateriaActivity extends AppCompatActivity {

    private TextInputEditText etNombre;
    private RadioGroup rgColores;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_materia);

        // 1. Encontrar vistas por ID
        etNombre = findViewById(R.id.etNombreMateria);
        rgColores = findViewById(R.id.rgColores);
        btnGuardar = findViewById(R.id.btnGuardarMateria);

        // 2. Configurar el click del botón
        btnGuardar.setOnClickListener(v -> guardarMateria());
    }

    private void guardarMateria() {
        String nombre = etNombre.getText().toString().trim();

        // Validación simple
        if (nombre.isEmpty()) {
            etNombre.setError("Escribe un nombre");
            return;
        }

        // Obtener color seleccionado
        String colorHex = "#000000"; // Negro por defecto
        int selectedId = rgColores.getCheckedRadioButtonId();

        if (selectedId == R.id.rbRojo) colorHex = "#F44336";
        else if (selectedId == R.id.rbAzul) colorHex = "#2196F3";
        else if (selectedId == R.id.rbVerde) colorHex = "#4CAF50";
        else if (selectedId == R.id.rbAmarillo) colorHex = "#FFEB3B";

        // Crear el objeto
        Materia nuevaMateria = new Materia(nombre, colorHex);

        // 3. GUARDAR EN BD (En segundo plano para no congelar la app)
        Executors.newSingleThreadExecutor().execute(() -> {

            // Operación de base de datos
            AppDatabase.getInstance(this).materiaDao().insert(nuevaMateria);

            // Volver al hilo principal para mostrar mensaje y cerrar
            runOnUiThread(() -> {
                Toast.makeText(this, "Materia guardada exitosamente", Toast.LENGTH_SHORT).show();
                finish(); // Cierra esta pantalla y regresa a la anterior
            });
        });
    }
}
