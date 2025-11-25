package com.example.calendariostareas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.calendariostareas.database.AppDatabase;
import com.example.calendariostareas.modelos.Materia;
import com.example.calendariostareas.modelos.Tarea;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class AgregarTareaActivity extends AppCompatActivity {

    private TextInputEditText etDescripcion, etFecha;
    private Spinner spinnerMaterias;
    private RadioGroup rgTipo;
    private Button btnGuardar;

    // Variables para guardar lo seleccionado
    private List<Materia> listaMateriasDB; // La lista completa con IDs y Colores
    private long fechaSeleccionada = 0; // Timestamp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tarea);

        // 1. Vincular Vistas
        etDescripcion = findViewById(R.id.etDescripcionTarea);
        etFecha = findViewById(R.id.etFechaTarea);
        spinnerMaterias = findViewById(R.id.spinnerMaterias);
        rgTipo = findViewById(R.id.rgTipoTarea);
        btnGuardar = findViewById(R.id.btnGuardarTarea);

        // 2. Configurar Selector de Fecha
        etFecha.setOnClickListener(v -> mostrarCalendario());

        // 3. Cargar Materias en el Spinner
        cargarMateriasEnSpinner();

        // 4. Guardar
        btnGuardar.setOnClickListener(v -> guardarTarea());
    }

    private void mostrarCalendario() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    // Guardar la fecha en variable Calendar para obtener milisegundos
                    Calendar calendarSeleccionado = Calendar.getInstance();
                    calendarSeleccionado.set(year1, month1, dayOfMonth);
                    fechaSeleccionada = calendarSeleccionado.getTimeInMillis();

                    // Mostrar texto bonito
                    etFecha.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void cargarMateriasEnSpinner() {
        Executors.newSingleThreadExecutor().execute(() -> {
            listaMateriasDB = AppDatabase.getInstance(this).materiaDao().getAll();

            List<String> nombresMaterias = new ArrayList<>();
            for (Materia m : listaMateriasDB) {
                nombresMaterias.add(m.nombre);
            }

            runOnUiThread(() -> {
                if (listaMateriasDB.isEmpty()) {
                    Toast.makeText(this, "Primero crea una materia", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, nombresMaterias);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMaterias.setAdapter(adapter);

                // --- NUEVO: Lógica de Pre-selección ---
                // 1. Verificar si recibimos un ID de la pantalla anterior
                int idRecibido = getIntent().getIntExtra("ID_MATERIA_PRESELECCIONADA", -1);

                if (idRecibido != -1) {
                    // 2. Buscar en la lista en qué posición está ese ID
                    for (int i = 0; i < listaMateriasDB.size(); i++) {
                        if (listaMateriasDB.get(i).idMateria == idRecibido) {
                            // 3. Mover el Spinner a esa posición
                            spinnerMaterias.setSelection(i);
                            break;
                        }
                    }
                    // Opcional: Deshabilitar el spinner para que no lo cambien por error
                    spinnerMaterias.setEnabled(false);
                }
            });
        });
    }
    private void guardarTarea() {
        String descripcion = etDescripcion.getText().toString().trim();

        if (descripcion.isEmpty()) {
            etDescripcion.setError("Escribe qué hay que hacer");
            return;
        }
        if (fechaSeleccionada == 0) {
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener Materia seleccionada
        // El spinner nos da la POSICIÓN seleccionada (0, 1, 2...)
        // Usamos esa posición para sacar el ID real de nuestra listaMateriasDB
        int posicion = spinnerMaterias.getSelectedItemPosition();
        Materia materiaSeleccionada = listaMateriasDB.get(posicion);

        // Obtener Tipo
        String tipo = "Tarea";
        if (rgTipo.getCheckedRadioButtonId() == R.id.rbExamen) tipo = "Examen";
        else if (rgTipo.getCheckedRadioButtonId() == R.id.rbProyecto) tipo = "Proyecto";

        // Crear Objeto
        Tarea nuevaTarea = new Tarea(materiaSeleccionada.idMateria, descripcion, fechaSeleccionada, tipo);

        // Guardar en BD
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).tareaDao().insert(nuevaTarea);
            runOnUiThread(() -> {
                Toast.makeText(this, "Tarea Agendada!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
