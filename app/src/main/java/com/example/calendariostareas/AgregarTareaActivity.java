package com.example.calendariostareas;

import android.app.DatePickerDialog;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AgregarTareaActivity extends AppCompatActivity {

    private TextInputEditText etDescripcion, etFecha;
    private Spinner spinnerMaterias;
    private RadioGroup rgTipo;
    private Button btnGuardar;

    // Variables de datos
    private List<Materia> listaMateriasDB;
    private long fechaSeleccionada = 0;

    // Variables para Edición
    private int idTareaEditar = -1; // -1 significa que estamos creando una nueva

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

        // 2. Configurar DatePicker
        etFecha.setOnClickListener(v -> mostrarCalendario());

        // 3. Cargar Materias (Y al terminar, verificar si es edición)
        cargarMateriasEnSpinner();

        // 4. Configurar Botón Guardar
        btnGuardar.setOnClickListener(v -> guardarTarea());
    }

    private void mostrarCalendario() {
        final Calendar c = Calendar.getInstance();

        // Si ya había una fecha seleccionada (edición), usar esa
        if (fechaSeleccionada != 0) {
            c.setTimeInMillis(fechaSeleccionada);
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    Calendar calendarSeleccionado = Calendar.getInstance();
                    calendarSeleccionado.set(year1, month1, dayOfMonth);
                    fechaSeleccionada = calendarSeleccionado.getTimeInMillis();

                    // Mostrar texto bonito
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    etFecha.setText(sdf.format(calendarSeleccionado.getTime()));
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

                // --- AQUÍ CONECTAMOS CON LA EDICIÓN ---
                // Una vez cargado el spinner, verificamos si tenemos que pre-llenar datos
                verificarSiEsEdicion();
            });
        });
    }

    private void verificarSiEsEdicion() {
        // Obtenemos los datos que nos pasó el Adapter (si existen)
        idTareaEditar = getIntent().getIntExtra("ID_TAREA_EDITAR", -1);

        // Si ID no es -1, significa que estamos EDITANDO
        if (idTareaEditar != -1) {
            btnGuardar.setText("Actualizar Tarea"); // Cambiar texto del botón

            // 1. Llenar Descripción
            String desc = getIntent().getStringExtra("DESC_TAREA");
            etDescripcion.setText(desc);

            // 2. Llenar Fecha
            long fecha = getIntent().getLongExtra("FECHA_TAREA", 0);
            fechaSeleccionada = fecha;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etFecha.setText(sdf.format(new Date(fecha)));

            // 3. Seleccionar Materia en el Spinner
            int idMateria = getIntent().getIntExtra("ID_MAT_TAREA", -1);
            for (int i = 0; i < listaMateriasDB.size(); i++) {
                if (listaMateriasDB.get(i).idMateria == idMateria) {
                    spinnerMaterias.setSelection(i);
                    break;
                }
            }

            // 4. Seleccionar Tipo (Radio Button)
            String tipo = getIntent().getStringExtra("TIPO_TAREA");
            if (tipo != null) {
                if (tipo.equals("Examen")) rgTipo.check(R.id.rbExamen);
                else if (tipo.equals("Proyecto")) rgTipo.check(R.id.rbProyecto);
                else rgTipo.check(R.id.rbTarea);
            }
        }
        else {
            // LÓGICA DE PRESELECCIÓN (Si venimos del botón + de una materia específica)
            int idPreseleccion = getIntent().getIntExtra("ID_MATERIA_PRESELECCIONADA", -1);
            if (idPreseleccion != -1) {
                for (int i = 0; i < listaMateriasDB.size(); i++) {
                    if (listaMateriasDB.get(i).idMateria == idPreseleccion) {
                        spinnerMaterias.setSelection(i);
                        break;
                    }
                }
            }
        }
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
        int posicion = spinnerMaterias.getSelectedItemPosition();
        Materia materiaSeleccionada = listaMateriasDB.get(posicion);

        // Obtener Tipo
        String tipo = "Tarea";
        if (rgTipo.getCheckedRadioButtonId() == R.id.rbExamen) tipo = "Examen";
        else if (rgTipo.getCheckedRadioButtonId() == R.id.rbProyecto) tipo = "Proyecto";

        // Crear Objeto Tarea
        Tarea tarea = new Tarea(materiaSeleccionada.idMateria, descripcion, fechaSeleccionada, tipo);

        Executors.newSingleThreadExecutor().execute(() -> {

            if (idTareaEditar != -1) {
                // MODO EDICIÓN: Asignamos el ID viejo para que Room sepa cuál actualizar
                tarea.idTarea = idTareaEditar;
                // Mantenemos el estado de "Completada" que tenía antes (opcional, o reseteamos a false)
                // Aquí estoy asumiendo que al editar no quieres desmarcarla si ya estaba hecha.
                // Si quieres leer el estado anterior, tendrías que haberlo pasado por Intent también.

                AppDatabase.getInstance(this).tareaDao().update(tarea);
            } else {
                // MODO CREACIÓN
                AppDatabase.getInstance(this).tareaDao().insert(tarea);
            }

            runOnUiThread(() -> {
                String mensaje = (idTareaEditar != -1) ? "Tarea actualizada" : "Tarea agendada";
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}